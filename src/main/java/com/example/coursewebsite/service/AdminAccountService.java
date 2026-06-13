package com.example.coursewebsite.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.example.coursewebsite.dto.admin.AdminUserRequest;
import com.example.coursewebsite.dto.admin.AdminUserResponse;
import com.example.coursewebsite.model.User;

@Service
public class AdminAccountService {

    private final UserService userService;

    public AdminAccountService(UserService userService) {
        this.userService = userService;
    }

    public List<AdminUserResponse> getAllUsers() {
        return userService.getAllUsers().stream()
                .map(this::toResponse)
                .toList();
    }

    public AdminUserResponse getUser(Long id) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return toResponse(user);
    }

    public AdminUserResponse createUser(AdminUserRequest request) {
        User user = new User();
        user.setUsername(request.username());
        user.setPassword(request.password());
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPhoneNumber(request.phoneNumber());

        if (request.roles() != null && !request.roles().isEmpty()) {
            user.setRoles(request.roles());
        } else {
            user.setRoles(Set.of("ROLE_STUDENT"));
        }

        return toResponse(userService.registerNewUser(user));
    }

    public AdminUserResponse updateUser(Long id, AdminUserRequest request) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPhoneNumber(request.phoneNumber());

        if (request.roles() != null && !request.roles().isEmpty()) {
            user.setRoles(request.roles());
        }

        return toResponse(userService.updateUser(user));
    }

    public void updatePassword(Long id, String newPassword) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        userService.updateUserPassword(user, newPassword);
    }

    public void deleteUser(Long id) {
        userService.deleteUser(id);
    }

    private AdminUserResponse toResponse(User user) {
        return new AdminUserResponse(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRoles()
        );
    }
}
