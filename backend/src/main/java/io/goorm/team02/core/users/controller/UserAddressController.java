package io.goorm.team02.core.users.controller;

import io.goorm.team02.core.users.controller.dto.UserAddressRequest;
import io.goorm.team02.core.users.controller.dto.UserAddressResponse;
import io.goorm.team02.core.users.domain.UserAddress;
import io.goorm.team02.core.users.service.UserAddressService;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import io.goorm.team02.core.auth.security.SecurityUtils;

@RestController
@RequestMapping("/api/users/me/addresses")
@RequiredArgsConstructor
public class UserAddressController {

    private final UserAddressService userAddressService;

    // 새로운 주소 등록
    @PostMapping
    public ResponseEntity<UserAddressResponse> addAddress(@RequestBody UserAddressRequest request) {
    Long userId = SecurityUtils.getCurrentUserId(); // JWT에서 PK 추출
    UserAddressResponse response = userAddressService.addAddress(userId, request);
    return ResponseEntity.ok(response);
    }
 
    // 해당 id의 모든 주소 목록 조회
    @GetMapping
    public ResponseEntity<List<UserAddressResponse>> getAllAddresses() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<UserAddressResponse> addresses = userAddressService.getAllAddresses(userId);
        return ResponseEntity.ok(addresses);
    }

    // 특정 주소 수정
    @PatchMapping("/{addressId}")
    public ResponseEntity<UserAddressResponse> updateAddress(
            @PathVariable Long addressId,
            @RequestBody UserAddressRequest request) {
        
        Long userId = SecurityUtils.getCurrentUserId();
        UserAddressResponse updated = userAddressService.updateAddress(userId, addressId, request);
        return ResponseEntity.ok(updated);
    }
}