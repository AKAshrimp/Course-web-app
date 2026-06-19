package com.example.coursewebsite.dto.api;

import java.time.LocalDateTime;

public record MaterialDto(
        Long id,
        String title,
        String fileName,
        String fileType,
        LocalDateTime uploadTime
) {
}
