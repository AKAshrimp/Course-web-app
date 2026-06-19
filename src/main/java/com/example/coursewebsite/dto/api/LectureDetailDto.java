package com.example.coursewebsite.dto.api;

import java.time.LocalDateTime;
import java.util.List;

public record LectureDetailDto(
        Long id,
        String title,
        String description,
        LocalDateTime createdAt,
        List<MaterialDto> materials,
        List<CommentDto> comments
) {
}
