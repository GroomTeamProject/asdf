package io.goorm.team02.payment.event;

import org.springframework.stereotype.Component;
import io.goorm.team02.payment.event.PaymentEvent;

@Component
public class PaymentEventPublisher {

    public void publishPaymentCompleted(PaymentEvent event) {
        // 실제 이벤트 브로커 연동 시 구현
        System.out.println("[EVENT] Payment Completed: " + event.getPaymentKey());
    }

    public void publishPaymentFailed(PaymentEvent event) {
        // 실제 이벤트 브로커 연동 시 구현
        System.out.println("[EVENT] Payment Failed: " + event.getPaymentKey());
    }
}
