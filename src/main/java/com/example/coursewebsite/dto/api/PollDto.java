package com.example.coursewebsite.dto.api;

import java.time.LocalDateTime;
import java.util.List;

public record PollDto(Long id, String question, LocalDateTime createdAt, List<PollOptionDto> options) {
}
