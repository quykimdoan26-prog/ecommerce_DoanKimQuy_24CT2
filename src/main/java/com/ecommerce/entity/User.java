package com.ecommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username không được trống")
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank(message = "Password không được trống")
    @Size(min = 8, message = "Mật khẩu phải có ít nhất 8 ký tự")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "Email không được trống")
    @Email(message = "Email không đúng định dạng")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Full name không được trống")
    private String fullName;

    private String phone;
    private String address;

    @Column(nullable = false)
    private String role = "CUSTOMER"; // CUSTOMER, STAFF, ADMIN

    @Column(columnDefinition = "BOOLEAN DEFAULT 1")
    private Boolean status = true;

    private Long createdAt = System.currentTimeMillis();
    private Long updatedAt = System.currentTimeMillis();
}
