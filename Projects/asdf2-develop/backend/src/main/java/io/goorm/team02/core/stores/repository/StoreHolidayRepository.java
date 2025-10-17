package io.goorm.team02.core.stores.repository;

import io.goorm.team02.core.stores.domain.StoreHoliday;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface StoreHolidayRepository extends JpaRepository<StoreHoliday, Long> {

    // 중복 체크
    boolean existsByStoreIdAndDate(Long storeId, LocalDate date);

    // 특정 가게의 휴무일 목록
    List<StoreHoliday> findByStoreIdOrderByDateAsc(Long storeId);

    // 특정 날짜 이후의 휴무일들
    List<StoreHoliday> findByStoreIdAndDateGreaterThanEqualOrderByDateAsc(
        Long storeId, LocalDate fromDate);

    // 매년 반복 휴무일들
    List<StoreHoliday> findByStoreIdAndIsRecurringTrue(Long storeId);

    // 특정 가게의 특정 휴무일 조회
    Optional<StoreHoliday> findByIdAndStoreId(Long id, Long storeId);

    // 특정 가게의 특정 휴무일 삭제
    @Modifying
    @Transactional
    @Query("DELETE FROM StoreHoliday sh WHERE sh.id = :id AND sh.store.id = :storeId")
    int deleteByIdAndStoreId(@Param("id") Long id, @Param("storeId") Long storeId);
}