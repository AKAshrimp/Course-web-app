package com.example.coursewebsite.service;

import com.example.coursewebsite.model.Lecture;
import com.example.coursewebsite.model.LectureMaterial;
import com.example.coursewebsite.repository.LectureRepository;
import com.example.coursewebsite.repository.LectureMaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LectureService {
    
    private final LectureRepository lectureRepository;
    private final LectureMaterialRepository lectureMaterialRepository;
    private final String uploadDir = "uploads/materials/";
    
    @Autowired
    public LectureService(LectureRepository lectureRepository, LectureMaterialRepository lectureMaterialRepository) {
        this.lectureRepository = lectureRepository;
        this.lectureMaterialRepository = lectureMaterialRepository;
        
        // 确保上传目录存在
        try {
            Path dirPath = Paths.get(uploadDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public List<Lecture> getAllLectures() {
        return lectureRepository.findAll();
    }
    
    public Optional<Lecture> getLectureById(Long id) {
        return lectureRepository.findById(id);
    }
    
    public Lecture saveLecture(Lecture lecture) {
        return lectureRepository.save(lecture);
    }
    
    public void deleteLecture(Long id) {
        lectureRepository.deleteById(id);
    }
    
    public LectureMaterial uploadMaterial(Long lectureId, MultipartFile file) throws IOException {
        Optional<Lecture> optionalLecture = lectureRepository.findById(lectureId);
        if (!optionalLecture.isPresent()) {
            throw new IllegalArgumentException("课程不存在");
        }
        
        Lecture lecture = optionalLecture.get();
        
        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFilename = UUID.randomUUID().toString() + extension;
        
        // 保存文件
        String filePath = uploadDir + newFilename;
        file.transferTo(new File(filePath));
        
        // 创建并保存LectureMaterial
        LectureMaterial material = new LectureMaterial(originalFilename, file.getContentType(), filePath);
        material.setLecture(lecture);
        return lectureMaterialRepository.save(material);
    }
    
    public List<LectureMaterial> getMaterialsByLectureId(Long lectureId) {
        return lectureMaterialRepository.findByLectureId(lectureId);
    }
    
    public Optional<LectureMaterial> getMaterialById(Long id) {
        return lectureMaterialRepository.findById(id);
    }
    
    public void deleteMaterial(Long materialId) {
        Optional<LectureMaterial> optionalMaterial = lectureMaterialRepository.findById(materialId);
        if (!optionalMaterial.isPresent()) {
            throw new IllegalArgumentException("课程资料不存在");
        }
        
        LectureMaterial material = optionalMaterial.get();
        
        // 删除文件
        try {
            Path filePath = Paths.get(material.getFilePath());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // 删除数据库记录
        lectureMaterialRepository.deleteById(materialId);
    }
} 