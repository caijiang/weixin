package me.jiangcai.wx.couple;

import me.jiangcai.wx.MessageReply;
import me.jiangcai.wx.message.Message;
import me.jiangcai.wx.model.PublicAccount;
import me.jiangcai.wx.protocol.Algorithmic;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

/**
 * @author CJ
 */
@Component
public class WeixinRequestHandler {

    private static final Log log = LogFactory.getLog(WeixinRequestHandler.class);
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private ApplicationContext applicationContext;

    @ResponseBody
//    @ResponseStatus(HttpStatus.OK)
    public Message receive(PublicAccount publicAccount, @RequestBody Message message) {
        log.debug("Received:" + message);
        message.setAccount(publicAccount);
        applicationEventPublisher.publishEvent(message);

        for (MessageReply messageReply : applicationContext.getBeansOfType(MessageReply.class).values()) {
            if (!messageReply.focus(publicAccount, message))
                continue;
            Message reply = messageReply.reply(publicAccount, message);
            if (reply == null)
                return null;
//            reply.setId(UUID.randomUUID().toString());
            reply.setTime(LocalDateTime.now());
            reply.setFrom(message.getTo());
            reply.setTo(message.getFrom());
            
            return reply;
        }

        return null;
    }


    @ResponseBody
    public String hello(PublicAccount publicAccount, String signature, String timestamp, String nonce, String echostr) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        if (Algorithmic.interfaceCheck(publicAccount, signature, timestamp, nonce)) return echostr;
        return "WELCOME";
    }


}