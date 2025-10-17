package io.goorm.team02.demo;

// import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import io.goorm.team02.demo.client.OrderServiceClient;

// @SpringBootTest
@ActiveProfiles("test")
public abstract class TestEnv {

    @MockitoBean
    OrderServiceClient orderServiceClient;

}
