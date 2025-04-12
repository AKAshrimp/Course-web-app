package com.example.coursewebsite;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.coursewebsite.model.User;
import com.example.coursewebsite.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // initialize username and password
        if (userRepository.count() == 0) {
            initUsers();
        }
    }

    private void initUsers() {
        User student = new User();
        student.setUsername("student");
        student.setPassword(passwordEncoder.encode("password"));
        student.setEmail("student@example.com");
        student.setFullName("Kelvin Chan");
        student.setPhoneNumber("87654321");
        student.addRole("ROLE_STUDENT");
        
        User teacher = new User();
        teacher.setUsername("teacher");
        teacher.setPassword(passwordEncoder.encode("password"));
        teacher.setEmail("teacher@example.com");
        teacher.setFullName("Jacky Chan");
        teacher.setPhoneNumber("12345678");
        teacher.addRole("ROLE_TEACHER");
        
        userRepository.saveAll(Arrays.asList(student, teacher));
    }
} 