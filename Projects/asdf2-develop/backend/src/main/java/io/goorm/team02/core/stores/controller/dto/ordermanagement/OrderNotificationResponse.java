package io.goorm.team02.core.stores.controller.dto.ordermanagement;

import java.util.List;

public record OrderNotificationResponse(
        Integer newOrderCount,
        List<StoreOrderResponse> newOrders
) {}
