package com.tford.skatelines.service;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tford.skatelines.SkatelinesDbHelper;
import com.tford.skatelines.model.Skill;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tford on 1/22/17.
 */

public class SkillService {
    public static List<Skill> getAllSkills(SkatelinesDbHelper dbHelper) {
        List<Skill> skills = new ArrayList<Skill>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM skill;",
                new String[]{}
        );
        if (cursor.moveToFirst()) {
            do {
                Integer id = Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                Skill skill = new Skill(id, description);
                skills.add(skill);
            } while (cursor.moveToNext());
        }
        return skills;
    }
}
