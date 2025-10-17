package io.goorm.team02.core.orders.controller.dto;

import io.goorm.team02.core.orders.domain.Order;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
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

	public Order toEntity() {
		Order order = new Order();
		order.setDeliveryAddress(this.deliveryAddress);
		order.setDeliveryDetailAddress(this.deliveryDetailAddress);
		order.setPhone(this.phone);
		order.setOrderMemo(this.orderMemo);

		return order;
	}

	@Schema(description = "주문 아이템 요청")
	public record OrderItemRequest(
			@Schema(description = "메뉴 ID", example = "1") Long menuId,

			@Schema(description = "수량", example = "2") Integer quantity,

			@Schema(description = "주문 아이템 옵션 목록") List<OrderItemOptionRequest> options) {
	}

	@Schema(description = "주문 아이템 옵션 요청")
	public record OrderItemOptionRequest(
			@Schema(description = "옵션명", example = "사이즈") String optionName,

			@Schema(description = "옵션 아이템명", example = "대") String optionItemName,

			@Schema(description = "추가 가격", example = "2000.00") BigDecimal additionalPrice) {
	}

}
