package com.example.coursewebsite.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.coursewebsite.dto.admin.AdminUserPageResponse;
import com.example.coursewebsite.dto.admin.AdminUserRequest;
import com.example.coursewebsite.dto.admin.AdminUserResponse;
import com.example.coursewebsite.model.User;
import com.example.coursewebsite.repository.UserRepository;

@Service
public class AdminAccountService {

    private final UserService userService;
    private final UserRepository userRepository;

    public AdminAccountService(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public List<AdminUserResponse> getAllUsers() {
        return userService.getAllUsers().stream()
                .map(this::toResponse)
                .toList();
    }

    public AdminUserPageResponse getUsersPage(int page, int size, String search, String role) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), 100);
        String safeSearch = search == null ? "" : search.trim();
        String safeRole = normalizeRole(role);
        PageRequest pageRequest = PageRequest.of(
                safePage - 1,
                safeSize,
                Sort.by(Sort.Direction.ASC, "id")
        );
        Page<User> usersPage = userRepository.searchAdminUsers(safeSearch, safeRole, pageRequest);

        return new AdminUserPageResponse(
                usersPage.getContent().stream().map(this::toResponse).toList(),
                usersPage.getTotalElements(),
                safePage,
                safeSize,
                usersPage.getTotalPages(),
                userRepository.countByRole("ROLE_TEACHER"),
                userRepository.countByRole("ROLE_STUDENT")
        );
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

    private String normalizeRole(String role) {
        if (role == null || role.isBlank() || "ALL".equals(role)) {
            return "ALL";
        }
        if ("ROLE_STUDENT".equals(role) || "ROLE_TEACHER".equals(role)) {
            return role;
        }
        return "ALL";
    }
}
