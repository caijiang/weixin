package me.jiangcai.wx.pay.service;

import me.jiangcai.lib.ee.ServletUtils;
import me.jiangcai.payment.PayableOrder;
import me.jiangcai.payment.entity.PayOrder;
import me.jiangcai.payment.exception.SystemMaintainException;
import me.jiangcai.payment.service.PaymentGatewayService;
import me.jiangcai.wx.PublicAccountSupplier;
import me.jiangcai.wx.couple.WeixinRequestHandlerMapping;
import me.jiangcai.wx.model.PublicAccount;
import me.jiangcai.wx.model.pay.TradeType;
import me.jiangcai.wx.model.pay.UnifiedOrderRequest;
import me.jiangcai.wx.model.pay.UnifiedOrderResponse;
import me.jiangcai.wx.pay.entity.WeixinPayOrder;
import me.jiangcai.wx.pay.model.WeixinPayUrl;
import me.jiangcai.wx.protocol.Protocol;
import me.jiangcai.wx.protocol.event.OrderChangeEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author helloztt
 */
@Service
public class WeixinPaymentFormImpl implements WeixinPaymentForm {
    private static final Log log = LogFactory.getLog(WeixinPaymentFormImpl.class);
    @Autowired
    private PaymentGatewayService paymentGatewayService;
    @Autowired
    private WeixinRequestHandlerMapping weixinRequestHandlerMapping;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private WeixinPayUrl weixinPayUrl;
    @Autowired
    private PublicAccountSupplier publicAccountSupplier;

    @Override
    public PayOrder newPayOrder(HttpServletRequest request, PayableOrder order, Map<String, Object> additionalParameters) throws SystemMaintainException {
        WeixinPayOrder payOrder = new WeixinPayOrder();
        payOrder.setAmount(order.getOrderDueAmount());
        if (!StringUtils.isEmpty(additionalParameters.get("nickname"))) {
            payOrder.setNickname(additionalParameters.get("nickname").toString());
        }
        if (!StringUtils.isEmpty(additionalParameters.get("headUrl"))) {
            payOrder.setHeadImageUrl(additionalParameters.get("headUrl").toString());
        }

        UnifiedOrderRequest orderRequest = new UnifiedOrderRequest();
        orderRequest.setOrderNumber(payOrder.getMerchantOrderId());
        orderRequest.setBody(order.getOrderBody());
        orderRequest.setAmount(order.getOrderDueAmount());
        orderRequest.setClientIpAddress(ServletUtils.clientIpAddress(request));
        orderRequest.setNotifyUrl(weixinPayUrl.getAbsUrl());

        //交易类型，默认为jsapi
        Object tradeTypeObj = additionalParameters.get("tradeType");
        TradeType tradeType = TradeType.JSAPI;
        if (tradeTypeObj == null) {
        } else if (tradeTypeObj instanceof TradeType) {
            tradeType = (TradeType) tradeTypeObj;
        } else {
            tradeType = TradeType.valueOf(tradeTypeObj.toString());
        }
        orderRequest.setTradeType(tradeType);
        if (TradeType.JSAPI.equals(tradeType)) {
            orderRequest.setOpenId(additionalParameters.get("openId").toString());
        }
        try {
            UnifiedOrderResponse orderResponse = Protocol.forAccount(weixinRequestHandlerMapping.currentPublicAccount()).createUnifiedOrder(orderRequest);
            switch (tradeType) {
                case JSAPI:
                    payOrder.setPrepayId(orderResponse.getPrepayId());
                    payOrder.setJavascriptToPay(javascriptForWechatPay(orderResponse.getPrepayId()));
                    log.debug("javascript:" + payOrder.getJavascriptToPay());
                    break;
                case NATIVE:
                    payOrder.setCodeUrl(orderResponse.getCodeUrl());
                    break;
                default:
                    payOrder.setPrepayId(orderResponse.getPrepayId());
            }
        } catch (Exception e) {
            log.error("生成订单出错", e);
            throw new SystemMaintainException(e);
        }
        return payOrder;
    }

    @Override
    public void orderMaintain() {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<WeixinPayOrder> criteriaQuery = criteriaBuilder.createQuery(WeixinPayOrder.class);
        Root<WeixinPayOrder> root = criteriaQuery.from(WeixinPayOrder.class);
        criteriaQuery = criteriaQuery.where(
                criteriaBuilder.and(
                        // 未成功而且未失败
                        criteriaBuilder.equal(root.get("orderStatus"), "SUCCEED")
                        , PayOrder.Success(root, criteriaBuilder).not()
                        , PayOrder.Cancel(root, criteriaBuilder).not()
                )
        );
        entityManager.createQuery(criteriaQuery)
                .getResultList()
                .forEach(order -> paymentGatewayService.paySuccess(order));
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    @EventListener(OrderChangeEvent.class)
    public void orderChange(OrderChangeEvent event) {
        log.debug("trade event:" + event);
        //解析数据，并校验sign
        PublicAccount publicAccount = publicAccountSupplier.getAccounts().stream()
                .filter(account -> account.getAppID().equals(event.getData().get("appid")))
                .findFirst().orElse(null);
        UnifiedOrderResponse orderResponse = Protocol.forAccount(publicAccount).getOrderQueryResponse(event.getData());
        WeixinPayOrder order = paymentGatewayService.getOrderByMerchantOrderId(WeixinPayOrder.class, orderResponse.getOrderNumber());
        if (order == null) {
            log.warn("received trade event without system:" + event);
            return;
        }
        //校验金额是否一致
        if (order.getAmount().multiply(BigDecimal.valueOf(100L)).compareTo(orderResponse.getTotalFee()) != 0) {
            log.warn("received trade amount not equal:" + event);
            return;
        }
        if (StringUtils.isEmpty(order.getOpenId())) {
            order.setOpenId(orderResponse.getOpenId());
        }
        order.setPlatformId(orderResponse.getTransactionId());
        order.setEventTime(LocalDateTime.now());
        order.setOrderStatus(orderResponse.getTradeStatus());

        if (!order.isCancel()) {
            if ("SUCCESS".equals(order.getOrderStatus())) {
                paymentGatewayService.paySuccess(order);
            } else if ("CLOSED".equals(order.getOrderStatus())) {
                paymentGatewayService.payCancel(order);
            }
        }
    }

    private String javascriptForWechatPay(String prepayId) throws SystemMaintainException {
        try {
            return Protocol.forAccount(weixinRequestHandlerMapping.currentPublicAccount()).javascriptForWechatPay(prepayId);
        } catch (Exception e) {
            log.error("生成脚本出错", e);
            throw new SystemMaintainException(e);
        }
    }
}
