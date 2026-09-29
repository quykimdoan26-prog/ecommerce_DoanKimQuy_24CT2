package com.ecommerce.controller;

import com.ecommerce.service.ProductService;
import com.ecommerce.service.CategoryService;
import com.ecommerce.service.OrderService;
import com.ecommerce.entity.Product;
import com.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private ProductService productService;
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalProducts", productService.getAllProducts().size());
        model.addAttribute("pendingOrders", orderService.getOrdersByStatus("PENDING").size());
        // đẩy tổng doanh thu theo khoảng thời gian lên server
        model.addAttribute("totalRevenue", orderService.calculateRevenue(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000, System.currentTimeMillis()));
        return "admin/dashboard";
    }

    @GetMapping("/products")
    public String listProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "admin/products";
    }

    @GetMapping("/products/view/{id}")
    public String viewProduct(@PathVariable Long id, Model model) {
        productService.getProductById(id).ifPresent(product -> model.addAttribute("product", product));
        return "admin/product-detail";
    }

    @GetMapping("/products/add")
    public String addProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/product-form";
    }

    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute Product product) {
        productService.addProduct(product);
        return "redirect:/admin/products";
    }

    @GetMapping("/products/edit/{id}")
    public String editProduct(@PathVariable Long id, Model model) {
        productService.getProductById(id).ifPresent(p -> model.addAttribute("product", p));
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/product-form";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/admin/products";
    }

    @GetMapping("/orders")
    public String listOrders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        model.addAttribute("statuses", List.of("PENDING", "CONFIRMED", "SHIPPING", "DELIVERED", "CANCELLED"));
        return "admin/orders";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        if (List.of("PENDING", "CONFIRMED", "SHIPPING", "DELIVERED", "CANCELLED").contains(status)) {
            orderService.getOrderById(id).ifPresent(order -> {
                order.setStatus(status);
                orderService.updateOrder(order);
            });
        }
        return "redirect:/admin/orders";
    }

    @PostMapping("/orders/{id}/payment")
    public String updatePaymentStatus(@PathVariable Long id, @RequestParam String paymentStatus) {
        if (List.of("UNPAID", "PENDING", "PAID").contains(paymentStatus)) {
            orderService.getOrderById(id).ifPresent(order -> {
                order.setPaymentStatus(paymentStatus);
                orderService.updateOrder(order);
            });
        }
        return "redirect:/admin/orders";
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }

    @PostMapping("/users/{id}/status")
    public String updateUserStatus(@PathVariable Long id, @RequestParam Boolean status,
                                   org.springframework.security.core.Authentication auth) {
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STAFF"))) {
            userService.findById(id).filter(user -> "CUSTOMER".equals(user.getRole()))
                    .ifPresent(user -> userService.updateStatus(id, status));
        } else {
            userService.updateStatus(id, status);
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/role")
    public String updateUserRole(@PathVariable Long id, @RequestParam String role) {
        userService.updateRole(id, role);
        return "redirect:/admin/users";
    }
}
