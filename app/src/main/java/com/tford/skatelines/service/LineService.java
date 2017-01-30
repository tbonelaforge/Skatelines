package com.tford.skatelines.service;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tford.skatelines.SkatelinesDbHelper;
import com.tford.skatelines.model.Line;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tford on 12/26/16.
 */

public class LineService {
    public static boolean isValidLineId(SkatelinesDbHelper dbHelper, long lineId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM line where id = ?;",
                new String[]{String.valueOf(lineId)}
        );
        boolean hasFirst = cursor.moveToFirst();
        if (!hasFirst) {
            return false;
        }
        String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
        System.out.printf("Determined the line id %d is valid: %s %n", lineId, description);
        return true;
    }

    public static List<Line> getAllLines(SkatelinesDbHelper dbHelper) {
        List<Line> lines = new ArrayList<Line>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM line;",
                new String[]{}
        );
        if (cursor.moveToFirst()) {
            do {
                Integer id = Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                Line line = new Line(id, description);
                lines.add(line);
            } while (cursor.moveToNext());
        }
        return lines;
    }

    public static void updateLineDescription(SkatelinesDbHelper dbHelper, long lineId, String lineDescription) {
        String updateStatement = "UPDATE line where id = ? SET description = ?";
        String[] values = {
                String.valueOf(lineId),
                String.valueOf(lineDescription)
        };
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(updateStatement, values);
    }

    public static Line insertLine(SkatelinesDbHelper dbHelper, String lineDescription) {
        //Map<String, Object> lineValues = new HashMap<>();
        //lineValues.put("description", lineDescription);
        ContentValues lineValues = new ContentValues();
        lineValues.put("description", lineDescription);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long lineId = db.insert("line", null, lineValues);
        Line line = new Line(lineId, lineDescription);
        return line;
    }
}
