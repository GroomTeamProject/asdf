package io.goorm.team02.core.delivery.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private String status;
    private T data;

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>("success", data);
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>("created", data);
    }

    public static <T> ApiResponse<T> fail(T message) {
        return new ApiResponse<>("fail", message);
    }
}
