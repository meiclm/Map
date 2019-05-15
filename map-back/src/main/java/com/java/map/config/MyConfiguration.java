package com.java.map.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyConfiguration implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        映射文件路径，将本地的路径F:/myResource/映射成localhost:8085/upload/**
        registry.addResourceHandler("/imgResource/**").addResourceLocations("file:F:/myMap/");
    }
}
