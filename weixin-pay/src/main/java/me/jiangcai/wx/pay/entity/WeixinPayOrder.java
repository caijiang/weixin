package me.jiangcai.wx.pay.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.payment.PaymentForm;
import me.jiangcai.payment.entity.PayOrder;
import me.jiangcai.wx.pay.service.WeixinPaymentForm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import java.math.BigDecimal;

/**
 * 微信统一的支付订单
 *
 * @author helloztt
 */
@Entity
@Setter
@Getter
public class WeixinPayOrder extends PayOrder {
    /**
     * @since 2.3.0
     */
    @Column(columnDefinition = "varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String nickname;
    /**
     * @since 2.3.0
     */
    @Column(length = 180)
    private String headImageUrl;
    /**
     * 一段脚本可以引导支付
     */
    @Lob
    private String javascriptToPay;
    /**
     * 预支付交易会话标识
     */
    @Column(length = 64)
    private String prepayId;
    /**
     * openId
     */
    @Column(length = 30)
    private String openId;
    /**
     * 二维码链接
     */
    @Column(length = 64)
    private String codeUrl;

    @Column(scale = 2, precision = 11)
    private BigDecimal amount;

    /**
     * SUCCESS—支付成功
     * REFUND—转入退款
     * NOTPAY—未支付
     * CLOSED—已关闭
     * REVOKED—已撤销（刷卡支付）
     * USERPAYING--用户支付中
     * PAYERROR--支付失败(其他原因，如银行返回失败)
     */
    @Column(length = 15)
    private String orderStatus;

    /**
     * 支付成功的跳转地址
     */
    @Column
    private String redirectUrl;

    @Override
    public Class<? extends PaymentForm> getPaymentFormClass() {
        return WeixinPaymentForm.class;
    }


}
