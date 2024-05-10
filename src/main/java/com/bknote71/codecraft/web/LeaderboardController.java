package com.bknote71.codecraft.web;

import com.bknote71.codecraft.robocode.leaderboard.LeaderBoardInfo;
import com.bknote71.codecraft.robocode.leaderboard.LeaderBoardTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class LeaderboardController {
    @GetMapping("/get/leaderboard")
    public List<LeaderBoardInfo> getLeaderBoard(int battleId) {
        return LeaderBoardTemplate.getLeaderBoard(battleId);
    }

    @GetMapping("/get/today_ranking")
    public List<LeaderBoardInfo> getTodayRanking() {
        return LeaderBoardTemplate.getTodayRanking();
    }

    @GetMapping("/get/total_ranking")
    public List<LeaderBoardInfo> getTotalRanking() {
        LeaderBoardTemplate.union();
        return LeaderBoardTemplate.getTotalRanking();
    }
}
