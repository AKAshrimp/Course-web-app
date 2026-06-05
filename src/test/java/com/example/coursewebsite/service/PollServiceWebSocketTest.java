package com.example.coursewebsite.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.example.coursewebsite.dto.websocket.PollUpdateMessage;
import com.example.coursewebsite.model.Poll;
import com.example.coursewebsite.model.PollOption;
import com.example.coursewebsite.model.User;
import com.example.coursewebsite.model.Vote;
import com.example.coursewebsite.repository.PollOptionRepository;
import com.example.coursewebsite.repository.PollRepository;
import com.example.coursewebsite.repository.VoteRepository;

@ExtendWith(MockitoExtension.class)
class PollServiceWebSocketTest {

    @Mock
    private PollRepository pollRepository;

    @Mock
    private PollOptionRepository pollOptionRepository;

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    private PollService pollService;

    @BeforeEach
    void setUp() {
        pollService = new PollService(pollRepository, pollOptionRepository, voteRepository, messagingTemplate);
    }

    @Test
    void voteBroadcastsPollUpdateToPollTopic() {
        User user = user();
        Poll poll = poll();
        PollOption java = option(10L, "Java", poll);
        PollOption spring = option(11L, "Spring", poll);
        Vote savedVote = new Vote(user, poll, java);
        when(pollRepository.findById(1L)).thenReturn(Optional.of(poll));
        when(pollOptionRepository.findById(10L)).thenReturn(Optional.of(java));
        when(voteRepository.findByUserIdAndPollId(5L, 1L)).thenReturn(Optional.empty());
        when(voteRepository.save(any(Vote.class))).thenReturn(savedVote);
        when(pollOptionRepository.findByPollId(1L)).thenReturn(List.of(java, spring));
        when(voteRepository.findByPollId(1L)).thenReturn(List.of(savedVote));

        pollService.vote(1L, 10L, user);

        ArgumentCaptor<PollUpdateMessage> messageCaptor = ArgumentCaptor.forClass(PollUpdateMessage.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/poll/1"), messageCaptor.capture());
        assertThat(messageCaptor.getValue().pollId()).isEqualTo(1L);
        assertThat(messageCaptor.getValue().totalVotes()).isEqualTo(1);
        assertThat(messageCaptor.getValue().options()).hasSize(2);
        assertThat(messageCaptor.getValue().options().get(0).votes()).isEqualTo(1);
        assertThat(messageCaptor.getValue().options().get(0).percentage()).isEqualTo(100.0);
    }

    @Test
    void missingPollDoesNotBroadcast() {
        when(pollRepository.findById(1L)).thenReturn(Optional.empty());

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> pollService.vote(1L, 10L, user()))
                .isInstanceOf(IllegalArgumentException.class);

        verify(messagingTemplate, never()).convertAndSend(any(String.class), any(Object.class));
    }

    private User user() {
        User user = new User();
        user.setId(5L);
        user.setUsername("student");
        return user;
    }

    private Poll poll() {
        Poll poll = new Poll("Which topic?");
        poll.setId(1L);
        return poll;
    }

    private PollOption option(Long id, String text, Poll poll) {
        PollOption option = new PollOption(text, poll);
        option.setId(id);
        return option;
    }
}
