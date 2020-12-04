package com.aihelpdeskip.monitoringservice.configurations;

import com.aihelpdeskip.monitoringservice.components.ApplicationContextProvider;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfiguration {

    @Bean
    public static ApplicationContextProvider contextProvider() {
        return new ApplicationContextProvider();
    }

}