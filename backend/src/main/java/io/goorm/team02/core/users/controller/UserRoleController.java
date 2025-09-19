// UserRoleController.java
//POST http://localhost:8080/api/user-roles/1?role=OWNER
package io.goorm.team02.core.users.controller;

import io.goorm.team02.core.users.domain.UserRole;
import io.goorm.team02.core.users.controller.dto.UserAddressRequest;
import io.goorm.team02.core.users.controller.dto.UserRoleAddRequest;
import io.goorm.team02.core.users.controller.dto.UserRoleAddResponse;
import io.goorm.team02.core.users.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class UserRoleController {

    private final UserRoleService userRoleService;

    @PostMapping("/{userId}")
    public ResponseEntity<UserRoleAddResponse> addRole(@PathVariable Long userId,
                                            @RequestBody UserRoleAddRequest request) {
        //UserRole role = userRoleService.addRoleToUser(userId, request.getRole());
        //return ResponseEntity.ok(role);

        UserRoleAddResponse response = userRoleService.addRoleToUser(userId,request.getRole());
        return ResponseEntity.ok(response);
    }
}
