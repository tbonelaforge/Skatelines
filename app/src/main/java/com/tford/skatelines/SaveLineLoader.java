package com.tford.skatelines;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tford on 12/2/16.
 */

public class SaveLineLoader extends AsyncTaskLoader<Boolean> {
    private Integer lineId;
    private ArrayList<SkillObstaclePair> skillObstaclePairs;
    private SkatelinesDbHelper dbHelper;

    public SaveLineLoader(Context context, Integer lineId, ArrayList<SkillObstaclePair> skillObstaclePairs) {
        super(context);
        this.lineId = lineId;
        this.skillObstaclePairs = skillObstaclePairs;
        dbHelper = new SkatelinesDbHelper(context);
    }

    @Override
    public Boolean loadInBackground() {
        if (lineId == null || skillObstaclePairs == null) {
            return null;
        }
        if (!isValidLineId(lineId)) {
            System.out.printf("The line id %d is not valid. %n", lineId);
            return Boolean.FALSE;
        }
        if (!isValidSkillObstacleSequence(skillObstaclePairs)) {
            System.out.printf("The skill/obstacle sequence is not valid: %s %n", stringifySkillObstaclePairs());
            return Boolean.FALSE;
        }

        // We have a valid input.
        deleteLineSkills(lineId);
        for (int i = 0; i < skillObstaclePairs.size(); i++) {
            System.out.printf("Inside SaveLineLoader.loadInBackground, considering skillObstaclePair %d %n", i);
            SkillObstaclePair skillObstaclePair = skillObstaclePairs.get(i);
            insertLineSkill(lineId, skillObstaclePair.getSkillId(), skillObstaclePair.getObstacleId(), i);
        }
        try {
            //Thread.currentThread().sleep(2000);
        } catch (InterruptedException e) {
            System.out.println("Inside SaveLineTask, there was a problem sleeping the 'doInBackground' thread!");
        }
        return Boolean.TRUE;
    }

    @Override
    public void deliverResult(Boolean data) {
        System.out.println("Inside SaveLineLoader.deliverResult, got called...");
        if (isReset()) { // This load is no longer valid, ignore result.
            return;
        }
        if (isStarted()) {
            super.deliverResult(data);
        }
    }

    @Override
    protected void onStartLoading() {
        System.out.println("Inside SaveLineLoader.onStartLoading, got called, about to force load......");
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        System.out.println("Inside SaveLineLoader.onStopLoading, got called...");
        cancelLoad();
    }

    @Override
    protected void onReset() {
        System.out.println("Inside SaveLineLoader.onReset, got called...");
        onStopLoading();
    }

    @Override
    public void onCanceled(Boolean data) {
        System.out.println("Inside SaveLineLoader.onCanceled, got called...");
        super.onCanceled(data);
    }

    private boolean isValidLineId(int lineId) {
        System.out.println("Inside SaveLineLoader.isValidLineId, got called...");
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM line;", new String[]{});
        boolean hasFirst = cursor.moveToFirst();
        if (!hasFirst) {
            return false;
        }
        String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
        System.out.printf("Realized we are trying to edit the line %d: %s %n", lineId, description);
        return true;
    }

    private boolean isValidSkillObstacleSequence(List<SkillObstaclePair> skillObstaclePairs) {
        System.out.println("Inside SaveLineLoader.isValidSkillObstacleSequence, got called...");
        for (int i = 0; i < skillObstaclePairs.size(); i++) {
            SkillObstaclePair skillObstaclePair = skillObstaclePairs.get(i);
            int skillId = skillObstaclePair.getSkillId();
            Cursor cursor = querySkillById(skillId);
            if (cursor == null) {
                return false;
            }
            String skillDescription = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            System.out.printf("Realized we are specifying skill %d: %s at index %d %n", skillId, skillDescription, i);
            int obstacleId = skillObstaclePair.getObstacleId();
            cursor = queryTransitionById(obstacleId);
            if (cursor == null) {
                return false;
            }
            String obstacleDescription = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            System.out.printf("Realized we are specifying obstacle %d: %s at index %d %n", obstacleId, obstacleDescription, i);
        }
        return true;
    }

    private Cursor querySkillById(int skillId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM skill where id = ?", new String[]{String.valueOf(skillId)});
        boolean hasFirst = cursor.moveToFirst();
        if (!hasFirst) {
            return null;
        }
        return cursor;
    }

    private Cursor queryTransitionById(int transitionId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM transition where id = ?", new String[]{String.valueOf(transitionId)});
        boolean hasFirst = cursor.moveToFirst();
        if (!hasFirst) {
            return null;
        }
        return cursor;
    }

    private String stringifySkillObstaclePairs() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < skillObstaclePairs.size(); i++) {
            builder.append(skillObstaclePairs.get(i).toString());
            if (i < skillObstaclePairs.size() - 1) {
                builder.append(',');
            }
        }
        String printable = builder.toString();
        return printable;
    }

    private void deleteLineSkills(int lineId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM line_skill where line_id = ?", new String[]{String.valueOf(lineId)});
    }

    private void insertLineSkill(int lineId, int skillId, int obstacleId, int pos) {
        String[] values = {
                String.valueOf(lineId),
                String.valueOf(skillId),
                String.valueOf(obstacleId),
                String.valueOf(pos)
        };
        String insertStatement = "INSERT INTO line_skill (line_id, skill_id, obstacle_id, pos) VALUES (?, ?, ?, ?)";
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(insertStatement, values);
    }
}
