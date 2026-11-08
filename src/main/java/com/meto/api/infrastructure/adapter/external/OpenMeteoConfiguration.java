package com.meto.api.infrastructure.adapter.external;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Clock;

@Configuration
@EnableConfigurationProperties(OpenMeteoProperties.class)
public class OpenMeteoConfiguration {

    @Bean
    public OpenMeteoAdapter openMeteoAdapter(OpenMeteoProperties properties) {
        HttpClient http = HttpClient.newBuilder().connectTimeout(properties.timeout()).build();
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(http);
        factory.setReadTimeout(properties.timeout());
        return new OpenMeteoAdapter(properties, RestClient.builder().requestFactory(factory), Clock.systemUTC());
    }
}
