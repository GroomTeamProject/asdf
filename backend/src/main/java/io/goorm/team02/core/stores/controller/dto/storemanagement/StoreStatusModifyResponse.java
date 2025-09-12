package io.goorm.team02.core.stores.controller.dto.storemanagement;

import io.goorm.team02.core.stores.domain.Store;
import io.goorm.team02.core.stores.domain.enums.StoreStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StoreStatusModifyResponse {

    private Long storeId;
    private String storeName;
    private StoreStatus status;
    private String message;

    public static StoreStatusModifyResponse of(Store store, String message) {
        return new StoreStatusModifyResponse(
            store.getId(),
            store.getName(),
            store.getStatus(),
            message
        );
    }
}