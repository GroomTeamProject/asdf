package io.goorm.team02.core.users.controller;

import io.goorm.team02.core.users.controller.dto.UserAddressRequest;
import io.goorm.team02.core.users.controller.dto.UserAddressResponse;
import io.goorm.team02.core.users.domain.UserAddress;
import io.goorm.team02.core.users.service.UserAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import io.goorm.team02.core.auth.security.SecurityUtils;

@RestController
@RequestMapping("/api/users/me/addresses")
@RequiredArgsConstructor
public class UserAddressController {

    private final UserAddressService userAddressService;

    @PostMapping
    public ResponseEntity<UserAddressResponse> addAddress(@RequestBody UserAddressRequest request) {
    Long userId = SecurityUtils.getCurrentUserId(); // JWT에서 PK 추출
    UserAddressResponse response = userAddressService.addAddress(userId, request);
    return ResponseEntity.ok(response);
}
}