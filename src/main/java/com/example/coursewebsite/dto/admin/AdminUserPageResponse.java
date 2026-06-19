package com.example.coursewebsite.dto.admin;

import java.util.List;

public record AdminUserPageResponse(
        List<AdminUserResponse> items,
        long total,
        int page,
        int size,
        int totalPages,
        long teacherCount,
        long studentCount
) {
}
