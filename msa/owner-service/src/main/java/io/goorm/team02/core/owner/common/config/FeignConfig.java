package io.goorm.team02.core.owner.common.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "io.goorm.team02.core.owner.stores.client")
public class FeignConfig {
}