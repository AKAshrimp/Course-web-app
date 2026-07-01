package com.example.coursewebsite.controller.api;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.coursewebsite.dto.api.CommentDto;
import com.example.coursewebsite.dto.api.CommentRequestDto;
import com.example.coursewebsite.dto.api.CartItemRequest;
import com.example.coursewebsite.dto.api.CartResponse;
import com.example.coursewebsite.dto.api.CartSyncRequest;
import com.example.coursewebsite.dto.api.CoursePageResponse;
import com.example.coursewebsite.dto.api.CourseSummaryDto;
import com.example.coursewebsite.dto.api.LectureDetailDto;
import com.example.coursewebsite.dto.api.LectureDto;
import com.example.coursewebsite.dto.api.MaterialDto;
import com.example.coursewebsite.dto.api.PollDetailDto;
import com.example.coursewebsite.dto.api.PollDto;
import com.example.coursewebsite.dto.api.PollOptionDto;
import com.example.coursewebsite.dto.api.ProfileDto;
import com.example.coursewebsite.dto.api.ProfileUpdateRequestDto;
import com.example.coursewebsite.dto.api.UserVoteDto;
import com.example.coursewebsite.dto.api.VoteRequestDto;
import com.example.coursewebsite.dto.api.VoteResponseDto;
import com.example.coursewebsite.model.Comment;
import com.example.coursewebsite.model.Course;
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

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class ApiV1Controller {

    private final LectureService lectureService;
    private final CommentService commentService;
    private final PollService pollService;
    private final UserService userService;
    private final CourseService courseService;
    private final CartService cartService;

    public ApiV1Controller(LectureService lectureService, CommentService commentService, PollService pollService,
            UserService userService, CourseService courseService, CartService cartService) {
        this.lectureService = lectureService;
        this.commentService = commentService;
        this.pollService = pollService;
        this.userService = userService;
        this.courseService = courseService;
        this.cartService = cartService;
    }

    @GetMapping("/courses/popular")
    public CoursePageResponse getPopularCourses(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 12);
        Page<Course> courses = courseService.getPopularCourses(PageRequest.of(safePage, safeSize));
        return toCoursePageResponse(courses);
    }

    @GetMapping("/courses")
    public CoursePageResponse getCourses(@RequestParam String subject, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 12);
        Page<Course> courses = courseService.getCoursesBySubject(subject, PageRequest.of(safePage, safeSize));
        return toCoursePageResponse(courses);
    }

    private CoursePageResponse toCoursePageResponse(Page<Course> courses) {
        List<CourseSummaryDto> items = courses.getContent().stream()
                .map(this::toCourseSummaryDto)
                .toList();

        return new CoursePageResponse(items, courses.getTotalElements(), courses.getNumber(), courses.getSize(),
                courses.getTotalPages());
    }

    @GetMapping("/lectures")
    public List<LectureDto> getLectures() {
        return lectureService.getAllLectures().stream()
                .map(lecture -> new LectureDto(
                        lecture.getId(),
                        lecture.getTitle(),
                        lecture.getDescription(),
                        lecture.getCreatedAt()))
                .toList();
    }

    private CourseSummaryDto toCourseSummaryDto(Course course) {
        return new CourseSummaryDto(
                course.getId(),
                course.getTitle(),
                course.isPaid(),
                course.getPrice(),
                course.getSubscriberCount(),
                course.getReviewCount(),
                course.getLectureCount(),
                course.getLevel(),
                course.getContentDuration(),
                course.getPublishedAt().toString(),
                course.getSubject());
    }

    @GetMapping("/cart")
    public ResponseEntity<CartResponse> getCart(Principal principal) {
        // Persistent carts are available only for authenticated users.
        return findCurrentUser(principal)
                .map(user -> ResponseEntity.ok(toCartResponse(cartService.getCartCourses(user))))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/cart/items")
    public ResponseEntity<CartResponse> addCartItem(@Valid @RequestBody CartItemRequest request,
            Principal principal) {
        // Add one course and return the refreshed cart so the frontend can update immediately.
        return findCurrentUser(principal)
                .map(user -> ResponseEntity.ok(toCartResponse(cartService.addCourse(user, request.courseId()))))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/cart/items/batch")
    public ResponseEntity<CartResponse> syncCartItems(@RequestBody CartSyncRequest request, Principal principal) {
        // Batch add is used to merge localStorage cart items after login.
        List<Long> courseIds = request.courseIds() == null ? List.of() : request.courseIds();
        return findCurrentUser(principal)
                .map(user -> ResponseEntity.ok(toCartResponse(cartService.addCourses(user, courseIds))))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @DeleteMapping("/cart/items/{courseId}")
    public ResponseEntity<CartResponse> removeCartItem(@PathVariable Long courseId, Principal principal) {
        return findCurrentUser(principal)
                .map(user -> ResponseEntity.ok(toCartResponse(cartService.removeCourse(user, courseId))))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @DeleteMapping("/cart/items")
    public ResponseEntity<CartResponse> clearCart(Principal principal) {
        return findCurrentUser(principal)
                .map(user -> ResponseEntity.ok(toCartResponse(cartService.clearCart(user))))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    private CartResponse toCartResponse(List<Course> courses) {
        // Keep the cart response shape close to course list responses for frontend reuse.
        List<CourseSummaryDto> items = courses.stream()
                .map(this::toCourseSummaryDto)
                .toList();
        double total = courses.stream()
                .mapToDouble(Course::getPrice)
                .sum();
        return new CartResponse(items, total, items.size());
    }

    @GetMapping("/lectures/{lectureId}")
    public ResponseEntity<LectureDetailDto> getLecture(@PathVariable Long lectureId) {
        return lectureService.getLectureById(lectureId)
                .map(lecture -> ResponseEntity.ok(toLectureDetailDto(lecture)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/lectures/{lectureId}/comments")
    public ResponseEntity<CommentDto> addLectureComment(@PathVariable Long lectureId,
            @Valid @RequestBody CommentRequestDto request, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<User> optionalUser = userService.getUserByUsername(principal.getName());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Comment comment = commentService.addLectureComment(lectureId, optionalUser.get(), request.content());
        return ResponseEntity.ok(toCommentDto(comment));
    }

    @GetMapping("/polls")
    public List<PollDto> getPolls() {
        return pollService.getAllPolls().stream()
                .map(this::toPollDto)
                .toList();
    }

    @GetMapping("/polls/{pollId}")
    public ResponseEntity<PollDetailDto> getPoll(@PathVariable Long pollId, Principal principal) {
        return pollService.getPollById(pollId)
                .map(poll -> ResponseEntity.ok(toPollDetailDto(poll, principal)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/polls/{pollId}/vote")
    public ResponseEntity<VoteResponseDto> vote(@PathVariable Long pollId, @Valid @RequestBody VoteRequestDto request,
            Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<User> optionalUser = userService.getUserByUsername(principal.getName());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = optionalUser.get();
        boolean updated = pollService.getUserVoteOnPoll(user.getId(), pollId).isPresent();
        Vote vote = pollService.vote(pollId, request.optionId(), user);
        Long optionId = vote.getPollOption().getId();
        String message = updated ? "Vote updated successfully" : "Vote submitted successfully";

        return ResponseEntity.ok(new VoteResponseDto(pollId, optionId, updated, message));
    }

    @PostMapping("/polls/{pollId}/comments")
    public ResponseEntity<CommentDto> addPollComment(@PathVariable Long pollId,
            @Valid @RequestBody CommentRequestDto request, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<User> optionalUser = userService.getUserByUsername(principal.getName());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Comment comment = commentService.addPollComment(pollId, optionalUser.get(), request.content());
        return ResponseEntity.ok(toCommentDto(comment));
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileDto> getProfile(Principal principal) {
        return findCurrentUser(principal)
                .map(user -> ResponseEntity.ok(toProfileDto(user)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PutMapping("/profile")
    public ResponseEntity<ProfileDto> updateProfile(@Valid @RequestBody ProfileUpdateRequestDto request,
            Principal principal) {
        Optional<User> optionalUser = findCurrentUser(principal);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = optionalUser.get();
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPhoneNumber(request.phoneNumber());
        User updated = userService.updateUser(user);

        return ResponseEntity.ok(toProfileDto(updated));
    }

    @GetMapping("/me/votes")
    public ResponseEntity<List<UserVoteDto>> getMyVotes(Principal principal) {
        Optional<User> optionalUser = findCurrentUser(principal);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<UserVoteDto> votes = pollService.getUserVotes(optionalUser.get().getId()).stream()
                .map(this::toUserVoteDto)
                .toList();
        return ResponseEntity.ok(votes);
    }

    private PollDto toPollDto(Poll poll) {
        List<Vote> votes = pollService.getVotesByPollId(poll.getId());
        List<PollOptionDto> options = poll.getOptions().stream()
                .map(option -> toPollOptionDto(option, votes))
                .toList();
        return new PollDto(poll.getId(), poll.getQuestion(), poll.getCreatedAt(), options);
    }

    private PollDetailDto toPollDetailDto(Poll poll, Principal principal) {
        List<Vote> votes = pollService.getVotesByPollId(poll.getId());
        List<PollOptionDto> options = poll.getOptions().stream()
                .map(option -> toPollOptionDto(option, votes))
                .toList();
        List<CommentDto> comments = commentService.getPollComments(poll.getId()).stream()
                .map(this::toCommentDto)
                .toList();
        Long userVoteOptionId = resolveUserVoteOptionId(poll.getId(), principal);

        return new PollDetailDto(poll.getId(), poll.getQuestion(), poll.getCreatedAt(), options, comments,
                userVoteOptionId);
    }

    private Long resolveUserVoteOptionId(Long pollId, Principal principal) {
        if (principal == null) {
            return null;
        }

        return userService.getUserByUsername(principal.getName())
                .flatMap(user -> pollService.getUserVoteOnPoll(user.getId(), pollId))
                .map(Vote::getPollOption)
                .map(PollOption::getId)
                .orElse(null);
    }

    private PollOptionDto toPollOptionDto(PollOption option, List<Vote> votes) {
        int voteCount = (int) votes.stream()
                .filter(vote -> vote.getPollOption() != null
                        && vote.getPollOption().getId() != null
                        && vote.getPollOption().getId().equals(option.getId()))
                .count();
        return new PollOptionDto(option.getId(), option.getText(), voteCount);
    }

    private LectureDetailDto toLectureDetailDto(Lecture lecture) {
        List<MaterialDto> materials = lectureService.getMaterialsByLectureId(lecture.getId()).stream()
                .map(this::toMaterialDto)
                .toList();
        List<CommentDto> comments = commentService.getLectureComments(lecture.getId()).stream()
                .map(this::toCommentDto)
                .toList();

        return new LectureDetailDto(
                lecture.getId(),
                lecture.getTitle(),
                lecture.getDescription(),
                lecture.getCreatedAt(),
                materials,
                comments);
    }

    private MaterialDto toMaterialDto(LectureMaterial material) {
        return new MaterialDto(
                material.getId(),
                material.getTitle(),
                material.getFileName(),
                material.getFileType(),
                material.getUploadTime());
    }

    private CommentDto toCommentDto(Comment comment) {
        String authorName = comment.getUser() != null ? comment.getUser().getFullName() : "Unknown";
        return new CommentDto(
                comment.getId(),
                comment.getContent(),
                authorName,
                comment.getCreatedAt());
    }

    private Optional<User> findCurrentUser(Principal principal) {
        if (principal == null) {
            return Optional.empty();
        }

        return userService.getUserByUsername(principal.getName());
    }

    private ProfileDto toProfileDto(User user) {
        return new ProfileDto(
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getPhoneNumber(),
                List.copyOf(user.getRoles()));
    }

    private UserVoteDto toUserVoteDto(Vote vote) {
        Poll poll = vote.getPoll();
        PollOption option = vote.getPollOption();
        return new UserVoteDto(
                vote.getId(),
                poll != null ? poll.getId() : null,
                poll != null ? poll.getQuestion() : null,
                option != null ? option.getId() : null,
                option != null ? option.getText() : null,
                vote.getVotedAt());
    }
}
