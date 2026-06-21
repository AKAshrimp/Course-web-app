package com.example.coursewebsite.controller.api;

import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import com.example.coursewebsite.config.SecurityConfig;
import com.example.coursewebsite.service.JwtTokenService;
import com.example.coursewebsite.service.TokenSessionService;
import com.example.coursewebsite.service.UserService;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtTokenService jwtTokenService;

    @MockBean
    private TokenSessionService tokenSessionService;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    void logoutDeletesActiveTokenSession() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                        .with(jwt().jwt(jwt -> jwt.claim("jti", "token-1").subject("student"))))
                .andExpect(status().isNoContent());

        verify(tokenSessionService).deleteActiveToken("token-1");
    }
}
