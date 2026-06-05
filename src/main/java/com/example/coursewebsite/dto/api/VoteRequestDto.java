package com.example.coursewebsite.dto.api;

import jakarta.validation.constraints.NotNull;

public record VoteRequestDto(@NotNull Long optionId) {
}
