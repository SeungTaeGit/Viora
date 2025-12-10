package com.viora.config; // 경로는 본인 프로젝트에 맞게 수정

import com.recombee.api_client.RecombeeClient;
import com.recombee.api_client.exceptions.ApiException;
import com.recombee.api_client.util.Region;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RecombeeConfig {

    @Value("${recombee.database-id}")
    private String databaseId;

    @Value("${recombee.private-token}")
    private String privateToken;

    @Bean
    public RecombeeClient recombeeClient() {
        RecombeeClient client = new RecombeeClient(databaseId, privateToken)
                .setRegion(Region.AP_SE);

        return client;
    }
}