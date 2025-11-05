package com.cyrils.csnewsletter;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import io.camunda.client.CamundaClient;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestCamundaClientConfig {

    @Bean
    public CamundaClient camundaClient() {
        // provide a Mockito mock so application context can start without a real engine
        return mock(CamundaClient.class);
    }
}
