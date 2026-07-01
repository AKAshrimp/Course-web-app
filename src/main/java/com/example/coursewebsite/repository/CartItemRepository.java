package com.example.coursewebsite.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.coursewebsite.model.CartItem;
import com.example.coursewebsite.model.User;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // Load a user's cart in the same order the cart page displays it.
    List<CartItem> findByUserOrderByCreatedAtDesc(User user);

    // Used before insert so adding the same course twice stays idempotent.
    boolean existsByUserAndCourseId(User user, Long courseId);

    void deleteByUserAndCourseId(User user, Long courseId);

    void deleteByUser(User user);
}
