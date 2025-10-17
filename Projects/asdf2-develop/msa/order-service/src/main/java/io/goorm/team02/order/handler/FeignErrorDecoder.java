package io.goorm.team02.order.handler;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class FeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        // Feign에서 받은 HTTP 상태 코드를 그대로 전달
        HttpStatus status = HttpStatus.valueOf(response.status());
        String reason = "External service error: " + response.reason();
        
        return new ResponseStatusException(status, reason);
    }
}
