package com.example.coursewebsite.service;

import com.example.coursewebsite.model.User;
import com.example.coursewebsite.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public User registerNewUser(User user) {
        // 检查用户名和邮箱是否已存在
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("用户名已存在");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("邮箱已存在");
        }
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // 确保新用户默认为学生角色
        if (user.getRoles().isEmpty()) {
            user.addRole("ROLE_STUDENT");
        }
        
        return userRepository.save(user);
    }
    
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    public User updateUserPassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }
    
    public void deleteUser(Long id) {
        try {
            Optional<User> optionalUser = userRepository.findById(id);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                
                // 使用原生SQL语句清除用户的所有关联关系
                // 1. 删除用户的投票
                userRepository.deleteUserVotes(id);
                
                // 2. 删除用户的评论
                userRepository.deleteUserComments(id);
                
                // 3. 删除用户的角色
                userRepository.deleteUserRoles(id);
                
                // 4. 删除用户本身
                userRepository.delete(user);
            }
        } catch (Exception e) {
            // 记录错误但不抛出异常，确保操作能继续
            System.err.println("删除用户 ID: " + id + " 时出错: " + e.getMessage());
        }
    }
    
    public boolean isTeacher(User user) {
        return user.hasRole("ROLE_TEACHER");
    }
    
    public boolean isStudent(User user) {
        return user.hasRole("ROLE_STUDENT");
    }
} 