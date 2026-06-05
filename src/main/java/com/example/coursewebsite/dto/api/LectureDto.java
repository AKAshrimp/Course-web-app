package com.example.coursewebsite.dto.api;

import java.time.LocalDateTime;

public record LectureDto(Long id, String title, String description, LocalDateTime createdAt) {
}
