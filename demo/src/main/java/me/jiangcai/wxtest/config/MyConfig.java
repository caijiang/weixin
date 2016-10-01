package me.jiangcai.wxtest.config;

import me.jiangcai.wx.MessageReply;
import me.jiangcai.wx.PublicAccountSupplier;
import me.jiangcai.wx.classic.ClassicMessageReply;
import me.jiangcai.wx.model.PublicAccount;
import me.jiangcai.wx.web.WeixinWebSpringConfig;
import me.jiangcai.wx.web.thymeleaf.WeixinDialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.Collections;
import java.util.List;

/**
 * @author CJ
 */
@Configuration
@Import({WeixinWebSpringConfig.class, MyConfig.Config.class})
@ComponentScan("me.jiangcai.wxtest.controller")
@EnableWebMvc
public class MyConfig extends WebMvcConfigurerAdapter {

    private final PublicAccount account;

    public MyConfig() {
        account = publicAccount();
    }

    @Bean
    public PublicAccountSupplier publicAccountSupplier() {
        return new PublicAccountSupplier() {
            @Override
            public List<PublicAccount> getAccounts() {
                return Collections.singletonList(account);
            }

            @Override
            public PublicAccount findByIdentifier(String identifier) {
                return account;
            }

            @Override
            public PublicAccount findByHost(String host) {
                return account;
            }
        };
    }

    private PublicAccount publicAccount() {
        PublicAccount publicAccount = new PublicAccount();
        publicAccount.setAppID("wx59b0162cdf0967af");
        publicAccount.setAppSecret("ffcf655fce7c4175bbddae7b594c4e27");
        publicAccount.setInterfaceURL("http://wxtest.jiangcai.me/wxtest/");
        publicAccount.setInterfaceToken("jiangcai");
        return publicAccount;
    }

    @Bean
    public MessageReply messageReply() {
        return new ClassicMessageReply();
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        super.addViewControllers(registry);
        registry.addViewController("/js.html")
                .setViewName("js.html");
    }

    @Configuration
    @EnableWebMvc
    @Import(Config.ThymeleafConfig.class)
    static class Config extends WebMvcConfigurerAdapter {

        @Autowired
        private ThymeleafViewResolver thymeleafViewResolver;

        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
            super.addViewControllers(registry);
            registry.addViewController("/js")
                    .setViewName("js.html");
        }

        @Override
        public void configureViewResolvers(ViewResolverRegistry registry) {
            super.configureViewResolvers(registry);
            registry.viewResolver(thymeleafViewResolver);
        }

        @Import(ThymeleafConfig.ThymeleafTemplateConfig.class)
        static class ThymeleafConfig {
            @Autowired
            private TemplateEngine engine;

            @Bean
            private ThymeleafViewResolver thymeleafViewResolver() {
                ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
                viewResolver.setCache(false);
                viewResolver.setTemplateEngine(engine);
                viewResolver.setCharacterEncoding("UTF-8");
                viewResolver.setContentType("text/html;charset=UTF-8");
                return viewResolver;
            }

            static class ThymeleafTemplateConfig {
                @Autowired
                private WebApplicationContext webApplicationContext;
                @Autowired
                private WeixinDialect weixinDialect;

                @Bean
                public TemplateEngine templateEngine() {
                    SpringTemplateEngine engine = new SpringTemplateEngine();
                    engine.setEnableSpringELCompiler(true);
                    engine.setTemplateResolver(templateResolver());
                    engine.addDialect(weixinDialect);
                    return engine;
                }

                private ITemplateResolver templateResolver() {
                    SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
                    resolver.setCacheable(false);
                    resolver.setApplicationContext(webApplicationContext);
                    resolver.setCharacterEncoding("UTF-8");
                    resolver.setPrefix("/");
                    resolver.setTemplateMode(TemplateMode.HTML);
                    return resolver;
                }
            }

        }
    }
}
