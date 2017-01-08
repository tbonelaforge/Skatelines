package com.tford.skatelines;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tford on 12/23/16.
 */

public class SaveSessionLoader extends AsyncTaskLoader<SessionList> {
    private Integer lineId;
    private String date;
    private SkatelinesDbHelper dbHelper;
    private List<Line> lines;

    public SaveSessionLoader(Context context, Integer lineId, String date) {
        super(context);
        this.lineId = lineId;
        this.date = date;
        dbHelper = new SkatelinesDbHelper(context);
    }

    @Override
    public SessionList loadInBackground() {
        List<Session> sessions;
        List<Line> lines = getLines();
        SessionList sessionList;
        if (lineId == null || date == null) {
            sessions = querySessions();
            sessionList = new SessionList(sessions, lines);
            return sessionList;
        }
        if (!LineService.isValidLineId(dbHelper, lineId)) {
            System.out.printf("The line id %d is not valid. %n", lineId);
            return null;
        }
        Date parsedDate = tryToParseDate(date);
        if (parsedDate == null) {
            System.out.printf("Inside SaveSessionLoader.loadInBackground, the date %s is not valid. %n", date);
            return null;
        }
        insertSession(lineId, date);
        sessions = querySessions();
        return new SessionList(sessions, lines);
    }

    @Override
    public void deliverResult(SessionList data) {
        if (isReset()) { // This load is no longer valid, ignore result.
            return;
        }
        if (isStarted()) {
            super.deliverResult(data);
        }
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        onStopLoading();
    }

    @Override
    public void onCanceled(SessionList data) {
        super.onCanceled(data);
        lines = null;
    }

    private void insertSession(int lineId, String date) {
        String[] values = {
                String.valueOf(lineId),
                String.valueOf(date)
        };
        String insertStatement = "INSERT INTO session(line_id, date) VALUES (?, ?)";
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(insertStatement, values);
    }

    private Date tryToParseDate(String date) {
        try {
            Date parsedDate = Session.simpleDateFormat.parse(date);
            return parsedDate;
        } catch (ParseException e) {
            System.out.printf("Date input %s caused parse error: %s %n", date, e.toString());
            return null;
        }
    }

    private List<Session> querySessions() {
        Map<Integer, Line> linesById = getLinesById();
        List<Session> sessions = new ArrayList<Session>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM session order by date desc;",
                new String[]{}
        );
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                int lineId = cursor.getInt(cursor.getColumnIndexOrThrow("line_id"));
                Date date = tryToParseDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                Session session = new Session(id, lineId, date);
                session.setLine(linesById.get(session.getLineId()));
                sessions.add(session);
            } while (cursor.moveToNext());
        }
        return sessions;
    }

    private List<Line> getLines() {
        if (lines == null) {
            lines = LineService.getAllLines(dbHelper);
        }
        return lines;
    }

    private Map<Integer, Line> getLinesById() {
        Map<Integer, Line> linesById = new HashMap<Integer, Line>();
        for (Line line : getLines()) {
            linesById.put(Integer.valueOf(line.getId()), line);
        }
        return linesById;
    }
}
