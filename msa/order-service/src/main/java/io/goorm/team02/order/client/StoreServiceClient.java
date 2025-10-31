package io.goorm.team02.order.client;

import io.goorm.team02.dto.owner.menus.menucreate.MenuResponse;
import io.goorm.team02.dto.owner.stores.storemanagement.StoreResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "store-service", url = "${msa.gateway.url}")
public interface StoreServiceClient {

    @GetMapping("/api/stores/{storeId}")
    ResponseEntity<StoreResponse> getStoreById(@PathVariable Long storeId);

    @GetMapping("/api/stores/{storeId}/menus")
    ResponseEntity<List<MenuResponse>> getMenusByStoreId(@PathVariable Long storeId);
}
