package com.tford.skatelines;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tford on 11/28/16.
 */

public class LineEditorActivity extends Activity {
    private SkatelinesDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("LineEditorActivity.onCreate", "Got called!!!");
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_edit_line);
        dbHelper = new SkatelinesDbHelper(getApplicationContext());
    }

    public void saveLine(View view) {
        EditText lineIdBox = (EditText) findViewById(R.id.line_id);
        int lineId = Integer.valueOf(lineIdBox.getText().toString());
        EditText skillObstacleSequenceBox = (EditText) findViewById(R.id.skill_obstacle_sequence);
        String skillObstacleSequenceString = skillObstacleSequenceBox.getText().toString();
        System.out.printf("Inside LineEditorActivity, got called, extracted lineId: %s and skill/obstacle sequence: %s %n ", lineId, skillObstacleSequenceString);

        List<SkillObstaclePair> skillObstacleSequence = parseSkillObstacleSequence(skillObstacleSequenceString);
        if (skillObstacleSequence == null) {
            System.out.printf("The skill/obstance sequence cannot be parsed. %n");
        }
        SaveLineTask saveLineTask = new SaveLineTask(lineId, skillObstacleSequence);
        saveLineTask.execute();
    }

    private List<SkillObstaclePair> parseSkillObstacleSequence(String input) {
        String strippedInput = input.replaceAll("\\s", "");
        String chunkDelimiterRegex = "(?<=\\)),(?=\\()";
        String[] chunks = strippedInput.split(chunkDelimiterRegex);
        System.out.printf("Inside parseSkillObstacleSequence, got chunks: %s %n", chunks.toString());
        List<SkillObstaclePair> parsed = new ArrayList<SkillObstaclePair>();
        for (int i = 0; i < chunks.length; i++) {
            if (!SkillObstaclePair.isValidSyntax(chunks[i])) {
                return null;
            }
            SkillObstaclePair skillObstaclePair = SkillObstaclePair.parse(chunks[i]);
            parsed.add(skillObstaclePair);
        }
        return parsed;
    }

    private class SaveLineTask extends AsyncTask<Void, Void, Void> {
        private int lineId;
        private List<SkillObstaclePair> skillObstaclePairs;

        public SaveLineTask(int lineId, List<SkillObstaclePair> skillObstaclePairs) {
            this.lineId = lineId;
            this.skillObstaclePairs = skillObstaclePairs;
        }

        @Override
        protected Void doInBackground(Void... inputs) {
            if (!isValidLineId(lineId)) {
                System.out.printf("The line id %d is not valid. %n", lineId);
                return null;
            }
            System.out.printf("Inside SaveLineTask.doInBackground, made it past the valid line id check!%n");
            //return null;

            if (!isValidSkillObstacleSequence(skillObstaclePairs)) {
                System.out.printf("The skill/obstacle sequence is not valid: %s %n", stringifySkillObstaclePairs());
                return null;
            }

            // We have a valid input.
            deleteLineSkills(lineId);
            for (int i = 0; i < skillObstaclePairs.size(); i++) {
                SkillObstaclePair skillObstaclePair = skillObstaclePairs.get(i);
                insertLineSkill(lineId, skillObstaclePair.getSkillId(), skillObstaclePair.getObstacleId(), i);
            }
            return null;
        }

        private boolean isValidLineId(int lineId) {
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
            Cursor cursor = db.rawQuery("SELECT * FROM skill where id = ?", new String[] {String.valueOf(skillId)});
            boolean hasFirst = cursor.moveToFirst();
            if (!hasFirst) {
                return null;
            }
            return cursor;
        }

        private Cursor queryTransitionById(int transitionId) {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM transition where id = ?", new String[] {String.valueOf(transitionId)});
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
            db.execSQL("DELETE FROM line_skill where line_id = ?", new String[] {String.valueOf(lineId)});
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
}
