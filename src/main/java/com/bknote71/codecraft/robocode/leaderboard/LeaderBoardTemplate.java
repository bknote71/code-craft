package com.bknote71.codecraft.robocode.leaderboard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class LeaderBoardTemplate {
    private static RedisTemplate<String, String> redisTemplate;
    private static ZSetOperations<String, String> ops;
    private static final String prefix = "battle";


    @Autowired
    public LeaderBoardTemplate(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.ops = redisTemplate.opsForZSet();
    }

    public static void incrementLeaderboardScore(int battleId, String username, double score) {
        ops.incrementScore(prefix + ":" + battleId, username, score); // 실시간 점수
    }

    public static void resetLeaderboardScore(int battleId, String username) {
        ops.add(prefix + ":" + battleId, username, 0);
    }

    public static void removeFromLeaderboard(int battleId, String username) {
        ops.remove(prefix + ":" + battleId, username);
    }

    public static void updateTodayLeaderBoard(String username, double score) {
        // 죽었을 때나 혹은 종료되었을 때 가장 높은 점수로
        Double orgScore = ops.score(prefix + ":" + LocalDate.now(), username);
        if (orgScore == null || orgScore < score)
            ops.incrementScore(prefix + ":" + LocalDate.now(), username, score);
    }

    public static List<LeaderBoardInfo> getLeaderBoard(int battleId) {
        Set<ZSetOperations.TypedTuple<String>> typedTuples = ops.reverseRangeWithScores(prefix + ":" + battleId, 0, -1);
        List<LeaderBoardInfo> leaderBoardInfos = typedTuples.stream()
                .map(typedTuple -> {
                    String username = typedTuple.getValue();
                    Double score = typedTuple.getScore();
                    return new LeaderBoardInfo(username, score.intValue());
                })
                .collect(Collectors.toList());
        System.out.println("leaderboard? " + Arrays.toString(leaderBoardInfos.toArray()));
        return leaderBoardInfos;
    }

    public static void union() {
        // 모든 룸들의 today 플레이어들을 통합 <<
        String pattern = prefix + ":*-*-*";
        String destKey = "total_ranking";
        Set<String> keys = scanKeysByPattern(pattern);
        ops.unionAndStore(destKey, keys, destKey);
    }

    private static Set<String> scanKeysByPattern(String pattern) {
        Set<String> keys = new HashSet<>();
        ScanOptions options = ScanOptions.scanOptions().match(pattern).build();
        Cursor<byte[]> cursor = redisTemplate.getConnectionFactory().getConnection().scan(options);

        while (cursor.hasNext()) {
            keys.add(new String(cursor.next()));
        }
        cursor.close();

        return keys;
    }

    public static List<LeaderBoardInfo> getTotalRanking() {
        Set<ZSetOperations.TypedTuple<String>> typedTuples = ops.reverseRangeWithScores("total_ranking", 0, -1);
        return typedTuples.stream()
                .map(typedTuple -> {
                    String username = typedTuple.getValue();
                    Double score = typedTuple.getScore();
                    return new LeaderBoardInfo(username, score.intValue());
                })
                .collect(Collectors.toList());
    }

    public static List<LeaderBoardInfo> getTodayRanking() {
        String pattern = prefix + ":" + LocalDate.now();
        Set<ZSetOperations.TypedTuple<String>> typedTuples = ops.reverseRangeWithScores(pattern, 0, -1);
        return typedTuples.stream()
                .map(typedTuple -> {
                    String username = typedTuple.getValue();
                    Double score = typedTuple.getScore();
                    return new LeaderBoardInfo(username, score.intValue());
                })
                .collect(Collectors.toList());
    }

    public static void unionTotal() {

    }
}