package com.tford.skatelines.model;

/**
 * Created by tford on 1/22/17.
 */

public class Obstacle {
    private long id;
    private String description;

    public Obstacle(long id, String description) {
        this.id = id;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
}
