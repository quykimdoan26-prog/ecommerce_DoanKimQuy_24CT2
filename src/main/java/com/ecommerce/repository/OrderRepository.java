package com.ecommerce.repository;

import com.ecommerce.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
    List<Order> findByStatus(String status);

    // Tìm các đơn hàng theo trạng thái và khoảng thời gian
    public List<Order> findByStatusAndCreatedAtBetween(String status, long startTime, long endTime);
}
