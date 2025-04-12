package com.example.coursewebsite.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.coursewebsite.model.Lecture;
import com.example.coursewebsite.model.Poll;
import com.example.coursewebsite.model.User;
import com.example.coursewebsite.service.LectureService;
import com.example.coursewebsite.service.PollService;
import com.example.coursewebsite.service.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/teacher")
@PreAuthorize("hasRole('TEACHER')")
public class TeacherController {
    
    private final UserService userService;
    private final LectureService lectureService;
    private final PollService pollService;
    
    @Autowired
    public TeacherController(UserService userService, LectureService lectureService, PollService pollService) {
        this.userService = userService;
        this.lectureService = lectureService;
        this.pollService = pollService;
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<User> users = userService.getAllUsers();
        List<Lecture> lectures = lectureService.getAllLectures();
        List<Poll> polls = pollService.getAllPolls();
        
        model.addAttribute("users", users);
        model.addAttribute("lectures", lectures);
        model.addAttribute("polls", polls);
        
        return "teacher/dashboard";
    }
    
    // 用户管理
    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "teacher/users";
    }
    
    @GetMapping("/users/add")
    public String addUserForm(Model model) {
        model.addAttribute("user", new User());
        return "teacher/addUser";
    }
    
    @PostMapping("/users/add")
    public String addUser(@Valid @ModelAttribute User user, BindingResult result) {
        if (result.hasErrors()) {
            return "teacher/addUser";
        }
        
        try {
            userService.registerNewUser(user);
            return "redirect:/teacher/users";
        } catch (Exception e) {
            result.rejectValue("username", "error.user", e.getMessage());
            return "teacher/addUser";
        }
    }
    
    @GetMapping("/users/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        Optional<User> user = userService.getUserById(id);
        if (!user.isPresent()) {
            return "redirect:/teacher/users";
        }
        
        model.addAttribute("user", user.get());
        return "teacher/editUser";
    }
    
    @PostMapping("/users/edit/{id}")
    public String updateUser(@PathVariable Long id, @Valid @ModelAttribute User user, BindingResult result) {
        if (result.hasErrors()) {
            return "teacher/editUser";
        }
        
        Optional<User> existingUser = userService.getUserById(id);
        if (!existingUser.isPresent()) {
            return "redirect:/teacher/users";
        }
        
        User updatedUser = existingUser.get();
        updatedUser.setFullName(user.getFullName());
        updatedUser.setEmail(user.getEmail());
        updatedUser.setPhoneNumber(user.getPhoneNumber());
        updatedUser.setRoles(user.getRoles());
        
        userService.updateUser(updatedUser);
        return "redirect:/teacher/users";
    }
    
    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/teacher/users";
    }
    
    // 课程管理
    @GetMapping("/lectures")
    public String listLectures(Model model) {
        List<Lecture> lectures = lectureService.getAllLectures();
        model.addAttribute("lectures", lectures);
        return "teacher/lectures";
    }
    
    @GetMapping("/lectures/add")
    public String addLectureForm(Model model) {
        model.addAttribute("lecture", new Lecture());
        return "teacher/addLecture";
    }
    
    @PostMapping("/lectures/add")
    public String addLecture(@Valid @ModelAttribute Lecture lecture, BindingResult result) {
        if (result.hasErrors()) {
            return "teacher/addLecture";
        }
        
        lecture.setCreatedAt(java.time.LocalDateTime.now());
        lectureService.saveLecture(lecture);
        return "redirect:/teacher/lectures";
    }
    
    @GetMapping("/lectures/edit/{id}")
    public String editLectureForm(@PathVariable Long id, Model model) {
        Optional<Lecture> lecture = lectureService.getLectureById(id);
        if (!lecture.isPresent()) {
            return "redirect:/teacher/lectures";
        }
        
        model.addAttribute("lecture", lecture.get());
        return "teacher/editLecture";
    }
    
    @PostMapping("/lectures/edit/{id}")
    public String updateLecture(@PathVariable Long id, @Valid @ModelAttribute Lecture lecture, BindingResult result) {
        if (result.hasErrors()) {
            return "teacher/editLecture";
        }
        
        Optional<Lecture> existingLecture = lectureService.getLectureById(id);
        if (!existingLecture.isPresent()) {
            return "redirect:/teacher/lectures";
        }
        
        Lecture updatedLecture = existingLecture.get();
        updatedLecture.setTitle(lecture.getTitle());
        updatedLecture.setDescription(lecture.getDescription());
        
        lectureService.saveLecture(updatedLecture);
        return "redirect:/teacher/lectures";
    }
    
    @PostMapping("/lectures/delete/{id}")
    public String deleteLecture(@PathVariable Long id) {
        lectureService.deleteLecture(id);
        return "redirect:/teacher/lectures";
    }
    
    // 投票管理
    @GetMapping("/polls")
    public String listPolls(Model model) {
        List<Poll> polls = pollService.getAllPolls();
        model.addAttribute("polls", polls);
        return "teacher/polls";
    }
    
    @GetMapping("/polls/add")
    public String addPollForm(Model model) {
        model.addAttribute("poll", new Poll());
        return "teacher/addPoll";
    }
    
    @PostMapping("/polls/add")
    public String addPoll(@Valid @ModelAttribute Poll poll, BindingResult result) {
        if (result.hasErrors()) {
            return "teacher/addPoll";
        }
        
        pollService.createPoll(poll);
        return "redirect:/teacher/polls";
    }
    
    @GetMapping("/polls/edit/{id}")
    public String editPollForm(@PathVariable Long id, Model model) {
        Optional<Poll> poll = pollService.getPollById(id);
        if (!poll.isPresent()) {
            return "redirect:/teacher/polls";
        }
        
        model.addAttribute("poll", poll.get());
        return "teacher/editPoll";
    }
    
    @PostMapping("/polls/edit/{id}")
    public String updatePoll(@PathVariable Long id, @Valid @ModelAttribute Poll poll, BindingResult result) {
        if (result.hasErrors()) {
            return "teacher/editPoll";
        }
        
        Optional<Poll> existingPoll = pollService.getPollById(id);
        if (!existingPoll.isPresent()) {
            return "redirect:/teacher/polls";
        }
        
        Poll updatedPoll = existingPoll.get();
        updatedPoll.setQuestion(poll.getQuestion());
        
        pollService.updatePoll(updatedPoll);
        return "redirect:/teacher/polls";
    }
    
    @PostMapping("/polls/delete/{id}")
    public String deletePoll(@PathVariable Long id) {
        pollService.deletePoll(id);
        return "redirect:/teacher/polls";
    }
} 