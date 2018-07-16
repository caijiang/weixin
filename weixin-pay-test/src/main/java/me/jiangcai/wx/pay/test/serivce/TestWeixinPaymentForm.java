package me.jiangcai.wx.pay.test.serivce;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.jiangcai.payment.PayableOrder;
import me.jiangcai.payment.entity.PayOrder;
import me.jiangcai.payment.exception.SystemMaintainException;
import me.jiangcai.wx.pay.entity.WeixinPayOrder;
import me.jiangcai.wx.pay.service.WeixinPaymentFormImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author CJ
 */
@Service
@Primary
public class TestWeixinPaymentForm extends WeixinPaymentFormImpl {
    private static final ObjectMapper objectMapper = new ObjectMapper();

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
        String id = UUID.randomUUID().toString().replaceAll("-", "");
        payOrder.setPrepayId(id);
        payOrder.setJavascriptToPay(javascriptForWechatPay(id));
        return payOrder;
    }

    /**
     * copy from ProtocolImpl
     *
     * @param prepayId
     * @return
     */
    private String javascriptForWechatPay(String prepayId) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        Map<String, String> orderInfoMap = new HashMap<>();
        orderInfoMap.put("appId", "appId");
        orderInfoMap.put("timeStamp", String.valueOf(System.currentTimeMillis()));
        orderInfoMap.put("nonceStr", uuid);
        orderInfoMap.put("package", "prepay_id=" + prepayId);
        orderInfoMap.put("signType", "MD5");
        orderInfoMap.put("paySign", "sign");
        try {
            return objectMapper.writeValueAsString(orderInfoMap);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }
}
