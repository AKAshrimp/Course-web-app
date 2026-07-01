package com.example.coursewebsite.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.coursewebsite.model.Course;
import com.example.coursewebsite.repository.CourseRepository;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public Page<Course> getPopularCourses(Pageable pageable) {
        return courseRepository.findAllByOrderByReviewCountDescSubscriberCountDescIdAsc(pageable);
    }

    public Page<Course> getCoursesBySubject(String subject, Pageable pageable) {
        return courseRepository.findBySubjectOrderBySubscriberCountDescReviewCountDescIdAsc(subject, pageable);
    }
}
