package com.example.coursewebsite.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.example.coursewebsite.model.Poll;
import com.example.coursewebsite.model.PollOption;
import com.example.coursewebsite.model.User;
import com.example.coursewebsite.model.Vote;
import com.example.coursewebsite.repository.PollOptionRepository;
import com.example.coursewebsite.repository.PollRepository;
import com.example.coursewebsite.repository.UserRepository;
import com.example.coursewebsite.repository.VoteRepository;

@DataJpaTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Import(PollService.class)
class PollServiceDataJpaTest {

    @Autowired
    private PollService pollService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private PollOptionRepository pollOptionRepository;

    @Autowired
    private VoteRepository voteRepository;

    @MockBean
    private SimpMessagingTemplate messagingTemplate;

    @Test
    void voteCreatesNewVoteForUserAndOption() {
        User user = saveUser("student");
        Poll poll = savePollWithOptions("Which topic?", "Java", "Spring");
        PollOption option = poll.getOptions().get(0);

        Vote vote = pollService.vote(poll.getId(), option.getId(), user);

        List<Vote> votes = voteRepository.findByUserId(user.getId());
        assertThat(votes).hasSize(1);
        assertThat(vote.getPoll().getId()).isEqualTo(poll.getId());
        assertThat(vote.getPollOption().getId()).isEqualTo(option.getId());
    }

    @Test
    void voteExistingVoteUpdatesOptionInsteadOfCreatingDuplicate() {
        User user = saveUser("student");
        Poll poll = savePollWithOptions("Which topic?", "Java", "Spring");
        PollOption firstOption = poll.getOptions().get(0);
        PollOption secondOption = poll.getOptions().get(1);

        Vote firstVote = pollService.vote(poll.getId(), firstOption.getId(), user);
        Vote updatedVote = pollService.vote(poll.getId(), secondOption.getId(), user);

        List<Vote> votes = voteRepository.findByUserId(user.getId());
        assertThat(votes).hasSize(1);
        assertThat(updatedVote.getId()).isEqualTo(firstVote.getId());
        assertThat(votes.get(0).getPollOption().getId()).isEqualTo(secondOption.getId());
    }

    @Test
    void voteMissingPollThrowsIllegalArgumentException() {
        User user = saveUser("student");

        assertThatThrownBy(() -> pollService.vote(999L, 1L, user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("投票不存在");
    }

    @Test
    void voteMissingOptionThrowsIllegalArgumentException() {
        User user = saveUser("student");
        Poll poll = pollRepository.save(new Poll("Which topic?"));

        assertThatThrownBy(() -> pollService.vote(poll.getId(), 999L, user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("选项不存在");
    }

    private User saveUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("password123");
        user.setFullName("Student One");
        user.setEmail(username + "@example.com");
        user.addRole("ROLE_STUDENT");
        return userRepository.save(user);
    }

    private Poll savePollWithOptions(String question, String firstOptionText, String secondOptionText) {
        Poll poll = new Poll(question);
        poll = pollRepository.save(poll);
        PollOption firstOption = pollOptionRepository.save(new PollOption(firstOptionText, poll));
        PollOption secondOption = pollOptionRepository.save(new PollOption(secondOptionText, poll));
        poll.setOptions(List.of(firstOption, secondOption));
        return poll;
    }
}
