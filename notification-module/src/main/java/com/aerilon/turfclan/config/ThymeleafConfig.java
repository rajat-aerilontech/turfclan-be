package com.aerilon.turfclan.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.nio.charset.StandardCharsets;

@Configuration
public class ThymeleafConfig {

    @Value("${spring.thymeleaf.cache:30}")
    private int thymeleafCache;

    @Bean
    public ITemplateResolver emailTemplateResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/emails/"); // path to your compiled html email templates
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        templateResolver.setCacheable(false); // enable caching for production
        templateResolver.setOrder(1); // set a higher order if you have other template resolvers
        return templateResolver;
    }

    @Bean
    public ITemplateResolver textTemplateResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/textTemplates/");
        templateResolver.setSuffix(".txt");
        templateResolver.setTemplateMode(TemplateMode.TEXT);
        templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        templateResolver.setCacheable(false);
        templateResolver.setOrder(2); // set a lower order than the primary email resolver
        return templateResolver;
    }

    @Bean(name = "emailTemplateEngine")
    public SpringTemplateEngine emailTemplateEngine(
            ITemplateResolver emailTemplateResolver,
            ITemplateResolver textTemplateResolver,
            MessageSource emailMessageSource
    ) {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(emailTemplateResolver);
        templateEngine.addTemplateResolver(textTemplateResolver);
        templateEngine.setTemplateEngineMessageSource(emailMessageSource);
        return templateEngine;
    }

    @Bean("emailMessageSource")
    public ResourceBundleMessageSource emailMessageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.addBasenames("templates/emailProperties/joinWaitlist", "templates/emailProperties/joinWaitlistAdmin");
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        messageSource.setCacheSeconds(thymeleafCache);
        return messageSource;
    }
}
