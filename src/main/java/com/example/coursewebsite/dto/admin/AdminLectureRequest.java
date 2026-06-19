package com.example.coursewebsite.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminLectureRequest(
        @NotBlank(message = "Lecture title is required")
        @Size(max = 150, message = "Lecture title must not exceed 150 characters")
        String title,

        @NotBlank(message = "Lecture description is required")
        @Size(max = 2000, message = "Lecture description must not exceed 2000 characters")
        String description
) {
}
