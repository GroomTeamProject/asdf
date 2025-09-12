package io.goorm.team02.core.stores.controller.dto.ordermanagement;

import java.math.BigDecimal;

public record StoreOrderOptionResponse(
        String optionName,
        String optionItemName,
        BigDecimal additionalPrice
) {}