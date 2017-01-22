package com.tford.skatelines;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.tford.skatelines.adapter.SessionAdapter;
import com.tford.skatelines.model.Line;
import com.tford.skatelines.model.Session;

import java.util.ArrayList;

public class SessionLogActivity extends Activity implements LoaderManager.LoaderCallbacks<SessionList> {
    private SkatelinesDbHelper dbHelper;
    private Button savingButton;
    private SessionAdapter sessionAdapter;
    private LineSpinnerAdapter lineSpinnerAdapter;
    private Spinner spinner;

    private static final String EXTRA_SESSION_LINE_ID = "session_line_id";
    private static final String EXTRA_SESSION_DATE = "session_date";

    private static final int SESSION_LOG_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_log);
        sessionAdapter = new SessionAdapter(this, new ArrayList<Session>());
        ListView sessionListView = (ListView) findViewById(R.id.logged_sessions);
        sessionListView.setAdapter(sessionAdapter);
        savingButton = (Button) findViewById(R.id.save_session_button);
        spinner = (Spinner) findViewById(R.id.lines_spinner);
        lineSpinnerAdapter = new LineSpinnerAdapter(this, new ArrayList<Line>());
        spinner.setAdapter(lineSpinnerAdapter);
        dbHelper = new SkatelinesDbHelper(getApplicationContext());
        getLoaderManager().initLoader(SESSION_LOG_LOADER_ID, new Bundle(), this);
    }

    public void saveSession(View view) {
        Line selectedLine = (Line) spinner.getSelectedItem();
        int sessionLineId = selectedLine.getId();
        EditText sessionDateBox = (EditText) findViewById(R.id.session_date);
        String sessionDateString = sessionDateBox.getText().toString();
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
    public Loader<SessionList> onCreateLoader(int id, Bundle args) {
        Integer sessionLineId = (Integer) args.getSerializable(EXTRA_SESSION_LINE_ID);
        String sessionDateString = (String) args.getSerializable(EXTRA_SESSION_DATE);
        SaveSessionLoader saveSessionLoader = new SaveSessionLoader(this, sessionLineId, sessionDateString);
        return saveSessionLoader;
    }

    @Override
    public void onLoaderReset(Loader<SessionList> loader) {
        System.out.printf("Inside SessionLogActivity.onLoaderReset, got called!!!");
        sessionAdapter.setSessions(new ArrayList<Session>());
    }

    @Override
    public void onLoadFinished(Loader<SessionList> loader, SessionList data) {
        if (data != null) {
            System.out.printf("Inside SessionLogActivity.onLoadFinished, got called with data: %s %n", data.toString());
            sessionAdapter.setSessions(data.getSessions());
            lineSpinnerAdapter.setLines(data.getLines());
            //findSavingButton().setText("Saved");
        }
    }

}

