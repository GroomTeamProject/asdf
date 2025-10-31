package io.goorm.team02.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "io.goorm.team02.payment")
@EnableJpaRepositories(basePackages = "io.goorm.team02.payment.repository")
@EntityScan(basePackages = "io.goorm.team02.payment.entity")
@EnableFeignClients(basePackages = "io.goorm.team02.payment.client")
public class PaymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }
}
