package com.example.coursewebsite.dto.api;

import java.time.LocalDateTime;
import java.util.List;

public record PollDetailDto(
        Long id,
        String question,
        LocalDateTime createdAt,
        List<PollOptionDto> options,
        List<CommentDto> comments,
        Long userVoteOptionId
) {
}
