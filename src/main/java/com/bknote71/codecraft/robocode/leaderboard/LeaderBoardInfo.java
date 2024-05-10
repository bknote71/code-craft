package com.bknote71.codecraft.robocode.leaderboard;


public class LeaderBoardInfo {
    private String username;
    private int score;

    public LeaderBoardInfo() {
    }

    public LeaderBoardInfo(String username, int score) {
        this.username = username;
        this.score = score;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "LeaderBoardInfo{" +
                "username='" + username + '\'' +
                ", score=" + score +
                '}';
    }
}
