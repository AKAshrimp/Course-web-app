package com.example.coursewebsite.dto.api;

import java.util.List;

public record CartSyncRequest(
        List<Long> courseIds
) {
}
