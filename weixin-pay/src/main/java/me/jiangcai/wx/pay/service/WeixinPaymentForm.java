package me.jiangcai.wx.pay.service;

import me.jiangcai.payment.PaymentForm;
import me.jiangcai.wx.protocol.event.OrderChangeEvent;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author helloztt
 */
public interface WeixinPaymentForm extends PaymentForm {

    /**
     * 订单状态修改事件监听
     * @param event
     */
    @Transactional
    @EventListener(OrderChangeEvent.class)
    void orderChange(OrderChangeEvent event);
}
