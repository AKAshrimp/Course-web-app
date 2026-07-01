package com.example.coursewebsite;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.coursewebsite.model.Course;
import com.example.coursewebsite.model.User;
import com.example.coursewebsite.repository.CourseRepository;
import com.example.coursewebsite.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Set<String> VALID_SUBJECTS = Set.of(
            "Business Finance",
            "Graphic Design",
            "Musical Instruments",
            "Web Development");
    private static final Set<String> VALID_LEVELS = Set.of(
            "All Levels",
            "Beginner Level",
            "Intermediate Level",
            "Expert Level");

    @Autowired
    public DataInitializer(
            UserRepository userRepository,
            CourseRepository courseRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // initialize username and password
        if (userRepository.count() == 0) {
            initUsers();
        }
        if (courseRepository.count() == 0) {
            initCourses();
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

    private void initCourses() {
        ClassPathResource resource = new ClassPathResource("data/udemy_courses.csv");
        if (!resource.exists()) {
            return;
        }

        List<Course> courses = new ArrayList<>();
        Set<Long> seenIds = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                parseCourse(line).ifPresent(course -> {
                    if (seenIds.add(course.getId())) {
                        courses.add(course);
                    }
                });
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to import Udemy courses CSV", e);
        }

        courseRepository.saveAll(courses);
    }

    private java.util.Optional<Course> parseCourse(String line) {
        List<String> columns = parseCsvLine(line);
        if (columns.size() != 12) {
            return java.util.Optional.empty();
        }

        String subject = columns.get(11).trim();
        String level = columns.get(8).trim();
        if (!VALID_SUBJECTS.contains(subject) || !VALID_LEVELS.contains(level)) {
            return java.util.Optional.empty();
        }

        try {
            Course course = new Course();
            course.setId(Long.parseLong(columns.get(0).trim()));
            course.setTitle(columns.get(1).trim());
            course.setPaid(Boolean.parseBoolean(columns.get(3).trim()));
            course.setPrice(Double.parseDouble(columns.get(4).trim()));
            course.setSubscriberCount(Integer.parseInt(columns.get(5).trim()));
            course.setReviewCount(Integer.parseInt(columns.get(6).trim()));
            course.setLectureCount(Integer.parseInt(columns.get(7).trim()));
            course.setLevel(level);
            course.setContentDuration(Double.parseDouble(columns.get(9).trim()));
            course.setPublishedAt(OffsetDateTime.parse(columns.get(10).trim()).toLocalDateTime());
            course.setSubject(subject);
            return java.util.Optional.of(course);
        } catch (RuntimeException e) {
            return java.util.Optional.empty();
        }
    }

    private List<String> parseCsvLine(String line) {
        List<String> columns = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char value = line.charAt(i);
            if (value == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (value == ',' && !inQuotes) {
                columns.add(current.toString());
                current.setLength(0);
            } else {
                current.append(value);
            }
        }
        columns.add(current.toString());

        return columns;
    }
} 