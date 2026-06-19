package com.example.coursewebsite.controller.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import com.example.coursewebsite.config.SecurityConfig;
import com.example.coursewebsite.model.Lecture;
import com.example.coursewebsite.model.Poll;
import com.example.coursewebsite.model.PollOption;
import com.example.coursewebsite.service.LectureService;
import com.example.coursewebsite.service.PollService;
import com.example.coursewebsite.service.UserService;

@WebMvcTest(AdminContentController.class)
@Import(SecurityConfig.class)
class AdminContentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LectureService lectureService;

    @MockBean
    private PollService pollService;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    void teacherCanReadDashboardContent() throws Exception {
        Lecture lecture = new Lecture();
        lecture.setId(1L);
        lecture.setTitle("Week 1");
        lecture.setDescription("Introduction");
        lecture.setCreatedAt(LocalDateTime.of(2026, 6, 5, 10, 0));
        Poll poll = new Poll("Which topic?");
        poll.setId(2L);
        PollOption option = new PollOption("Java", poll);
        option.setId(3L);
        poll.setOptions(List.of(option));

        when(userService.getAllUsers()).thenReturn(List.of());
        when(lectureService.getAllLectures()).thenReturn(List.of(lecture));
        when(pollService.getAllPolls()).thenReturn(List.of(poll));
        when(pollService.getVotesByPollId(2L)).thenReturn(List.of());

        mockMvc.perform(get("/api/admin/content/dashboard").with(user("teacher").roles("TEACHER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userCount").value(0))
                .andExpect(jsonPath("$.lectures[0].id").value(1))
                .andExpect(jsonPath("$.polls[0].id").value(2))
                .andExpect(jsonPath("$.polls[0].options[0].text").value("Java"));
    }

    @Test
    void teacherCanCreateAndDeleteLecture() throws Exception {
        Lecture lecture = new Lecture();
        lecture.setId(10L);
        lecture.setTitle("Week 2");
        lecture.setDescription("REST APIs");
        when(lectureService.saveLecture(any(Lecture.class))).thenReturn(lecture);

        mockMvc.perform(post("/api/admin/content/lectures")
                        .with(user("teacher").roles("TEACHER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Week 2\",\"description\":\"REST APIs\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("Week 2"));

        mockMvc.perform(delete("/api/admin/content/lectures/10").with(user("teacher").roles("TEACHER")))
                .andExpect(status().isOk());

        verify(lectureService).deleteLecture(10L);
    }

    @Test
    void teacherCanCreateAndDeletePoll() throws Exception {
        Poll poll = new Poll("Which topic?");
        poll.setId(20L);
        poll.addOption(new PollOption("Java", poll));
        when(pollService.createPoll(any(Poll.class))).thenReturn(poll);
        when(pollService.getVotesByPollId(20L)).thenReturn(List.of());

        mockMvc.perform(post("/api/admin/content/polls")
                        .with(user("teacher").roles("TEACHER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"question\":\"Which topic?\",\"options\":[\"Java\",\"React\"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(20))
                .andExpect(jsonPath("$.question").value("Which topic?"));

        mockMvc.perform(delete("/api/admin/content/polls/20").with(user("teacher").roles("TEACHER")))
                .andExpect(status().isOk());

        verify(pollService).deletePoll(20L);
    }
}
