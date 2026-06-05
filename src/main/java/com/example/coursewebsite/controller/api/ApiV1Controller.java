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

import com.example.coursewebsite.dto.api.LectureDto;
import com.example.coursewebsite.dto.api.PollDto;
import com.example.coursewebsite.dto.api.PollOptionDto;
import com.example.coursewebsite.dto.api.VoteRequestDto;
import com.example.coursewebsite.dto.api.VoteResponseDto;
import com.example.coursewebsite.model.Poll;
import com.example.coursewebsite.model.PollOption;
import com.example.coursewebsite.model.User;
import com.example.coursewebsite.model.Vote;
import com.example.coursewebsite.service.LectureService;
import com.example.coursewebsite.service.PollService;
import com.example.coursewebsite.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class ApiV1Controller {

    private final LectureService lectureService;
    private final PollService pollService;
    private final UserService userService;

    public ApiV1Controller(LectureService lectureService, PollService pollService, UserService userService) {
        this.lectureService = lectureService;
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
}
