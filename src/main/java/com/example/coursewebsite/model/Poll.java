package com.example.coursewebsite.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "polls")
public class Poll {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String question;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PollOption> options = new ArrayList<>();
    
    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vote> votes = new ArrayList<>();
    
    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();
    
    public Poll() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Poll(String question) {
        this.question = question;
        this.createdAt = LocalDateTime.now();
    }
    
    public void addDefaultOptions() {
        for (int i = 1; i <= 4; i++) {
            PollOption option = new PollOption("选项 " + i, this);
            this.options.add(option);
        }
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getQuestion() {
        return question;
    }
    
    public void setQuestion(String question) {
        this.question = question;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public List<PollOption> getOptions() {
        return options;
    }
    
    public void setOptions(List<PollOption> options) {
        this.options = options;
    }
    
    public void addOption(PollOption option) {
        options.add(option);
        option.setPoll(this);
    }
    
    public void removeOption(PollOption option) {
        options.remove(option);
        option.setPoll(null);
    }
    
    public List<Vote> getVotes() {
        return votes;
    }
    
    public void setVotes(List<Vote> votes) {
        this.votes = votes;
    }
    
    public void addVote(Vote vote) {
        votes.add(vote);
        vote.setPoll(this);
    }
    
    public int getVoteCount(Long optionId) {
        return (int) votes.stream()
                .filter(v -> v.getPollOption().getId().equals(optionId))
                .count();
    }
    
    public List<Comment> getComments() {
        return comments;
    }
    
    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
    
    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setPoll(this);
    }
    
    public void removeComment(Comment comment) {
        comments.remove(comment);
        comment.setPoll(null);
    }
} 