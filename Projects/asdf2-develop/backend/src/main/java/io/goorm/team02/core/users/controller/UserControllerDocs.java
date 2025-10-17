package io.goorm.team02.core.users.controller;

import io.goorm.team02.core.users.controller.dto.UserResponse;
import io.goorm.team02.core.users.controller.dto.UserAddressResponse;
import io.goorm.team02.core.users.controller.dto.UserUpdateRequest;
import io.goorm.team02.core.users.controller.dto.UserAddressRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "사용자 관리", description = "사용자 관련 API")
public interface UserControllerDocs {

    @Operation(summary = "사용자 정보 조회", description = "사용자 ID로 사용자 정보를 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    UserResponse getUser(
            @Parameter(description = "사용자 ID", required = true) @PathVariable Long userId);

    @Operation(summary = "사용자 주소 목록 조회", description = "사용자 ID로 사용자의 주소 목록을 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주소 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    List<UserAddressResponse> getUserAddresses(
            @Parameter(description = "사용자 ID", required = true) @PathVariable Long userId);

    @Operation(summary = "사용자 정보 업데이트", description = "사용자 정보를 업데이트합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 정보 업데이트 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    UserResponse updateUser(
            @Parameter(description = "사용자 ID", required = true) @PathVariable Long userId,
            @RequestBody UserUpdateRequest request);

    @Operation(summary = "사용자 주소 생성", description = "새로운 사용자 주소를 생성합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주소 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    UserAddressResponse createUserAddress(
            @Parameter(description = "사용자 ID", required = true) @PathVariable Long userId,
            @RequestBody UserAddressRequest request);

    @Operation(summary = "사용자 주소 수정", description = "기존 사용자 주소를 수정합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주소 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "사용자 또는 주소를 찾을 수 없음")
    })
    UserAddressResponse updateUserAddress(
            @Parameter(description = "사용자 ID", required = true) @PathVariable Long userId,
            @Parameter(description = "주소 ID", required = true) @PathVariable Long addressId,
            @RequestBody UserAddressRequest request);

    @Operation(summary = "사용자 주소 삭제", description = "사용자 주소를 삭제합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주소 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "사용자 또는 주소를 찾을 수 없음")
    })
    void deleteUserAddress(
            @Parameter(description = "사용자 ID", required = true) @PathVariable Long userId,
            @Parameter(description = "주소 ID", required = true) @PathVariable Long addressId);

    @Operation(summary = "기본 배송지 설정", description = "특정 주소를 기본 배송지로 설정합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "기본 배송지 설정 성공"),
            @ApiResponse(responseCode = "404", description = "사용자 또는 주소를 찾을 수 없음")
    })
    UserAddressResponse setDefaultAddress(
            @Parameter(description = "사용자 ID", required = true) @PathVariable Long userId,
            @Parameter(description = "주소 ID", required = true) @PathVariable Long addressId);
}
