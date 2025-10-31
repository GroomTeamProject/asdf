package io.goorm.team02.dto.owner.stores.ordermanagement;

import java.util.List;

public record OrderNotificationResponse(
        Integer newOrderCount,
        List<StoreOrderResponse> newOrders
) {}
