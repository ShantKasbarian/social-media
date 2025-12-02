package com.social_media.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${app.base.url}")
    private String baseUrl;

    @Value("${app.login.frontend.url}")
    private String loginFrontendUrl;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController(baseUrl, loginFrontendUrl);
    }
}
