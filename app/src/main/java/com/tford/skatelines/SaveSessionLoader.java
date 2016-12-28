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

public class SaveSessionLoader extends AsyncTaskLoader<List<Session>> {
    private Integer lineId;
    private String date;
    private SkatelinesDbHelper dbHelper;

    public SaveSessionLoader(Context context, Integer lineId, String date) {
        super(context);
        this.lineId = lineId;
        this.date = date;
        dbHelper = new SkatelinesDbHelper(context);
    }

    @Override
    public List<Session> loadInBackground() {
        List<Session> results;
        if (lineId == null || date == null) {
            results = querySessions();
            return results;
        }
        System.out.println("Inside SaveSessionLoader.loadInBackground, got called.");
        System.out.println("Here is where we would actually save the new session record");
        if (!LineService.isValidLineId(dbHelper, lineId)) {
            System.out.printf("The line id %d is not valid. %n", lineId);
            return null;
        }
        Date parsedDate = tryToParseDate(date);
        if (parsedDate == null) {
            System.out.printf("Inside SaveSessionLoader.loadInBackground, the date %s is not valid. %n", date);
            return null;
        } else {
            System.out.printf("Inside SaveSessionLoader.loadInBackground, determined the date %s IS valid -- about to insert a session record. %n", date);
        }
        insertSession(lineId, date);
        results = querySessions();
        return results;
    }

    @Override
    public void deliverResult(List<Session> data) {
        if (data != null) {
            System.out.printf("Inside SaveSessionLoader.deliverResult, got called with data %s", data.toString());
        } else {
            System.out.printf("INside SaveSessionLoader.deliverResult, got called with null data! %n");
        }
        if (isReset()) { // This load is no longer valid, ignore result.
            return;
        }
        if (isStarted()) {
            super.deliverResult(data);
        }
    }

    @Override
    protected void onStartLoading() {
        System.out.println("Inside SaveSessionLoader.onStartLoading, got called, about to force load......");
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        System.out.println("Inside SaveSessionLoader.onStopLoading, got called, about to cancel load...");
        cancelLoad();
    }

    @Override
    protected void onReset() {
        System.out.println("Inside SaveSessionLoader.onReset, got called, about to forward to onStopLoading...");
        onStopLoading();
    }

    @Override
    public void onCanceled(List<Session> data) {
        System.out.println("Inside SaveSessionLoader.onCanceled, got called...");
        super.onCanceled(data);
    }

    private void insertSession(int lineId, String date) {
        System.out.printf("Inside SaveSessionLoader, got called with lineId: %d and date %s", lineId, date);
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
        Map<Integer, Line> linesById = getAllLinesById();
        List<Session> sessions = new ArrayList<Session>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM session;",
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

    private Map<Integer, Line> getAllLinesById() {
        Map<Integer, Line> linesById = new HashMap<Integer, Line>();
        for (Line line : LineService.getAllLines(dbHelper)) {
            linesById.put(Integer.valueOf(line.getId()), line);
        }
        return linesById;
    }
}
