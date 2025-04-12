package com.example.coursewebsite.repository;

import com.example.coursewebsite.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    List<Material> findByLectureId(Long lectureId);
} 