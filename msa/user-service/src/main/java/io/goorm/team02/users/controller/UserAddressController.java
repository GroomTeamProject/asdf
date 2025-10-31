/*/ 새로운 주소 등록
package io.goorm.team02.core.users.controller;

import io.goorm.team02.core.users.controller.dto.UserAddressRequest;
import io.goorm.team02.core.users.controller.dto.UserAddressResponse;
import io.goorm.team02.core.users.service.UserAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/me/addresses")
@RequiredArgsConstructor
public class UserAddressController {

    private final UserAddressService userAddressService;

    @PostMapping
    public ResponseEntity<UserAddressResponse> addAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UserAddressRequest request) {

        Long userId = Long.parseLong(userDetails.getUsername()); // JWT에서 id 추출해서 username에 넣었다고 가정
        UserAddressResponse response = userAddressService.addAddress(userId, request);
        return ResponseEntity.ok(response);
    }
}*/

