package com.example.coursewebsite.repository;

import com.example.coursewebsite.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    List<Vote> findByUserId(Long userId);
    List<Vote> findByPollId(Long pollId);
    Optional<Vote> findByUserIdAndPollId(Long userId, Long pollId);
} 