package com.example.coursewebsite.service;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.coursewebsite.model.CartItem;
import com.example.coursewebsite.model.Course;
import com.example.coursewebsite.model.User;
import com.example.coursewebsite.repository.CartItemRepository;
import com.example.coursewebsite.repository.CourseRepository;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final CourseRepository courseRepository;

    public CartService(CartItemRepository cartItemRepository, CourseRepository courseRepository) {
        this.cartItemRepository = cartItemRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional(readOnly = true)
    public List<Course> getCartCourses(User user) {
        // Return course DTO source data in newest-added order.
        return cartItemRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(CartItem::getCourse)
                .toList();
    }

    @Transactional
    public List<Course> addCourse(User user, Long courseId) {
        // The unique DB constraint also protects this, but checking first avoids duplicate insert errors.
        if (!cartItemRepository.existsByUserAndCourseId(user, courseId)) {
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new IllegalArgumentException("Course not found"));
            CartItem item = new CartItem();
            item.setUser(user);
            item.setCourse(course);
            cartItemRepository.save(item);
        }
        return getCartCourses(user);
    }

    @Transactional
    public List<Course> addCourses(User user, Collection<Long> courseIds) {
        // Used after login to merge anonymous localStorage cart items into the persistent DB cart.
        for (Long courseId : courseIds) {
            addCourse(user, courseId);
        }
        return getCartCourses(user);
    }

    @Transactional
    public List<Course> removeCourse(User user, Long courseId) {
        cartItemRepository.deleteByUserAndCourseId(user, courseId);
        return getCartCourses(user);
    }

    @Transactional
    public List<Course> clearCart(User user) {
        cartItemRepository.deleteByUser(user);
        return List.of();
    }
}
