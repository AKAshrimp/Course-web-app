package com.example.coursewebsite.dto.api;

import java.util.List;

public record CoursePageResponse(
        List<CourseSummaryDto> items,
        long total,
        int page,
        int size,
        int totalPages
) {
}
