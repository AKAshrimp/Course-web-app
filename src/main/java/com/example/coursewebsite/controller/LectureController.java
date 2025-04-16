package com.example.coursewebsite.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.coursewebsite.model.Comment;
import com.example.coursewebsite.model.Lecture;
import com.example.coursewebsite.model.LectureMaterial;
import com.example.coursewebsite.model.User;
import com.example.coursewebsite.service.CommentService;
import com.example.coursewebsite.service.LectureService;
import com.example.coursewebsite.service.UserService;

@Controller
public class LectureController {
    
    private final LectureService lectureService;
    private final CommentService commentService;
    private final UserService userService;
    
    @Autowired
    public LectureController(LectureService lectureService, CommentService commentService, UserService userService) {
        this.lectureService = lectureService;
        this.commentService = commentService;
        this.userService = userService;
    }
    
    @GetMapping("/lecture/{id}")
    public String viewLecture(@PathVariable Long id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName().equals("anonymousUser")) {
            return "loginRequired";
        }
        
        Optional<Lecture> optionalLecture = lectureService.getLectureById(id);
        if (!optionalLecture.isPresent()) {
            return "redirect:/index";
        }
        
        Lecture lecture = optionalLecture.get();
        List<LectureMaterial> materials = lectureService.getMaterialsByLectureId(id);
        List<Comment> comments = commentService.getLectureComments(id);
        
        model.addAttribute("lecture", lecture);
        model.addAttribute("materials", materials);
        model.addAttribute("comments", comments);
        model.addAttribute("newComment", "");
        
        return "lecture/view";
    }
    
    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/admin/lectures/add")
    public String addLectureForm(Model model) {
        model.addAttribute("lecture", new Lecture());
        model.addAttribute("title", "lecture.add.title");
        return "admin/addLecture";
    }
    
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/admin/lectures/add")
    public String addLecture(@ModelAttribute Lecture lecture, RedirectAttributes redirectAttributes) {
        Lecture savedLecture = lectureService.saveLecture(lecture);
        redirectAttributes.addFlashAttribute("successMessage", "lecture.add.success");
        return "redirect:/admin/dashboard";
    }
    
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/admin/lectures/{id}/delete")
    public String deleteLecture(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Lecture> optionalLecture = lectureService.getLectureById(id);
        if (optionalLecture.isPresent()) {
            String lectureTitle = optionalLecture.get().getTitle();
            lectureService.deleteLecture(id);
            redirectAttributes.addFlashAttribute("successMessage", "lecture.delete.success");
        }
        return "redirect:/admin/dashboard";
    }
    
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/admin/lectures/{id}/upload")
    public String uploadMaterial(@PathVariable Long id, @RequestParam("file") MultipartFile file, @RequestParam("title") String title) {
        try {
            lectureService.uploadMaterial(id, file, title);
            return "redirect:/lecture/" + id;
        } catch (IOException e) {
            return "redirect:/lecture/" + id + "?error=upload";
        }
    }
    
    @GetMapping("/lecture/material/{id}/download")
    @ResponseBody
    public ResponseEntity<Resource> downloadMaterial(@PathVariable Long id) {
        Optional<LectureMaterial> optionalMaterial = lectureService.getMaterialById(id);
        if (!optionalMaterial.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        LectureMaterial material = optionalMaterial.get();
        try {
            Path filePath = Paths.get(material.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());
            
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + material.getFileName() + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, material.getFileType())
                    .body(resource);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/admin/lectures/material/{id}/delete")
    public String deleteMaterial(@PathVariable Long id) {
        Optional<LectureMaterial> optionalMaterial = lectureService.getMaterialById(id);
        if (!optionalMaterial.isPresent()) {
            return "redirect:/index";
        }
        
        Long lectureId = optionalMaterial.get().getLecture().getId();
        lectureService.deleteMaterial(id);
        return "redirect:/lecture/" + lectureId;
    }
    
    @PostMapping("/lecture/{id}/comment")
    public String addComment(@PathVariable Long id, @RequestParam("content") String content) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        Optional<User> optionalUser = userService.getUserByUsername(username);
        if (!optionalUser.isPresent()) {
            return "redirect:/login";
        }
        
        User user = optionalUser.get();
        commentService.addLectureComment(id, user, content);
        return "redirect:/lecture/" + id;
    }
    
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/admin/lecture/comment/{id}/delete")
    public String deleteComment(@PathVariable Long id) {
        Optional<Comment> optionalComment = commentService.getCommentById(id);
        if (!optionalComment.isPresent()) {
            return "redirect:/index";
        }
        
        Long lectureId = optionalComment.get().getLecture().getId();
        commentService.deleteComment(id);
        return "redirect:/lecture/" + lectureId;
    }
    
    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/admin/lectures/edit/{id}")
    public String editLectureForm(@PathVariable Long id, Model model) {
        Optional<Lecture> optionalLecture = lectureService.getLectureById(id);
        if (!optionalLecture.isPresent()) {
            return "redirect:/admin/dashboard";
        }
        
        model.addAttribute("lecture", optionalLecture.get());
        return "admin/editLecture";
    }
    
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/admin/lectures/edit/{id}")
    public String updateLecture(@PathVariable Long id, @ModelAttribute Lecture lecture, RedirectAttributes redirectAttributes) {
        Optional<Lecture> optionalLecture = lectureService.getLectureById(id);
        if (!optionalLecture.isPresent()) {
            return "redirect:/admin/dashboard";
        }
        
        Lecture existingLecture = optionalLecture.get();
        existingLecture.setTitle(lecture.getTitle());
        existingLecture.setDescription(lecture.getDescription());
        
        lectureService.saveLecture(existingLecture);
        redirectAttributes.addFlashAttribute("successMessage", "lecture.update.success");
        return "redirect:/admin/dashboard";
    }
} 