package com.tford.skatelines;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tford.skatelines.model.Line;
import com.tford.skatelines.model.Obstacle;
import com.tford.skatelines.model.Skill;
import com.tford.skatelines.service.LineService;
import com.tford.skatelines.service.ObstacleService;
import com.tford.skatelines.service.SkillService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tford on 12/2/16.
 */

public class SaveLineLoader extends AsyncTaskLoader<LineEditorData> {
    private Long lineId;
    private String lineDescription;
    private ArrayList<SkillObstaclePair> skillObstaclePairs;
    private SkatelinesDbHelper dbHelper;

    //public SaveLineLoader(Context context, Integer lineId, ArrayList<SkillObstaclePair> skillObstaclePairs) {
    public SaveLineLoader(Context context, Long lineId, String lineDescription, ArrayList<SkillObstaclePair> skillObstaclePairs) {
        super(context);
        this.lineId = lineId;
        this.lineDescription = lineDescription;
        this.skillObstaclePairs = skillObstaclePairs;
        dbHelper = new SkatelinesDbHelper(context);
    }

    @Override
    public LineEditorData loadInBackground() {
        List<Skill> skills = SkillService.getAllSkills(dbHelper);
        List<Obstacle> obstacles = ObstacleService.getAllObstacles(dbHelper);
        LineEditorData lineEditorData = new LineEditorData(skills, obstacles);
        if (skillObstaclePairs == null) {
            return new LineEditorData(skills, obstacles);
        }
        if (!isValidSkillObstacleSequence(skillObstaclePairs)) {
            System.out.printf("The skill/obstacle sequence is not valid: %s %n", stringifySkillObstaclePairs());
            return null;
        }
        if (lineId == 0) { // Trying to create new line.
            Line newLine = createNewLine();
            lineEditorData.setLineId(newLine.getId());
            return lineEditorData;
        }

        // Trying to update existing line.
        if (!isValidLineId(lineId)) {
            System.out.printf("The line id %d is not valid. %n", lineId);
            return null;
        }
        updateExistingLine();
        lineEditorData.setLineId(lineId);
        return lineEditorData;




        /*
        // We have a valid input.
        deleteLineSkills(lineId);
        for (int i = 0; i < skillObstaclePairs.size(); i++) {
            System.out.printf("Inside SaveLineLoader.loadInBackground, considering skillObstaclePair %d %n", i);
            SkillObstaclePair skillObstaclePair = skillObstaclePairs.get(i);
            insertLineSkill(lineId, skillObstaclePair.getSkillId(), skillObstaclePair.getObstacleId(), i);
        }
        try {
            Thread.currentThread().sleep(2);
        } catch (InterruptedException e) {
            System.out.println("Inside SaveLineTask, there was a problem sleeping the 'doInBackground' thread!");
        }
        return new LineEditorData(skills, obstacles);
        */
    }

    @Override
    public void deliverResult(LineEditorData lineEditorData) {
        System.out.println("Inside SaveLineLoader.deliverResult, got called...");
        if (isReset()) { // This load is no longer valid, ignore result.
            return;
        }
        if (isStarted()) {
            super.deliverResult(lineEditorData);
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
    public void onCanceled(LineEditorData lineEditorData) {
        System.out.println("Inside SaveLineLoader.onCanceled, got called...");
        super.onCanceled(lineEditorData);
    }

    private void updateExistingLine() {
        LineService.updateLineDescription(dbHelper, lineId, lineDescription);
        deleteLineSkills(lineId);
        insertSkillObstacleRows(lineId);
        /*
        for (int i = 0; i < skillObstaclePairs.size(); i++) {
            System.out.printf("Inside SaveLineLoader.loadInBackground, considering skillObstaclePair %d %n", i);
            SkillObstaclePair skillObstaclePair = skillObstaclePairs.get(i);
            insertLineSkill(lineId, skillObstaclePair.getSkillId(), skillObstaclePair.getObstacleId(), i);
        }
        */
        /*
        try {
            Thread.currentThread().sleep(2);
        } catch (InterruptedException e) {
            System.out.println("Inside SaveLineTask, there was a problem sleeping the 'doInBackground' thread!");
        }
        */
    }

    private Line createNewLine() {
        Line line = LineService.insertLine(dbHelper, lineDescription);
        insertSkillObstacleRows(line.getId());
        return line;
    }

    private void insertSkillObstacleRows(long lineId) {
        for (int i = 0; i < skillObstaclePairs.size(); i++) {
            System.out.printf("Inside SaveLineLoader.loadInBackground, considering skillObstaclePair %d %n", i);
            SkillObstaclePair skillObstaclePair = skillObstaclePairs.get(i);
            insertLineSkill(lineId, skillObstaclePair.getSkillId(), skillObstaclePair.getObstacleId(), i);
        }
    }

    private boolean isValidLineId(long lineId) {
        System.out.println("Inside SaveLineLoader.isValidLineId, got called...");
        boolean result = LineService.isValidLineId(dbHelper, lineId);
        if (result) {
            System.out.printf("Realized the line id %d is valid %n", lineId);
        } else {
            System.out.printf("Realized the line id %d is NOT valid %n", lineId);
        }
        return result;
    }

    private boolean isValidSkillObstacleSequence(List<SkillObstaclePair> skillObstaclePairs) {
        System.out.println("Inside SaveLineLoader.isValidSkillObstacleSequence, got called...");
        for (int i = 0; i < skillObstaclePairs.size(); i++) {
            SkillObstaclePair skillObstaclePair = skillObstaclePairs.get(i);
            long skillId = skillObstaclePair.getSkillId();
            Cursor cursor = querySkillById(skillId);
            if (cursor == null) {
                return false;
            }
            String skillDescription = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            System.out.printf("Realized we are specifying skill %d: %s at index %d %n", skillId, skillDescription, i);
            long obstacleId = skillObstaclePair.getObstacleId();
            cursor = queryTransitionById(obstacleId);
            if (cursor == null) {
                return false;
            }
            String obstacleDescription = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            System.out.printf("Realized we are specifying obstacle %d: %s at index %d %n", obstacleId, obstacleDescription, i);
        }
        return true;
    }

    private Cursor querySkillById(long skillId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM skill where id = ?", new String[]{String.valueOf(skillId)});
        boolean hasFirst = cursor.moveToFirst();
        if (!hasFirst) {
            return null;
        }
        return cursor;
    }

    private Cursor queryTransitionById(long transitionId) {
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

    private void deleteLineSkills(long lineId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM line_skill where line_id = ?", new String[]{String.valueOf(lineId)});
    }

    private void insertLineSkill(long lineId, long skillId, long obstacleId, int pos) {
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
