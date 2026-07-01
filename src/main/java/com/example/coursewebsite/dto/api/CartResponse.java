package com.example.coursewebsite.dto.api;

import java.util.List;

public record CartResponse(
        List<CourseSummaryDto> items,
        double total,
        int count
) {
}
