package com.ecommerce.service;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderItem;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.OrderItemRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.CartItemRepository;
import com.ecommerce.entity.CartItem;
import com.ecommerce.entity.User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    public Order createOrder(Order order) {
        order.setCreatedAt(System.currentTimeMillis());
        order.setStatus("PENDING");
        return orderRepository.save(order);
    }

    public void addOrderItem(OrderItem item) {
        item.setCreatedAt(System.currentTimeMillis());
        orderItemRepository.save(item);
    }

    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public List<OrderItem> getOrderItems(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    public Order updateOrder(Order order) {
        order.setUpdatedAt(System.currentTimeMillis());
        return orderRepository.save(order);
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    @Transactional
    public Order createFromCart(User user, String shippingAddress, String notes,
                                String paymentMethod) {
        if (!List.of("COD", "QR").contains(paymentMethod)) {
            throw new IllegalArgumentException("Phương thức thanh toán không hợp lệ");
        }
        List<CartItem> cartItems = cartItemRepository.findByUserId(user.getId());
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Giỏ hàng đang trống");
        }
        
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shippingAddress);
        order.setNotes(notes);
        order.setPaymentMethod(paymentMethod);
        order.setPaymentStatus("QR".equals(paymentMethod) ? "PENDING" : "UNPAID");
        order.setTotalAmount(cartItems.stream()
                .mapToDouble(item -> item.getUnitPrice() * item.getQuantity())
                .sum());
        Order savedOrder = createOrder(order);

        for (CartItem cartItem : cartItems) {
            if (cartItem.getQuantity() > cartItem.getProduct().getQuantity()) {
                throw new IllegalStateException("Sản phẩm không đủ tồn kho: "
                        + cartItem.getProduct().getName());
            }
            OrderItem item = new OrderItem();
            item.setOrder(savedOrder);
            item.setProduct(cartItem.getProduct());
            item.setQuantity(cartItem.getQuantity());
            item.setUnitPrice(cartItem.getUnitPrice());
            item.setTotalPrice(cartItem.getUnitPrice() * cartItem.getQuantity());
            addOrderItem(item);
            cartItem.getProduct().setQuantity(cartItem.getProduct().getQuantity() - cartItem.getQuantity());
            productRepository.save(cartItem.getProduct());
        }
        cartItemRepository.deleteByUserId(user.getId());
        return savedOrder;
    }
    public List<Order> findByStatusAndCreatedAtBetween(String status, long startTime, long endTime) {
        return orderRepository.findByStatusAndCreatedAtBetween(status, startTime, endTime);
    }
    // viết hàm tính tổng doanh thu từ các đơn hàng đã hoàn thành (DELIVERED) trong khoảng thời gian nhất định
    public double calculateRevenue(long startTime, long endTime) {
            return orderRepository.findByStatusAndCreatedAtBetween("DELIVERED", startTime, endTime).stream()
                    .mapToDouble(Order::getTotalAmount)
                .sum();
    }
}
