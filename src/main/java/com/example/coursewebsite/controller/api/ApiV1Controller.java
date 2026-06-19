package com.example.coursewebsite.controller.api;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.coursewebsite.dto.api.CommentDto;
import com.example.coursewebsite.dto.api.CommentRequestDto;
import com.example.coursewebsite.dto.api.LectureDetailDto;
import com.example.coursewebsite.dto.api.LectureDto;
import com.example.coursewebsite.dto.api.MaterialDto;
import com.example.coursewebsite.dto.api.PollDto;
import com.example.coursewebsite.dto.api.PollOptionDto;
import com.example.coursewebsite.dto.api.VoteRequestDto;
import com.example.coursewebsite.dto.api.VoteResponseDto;
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

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class ApiV1Controller {

    private final LectureService lectureService;
    private final CommentService commentService;
    private final PollService pollService;
    private final UserService userService;

    public ApiV1Controller(LectureService lectureService, CommentService commentService, PollService pollService,
            UserService userService) {
        this.lectureService = lectureService;
        this.commentService = commentService;
        this.pollService = pollService;
        this.userService = userService;
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

    private PollDto toPollDto(Poll poll) {
        List<Vote> votes = pollService.getVotesByPollId(poll.getId());
        List<PollOptionDto> options = poll.getOptions().stream()
                .map(option -> toPollOptionDto(option, votes))
                .toList();
        return new PollDto(poll.getId(), poll.getQuestion(), poll.getCreatedAt(), options);
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
}
