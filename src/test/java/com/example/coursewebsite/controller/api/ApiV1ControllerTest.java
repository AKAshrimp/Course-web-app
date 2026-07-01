package com.example.coursewebsite.controller.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import com.example.coursewebsite.config.SecurityConfig;
import com.example.coursewebsite.model.Course;
import com.example.coursewebsite.model.Comment;
import com.example.coursewebsite.model.Lecture;
import com.example.coursewebsite.model.LectureMaterial;
import com.example.coursewebsite.model.Poll;
import com.example.coursewebsite.model.PollOption;
import com.example.coursewebsite.model.User;
import com.example.coursewebsite.model.Vote;
import com.example.coursewebsite.service.CommentService;
import com.example.coursewebsite.service.CourseService;
import com.example.coursewebsite.service.CartService;
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
    private CourseService courseService;

    @MockBean
    private CartService cartService;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    void getPopularCoursesReturnsFourItemPage() throws Exception {
        Course course = new Course();
        course.setId(1070968L);
        course.setTitle("React for beginners");
        course.setPaid(true);
        course.setPrice(13.99);
        course.setSubscriberCount(2147);
        course.setReviewCount(916);
        course.setLectureCount(51);
        course.setLevel("Beginner");
        course.setContentDuration(8.5);
        course.setPublishedAt(LocalDateTime.of(2017, 1, 18, 20, 58, 58));
        course.setSubject("Web Development");

        when(courseService.getPopularCourses(PageRequest.of(0, 4)))
                .thenReturn(new PageImpl<>(List.of(course), PageRequest.of(0, 4), 12));

        mockMvc.perform(get("/api/v1/courses/popular?page=0&size=4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].id").value(1070968))
                .andExpect(jsonPath("$.items[0].title").value("React for beginners"))
                .andExpect(jsonPath("$.items[0].paid").value(true))
                .andExpect(jsonPath("$.items[0].price").value(13.99))
                .andExpect(jsonPath("$.items[0].subscriberCount").value(2147))
                .andExpect(jsonPath("$.items[0].reviewCount").value(916))
                .andExpect(jsonPath("$.items[0].lectureCount").value(51))
                .andExpect(jsonPath("$.items[0].level").value("Beginner"))
                .andExpect(jsonPath("$.items[0].contentDuration").value(8.5))
                .andExpect(jsonPath("$.items[0].subject").value("Web Development"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(4))
                .andExpect(jsonPath("$.total").value(12))
                .andExpect(jsonPath("$.totalPages").value(3));
    }

    @Test
    void getCoursesReturnsSubjectFilteredPage() throws Exception {
        Course course = new Course();
        course.setId(625204L);
        course.setTitle("The Web Developer Bootcamp");
        course.setPaid(true);
        course.setPrice(200);
        course.setSubscriberCount(121584);
        course.setReviewCount(27445);
        course.setLectureCount(342);
        course.setLevel("All Levels");
        course.setContentDuration(43);
        course.setPublishedAt(LocalDateTime.of(2015, 11, 2, 21, 13, 27));
        course.setSubject("Web Development");

        when(courseService.getCoursesBySubject("Web Development", PageRequest.of(0, 4)))
                .thenReturn(new PageImpl<>(List.of(course), PageRequest.of(0, 4), 1200));

        mockMvc.perform(get("/api/v1/courses")
                        .queryParam("subject", "Web Development")
                        .queryParam("page", "0")
                        .queryParam("size", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].id").value(625204))
                .andExpect(jsonPath("$.items[0].title").value("The Web Developer Bootcamp"))
                .andExpect(jsonPath("$.items[0].subject").value("Web Development"))
                .andExpect(jsonPath("$.items[0].subscriberCount").value(121584))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(4))
                .andExpect(jsonPath("$.total").value(1200))
                .andExpect(jsonPath("$.totalPages").value(300));
    }

    @Test
    void authenticatedUserCanReadPersistentCart() throws Exception {
        User user = new User();
        user.setId(5L);
        user.setUsername("student");

        Course course = course(625204L, "The Web Developer Bootcamp", 200, 27445);

        when(userService.getUserByUsername("student")).thenReturn(Optional.of(user));
        when(cartService.getCartCourses(user)).thenReturn(List.of(course));

        mockMvc.perform(get("/api/v1/cart").with(user("student").roles("STUDENT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].id").value(625204))
                .andExpect(jsonPath("$.items[0].title").value("The Web Developer Bootcamp"))
                .andExpect(jsonPath("$.items[0].price").value(200))
                .andExpect(jsonPath("$.total").value(200))
                .andExpect(jsonPath("$.count").value(1));
    }

    @Test
    void authenticatedUserCanAddCourseToCart() throws Exception {
        User user = new User();
        user.setId(5L);
        user.setUsername("student");

        Course course = course(625204L, "The Web Developer Bootcamp", 200, 27445);

        when(userService.getUserByUsername("student")).thenReturn(Optional.of(user));
        when(cartService.addCourse(user, 625204L)).thenReturn(List.of(course));

        mockMvc.perform(post("/api/v1/cart/items")
                        .with(user("student").roles("STUDENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"courseId\":625204}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].id").value(625204))
                .andExpect(jsonPath("$.total").value(200))
                .andExpect(jsonPath("$.count").value(1));
    }

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
    void getPollDetailReturnsOptionsCommentsAndCurrentUserVote() throws Exception {
        Poll poll = new Poll("Which topic?");
        poll.setId(1L);
        poll.setCreatedAt(LocalDateTime.of(2026, 6, 5, 10, 0));
        PollOption javaOption = new PollOption("Java", poll);
        javaOption.setId(10L);
        PollOption reactOption = new PollOption("React", poll);
        reactOption.setId(11L);
        poll.setOptions(List.of(javaOption, reactOption));

        User user = new User();
        user.setId(5L);
        user.setUsername("student");
        user.setFullName("Kelvin Chan");
        Vote vote = new Vote(user, poll, javaOption);
        Comment comment = new Comment("React please", user);
        comment.setId(20L);
        comment.setCreatedAt(LocalDateTime.of(2026, 6, 5, 12, 0));

        when(pollService.getPollById(1L)).thenReturn(Optional.of(poll));
        when(pollService.getVotesByPollId(1L)).thenReturn(List.of(vote));
        when(commentService.getPollComments(1L)).thenReturn(List.of(comment));
        when(userService.getUserByUsername("student")).thenReturn(Optional.of(user));
        when(pollService.getUserVoteOnPoll(5L, 1L)).thenReturn(Optional.of(vote));

        mockMvc.perform(get("/api/v1/polls/1").with(user("student").roles("STUDENT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.question").value("Which topic?"))
                .andExpect(jsonPath("$.userVoteOptionId").value(10))
                .andExpect(jsonPath("$.options[0].id").value(10))
                .andExpect(jsonPath("$.options[0].voteCount").value(1))
                .andExpect(jsonPath("$.comments[0].authorName").value("Kelvin Chan"))
                .andExpect(jsonPath("$.comments[0].content").value("React please"));
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
    void authenticatedUserCanAddPollCommentThroughApi() throws Exception {
        User user = new User();
        user.setId(5L);
        user.setUsername("student");
        user.setFullName("Kelvin Chan");
        Comment savedComment = new Comment("React please", user);
        savedComment.setId(12L);
        savedComment.setCreatedAt(LocalDateTime.of(2026, 6, 5, 13, 0));
        when(userService.getUserByUsername("student")).thenReturn(Optional.of(user));
        when(commentService.addPollComment(1L, user, "React please")).thenReturn(savedComment);

        mockMvc.perform(post("/api/v1/polls/1/comments")
                        .with(user("student").roles("STUDENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"React please\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(12))
                .andExpect(jsonPath("$.content").value("React please"))
                .andExpect(jsonPath("$.authorName").value("Kelvin Chan"));

        verify(commentService).addPollComment(1L, user, "React please");
    }

    @Test
    void authenticatedUserCanReadAndUpdateProfileThroughApi() throws Exception {
        User user = new User();
        user.setId(5L);
        user.setUsername("student");
        user.setFullName("Kelvin Chan");
        user.setEmail("kelvin@example.com");
        user.setPhoneNumber("91234567");
        user.addRole("ROLE_STUDENT");
        when(userService.getUserByUsername("student")).thenReturn(Optional.of(user));
        when(userService.updateUser(any())).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(get("/api/v1/profile").with(user("student").roles("STUDENT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("student"))
                .andExpect(jsonPath("$.fullName").value("Kelvin Chan"))
                .andExpect(jsonPath("$.email").value("kelvin@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("91234567"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_STUDENT"));

        mockMvc.perform(put("/api/v1/profile")
                        .with(user("student").roles("STUDENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"Kelvin Wong\",\"email\":\"kelvin.wong@example.com\",\"phoneNumber\":\"81234567\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Kelvin Wong"))
                .andExpect(jsonPath("$.email").value("kelvin.wong@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("81234567"));

        verify(userService).updateUser(user);
    }

    @Test
    void authenticatedUserCanReadVotesThroughApi() throws Exception {
        User user = new User();
        user.setId(5L);
        user.setUsername("student");
        Poll poll = new Poll("Which topic?");
        poll.setId(1L);
        poll.setCreatedAt(LocalDateTime.of(2026, 6, 5, 10, 0));
        PollOption option = new PollOption("Java", poll);
        option.setId(10L);
        Vote vote = new Vote(user, poll, option);
        vote.setId(20L);
        vote.setVotedAt(LocalDateTime.of(2026, 6, 5, 13, 0));
        when(userService.getUserByUsername("student")).thenReturn(Optional.of(user));
        when(pollService.getUserVotes(5L)).thenReturn(List.of(vote));

        mockMvc.perform(get("/api/v1/me/votes").with(user("student").roles("STUDENT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(20))
                .andExpect(jsonPath("$[0].pollId").value(1))
                .andExpect(jsonPath("$[0].pollQuestion").value("Which topic?"))
                .andExpect(jsonPath("$[0].optionId").value(10))
                .andExpect(jsonPath("$[0].optionText").value("Java"));
    }

    @Test
    void anonymousUserCannotVoteThroughApi() throws Exception {
        mockMvc.perform(post("/api/v1/polls/1/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"optionId\":10}"))
                .andExpect(status().isUnauthorized());
    }

    private Course course(Long id, String title, double price, int reviewCount) {
        Course course = new Course();
        course.setId(id);
        course.setTitle(title);
        course.setPaid(price > 0);
        course.setPrice(price);
        course.setSubscriberCount(121584);
        course.setReviewCount(reviewCount);
        course.setLectureCount(342);
        course.setLevel("All Levels");
        course.setContentDuration(43);
        course.setPublishedAt(LocalDateTime.of(2015, 11, 2, 21, 13, 27));
        course.setSubject("Web Development");
        return course;
    }
}
