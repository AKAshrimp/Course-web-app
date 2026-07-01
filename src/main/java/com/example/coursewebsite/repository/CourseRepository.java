package com.example.coursewebsite.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.coursewebsite.model.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    Page<Course> findAllByOrderByReviewCountDescSubscriberCountDescIdAsc(Pageable pageable);

    Page<Course> findBySubjectOrderBySubscriberCountDescReviewCountDescIdAsc(String subject, Pageable pageable);
}
