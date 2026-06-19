package com.example.coursewebsite.dto.api;

import java.time.LocalDateTime;

public record UserVoteDto(
        Long id,
        Long pollId,
        String pollQuestion,
        Long optionId,
        String optionText,
        LocalDateTime votedAt
) {
}
