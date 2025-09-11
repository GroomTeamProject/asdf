package io.goorm.team02.core.users.controller;

import io.goorm.team02.core.users.controller.dto.ProfileResponse;
import io.goorm.team02.core.users.controller.dto.ProfileUpdateRequest;
import io.goorm.team02.core.users.controller.dto.ProfilePasswordEdit;
import io.goorm.team02.core.users.domain.User;
import io.goorm.team02.core.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    // ✅ 내 정보 조회
    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(ProfileResponse.fromEntity(user));
    }

    // ✅ 내 정보 수정
    @PutMapping("/me")
    public ResponseEntity<ProfileResponse> updateMyProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ProfileUpdateRequest request) {

        User updatedUser = userService.updateUserProfile(userDetails.getUsername(), request);
        return ResponseEntity.ok(ProfileResponse.fromEntity(updatedUser));
    }

    // 비밀번호 변경
    @PatchMapping("/me/password") // put이 아니라, patch??
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ProfilePasswordEdit request
    ) {
        userService.changePassword(userDetails.getUsername(), request.getCurrentPassword(), request.getNewPassword());
        return ResponseEntity.ok("Password changed successfully");
    }

}