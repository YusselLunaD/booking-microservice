package com.luna.bookingmicroservice.controller;

import com.luna.bookingmicroservice.repository.OrderRepositiry;
//import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.luna.bookingmicroservice.client.StockClient;
import com.luna.bookingmicroservice.dto.OrderDTO;
import com.luna.bookingmicroservice.entity.Order;

import com.netflix.ribbon.proxy.annotation.Hystrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.CircuitBreaker;
import org.springframework.retry.annotation.Recover;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/booking")
public class BookingController {

    @Autowired
    private OrderRepositiry orderRepositiry;

    @Autowired
    private StockClient stockClient;

    private static final String CIRCUIT_BREAKER_NAME = "myCircuitBreaker";


    @PostMapping("/order")
    //@Hystrix(fallbackMethod = "fallbackToStockService")
   // @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "fallbackToStockService")
    @CircuitBreaker(maxAttempts = 3, openTimeout = 5000, resetTimeout = 10000, label = "fallbackToStockService")
    public String saveOrder(@RequestBody OrderDTO orderDTO) {

        boolean inStock = orderDTO.getOrderItems().stream()
                .allMatch(orderItem -> stockClient.stockAvailable(orderItem.getCode()));

        if(inStock) {
            Order order = new Order();

            order.setOrderNo(UUID.randomUUID().toString());
            order.setOrderItems(orderDTO.getOrderItems());


            orderRepositiry.save(order);

            return "Order Saved";
        }

        return "Order Cannot be Saved";
    }
    @Recover
    private String fallbackToStockService() {
        return "Something went wrong, please try after some time";
    }
}