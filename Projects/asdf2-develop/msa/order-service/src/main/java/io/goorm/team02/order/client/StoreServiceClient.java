package io.goorm.team02.order.client;

import io.goorm.team02.order.client.dto.MenuDTO;
import io.goorm.team02.order.client.dto.StoreDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "store-service", url = "${msa.gateway.url:http://localhost:8080}")
public interface StoreServiceClient {

    @GetMapping("/api/stores/{storeId}")
    StoreDTO getStoreById(@PathVariable("storeId") Long storeId);

    @GetMapping("/api/stores/{storeId}/menus")
    List<MenuDTO> getMenusByStoreId(@PathVariable("storeId") Long storeId);
}