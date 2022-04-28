package com.tce.gateway;

import com.amazonaws.auth.BasicAWSCredentials;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Bean
    public BasicAWSCredentials getBasicAWSCredentials(@Value("${aws.accessKey}") String accessKey, @Value("${aws.secretKey}") String secretKey) {
        return new BasicAWSCredentials(accessKey, secretKey);
    }

    @Bean
    public Gson getGson() {
        return new Gson();
    }

}
