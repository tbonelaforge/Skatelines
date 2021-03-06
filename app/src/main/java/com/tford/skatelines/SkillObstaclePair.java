package com.tford.skatelines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tford on 11/28/16.
 */

public class SkillObstaclePair {
    private long skillId;
    private long obstacleId;

    private static Pattern orderedPairPattern = Pattern.compile("^\\((\\d+),(\\d+)\\)$");

    public SkillObstaclePair(long skillId, long obstacleId) {
        this.skillId = skillId;
        this.obstacleId = obstacleId;
    }

    public long getSkillId() {
        return skillId;
    }

    public long getObstacleId() {
        return obstacleId;
    }

    public String toString() {
        return String.format("(%d, %d)", skillId, obstacleId);
    }

    public static boolean isValidSyntax(String input) {
        return getMatcher(input).find();
    }

    private static Matcher getMatcher(String input) {
        System.out.printf("Inside SkillObstaclePair.getMatcher, got called with input: %s %n", input);
        input.replaceAll("\\s", "");
        System.out.printf("Inside SkillObstaclePair.getMatcher, after stripping out all whitespace, the input looks like: %s %n", input);
        Matcher matcher = orderedPairPattern.matcher(input);
        return matcher;
    }

    public static SkillObstaclePair parse(String input) {
        Matcher matcher = getMatcher(input);
        if (!matcher.find()) {
            return null;
        }
        long skillId;
        long obstacleId;
        try {
            skillId = Long.valueOf(matcher.group(1));
        } catch (NumberFormatException e) {
            System.out.printf("The string %s is not a valid number (expected skill id) %n", matcher.group(1));
            return null;
        }
        try {
            obstacleId = Long.valueOf(matcher.group(2));
        } catch (NumberFormatException e) {
            System.out.printf("The string %s is not a valid number (expected obstacle id) %n", matcher.group(2));
            return null;
        }
        SkillObstaclePair parsed = new SkillObstaclePair(skillId, obstacleId);
        return parsed;
    }
}
