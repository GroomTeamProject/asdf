package io.goorm.team02.core.stores.controller.dto.storemanagement;

import io.goorm.team02.core.stores.domain.enums.StoreCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StoreUpdateRequest {

    private String name;
    private String description;
    private StoreCategory category;
    private String imageUrl;
}