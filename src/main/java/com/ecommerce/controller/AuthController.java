package com.ecommerce.controller;

import com.ecommerce.entity.User;
import com.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", bindingResult.getFieldErrors().get(0).getDefaultMessage());
            return "auth/register";
        }
        try {
            userService.registerUser(user);
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("error", "Email hoặc tên đăng nhập đã được đăng ký.");
            return "auth/register";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
        return "redirect:/auth/login?success";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/?logout";
    }
}
