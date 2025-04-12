package com.example.coursewebsite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.coursewebsite.model.Lecture;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {
} 