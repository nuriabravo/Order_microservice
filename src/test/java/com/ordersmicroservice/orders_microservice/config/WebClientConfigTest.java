package com.ordersmicroservice.orders_microservice.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class WebClientConfigTest {
    @Test
    @DisplayName("Setup for integration tests")
    void testWebClient() {

        WebClient.Builder builder = Mockito.mock(WebClient.Builder.class);
        Mockito.when(builder.baseUrl(Mockito.anyString())).thenReturn(builder);
        Mockito.when(builder.build()).thenReturn(Mockito.mock(WebClient.class));

        WebClientConfig config = new WebClientConfig();
        WebClient client = config.webClient(builder);

        assertNotNull(client);
        Mockito.verify(builder).baseUrl("http://localhost:8080");
        Mockito.verify(builder).build();
    }
}
