package com.ticket.box.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourcesWebConfiguration
        implements WebMvcConfigurer {

    @Value("${ticketbox.upload-file.base-uri}")
    private String baseURI;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // them duong dan vd: api/v1/storage/tenfile
        registry.addResourceHandler("/storage/**")
                // gan duong dan localhost vs duong dan local server(spring auto tim file trong
                // basePath)
                .addResourceLocations(baseURI);
    }
}
