package io.goorm.team02.dto.orders;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

import java.util.List;

@Schema(description = "주문 생성 요청")
public record OrderRequest(
		@Schema(description = "사용자 ID", example = "1") Long userId,

		@Schema(description = "가게 ID", example = "1") Long storeId,

		@Schema(description = "배달 주소", example = "서울시 강남구 역삼동 123-45") String deliveryAddress,

		@Schema(description = "상세 주소", example = "101호") String deliveryDetailAddress,

		@Schema(description = "연락처", example = "010-1234-5678") String phone,

		@Schema(description = "주문 메모", example = "문 앞에 놓아주세요") String orderMemo,

		@Schema(description = "주문 아이템 목록") List<OrderItemRequest> orderItems) {

	@Schema(description = "주문 아이템 요청")
	public record OrderItemRequest(
			@Schema(description = "메뉴 ID", example = "1") Long menuId,

			@Schema(description = "수량", example = "2", minimum = "1", maximum = "100")
			@Min(value = 1, message = "수량은 1 이상이어야 합니다")
			@Max(value = 100, message = "수량은 100 이하여야 합니다")
			int quantity,

			@Schema(description = "주문 아이템 옵션 목록") List<OrderItemOptionRequest> options) {
	}

	@Schema(description = "주문 아이템 옵션 요청")
	public record OrderItemOptionRequest(
			@Schema(description = "옵션 그룹 ID", example = "1") Long optionId,
			@Schema(description = "옵션 아이템 ID", example = "3") Long optionItemId) {
	}

}
