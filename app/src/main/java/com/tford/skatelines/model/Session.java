package com.tford.skatelines.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by tford on 12/28/16.
 */

public class Session {
    private long id;
    private long lineId;
    private Date date;
    private Line line;

    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public Session(long id, long lineId, Date date) {
        this.id = id;
        this.lineId = lineId;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public long getLineId() {
        return lineId;
    }

    public Date getDate() {
        return date;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public Line getLine() {
        return line;
    }

    @Override
    public String toString() {
        return String.format("(id: %d, lineId: %d, date: %s)", id, lineId, date);
    }
}
