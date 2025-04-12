package com.example.coursewebsite.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.coursewebsite.model.User;
import com.example.coursewebsite.model.Vote;
import com.example.coursewebsite.service.UserService;
import com.example.coursewebsite.service.PollService;

import jakarta.validation.Valid;

@Controller
public class UserController {
    
    private final UserService userService;
    private final PollService pollService;
    
    @Autowired
    public UserController(UserService userService, PollService pollService) {
        this.userService = userService;
        this.pollService = pollService;
    }
    
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
    
    @PostMapping("/register")
    public String registerSubmit(@Valid @ModelAttribute("user") User user, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "register";
        }
        
        try {
            userService.registerNewUser(user);
            redirectAttributes.addFlashAttribute("registerSuccess", true);
            redirectAttributes.addFlashAttribute("message", "user.register.success");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            result.rejectValue("username", "error.user", e.getMessage());
            return "register";
        }
    }
    
    @GetMapping("/profile")
    public String profile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        Optional<User> optionalUser = userService.getUserByUsername(username);
        if (!optionalUser.isPresent()) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", optionalUser.get());
        return "profile";
    }
    
    @PostMapping("/profile/update")
    public String updateProfile(@Valid @ModelAttribute("user") User user, BindingResult result) {
        if (result.hasErrors()) {
            return "profile";
        }
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        Optional<User> optionalUser = userService.getUserByUsername(username);
        if (!optionalUser.isPresent()) {
            return "redirect:/login";
        }
        
        User existingUser = optionalUser.get();
        existingUser.setFullName(user.getFullName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhoneNumber(user.getPhoneNumber());
        
        userService.updateUser(existingUser);
        return "redirect:/profile?success";
    }
    
    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/admin/users")
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }
    
    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/admin/users/edit/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        Optional<User> optionalUser = userService.getUserById(id);
        if (!optionalUser.isPresent()) {
            return "redirect:/admin/dashboard";
        }
        
        model.addAttribute("user", optionalUser.get());
        return "admin/editUser";
    }
    
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/admin/users/edit/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute User user, RedirectAttributes redirectAttributes) {
        Optional<User> optionalUser = userService.getUserById(id);
        if (!optionalUser.isPresent()) {
            return "redirect:/admin/dashboard";
        }
        
        User existingUser = optionalUser.get();
        existingUser.setFullName(user.getFullName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhoneNumber(user.getPhoneNumber());
        existingUser.setRoles(user.getRoles());
        
        // 处理密码更新
        String password = user.getPassword();
        if (password != null && !password.trim().isEmpty()) {
            userService.updateUserPassword(existingUser, password);
        } else {
            userService.updateUser(existingUser);
        }
        
        redirectAttributes.addFlashAttribute("successMessage", "user.update.success");
        return "redirect:/admin/dashboard";
    }
    
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/admin/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<User> optionalUser = userService.getUserById(id);
        if (optionalUser.isPresent()) {
            String userName = optionalUser.get().getUsername();
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "user.delete.success");
        }
        return "redirect:/admin/dashboard";
    }
    
    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/admin/users/add")
    public String addUserForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/addUser";
    }
    
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/admin/users/add")
    public String addUser(@Valid @ModelAttribute("user") User user, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/addUser";
        }
        
        try {
            userService.registerNewUser(user);
            redirectAttributes.addFlashAttribute("successMessage", "user.add.success");
            return "redirect:/admin/dashboard";
        } catch (IllegalArgumentException e) {
            result.rejectValue("username", "error.user", e.getMessage());
            return "admin/addUser";
        }
    }

    @GetMapping("/user/voting-history")
    public String viewVotingHistory(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        Optional<User> optionalUser = userService.getUserByUsername(username);
        if (!optionalUser.isPresent()) {
            return "redirect:/login";
        }
        
        User user = optionalUser.get();
        List<Vote> votes = pollService.getUserVotes(user.getId());
        
        model.addAttribute("user", user);
        model.addAttribute("votes", votes);
        return "user/votingHistory";
    }

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/admin/users/{id}/voting-history")
    public String viewUserVotingHistory(@PathVariable Long id, Model model) {
        Optional<User> optionalUser = userService.getUserById(id);
        if (!optionalUser.isPresent()) {
            return "redirect:/admin/dashboard";
        }
        
        User user = optionalUser.get();
        List<Vote> votes = pollService.getUserVotes(user.getId());
        
        model.addAttribute("user", user);
        model.addAttribute("votes", votes);
        return "user/votingHistory";
    }
} 