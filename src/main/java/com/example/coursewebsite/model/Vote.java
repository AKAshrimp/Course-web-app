package com.example.coursewebsite.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "votes")
public class Vote {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "poll_id", nullable = false)
    private Poll poll;
    
    @ManyToOne
    @JoinColumn(name = "option_id", nullable = false)
    private PollOption pollOption;
    
    @Column(name = "voted_at", nullable = false)
    private LocalDateTime votedAt;
    
    public Vote() {
        this.votedAt = LocalDateTime.now();
    }
    
    public Vote(User user, Poll poll, PollOption pollOption) {
        this.user = user;
        this.poll = poll;
        this.pollOption = pollOption;
        this.votedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Poll getPoll() {
        return poll;
    }
    
    public void setPoll(Poll poll) {
        this.poll = poll;
    }
    
    public PollOption getPollOption() {
        return pollOption;
    }
    
    public void setPollOption(PollOption pollOption) {
        this.pollOption = pollOption;
    }
    
    public LocalDateTime getVotedAt() {
        return votedAt;
    }
    
    public void setVotedAt(LocalDateTime votedAt) {
        this.votedAt = votedAt;
    }
} 