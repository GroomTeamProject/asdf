package io.goorm.team02.core.owner.stores.repository;

import io.goorm.team02.core.owner.stores.domain.StoreHour;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface StoreHourRepository extends JpaRepository<StoreHour, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM StoreHour sh WHERE sh.store.id = :storeId AND sh.dayOfWeek IN :dayOfWeeks")
    void deleteByStoreIdAndDayOfWeekIn(@Param("storeId") Long storeId, @Param("dayOfWeeks") Set<Integer> dayOfWeeks);
}