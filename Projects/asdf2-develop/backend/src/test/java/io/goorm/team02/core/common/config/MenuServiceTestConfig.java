package io.goorm.team02.core.common.config;

import io.goorm.team02.core.common.service.S3Service;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class MenuServiceTestConfig {

    @Bean
    @Primary
    public S3Service mockS3Service() {
        return Mockito.mock(S3Service.class);
    }
}
