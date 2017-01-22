package com.tford.skatelines;

import com.tford.skatelines.model.Line;
import com.tford.skatelines.model.Session;

import java.util.List;

/**
 * Created by tford on 12/28/16.
 */

public class SessionList {
    private List<Session> sessions;
    private List<Line> lines;

    public SessionList(List<Session> sessions, List<Line> lines) {
        this.sessions = sessions;
        this.lines = lines;
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public List<Line> getLines() {
        return lines;
    }
}
