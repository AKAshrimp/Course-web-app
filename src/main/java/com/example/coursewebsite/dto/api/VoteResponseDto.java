package com.example.coursewebsite.dto.api;

public record VoteResponseDto(Long pollId, Long optionId, boolean updated, String message) {
}
