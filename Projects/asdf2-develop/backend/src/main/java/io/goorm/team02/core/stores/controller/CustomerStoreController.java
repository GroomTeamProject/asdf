package io.goorm.team02.core.stores.controller;

import io.goorm.team02.core.stores.controller.dto.storemanagement.StoreResponse;
import io.goorm.team02.core.stores.domain.Store;
import io.goorm.team02.core.stores.service.CustomerStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 고객용 가게 조회 API
 * 주문 도메인 구현을 위해 임시로 작성
 */
@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class CustomerStoreController implements CustomerStoreControllerDocs {

    private final CustomerStoreService customerStoreService;

    @GetMapping
    public ResponseEntity<List<StoreResponse>> getAllStores() {
        List<Store> stores = customerStoreService.getAllStores();
        List<StoreResponse> response = stores.stream()
                .map(StoreResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<StoreResponse> getStore(@PathVariable Long storeId) {
        Store store = customerStoreService.getStoreById(storeId);
        return ResponseEntity.ok(StoreResponse.from(store));
    }

}
