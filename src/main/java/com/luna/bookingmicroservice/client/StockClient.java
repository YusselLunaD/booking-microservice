package com.luna.bookingmicroservice.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "stock-microservice")
public interface StockClient {

    @RequestMapping("/api/stock/{code}")
    boolean stockAvailable(@PathVariable String code);
}
