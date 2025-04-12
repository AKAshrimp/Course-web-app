package com.example.coursewebsite.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.coursewebsite.model.Comment;
import com.example.coursewebsite.model.Poll;
import com.example.coursewebsite.model.PollOption;
import com.example.coursewebsite.model.User;
import com.example.coursewebsite.model.Vote;
import com.example.coursewebsite.service.CommentService;
import com.example.coursewebsite.service.PollService;
import com.example.coursewebsite.service.UserService;

@Controller
public class PollController {
    
    private final PollService pollService;
    private final CommentService commentService;
    private final UserService userService;
    
    @Autowired
    public PollController(PollService pollService, CommentService commentService, UserService userService) {
        this.pollService = pollService;
        this.commentService = commentService;
        this.userService = userService;
    }
    
    @GetMapping("/poll/{id}")
    public String viewPoll(@PathVariable Long id, Model model) {
        // 检查用户是否已登录
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName().equals("anonymousUser")) {
            return "loginRequired"; // 未登录用户重定向到登录提示页面
        }
        
        Optional<Poll> optionalPoll = pollService.getPollById(id);
        if (!optionalPoll.isPresent()) {
            return "redirect:/index";
        }
        
        Poll poll = optionalPoll.get();
        List<Comment> comments = commentService.getPollComments(id);
        
