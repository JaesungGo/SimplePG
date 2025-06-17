package me.jaesung.simplepg.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.*;

@EnableWebMvc
@Configuration
@PropertySource({"classpath:/application.properties"})
@PropertySource(value = "classpath:application-${spring.profiles.active}.properties",
        ignoreResourceNotFound = true)
@ComponentScan(basePackages = "me.jaesung.simplepg.controller")
public class ServletConfig implements WebMvcConfigurer {

    @Value("${server.URI}")
    String base_uri;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("/resources/");
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("/resources/assets/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*");
    }

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }
}