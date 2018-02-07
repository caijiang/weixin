package me.jiangcai.wx.pay.model;

import lombok.Data;

/**
 * 微信支付相关请求地址定义
 * @author helloztt
 */
@Data
public class WeixinPayUrl {
    /**
     * 完整的请求地址 http://
     */
    private String absUrl;
    /**
     * 相对的请求地址，用于定义 RequestMapping
     */
    private String relUrl;
}
