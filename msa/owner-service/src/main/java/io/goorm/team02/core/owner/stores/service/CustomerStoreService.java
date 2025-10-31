package io.goorm.team02.core.owner.stores.service;

import io.goorm.team02.core.owner.stores.domain.Store;
import io.goorm.team02.core.owner.stores.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * 고객용 가게 서비스
 * 주문 도메인 구현을 위해 임시로 작성
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CustomerStoreService {

    private final StoreRepository storeRepository;

    /**
     * 모든 가게 목록 조회 (고객용)
     */
    public List<Store> getAllStores() {
        log.info("모든 가게 목록 조회");
        return storeRepository.findByIsActiveTrue();
    }

    /**
     * 가게 ID로 가게 조회 (고객용)
     */
    public Store getStoreById(Long storeId) {
        log.info("가게 ID로 조회: {}", storeId);
        return storeRepository.findByIdAndIsActiveTrue(storeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "가게를 찾을 수 없음"));
    }
}