        // 获取当前用户信息
        if (auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            String username = auth.getName();
            Optional<User> optionalUser = userService.getUserByUsername(username);
            
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                Optional<Vote> userVote = pollService.getUserVoteOnPoll(user.getId(), id);
                model.addAttribute("userVote", userVote.orElse(null));
            }
        }
        
        model.addAttribute("poll", poll);
        model.addAttribute("comments", comments);
        model.addAttribute("newComment", "");
        
        return "poll/view";
    }
    
    @PostMapping("/poll/{pollId}/vote")
    public String votePoll(@PathVariable Long pollId, @RequestParam Long optionId, Model model, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        Optional<User> optionalUser = userService.getUserByUsername(username);
        if (!optionalUser.isPresent()) {
            return "redirect:/login";
        }
        
        User user = optionalUser.get();
        
        // 检查用户是否已经投过票
        Optional<Vote> existingVote = pollService.getUserVoteOnPoll(user.getId(), pollId);
        boolean isUpdate = existingVote.isPresent();
        
        pollService.vote(pollId, optionId, user);
        
        if (isUpdate) {
            redirectAttributes.addFlashAttribute("voteMessage", "poll.already.voted");
        } else {
            redirectAttributes.addFlashAttribute("voteMessage", "poll.voted.success");
        }
        redirectAttributes.addFlashAttribute("showVoteAlert", true);
        
        return "redirect:/poll/" + pollId;
    }
    
    @PostMapping("/poll/{id}/comment")
    public String addComment(@PathVariable Long id, @RequestParam("content") String content) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        Optional<User> optionalUser = userService.getUserByUsername(username);
        if (!optionalUser.isPresent()) {
            return "redirect:/login";
        }
        
        User user = optionalUser.get();
        commentService.addPollComment(id, user, content);
        return "redirect:/poll/" + id;
    }
    
    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/admin/polls/add")
    public String addPollForm(Model model) {
        Poll poll = new Poll();
        // 添加默认的4个空选项
        for (int i = 0; i < 4; i++) {
            poll.addOption(new PollOption());
        }
        model.addAttribute("poll", poll);
        return "admin/addPoll";
    }
    
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/admin/polls/add")
    public String addPoll(@ModelAttribute Poll poll, @RequestParam("optionText") List<String> optionTexts, RedirectAttributes redirectAttributes) {
        // 清除空选项
        poll.getOptions().clear();
        
        // 添加非空选项
        for (String text : optionTexts) {
            if (text != null && !text.trim().isEmpty()) {
                poll.addOption(new PollOption(text.trim(), poll));
            }
        }
        
        // 如果没有选项，添加默认选项
        if (poll.getOptions().isEmpty()) {
            poll.addDefaultOptions();
        }
        
        pollService.createPoll(poll);
        redirectAttributes.addFlashAttribute("successMessage", "poll.add.success");
        return "redirect:/admin/dashboard";
    }
    
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/admin/polls/{id}/delete")
    public String deletePoll(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Poll> optionalPoll = pollService.getPollById(id);
        if (optionalPoll.isPresent()) {
            pollService.deletePoll(id);
            redirectAttributes.addFlashAttribute("successMessage", "poll.delete.success");
        }
        return "redirect:/admin/dashboard";
    }
    
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/admin/poll/comment/{id}/delete")
    public String deleteComment(@PathVariable Long id) {
        Optional<Comment> optionalComment = commentService.getCommentById(id);
        if (!optionalComment.isPresent()) {
            return "redirect:/index";
        }
        
        Long pollId = optionalComment.get().getPoll().getId();
        commentService.deleteComment(id);
        return "redirect:/poll/" + pollId;
    }
    
    @GetMapping("/user/votes")
    public String viewUserVotes(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        Optional<User> optionalUser = userService.getUserByUsername(username);
        if (!optionalUser.isPresent()) {
            return "redirect:/login";
        }
        
        User user = optionalUser.get();
        List<Vote> votes = pollService.getUserVotes(user.getId());
        
        model.addAttribute("votes", votes);
        return "user/votes";
    }
    
    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/admin/polls/edit/{id}")
    public String editPollForm(@PathVariable Long id, Model model) {
        Optional<Poll> optionalPoll = pollService.getPollById(id);
        if (!optionalPoll.isPresent()) {
            return "redirect:/admin/dashboard";
        }
        
        Poll poll = optionalPoll.get();
        model.addAttribute("poll", poll);
        return "admin/editPoll";
    }
    
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/admin/polls/edit/{id}")
    public String updatePoll(@PathVariable Long id, @ModelAttribute Poll poll, 
                            @RequestParam(value = "optionText", required = false) List<String> optionTexts,
                            @RequestParam(value = "optionId", required = false) List<Long> optionIds,
                            @RequestParam(value = "newOptionText", required = false) List<String> newOptionTexts,
                            RedirectAttributes redirectAttributes) {
        Optional<Poll> optionalPoll = pollService.getPollById(id);
        if (!optionalPoll.isPresent()) {
            return "redirect:/admin/dashboard";
        }
        
        Poll existingPoll = optionalPoll.get();
        existingPoll.setQuestion(poll.getQuestion());
        
        // 更新现有选项
        if (optionTexts != null && optionIds != null && optionTexts.size() == optionIds.size()) {
            for (int i = 0; i < optionIds.size(); i++) {
                Long optionId = optionIds.get(i);
                String text = optionTexts.get(i);
                
                if (text != null && !text.trim().isEmpty()) {
                    pollService.updatePollOption(optionId, text.trim());
                }
            }
        }
        
        // 添加新选项
        if (newOptionTexts != null) {
            for (String text : newOptionTexts) {
                if (text != null && !text.trim().isEmpty()) {
                    existingPoll.addOption(new PollOption(text.trim(), existingPoll));
                }
            }
        }
        
        pollService.updatePoll(existingPoll);
        redirectAttributes.addFlashAttribute("successMessage", "poll.update.success");
        return "redirect:/admin/dashboard";
    }
    
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/admin/polls/options/delete/{id}")
    public String deletePollOption(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<PollOption> optionalOption = pollService.getPollOptionById(id);
        if (!optionalOption.isPresent()) {
            return "redirect:/admin/dashboard";
        }
        
        PollOption option = optionalOption.get();
        pollService.deletePollOption(id);
        redirectAttributes.addFlashAttribute("successMessage", "poll.option.delete.success");
        return "redirect:/admin/dashboard";
    }
    
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/admin/polls/{pollId}/options/add")
    public String addPollOption(@PathVariable Long pollId, @RequestParam("text") String text, RedirectAttributes redirectAttributes) {
        Optional<Poll> optionalPoll = pollService.getPollById(pollId);
        if (!optionalPoll.isPresent()) {
            return "redirect:/admin/dashboard";
        }
        
        Poll poll = optionalPoll.get();
        if (text != null && !text.trim().isEmpty()) {
            poll.addOption(new PollOption(text.trim(), poll));
            pollService.updatePoll(poll);
            redirectAttributes.addFlashAttribute("successMessage", "poll.option.add.success");
        }
        
        return "redirect:/admin/dashboard";
    }
} 