package com.example.coursewebsite.controller.api;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import com.example.coursewebsite.config.SecurityConfig;
import com.example.coursewebsite.dto.admin.AdminUserRequest;
import com.example.coursewebsite.dto.admin.AdminUserResponse;
import com.example.coursewebsite.service.AdminAccountService;

@WebMvcTest(AdminUserController.class)
@Import(SecurityConfig.class)
class AdminUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminAccountService adminAccountService;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    void getUsersReturnsAdminUserResponses() throws Exception {
        when(adminAccountService.getAllUsers()).thenReturn(List.of(
                new AdminUserResponse(1L, "teacher", "Teacher One", "teacher@example.com", "123", Set.of("ROLE_TEACHER"))));

        mockMvc.perform(get("/api/admin/users")
                        .with(user("teacher").roles("TEACHER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("teacher"))
                .andExpect(jsonPath("$[0].fullName").value("Teacher One"))
                .andExpect(jsonPath("$[0].email").value("teacher@example.com"))
                .andExpect(jsonPath("$[0].roles[0]").value("ROLE_TEACHER"));
    }

    @Test
    void getUserReturnsSingleAdminUserResponse() throws Exception {
        when(adminAccountService.getUser(1L))
                .thenReturn(new AdminUserResponse(1L, "student", "Student One", "student@example.com", null, Set.of("ROLE_STUDENT")));

        mockMvc.perform(get("/api/admin/users/1")
                        .with(user("teacher").roles("TEACHER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("student"));
    }

    @Test
    void createUserPassesValidatedRequestToService() throws Exception {
        when(adminAccountService.createUser(org.mockito.ArgumentMatchers.any(AdminUserRequest.class)))
                .thenReturn(new AdminUserResponse(2L, "newuser", "New User", "new@example.com", null, Set.of("ROLE_STUDENT")));

        mockMvc.perform(post("/api/admin/users")
                        .with(user("teacher").roles("TEACHER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "newuser",
                                  "password": "password123",
                                  "fullName": "New User",
                                  "email": "new@example.com",
                                  "roles": ["ROLE_STUDENT"]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.username").value("newuser"));
    }

    @Test
    void updateUserPassesIdAndRequestToService() throws Exception {
        when(adminAccountService.updateUser(org.mockito.ArgumentMatchers.eq(2L), org.mockito.ArgumentMatchers.any(AdminUserRequest.class)))
                .thenReturn(new AdminUserResponse(2L, "newuser", "Updated User", "updated@example.com", null, Set.of("ROLE_STUDENT")));

        mockMvc.perform(put("/api/admin/users/2")
                        .with(user("teacher").roles("TEACHER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "newuser",
                                  "fullName": "Updated User",
                                  "email": "updated@example.com"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Updated User"));
    }

    @Test
    void updatePasswordPassesNewPasswordToService() throws Exception {
        mockMvc.perform(put("/api/admin/users/2/password")
                        .with(user("teacher").roles("TEACHER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newPassword\":\"newPassword123\"}"))
                .andExpect(status().isOk());

        verify(adminAccountService).updatePassword(2L, "newPassword123");
    }

    @Test
    void deleteUserPassesIdToService() throws Exception {
        mockMvc.perform(delete("/api/admin/users/2")
                        .with(user("teacher").roles("TEACHER")))
                .andExpect(status().isOk());

        verify(adminAccountService).deleteUser(2L);
    }
}
