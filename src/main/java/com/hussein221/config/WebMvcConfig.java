package com.hussein221.config;

import com.hussein221.interceptor.Authorizationinterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private final Authorizationinterceptor authorizationinterceptor;
    public WebMvcConfig(Authorizationinterceptor authorizationinterceptor) {
        this.authorizationinterceptor = authorizationinterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authorizationinterceptor)
                .addPathPatterns("/api/user");
    }
}
