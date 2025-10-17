package io.goorm.team02.core.stores.controller.dto.ordermanagement;

import java.math.BigDecimal;
import java.util.List;

public record StoreOrderItemResponse(
        String menuName,
        Integer quantity,
        BigDecimal menuPrice,
        BigDecimal totalPrice,
        List<String> optionNames
) {}