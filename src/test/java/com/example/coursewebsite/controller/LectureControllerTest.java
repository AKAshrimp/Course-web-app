package com.example.coursewebsite.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.example.coursewebsite.config.SecurityConfig;
import com.example.coursewebsite.model.Lecture;
import com.example.coursewebsite.service.CommentService;
import com.example.coursewebsite.service.LectureService;
import com.example.coursewebsite.service.UserService;

@WebMvcTest(LectureController.class)
@Import(SecurityConfig.class)
class LectureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LectureService lectureService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private UserService userService;

    @Test
    void viewLectureAnonymousUserReturnsLoginRequiredView() throws Exception {
        mockMvc.perform(get("/lecture/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("loginRequired"));

        verify(lectureService, never()).getLectureById(any());
    }

    @Test
    void viewLectureAuthenticatedUserWithExistingLectureShowsLectureView() throws Exception {
        Lecture lecture = new Lecture();
        lecture.setId(1L);
        lecture.setTitle("Lecture 1");
        lecture.setDescription("Description");
        when(lectureService.getLectureById(1L)).thenReturn(Optional.of(lecture));
        when(lectureService.getMaterialsByLectureId(1L)).thenReturn(List.of());
        when(commentService.getLectureComments(1L)).thenReturn(List.of());

        mockMvc.perform(get("/lecture/1").with(user("student").roles("STUDENT")))
                .andExpect(status().isOk())
                .andExpect(view().name("lecture/view"))
                .andExpect(model().attribute("lecture", lecture))
                .andExpect(model().attribute("materials", List.of()))
                .andExpect(model().attribute("comments", List.of()))
                .andExpect(model().attribute("newComment", ""));
    }

    @Test
    void viewLectureAuthenticatedUserWithMissingLectureRedirectsIndex() throws Exception {
        when(lectureService.getLectureById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/lecture/1").with(user("student").roles("STUDENT")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/index"));
    }

    @Test
    void uploadMaterialRedirectsToLectureAndDelegatesToService() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "notes.pdf", "application/pdf", "content".getBytes());

        mockMvc.perform(multipart("/admin/lectures/1/upload")
                        .file(file)
                        .param("title", "Week 1 Notes")
                        .with(user("teacher").roles("TEACHER")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/lecture/1"));

        ArgumentCaptor<MultipartFile> fileCaptor = ArgumentCaptor.forClass(MultipartFile.class);
        verify(lectureService).uploadMaterial(eq(1L), fileCaptor.capture(), eq("Week 1 Notes"));
        org.assertj.core.api.Assertions.assertThat(fileCaptor.getValue().getOriginalFilename()).isEqualTo("notes.pdf");
    }
}
