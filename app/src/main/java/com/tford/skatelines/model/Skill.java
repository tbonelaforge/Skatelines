package com.tford.skatelines.model;

/**
 * Created by tford on 1/22/17.
 */

public class Skill {
    private int id;
    private String description;

    public Skill(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
}
