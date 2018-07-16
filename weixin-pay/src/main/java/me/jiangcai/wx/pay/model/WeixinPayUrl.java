package me.jiangcai.wx.pay.model;

import lombok.Data;

/**
 * 微信支付相关请求地址定义
 *
 * @author helloztt
 */
@Data
public class WeixinPayUrl {
    /**
     * 完整的请求地址 http://
     */
    private String absUrl;
    /**
     * 相对的请求地址，没必要给使用者定义吧
     * 2.3.0 之后可以用 wechat.pay.notify.uri 更换
     */
    public static final String relUrl = "/weixin/payment/notify";
}
