package io.goorm.team02.users.controller;

import io.goorm.team02.users.controller.dto.UserResponse;
import io.goorm.team02.users.controller.dto.UserAddressResponse;
import io.goorm.team02.users.controller.dto.UserUpdateRequest;
import io.goorm.team02.users.security.SecurityUtils;
import io.goorm.team02.users.controller.dto.ProfilePasswordEdit;
import io.goorm.team02.users.controller.dto.UserAddressRequest;
import io.goorm.team02.users.domain.User;
import io.goorm.team02.users.domain.UserAddress;
import io.goorm.team02.users.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController implements UserControllerDocs {

    private final UserService userService;

    /**
     * 사용자 정보 조회
     */
    // TODO: filter 이용해서 현재 사용자 정보 조회하도록 수정
    @GetMapping("/{userId}")
    public UserResponse getUser(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        System.out.println("user: " + user);
        return UserResponse.from(user);
    }

    /**
     * 사용자 주소 목록 조회
     */
    @GetMapping("/{userId}/addresses")
    public List<UserAddressResponse> getUserAddresses(@PathVariable Long userId) {
        List<UserAddress> addresses = userService.getUserAddresses(userId);
        return addresses.stream()
                .map(UserAddressResponse::from)
                .toList();
    }

    /**
     * 사용자 정보 업데이트
     */
    @PutMapping("/{userId}")
    public UserResponse updateUser(@PathVariable Long userId, @RequestBody UserUpdateRequest request) {
        User updatedUser = userService.updateUser(userId, request);
        return UserResponse.from(updatedUser);
    }

    /**
     * 사용자 주소 생성
     */
    @PostMapping("/{userId}/addresses")
    public UserAddressResponse createUserAddress(@PathVariable Long userId, @RequestBody UserAddressRequest request) {
        UserAddress address = userService.createUserAddress(userId, request);
        return UserAddressResponse.from(address);
    }

    /**
     * 사용자 주소 수정
     */
    @PutMapping("/{userId}/addresses/{addressId}")
    public UserAddressResponse updateUserAddress(@PathVariable Long userId, @PathVariable Long addressId, @RequestBody UserAddressRequest request) {
        UserAddress address = userService.updateUserAddress(userId, addressId, request);
        return UserAddressResponse.from(address);
    }

    /**
     * 사용자 주소 삭제
     */
    @DeleteMapping("/{userId}/addresses/{addressId}")
    public void deleteUserAddress(@PathVariable Long userId, @PathVariable Long addressId) {
        userService.deleteUserAddress(userId, addressId);
    }

    /**
     * 기본 배송지 설정
     */
    @PutMapping("/{userId}/addresses/{addressId}/default")
    public UserAddressResponse setDefaultAddress(@PathVariable Long userId, @PathVariable Long addressId) {
        UserAddress address = userService.setDefaultAddress(userId, addressId);
        return UserAddressResponse.from(address);
    }

    // 비밀번호 변경
    @PatchMapping("/me/password") // put이 아니라, patch??
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ProfilePasswordEdit request,
            HttpServletResponse response
    ) {
        userService.changePassword(userDetails.getUsername(), request.getCurrentPassword(), request.getNewPassword());
        // 3) 쿠키 삭제
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/api/auth/refresh")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        response.addHeader("Set-Cookie", deleteCookie.toString());

        return ResponseEntity.ok("Password changed successfully");
    }

    // 계정 삭제
    @DeleteMapping("/me/deactivate")
    public ResponseEntity<Void> deactivateUser() {
        Long userId = SecurityUtils.getCurrentUserId(); // 로그인한 사용자 ID 가져오기
        userService.deactivateUser(userId);
        return ResponseEntity.noContent().build();
    }
}