package me.jiangcai.wx.pay.controller;

import me.jiangcai.wx.couple.WeixinRequestHandlerMapping;
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
import org.springframework.web.bind.annotation.RequestHeader;
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
    private WeixinRequestHandlerMapping mapping;

    private static final Log log = LogFactory.getLog(WeixinPayHook.class);

    @RequestMapping(method = RequestMethod.POST,value = WeixinPayUrl.relUrl)
    public ResponseEntity<String> webRequest(HttpServletRequest request, @RequestHeader("Sign") String sign) throws Exception {
        final String content = StreamUtils.copyToString(request.getInputStream(), Charset.forName("UTF-8"));
        log.debug("来访数据:" + content);
        //解析数据，并校验sign
        Map<String,String> data = Protocol.forAccount(mapping.currentPublicAccount()).processResponseXml(content);
        OrderChangeEvent event = new OrderChangeEvent();
        event.setData(data);
        applicationEventPublisher.publishEvent(event);

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body("success");
    }


}
