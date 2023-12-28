package com.luna.bookingmicroservice.repository;

import com.luna.bookingmicroservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepositiry  extends JpaRepository<Order,Long> {
}
