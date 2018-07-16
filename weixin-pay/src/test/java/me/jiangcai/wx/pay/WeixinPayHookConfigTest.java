package me.jiangcai.wx.pay;

import me.jiangcai.lib.test.SpringWebTest;
import me.jiangcai.payment.PayableOrder;
import me.jiangcai.payment.entity.PayOrder;
import me.jiangcai.payment.event.PaymentEvent;
import me.jiangcai.payment.service.PayableSystemService;
import me.jiangcai.payment.service.PaymentGatewayService;
import me.jiangcai.payment.test.PaymentTestConfig;
import me.jiangcai.payment.test.service.MockPayToggle;
import me.jiangcai.wx.PublicAccountSupplier;
import me.jiangcai.wx.TokenType;
import me.jiangcai.wx.WeixinUserService;
import me.jiangcai.wx.model.PublicAccount;
import me.jiangcai.wx.model.UserAccessResponse;
import me.jiangcai.wx.model.WeixinUser;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.NestedServletException;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * WeixinTestConfig.class,
 *
 * @author CJ
 */
@ContextConfiguration(classes = {WeixinPayHookConfigTest.Config.class, WeixinPayHookConfig.class, PaymentTestConfig.class})
@WebAppConfiguration
public class WeixinPayHookConfigTest extends SpringWebTest {

    @Autowired
    private Environment environment;

    @Test
    public void uriTest() throws Exception {
        System.out.println(environment.getProperty("wechat.pay.notify.uri"));

        try {
            mockMvc.perform(
                    post("/event")
            )
                    .andDo(print());
//            mockMvc.perform(
//                    post(WeixinPayUrl.relUrl)
//            )
//                    .andDo(print());
            assert false;
        } catch (NestedServletException ignored) {

        }
    }

    @Configuration
    @EnableJpaRepositories
    @ImportResource("classpath:/datasource_local.xml")
    @PropertySource("classpath:/test_uri.properties")
    public static class Config {

        @Bean
        public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
            return new PropertySourcesPlaceholderConfigurer();
        }

        @Bean
        public WeixinUserService weixinUserService() {
            return new WeixinUserService() {
                @Override
                public <T> T userInfo(PublicAccount account, String openId, Class<T> clazz, Object data) {
                    return null;
                }

                @Override
                public void updateUserToken(PublicAccount account, UserAccessResponse response, Object data) {

                }

                @Override
                public WeixinUser getTokenInfo(PublicAccount account, String openId) {
                    return null;
                }
            };
        }

        @Bean
        public MockPayToggle mockPayToggle() {
            return new MockPayToggle() {

                @Override
                public Integer autoPaySeconds(PayableOrder payableOrder, PayOrder payOrder) throws Exception {
                    return null;
                }
            };
        }

        @Bean
        public PayableSystemService payableSystemService() {
            return new PayableSystemService() {

                @Override
                public ModelAndView paySuccess(HttpServletRequest httpServletRequest, PayableOrder payableOrder, PayOrder payOrder) {
                    return null;
                }

                @Override
                public ModelAndView pay(HttpServletRequest httpServletRequest, PayableOrder payableOrder, PayOrder payOrder, Map<String, Object> map) {
                    return null;
                }

                @Override
                public boolean isPaySuccess(String s) {
                    return false;
                }

                @Override
                public PayableOrder getOrder(String s) {
                    return null;
                }
            };
        }

        @Bean
        public PaymentGatewayService paymentGatewayService() {
            return new PaymentGatewayService() {
                @Override
                public <T extends PayOrder> T getOrder(Class<T> aClass, String s) {
                    return null;
                }

                @Override
                public <T extends PayOrder> T getOrderByMerchantOrderId(Class<T> aClass, String s) {
                    return null;
                }

                @Override
                public void makeEvent(PaymentEvent paymentEvent) {

                }

                @Override
                public void paySuccess(PayOrder payOrder) {

                }

                @Override
                public void payCancel(PayOrder payOrder) {

                }

                @Override
                public PayOrder getSuccessOrder(String s) {
                    return null;
                }

                @Override
                public PayOrder getLatestOrder(String s) {
                    return null;
                }

                @Override
                public void queryPayStatus(PayOrder payOrder) {

                }
            };
        }

        @Bean
        public PublicAccountSupplier publicAccountSupplier() {
            return new PublicAccountSupplier() {
                @Override
                public List<? extends PublicAccount> getAccounts() {
                    return Collections.emptyList();
                }

                @Override
                public PublicAccount findByIdentifier(String identifier) {
                    return null;
                }

                @Override
                public void updateToken(PublicAccount account, TokenType type, String token, LocalDateTime timeToExpire) throws Throwable {

                }

                @Override
                public void getTokens(PublicAccount account) {

                }

                @Override
                public PublicAccount findByHost(String host) {
                    return null;
                }
            };
        }
    }

}