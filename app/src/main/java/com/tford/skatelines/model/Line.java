package com.tford.skatelines.model;

/**
 * Created by tford on 12/28/16.
 */

public class Line {
    private long id;
    private String description;

    public Line(long id, String description) {
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
