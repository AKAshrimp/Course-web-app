package com.example.coursewebsite.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class SecurityController implements ErrorController {
    
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName().equals("anonymousUser")) {
            Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
            if (statusCode != null && statusCode == 403) {
                return "loginRequired";
            }
        }
        return "error";
    }
    
    @GetMapping("/access-denied")
    public String accessDenied() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName().equals("anonymousUser")) {
            return "loginRequired";
        }
        return "accessDenied";
    }
} 