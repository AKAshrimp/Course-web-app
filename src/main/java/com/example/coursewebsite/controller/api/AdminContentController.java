package com.example.coursewebsite.controller.api;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.coursewebsite.dto.admin.AdminDashboardResponse;
import com.example.coursewebsite.dto.admin.AdminLectureRequest;
import com.example.coursewebsite.dto.admin.AdminPollRequest;
import com.example.coursewebsite.dto.api.LectureDto;
import com.example.coursewebsite.dto.api.PollDto;
import com.example.coursewebsite.dto.api.PollOptionDto;
import com.example.coursewebsite.model.Lecture;
import com.example.coursewebsite.model.Poll;
import com.example.coursewebsite.model.PollOption;
import com.example.coursewebsite.model.Vote;
import com.example.coursewebsite.service.LectureService;
import com.example.coursewebsite.service.PollService;
import com.example.coursewebsite.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/teacher/content")
@PreAuthorize("hasRole('TEACHER')")
public class AdminContentController {

    private final UserService userService;
    private final LectureService lectureService;
    private final PollService pollService;

    public AdminContentController(UserService userService, LectureService lectureService, PollService pollService) {
        this.userService = userService;
        this.lectureService = lectureService;
        this.pollService = pollService;
    }

    @GetMapping("/dashboard")
    public AdminDashboardResponse getDashboard() {
        List<LectureDto> lectures = lectureService.getAllLectures().stream()
                .map(this::toLectureDto)
                .toList();
        List<PollDto> polls = pollService.getAllPolls().stream()
                .map(this::toPollDto)
                .toList();

        return new AdminDashboardResponse(
                userService.getAllUsers().size(),
                lectures.size(),
                polls.size(),
                lectures,
                polls);
    }

    @PostMapping("/lectures")
    public LectureDto createLecture(@Valid @RequestBody AdminLectureRequest request) {
        Lecture lecture = new Lecture();
        lecture.setTitle(request.title());
        lecture.setDescription(request.description());

        return toLectureDto(lectureService.saveLecture(lecture));
    }

    @DeleteMapping("/lectures/{id}")
    public void deleteLecture(@PathVariable Long id) {
        lectureService.deleteLecture(id);
    }

    @PostMapping("/polls")
    public PollDto createPoll(@Valid @RequestBody AdminPollRequest request) {
        Poll poll = new Poll(request.question());
        if (request.options() != null) {
            request.options().stream()
                    .map(String::trim)
                    .filter(option -> !option.isEmpty())
                    .forEach(option -> poll.addOption(new PollOption(option, poll)));
        }

        return toPollDto(pollService.createPoll(poll));
    }

    @DeleteMapping("/polls/{id}")
    public void deletePoll(@PathVariable Long id) {
        pollService.deletePoll(id);
    }

    private LectureDto toLectureDto(Lecture lecture) {
        return new LectureDto(lecture.getId(), lecture.getTitle(), lecture.getDescription(), lecture.getCreatedAt());
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
