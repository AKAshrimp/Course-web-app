package com.example.coursewebsite.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.coursewebsite.dto.websocket.PollUpdateMessage;
import com.example.coursewebsite.model.Poll;
import com.example.coursewebsite.model.PollOption;
import com.example.coursewebsite.model.User;
import com.example.coursewebsite.model.Vote;
import com.example.coursewebsite.repository.PollOptionRepository;
import com.example.coursewebsite.repository.PollRepository;
import com.example.coursewebsite.repository.VoteRepository;

@Service
public class PollService {
    
    private final PollRepository pollRepository;
    private final PollOptionRepository pollOptionRepository;
    private final VoteRepository voteRepository;
    private final SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    public PollService(PollRepository pollRepository, PollOptionRepository pollOptionRepository, VoteRepository voteRepository,
            SimpMessagingTemplate messagingTemplate) {
        this.pollRepository = pollRepository;
        this.pollOptionRepository = pollOptionRepository;
        this.voteRepository = voteRepository;
        this.messagingTemplate = messagingTemplate;
    }
    
    public List<Poll> getAllPolls() {
        return pollRepository.findAll();
    }
    
    public Optional<Poll> getPollById(Long id) {
        return pollRepository.findById(id);
    }
    
    @Transactional
    public Poll createPoll(Poll poll) {
        poll = pollRepository.save(poll);
        
        // 如果没有提供选项，添加默认选项
        if (poll.getOptions().isEmpty()) {
            poll.addDefaultOptions();
        }
        
        return pollRepository.save(poll);
    }
    
    public Poll updatePoll(Poll poll) {
        return pollRepository.save(poll);
    }
    
    public void deletePoll(Long id) {
        pollRepository.deleteById(id);
    }
    
    public List<PollOption> getPollOptions(Long pollId) {
        return pollOptionRepository.findByPollId(pollId);
    }
    
    @Transactional
    public Vote vote(Long pollId, Long optionId, User user) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new IllegalArgumentException("投票不存在"));
        
        PollOption option = pollOptionRepository.findById(optionId)
                .orElseThrow(() -> new IllegalArgumentException("选项不存在"));
        if (option.getPoll() != null && option.getPoll().getId() != null && !option.getPoll().getId().equals(pollId)) {
            throw new IllegalArgumentException("选项不属于该投票");
        }
        
        // 检查用户是否已经投票，如果已投票则更新
        Optional<Vote> existingVote = voteRepository.findByUserIdAndPollId(user.getId(), pollId);
        Vote savedVote;
        if (existingVote.isPresent()) {
            Vote vote = existingVote.get();
            vote.setPollOption(option);
            savedVote = voteRepository.save(vote);
        } else {
            // 创建新投票
            Vote vote = new Vote(user, poll, option);
            savedVote = voteRepository.save(vote);
        }
        
        broadcastPollUpdate(pollId);
        return savedVote;
    }
    
    public List<Vote> getVotesByPollId(Long pollId) {
        return voteRepository.findByPollId(pollId);
    }
    
    public Optional<Vote> getUserVoteOnPoll(Long userId, Long pollId) {
        return voteRepository.findByUserIdAndPollId(userId, pollId);
    }
    
    public List<Vote> getUserVotes(Long userId) {
        return voteRepository.findByUserId(userId);
    }
    
    public Optional<PollOption> getPollOptionById(Long id) {
        return pollOptionRepository.findById(id);
    }
    
    @Transactional
    public void updatePollOption(Long optionId, String text) {
        Optional<PollOption> optionalOption = pollOptionRepository.findById(optionId);
        if (optionalOption.isPresent()) {
            PollOption option = optionalOption.get();
            option.setText(text);
            pollOptionRepository.save(option);
        }
    }
    
    @Transactional
    public void deletePollOption(Long id) {
        Optional<PollOption> optionalOption = pollOptionRepository.findById(id);
        if (optionalOption.isPresent()) {
            PollOption option = optionalOption.get();
            Poll poll = option.getPoll();
            poll.removeOption(option);
            pollRepository.save(poll);
        }
    }

    private void broadcastPollUpdate(Long pollId) {
        List<PollOption> options = pollOptionRepository.findByPollId(pollId);
        List<Vote> votes = voteRepository.findByPollId(pollId);
        long totalVotes = votes.size();

        List<PollUpdateMessage.OptionVoteCount> optionCounts = options.stream()
                .map(option -> {
                    long count = votes.stream()
                            .filter(vote -> vote.getPollOption() != null
                                    && vote.getPollOption().getId() != null
                                    && vote.getPollOption().getId().equals(option.getId()))
                            .count();
                    double percentage = totalVotes == 0
                            ? 0
                            : Math.round(((double) count / totalVotes * 100.0) * 100.0) / 100.0;
                    return new PollUpdateMessage.OptionVoteCount(option.getId(), option.getText(), count, percentage);
                })
                .toList();

        messagingTemplate.convertAndSend("/topic/poll/" + pollId,
                new PollUpdateMessage(pollId, totalVotes, optionCounts));
    }
} 