package com.example.coursewebsite.dto.api;

public record CourseSummaryDto(
        Long id,
        String title,
        boolean paid,
        double price,
        int subscriberCount,
        int reviewCount,
        int lectureCount,
        String level,
        double contentDuration,
        String publishedAt,
        String subject
) {
}
