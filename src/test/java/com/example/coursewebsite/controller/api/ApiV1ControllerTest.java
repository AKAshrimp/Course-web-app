package com.example.coursewebsite.controller.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import com.example.coursewebsite.config.SecurityConfig;
import com.example.coursewebsite.model.Comment;
import com.example.coursewebsite.model.Lecture;
import com.example.coursewebsite.model.LectureMaterial;
import com.example.coursewebsite.model.Poll;
import com.example.coursewebsite.model.PollOption;
import com.example.coursewebsite.model.User;
import com.example.coursewebsite.model.Vote;
import com.example.coursewebsite.service.CommentService;
import com.example.coursewebsite.service.LectureService;
import com.example.coursewebsite.service.PollService;
import com.example.coursewebsite.service.UserService;

@WebMvcTest(ApiV1Controller.class)
@Import(SecurityConfig.class)
class ApiV1ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LectureService lectureService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private PollService pollService;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    void getLecturesReturnsCompactJsonDtos() throws Exception {
        Lecture lecture = new Lecture();
        lecture.setId(1L);
        lecture.setTitle("Week 1");
        lecture.setDescription("Introduction");
        lecture.setCreatedAt(LocalDateTime.of(2026, 6, 5, 10, 0));
        when(lectureService.getAllLectures()).thenReturn(List.of(lecture));

        mockMvc.perform(get("/api/v1/lectures"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Week 1"))
                .andExpect(jsonPath("$[0].description").value("Introduction"))
                .andExpect(jsonPath("$[0].materials").doesNotExist())
                .andExpect(jsonPath("$[0].comments").doesNotExist());
    }

    @Test
    void getLectureDetailReturnsMaterialsAndComments() throws Exception {
        Lecture lecture = new Lecture();
        lecture.setId(1L);
        lecture.setTitle("Week 1");
        lecture.setDescription("Introduction");
        lecture.setCreatedAt(LocalDateTime.of(2026, 6, 5, 10, 0));

        LectureMaterial material = new LectureMaterial("slides.pdf", "application/pdf", "/tmp/slides.pdf", "Slides");
        material.setId(2L);
        material.setUploadTime(LocalDateTime.of(2026, 6, 5, 11, 0));

        User author = new User();
        author.setFullName("Kelvin Chan");
        Comment comment = new Comment("Great lecture", author);
        comment.setId(3L);
        comment.setCreatedAt(LocalDateTime.of(2026, 6, 5, 12, 0));

        when(lectureService.getLectureById(1L)).thenReturn(Optional.of(lecture));
        when(lectureService.getMaterialsByLectureId(1L)).thenReturn(List.of(material));
        when(commentService.getLectureComments(1L)).thenReturn(List.of(comment));

        mockMvc.perform(get("/api/v1/lectures/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Week 1"))
                .andExpect(jsonPath("$.materials[0].id").value(2))
                .andExpect(jsonPath("$.materials[0].title").value("Slides"))
                .andExpect(jsonPath("$.materials[0].fileName").value("slides.pdf"))
                .andExpect(jsonPath("$.comments[0].id").value(3))
                .andExpect(jsonPath("$.comments[0].authorName").value("Kelvin Chan"))
                .andExpect(jsonPath("$.comments[0].content").value("Great lecture"));
    }

    @Test
    void getPollsReturnsOptionsWithVoteCountsWithoutRecursiveEntities() throws Exception {
        Poll poll = new Poll("Which topic?");
        poll.setId(1L);
        PollOption option = new PollOption("Java", poll);
        option.setId(10L);
        poll.setOptions(List.of(option));
        Vote vote = new Vote();
        vote.setPollOption(option);
        when(pollService.getAllPolls()).thenReturn(List.of(poll));
        when(pollService.getVotesByPollId(1L)).thenReturn(List.of(vote));

        mockMvc.perform(get("/api/v1/polls"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].question").value("Which topic?"))
                .andExpect(jsonPath("$[0].options[0].id").value(10))
                .andExpect(jsonPath("$[0].options[0].text").value("Java"))
                .andExpect(jsonPath("$[0].options[0].voteCount").value(1))
                .andExpect(jsonPath("$[0].options[0].poll").doesNotExist())
                .andExpect(jsonPath("$[0].votes").doesNotExist());
    }

    @Test
    void authenticatedUserCanVoteThroughApi() throws Exception {
        User user = new User();
        user.setId(5L);
        user.setUsername("student");
        Vote savedVote = new Vote();
        PollOption option = new PollOption();
        option.setId(10L);
        savedVote.setPollOption(option);
        when(userService.getUserByUsername("student")).thenReturn(Optional.of(user));
        when(pollService.getUserVoteOnPoll(5L, 1L)).thenReturn(Optional.empty());
        when(pollService.vote(any(), any(), any())).thenReturn(savedVote);

        mockMvc.perform(post("/api/v1/polls/1/vote")
                        .with(user("student").roles("STUDENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"optionId\":10}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pollId").value(1))
                .andExpect(jsonPath("$.optionId").value(10))
                .andExpect(jsonPath("$.updated").value(false))
                .andExpect(jsonPath("$.message").value("Vote submitted successfully"));

        verify(pollService).vote(1L, 10L, user);
    }

    @Test
    void authenticatedUserCanAddLectureCommentThroughApi() throws Exception {
        User user = new User();
        user.setId(5L);
        user.setUsername("student");
        user.setFullName("Kelvin Chan");
        Comment savedComment = new Comment("Looks good", user);
        savedComment.setId(9L);
        savedComment.setCreatedAt(LocalDateTime.of(2026, 6, 5, 12, 30));
        when(userService.getUserByUsername("student")).thenReturn(Optional.of(user));
        when(commentService.addLectureComment(1L, user, "Looks good")).thenReturn(savedComment);

        mockMvc.perform(post("/api/v1/lectures/1/comments")
                        .with(user("student").roles("STUDENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"Looks good\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(9))
                .andExpect(jsonPath("$.content").value("Looks good"))
                .andExpect(jsonPath("$.authorName").value("Kelvin Chan"));

        verify(commentService).addLectureComment(1L, user, "Looks good");
    }

    @Test
    void anonymousUserCannotVoteThroughApi() throws Exception {
        mockMvc.perform(post("/api/v1/polls/1/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"optionId\":10}"))
                .andExpect(status().isUnauthorized());
    }
}
