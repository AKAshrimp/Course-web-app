package com.example.coursewebsite.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import com.example.coursewebsite.model.Course;

@DataJpaTest
class CourseRepositoryTest {

    @Autowired
    private CourseRepository courseRepository;

    @Test
    void findsPopularCoursesByReviewCountDescending() {
        Course moreSubscribers = course(1L, "More subscribers", 100_000, 10);
        Course moreReviews = course(2L, "More reviews", 1_000, 900);
        courseRepository.save(moreSubscribers);
        courseRepository.save(moreReviews);

        assertThat(courseRepository.findAllByOrderByReviewCountDescSubscriberCountDescIdAsc(PageRequest.of(0, 2))
                .getContent())
                .extracting(Course::getId)
                .containsExactly(2L, 1L);
    }

    private Course course(Long id, String title, int subscriberCount, int reviewCount) {
        Course course = new Course();
        course.setId(id);
        course.setTitle(title);
        course.setPaid(true);
        course.setPrice(10);
        course.setSubscriberCount(subscriberCount);
        course.setReviewCount(reviewCount);
        course.setLectureCount(20);
        course.setLevel("All Levels");
        course.setContentDuration(3);
        course.setPublishedAt(LocalDateTime.of(2026, 1, 1, 0, 0));
        course.setSubject("Web Development");
        return course;
    }
}
