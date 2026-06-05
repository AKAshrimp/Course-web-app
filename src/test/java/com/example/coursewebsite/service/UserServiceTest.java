package com.example.coursewebsite.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.coursewebsite.model.User;
import com.example.coursewebsite.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void registerNewUserLowercasesUsernameHashesPasswordAddsDefaultStudentRoleAndSaves() {
        User user = new User();
        user.setUsername("StudentOne");
        user.setPassword("password123");
        user.setFullName("Student One");
        user.setEmail("student@example.com");
        when(userRepository.existsByUsername("studentone")).thenReturn(false);
        when(userRepository.existsByEmail("student@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User savedUser = userService.registerNewUser(user);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(savedUser).isSameAs(userCaptor.getValue());
        assertThat(userCaptor.getValue().getUsername()).isEqualTo("studentone");
        assertThat(userCaptor.getValue().getPassword()).isEqualTo("encoded-password");
        assertThat(userCaptor.getValue().getRoles()).containsExactly("ROLE_STUDENT");
    }

    @Test
    void registerNewUserDuplicateUsernameThrowsAndDoesNotSave() {
        User user = new User();
        user.setUsername("student");
        user.setPassword("password123");
        user.setEmail("student@example.com");
        when(userRepository.existsByUsername("student")).thenReturn(true);

        assertThatThrownBy(() -> userService.registerNewUser(user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("用户名已存在");

        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerNewUserDuplicateEmailThrowsAndDoesNotSave() {
        User user = new User();
        user.setUsername("student");
        user.setPassword("password123");
        user.setEmail("student@example.com");
        when(userRepository.existsByUsername("student")).thenReturn(false);
        when(userRepository.existsByEmail("student@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.registerNewUser(user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("邮箱已存在");

        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }
}
