package io.goorm.team02.demo;

import io.goorm.team02.payment.client.PaymentServiceClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import static org.mockito.Mockito.*;

@TestConfiguration
public class PaymentClientMockConfig {

    @Bean
    public PaymentServiceClient paymentServiceClient() {
        PaymentServiceClient mock = mock(PaymentServiceClient.class);
        when(mock.getOrderEvent(anyString()))
                .thenAnswer(invocation -> new PaymentServiceClient.OrderEventResponse(
                        invocation.getArgument(0), "CREATED"
                ));
        return mock;
    }
}
