package com.example.coursewebsite.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "poll_options")
public class PollOption {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    @NotBlank(message = "Poll option text is required")
    @Size(max = 150, message = "Poll option text must not exceed 150 characters")
    private String text;
    
    @ManyToOne
    @JoinColumn(name = "poll_id")
    private Poll poll;
    
    @OneToMany(mappedBy = "pollOption", cascade = CascadeType.ALL)
    private List<Vote> votes = new ArrayList<>();
    
    public PollOption() {
    }
    
    public PollOption(String text, Poll poll) {
        this.text = text;
        this.poll = poll;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public Poll getPoll() {
        return poll;
    }
    
    public void setPoll(Poll poll) {
        this.poll = poll;
    }
    
    public List<Vote> getVotes() {
        return votes;
    }
    
    public void setVotes(List<Vote> votes) {
        this.votes = votes;
    }
    
    public int getVoteCount() {
        return votes.size();
    }
} 