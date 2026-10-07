package com.ecommerce.service;

import com.ecommerce.entity.User;
import com.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(User user) {
        if(userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email đã được đăng ký");
        }
        if(userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Tên đăng nhập đã được đăng ký");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("CUSTOMER");
        user.setCreatedAt(System.currentTimeMillis());
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(User user) {
        user.setUpdatedAt(System.currentTimeMillis());
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User updateStatus(Long id, Boolean status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản"));
        user.setStatus(status);
        user.setUpdatedAt(System.currentTimeMillis());
        return userRepository.save(user);
    }

    public User updateRole(Long id, String role) {
        if (!List.of("CUSTOMER", "STAFF", "ADMIN").contains(role)) {
            throw new IllegalArgumentException("Vai trò không hợp lệ");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản"));
        user.setRole(role);
        user.setUpdatedAt(System.currentTimeMillis());
        return userRepository.save(user);
    }

    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
