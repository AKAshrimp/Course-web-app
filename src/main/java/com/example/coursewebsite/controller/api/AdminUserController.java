package com.example.coursewebsite.controller.api;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.coursewebsite.dto.admin.AdminPasswordUpdateRequest;
import com.example.coursewebsite.dto.admin.AdminUserPageResponse;
import com.example.coursewebsite.dto.admin.AdminUserRequest;
import com.example.coursewebsite.dto.admin.AdminUserResponse;
import com.example.coursewebsite.service.AdminAccountService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('TEACHER')")
public class AdminUserController {

    private final AdminAccountService adminAccountService;

    public AdminUserController(AdminAccountService adminAccountService) {
        this.adminAccountService = adminAccountService;
    }

    @GetMapping
    public AdminUserPageResponse getUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "ALL") String role) {
        return adminAccountService.getUsersPage(page, size, search, role);
    }

    @GetMapping("/{id}")
    public AdminUserResponse getUser(@PathVariable Long id) {
        return adminAccountService.getUser(id);
    }

    @PostMapping
    public AdminUserResponse createUser(@Valid @RequestBody AdminUserRequest request) {
        return adminAccountService.createUser(request);
    }

    @PutMapping("/{id}")
    public AdminUserResponse updateUser(@PathVariable Long id, @Valid @RequestBody AdminUserRequest request) {
        return adminAccountService.updateUser(id, request);
    }

    @PutMapping("/{id}/password")
    public void updatePassword(@PathVariable Long id, @Valid @RequestBody AdminPasswordUpdateRequest request) {
        adminAccountService.updatePassword(id, request.newPassword());
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        adminAccountService.deleteUser(id);
    }
}
