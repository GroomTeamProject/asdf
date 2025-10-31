package io.goorm.team02.dto.owner.stores.ordermanagement;

import java.math.BigDecimal;

public record StoreOrderOptionResponse(
        String optionName,
        String optionItemName,
        BigDecimal additionalPrice
) {}