package com.tsm.shop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.squareup.square.Environment;
import com.squareup.square.SquareClient;

import com.squareup.square.authentication.BearerAuthModel;

@Configuration
@ConfigurationProperties(prefix = "square")
public class SquareConfig {
    @Value("${square.access.token}")
    private String accessToken;
    
    @Value("${square.application.id}")
    private String applicationId;
    
    @Value("${square.location.id}")
    private String locationId;
    
    @Value("${square.environment}")
    private String environment;

    @Bean
    public SquareClient squareClient() {
        return new SquareClient.Builder()
            .environment(environment.equals("production") ? Environment.PRODUCTION : Environment.SANDBOX)
            .bearerAuthCredentials(new BearerAuthModel.Builder(accessToken).build())
            .build();
    }
    
    // Getters and setters
    public String getLocationId() {
        return locationId;
    }

    public String getApplicationId() {
        return applicationId;
    }
}