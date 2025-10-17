package io.goorm.team02.order.entity;

import io.goorm.team02.order.controller.dto.OrderRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "order_item_options")
@Data
public class OrderItemOption{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "order_item_id", nullable = false)
	@JsonIgnore
	private OrderItem orderItem;

	@Column(nullable = false, length = 50)
	private String optionName;

	@Column(nullable = false, length = 50)
	private String optionItemName;

	@Column(precision = 10, scale = 2)
	private BigDecimal additionalPrice = BigDecimal.ZERO;
	
	/**
	 * null 값 안전 처리를 위한 setter
	 */
	public void setAdditionalPrice(BigDecimal additionalPrice) {
		this.additionalPrice = additionalPrice != null ? additionalPrice : BigDecimal.ZERO;
	}

	/**
	 * OrderRequest에서 OrderItemOption 리스트 생성 (도메인 로직)
	 */
	public static List<OrderItemOption> create(OrderItem orderItem, 
			List<OrderRequest.OrderItemOptionRequest> optionRequests) {
		
		if (optionRequests == null || optionRequests.isEmpty()) {
			return new ArrayList<>();
		}
		
		List<OrderItemOption> options = new ArrayList<>();
		
		for (OrderRequest.OrderItemOptionRequest optionRequest : optionRequests) {
			if (optionRequest.optionName() != null && optionRequest.optionItemName() != null) {
				OrderItemOption option = new OrderItemOption();
				option.setOrderItem(orderItem);
				option.setOptionName(optionRequest.optionName());
				option.setOptionItemName(optionRequest.optionItemName());
				option.setAdditionalPrice(optionRequest.additionalPrice());
				options.add(option);
			}
		}
		
		return options;
	}

}
