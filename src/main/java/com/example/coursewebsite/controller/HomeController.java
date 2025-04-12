package com.example.coursewebsite.controller;

import com.example.coursewebsite.model.Lecture;
import com.example.coursewebsite.model.Poll;
import com.example.coursewebsite.service.LectureService;
import com.example.coursewebsite.service.PollService;
import com.example.coursewebsite.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    
    private final LectureService lectureService;
    private final PollService pollService;
    private final UserService userService;
    
    @Autowired
    public HomeController(LectureService lectureService, PollService pollService, UserService userService) {
        this.lectureService = lectureService;
        this.pollService = pollService;
        this.userService = userService;
    }
    
    @GetMapping("/")
    public String home(Model model) {
        List<Lecture> lectures = lectureService.getAllLectures();
        List<Poll> polls = pollService.getAllPolls();
        
        model.addAttribute("lectures", lectures);
        model.addAttribute("polls", polls);
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            boolean isTeacher = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"));
            model.addAttribute("isTeacher", isTeacher);
            
            if (isTeacher) {
                model.addAttribute("allUsers", userService.getAllUsers());
            }
        }
        
        return "index";
    }
    
    @GetMapping("/index")
    public String index(Model model) {
        List<Lecture> lectures = lectureService.getAllLectures();
        List<Poll> polls = pollService.getAllPolls();
        
        model.addAttribute("lectures", lectures);
        model.addAttribute("polls", polls);
        
        return "index";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
} 