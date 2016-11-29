package com.tford.skatelines;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by tford on 11/22/16.
 */

public class MainActivity extends AppCompatActivity {
    private SkatelinesDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new SkatelinesDbHelper(getApplicationContext());
    }

    public void loadDatabase(View view) {
        System.out.println("Hellooooo!!!!1\n about to load database...");
        LoadDatabaseTask loadDatabaseTask = new LoadDatabaseTask();
        loadDatabaseTask.execute();
    }

    public void queryDatabase(View view) {
        System.out.println("Helooooo!!!!\n about to query database...");
        QueryDatabaseTask queryDatabaseTask = new QueryDatabaseTask();
        queryDatabaseTask.execute();
    }

    public void startLineEditor(View view) {
        System.out.println("Here is where we should start the line editor activity...");
        Intent lineEditorActivityIntent = new Intent(this, LineEditorActivity.class);
        startActivity(lineEditorActivityIntent);
    }

    private class LoadDatabaseTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... inputs) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("description", "fakie");
            long newRowId = db.insert("skill", null, values);
            return null;
        }

        @Override
        protected void onPostExecute(Void voidValue) {
            System.out.println("Inside LoadDatabaseTask.onPostExecute, got called!!!");
        }
    }

    private class QueryDatabaseTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... inputs) {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM skill;", new String[]{});
            boolean hasFirst = cursor.moveToFirst();
            while (hasFirst) {
                long itemId = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                System.out.printf("Got item id: %d ; %s %n", itemId, description);
                if (!cursor.moveToNext()) {
                    break;
                }
            }
            return null;
        }
    }

}
