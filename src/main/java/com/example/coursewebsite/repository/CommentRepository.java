package com.example.coursewebsite.repository;

import com.example.coursewebsite.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByLectureId(Long lectureId);
    List<Comment> findByPollId(Long pollId);
    List<Comment> findByUserId(Long userId);
} 