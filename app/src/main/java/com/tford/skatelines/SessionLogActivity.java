package com.tford.skatelines;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.tford.skatelines.MainActivity;
import com.tford.skatelines.R;
import com.tford.skatelines.SkatelinesDbHelper;

import java.util.ArrayList;
import java.util.List;

public class SessionLogActivity extends Activity implements LoaderManager.LoaderCallbacks<List<Session>> {
    private SkatelinesDbHelper dbHelper;
    private Button savingButton;
    private SessionAdapter sessionAdapter;

    private static final String EXTRA_SESSION_LINE_ID = "session_line_id";
    private static final String EXTRA_SESSION_DATE = "session_date";

    private static final int SESSION_LOG_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_session_log);
        sessionAdapter = new SessionAdapter(this, new ArrayList<Session>());
        ListView sessionListView = (ListView) findViewById(R.id.logged_sessions);
        sessionListView.setAdapter(sessionAdapter);
        savingButton = (Button) findViewById(R.id.save_session_button);
        dbHelper = new SkatelinesDbHelper(getApplicationContext());
        getLoaderManager().initLoader(SESSION_LOG_LOADER_ID, new Bundle(), this);
    }

    public void saveSession(View view) {
        EditText sessionLineIdBox = (EditText) findViewById(R.id.session_line_id);
        int sessionLineId = Integer.valueOf(sessionLineIdBox.getText().toString());
        EditText sessionDateBox = (EditText) findViewById(R.id.session_date);
        String sessionDateString = sessionDateBox.getText().toString();
        System.out.printf("Inside SessionLogActivity.saveSession, I want to save (line_id: %d, date: %s)%n", sessionLineId, sessionDateString);
        LoaderManager loaderManager = getLoaderManager();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_SESSION_LINE_ID, new Integer(sessionLineId));
        bundle.putSerializable(EXTRA_SESSION_DATE, sessionDateString);
        loaderManager.restartLoader(SESSION_LOG_LOADER_ID, bundle, this); // Real loader, which should deliver.
    }

    public void backToMain(View view) {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(mainActivityIntent);
    }

    public void onPause() { super.onPause(); }

    public void onResume() {
        super.onResume();
    }

    public void onStop() {
        super.onStop();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public Loader<List<Session>> onCreateLoader(int id, Bundle args) {
        Integer sessionLineId = (Integer) args.getSerializable(EXTRA_SESSION_LINE_ID);
        String sessionDateString = (String) args.getSerializable(EXTRA_SESSION_DATE);
        SaveSessionLoader saveSessionLoader = new SaveSessionLoader(this, sessionLineId, sessionDateString);
        return saveSessionLoader;
    }

    @Override
    public void onLoaderReset(Loader<List<Session>> loader) {
        System.out.printf("Inside SessionLogActivity.onLoaderReset, got called!!!");
        sessionAdapter.setSessions(new ArrayList<Session>());
    }

    @Override
    public void onLoadFinished(Loader<List<Session>> loader, List<Session> data) {
        if (data != null) {
            System.out.printf("Inside SessionLogActivity.onLoadFinished, got called with data: %s %n", data.toString());
            sessionAdapter.setSessions(data);
            //findSavingButton().setText("Saved");
        }
    }

}

