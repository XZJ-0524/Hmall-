package com.hmall.api.client;

import com.hmall.api.config.DefaultFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "cart-service",configuration = DefaultFeignConfig.class)
public interface CartClient {
    @DeleteMapping("/carts")
    public void removeByItemIds(@RequestParam("ids") List<Long> ids);
}
