package com.example.coursewebsite.controller;

import com.example.coursewebsite.model.Lecture;
import com.example.coursewebsite.model.Poll;
import com.example.coursewebsite.model.User;
import com.example.coursewebsite.service.LectureService;
import com.example.coursewebsite.service.PollService;
import com.example.coursewebsite.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('TEACHER')")
public class AdminController {
    
    private final UserService userService;
    private final LectureService lectureService;
    private final PollService pollService;
    
    @Autowired
    public AdminController(UserService userService, LectureService lectureService, PollService pollService) {
        this.userService = userService;
        this.lectureService = lectureService;
        this.pollService = pollService;
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<User> users = userService.getAllUsers();
        List<Lecture> lectures = lectureService.getAllLectures();
        List<Poll> polls = pollService.getAllPolls();
        
        model.addAttribute("allUsers", users);
        model.addAttribute("lectures", lectures);
        model.addAttribute("polls", polls);
        
        return "admin/dashboard";
    }
} 