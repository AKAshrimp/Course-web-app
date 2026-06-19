package com.example.coursewebsite.dto.admin;

import java.util.List;

import com.example.coursewebsite.dto.api.LectureDto;
import com.example.coursewebsite.dto.api.PollDto;

public record AdminDashboardResponse(
        long userCount,
        long lectureCount,
        long pollCount,
        List<LectureDto> lectures,
        List<PollDto> polls
) {
}
