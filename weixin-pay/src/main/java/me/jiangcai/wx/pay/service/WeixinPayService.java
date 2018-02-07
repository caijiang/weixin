package me.jiangcai.wx.pay.service;

import me.jiangcai.payment.PayableOrder;
import me.jiangcai.payment.entity.PayOrder;
import me.jiangcai.payment.service.PayableSystemService;
import me.jiangcai.wx.pay.entity.WeixinPayOrder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author helloztt
 */
@Service
public class WeixinPayService implements PayableSystemService {
    @Override
    public ModelAndView paySuccess(HttpServletRequest request, PayableOrder payableOrder, PayOrder payOrder) {
        if (payOrder instanceof WeixinPayOrder) {
            return new ModelAndView("redirect:" + ((WeixinPayOrder) payOrder).getRedirectUrl());
        } else {
            throw new IllegalStateException("暂时不支持：" + payableOrder);
        }
    }

    @Override
    public ModelAndView pay(HttpServletRequest request, PayableOrder order, PayOrder payOrder, Map<String, Object> additionalParameters) {
        ModelAndView modelAndView = new ModelAndView();
        if (payOrder instanceof WeixinPayOrder) {
            WeixinPayOrder weixinPayOrder = (WeixinPayOrder) payOrder;
            weixinPayOrder.setRedirectUrl(additionalParameters.get("successRedirectUrl").toString());
            modelAndView.setViewName("/weixin-pay/paying");
            modelAndView.addObject("payRequestParam", weixinPayOrder.getJavascriptToPay());
            modelAndView.addObject("successRedirectUrl", weixinPayOrder.getRedirectUrl());
            modelAndView.addObject("failureRedirectUrl", additionalParameters.get("failureRedirectUrl").toString());
        }
        return modelAndView;
    }

    @Override
    public boolean isPaySuccess(String id) {
        return false;
    }

    @Override
    public PayableOrder getOrder(String id) {
        return null;
    }
}
