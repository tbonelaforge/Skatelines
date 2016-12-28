package com.tford.skatelines;

/**
 * Created by tford on 12/28/16.
 */

public class Line {
    private int id;
    private String description;

    public Line(int id, String description) {
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
