package com.example.coursewebsite.dto.websocket;

import java.util.List;

public record PollUpdateMessage(Long pollId, long totalVotes, List<OptionVoteCount> options) {

    public record OptionVoteCount(Long optionId, String text, long votes, double percentage) {
    }
}
