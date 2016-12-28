package com.tford.skatelines;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tford on 12/26/16.
 */

public class LineService {
    public static boolean isValidLineId(SkatelinesDbHelper dbHelper, int lineId) {
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
}
