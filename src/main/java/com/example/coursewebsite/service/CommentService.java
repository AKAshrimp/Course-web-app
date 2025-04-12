package com.example.coursewebsite.service;

import com.example.coursewebsite.model.Comment;
import com.example.coursewebsite.model.Lecture;
import com.example.coursewebsite.model.Poll;
import com.example.coursewebsite.model.User;
import com.example.coursewebsite.repository.CommentRepository;
import com.example.coursewebsite.repository.LectureRepository;
import com.example.coursewebsite.repository.PollRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    
    private final CommentRepository commentRepository;
    private final LectureRepository lectureRepository;
    private final PollRepository pollRepository;
    
    @Autowired
    public CommentService(CommentRepository commentRepository, LectureRepository lectureRepository, PollRepository pollRepository) {
        this.commentRepository = commentRepository;
        this.lectureRepository = lectureRepository;
        this.pollRepository = pollRepository;
    }
    
    public List<Comment> getLectureComments(Long lectureId) {
        return commentRepository.findByLectureId(lectureId);
    }
    
    public List<Comment> getPollComments(Long pollId) {
        return commentRepository.findByPollId(pollId);
    }
    
    public List<Comment> getUserComments(Long userId) {
        return commentRepository.findByUserId(userId);
    }
    
    public Optional<Comment> getCommentById(Long id) {
        return commentRepository.findById(id);
    }
    
    public Comment addLectureComment(Long lectureId, User user, String content) {
        Optional<Lecture> optionalLecture = lectureRepository.findById(lectureId);
        if (!optionalLecture.isPresent()) {
            throw new IllegalArgumentException("课程不存在");
        }
        
        Comment comment = new Comment(content, user);
        comment.setLecture(optionalLecture.get());
        return commentRepository.save(comment);
    }
    
    public Comment addPollComment(Long pollId, User user, String content) {
        Optional<Poll> optionalPoll = pollRepository.findById(pollId);
        if (!optionalPoll.isPresent()) {
            throw new IllegalArgumentException("投票不存在");
        }
        
        Comment comment = new Comment(content, user);
        comment.setPoll(optionalPoll.get());
        return commentRepository.save(comment);
    }
    
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }
} 