package me.jiangcai.wx.pay.controller;

import com.github.wxpay.sdk.WXPayUtil;
import me.jiangcai.wx.PublicAccountSupplier;
import me.jiangcai.wx.model.PublicAccount;
import me.jiangcai.wx.pay.model.WeixinPayUrl;
import me.jiangcai.wx.protocol.Protocol;
import me.jiangcai.wx.protocol.event.OrderChangeEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * @author helloztt
 */
@Controller
public class WeixinPayHook {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private PublicAccountSupplier publicAccountSupplier;

    private static final Log log = LogFactory.getLog(WeixinPayHook.class);

    @RequestMapping(method = RequestMethod.POST,value = WeixinPayUrl.relUrl)
    public ResponseEntity<String> webRequest(HttpServletRequest request) throws Exception {
        final String content = StreamUtils.copyToString(request.getInputStream(), Charset.forName("UTF-8"));
        log.debug("来访数据:" + content);
        Map<String, String> respData = WXPayUtil.xmlToMap(content);
        //解析数据，并校验sign
        PublicAccount publicAccount = publicAccountSupplier.getAccounts().stream()
                .filter(account->account.getAppID().equals(respData.get("appid")))
                .findFirst().orElse(null);
        Map<String,String> data = Protocol.forAccount(publicAccount).processResponseXml(content);
        OrderChangeEvent event = new OrderChangeEvent();
        event.setData(data);
        applicationEventPublisher.publishEvent(event);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML)
                .body("<xml><return_code><![CDATA[SUCCESS]]></return_code></xml>");
    }


}
