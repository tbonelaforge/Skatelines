package com.tford.skatelines.service;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tford.skatelines.SkatelinesDbHelper;
import com.tford.skatelines.model.Obstacle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tford on 1/22/17.
 */

public class ObstacleService {
    public static List<Obstacle> getAllObstacles(SkatelinesDbHelper dbHelper) {
        List<Obstacle> obstacles = new ArrayList<Obstacle>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM transition;",
                new String[]{}
        );
        if (cursor.moveToFirst()) {
            do {
                Integer id = Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                Obstacle obstacle = new Obstacle(id, description);
                obstacles.add(obstacle);
            } while (cursor.moveToNext());
        }
        return obstacles;
    }
}
