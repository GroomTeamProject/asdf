package io.goorm.team02.core.owner.stores.controller;

import io.goorm.team02.dto.owner.stores.storemanagement.*;
import io.goorm.team02.core.owner.stores.domain.Store;
import io.goorm.team02.core.owner.stores.mapper.StoreMapper; // 추가
import io.goorm.team02.core.owner.stores.service.CustomerStoreService;
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
    private final StoreMapper storeMapper; // 추가

    @GetMapping
    public ResponseEntity<List<StoreResponse>> getAllStores() {
        List<Store> stores = customerStoreService.getAllStores();
        // Mapper를 사용해서 변환
        List<StoreResponse> response = storeMapper.toStoreResponseList(stores);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<StoreResponse> getStore(@PathVariable Long storeId) {
        Store store = customerStoreService.getStoreById(storeId);
        // Mapper를 사용해서 변환
        StoreResponse response = storeMapper.toStoreResponse(store);
        return ResponseEntity.ok(response);
    }
}