package com.tford.skatelines;

import com.tford.skatelines.model.Obstacle;
import com.tford.skatelines.model.Skill;

import java.util.List;

/**
 * Created by tford on 1/22/17.
 */

public class LineEditorData {
    private Long lineId;
    private List<Skill> skills;
    private List<Obstacle> obstacles;

    LineEditorData(Long lineId, List<Skill> skills, List<Obstacle> obstacles) {
        this.lineId = lineId;
        this.skills = skills;
        this.obstacles = obstacles;
    }

    LineEditorData(List<Skill> skills, List<Obstacle> obstacles) {
        //this.skills = skills;
        //this.obstacles = obstacles;
        this(null, skills, obstacles);
    }

    LineEditorData() {
        this(null, null);
    }

    public void setLineId(Long lineId) {
        this.lineId = lineId;
    }

    public Long getLineId() {
        return lineId;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public List<Obstacle> getObstacles() {
        return obstacles;
    }
}
