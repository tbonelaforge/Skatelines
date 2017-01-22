package com.tford.skatelines;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.tford.skatelines.adapter.ObstacleAdapter;
import com.tford.skatelines.adapter.SessionAdapter;
import com.tford.skatelines.adapter.SkillAdapter;
import com.tford.skatelines.model.Line;
import com.tford.skatelines.model.Obstacle;
import com.tford.skatelines.model.Skill;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tford on 11/28/16.
 */

public class LineEditorActivity extends Activity implements LoaderManager.LoaderCallbacks<LineEditorData> {
    private SkatelinesDbHelper dbHelper;
    private Button savingButton;
    private TextWatcher textWatcher;
    private SkillAdapter skillAdapter;
    private ObstacleAdapter obstacleAdapter;
    private Spinner spinner;

    private static final int SAVE_LINE_LOADER_ID = 1;
    private static final String EXTRA_LINE_ID = "line_id";
    private static final String EXTRA_SKILL_OBSTACLE_SEQUENCE = "skill_obstacle_sequence";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("LineEditorActivity.onCreate", "Got called!!!");
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_edit_line);
        savingButton = (Button) findViewById(R.id.save_line_button);
        dbHelper = new SkatelinesDbHelper(getApplicationContext());
        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (savingButton != null) {
                    savingButton.setText("Save");
                }
            }
        };
        EditText lineIdBox = (EditText) findViewById(R.id.line_id);
        lineIdBox.addTextChangedListener(textWatcher);
        EditText skillObstacleSequenceBox = (EditText) findViewById(R.id.skill_obstacle_sequence);
        skillObstacleSequenceBox.addTextChangedListener(textWatcher);

        // Set up Skill Spinner
        Spinner skillSpinner = (Spinner) findViewById(R.id.skill_spinner);
        skillAdapter = new SkillAdapter(this, new ArrayList<Skill>());
        skillSpinner.setAdapter(skillAdapter);

        // Set up Obstacle Spinner
        Spinner obstacleSpinner = (Spinner) findViewById(R.id.obstacle_spinner);
        obstacleAdapter = new ObstacleAdapter(this, new ArrayList<Obstacle>());
        obstacleSpinner.setAdapter(obstacleAdapter);

        // Init Loader.
        getLoaderManager().initLoader(SAVE_LINE_LOADER_ID, new Bundle(), this); // Empty loader should never deliver.
    }

    @Override
    public Loader<LineEditorData> onCreateLoader(int id, Bundle args) {
        Integer lineId = (Integer) args.getSerializable(EXTRA_LINE_ID);
        ArrayList<SkillObstaclePair> skillObstacleSequence = (ArrayList<SkillObstaclePair>) args.getSerializable(EXTRA_SKILL_OBSTACLE_SEQUENCE);
        SaveLineLoader lineSaveLoader = new SaveLineLoader(this, lineId, skillObstacleSequence);
        return lineSaveLoader;
    }

    @Override
    public void onLoaderReset(Loader<LineEditorData> loader) {

    }

    @Override
    public void onLoadFinished(Loader<LineEditorData> loader, LineEditorData lineEditorData) {
        skillAdapter.setSkills(lineEditorData.getSkills());
        obstacleAdapter.setObstacles(lineEditorData.getObstacles());
        if (lineEditorData.getLineId() != null) {
            findSavingButton().setText("Saved");
        }
    }

    public void saveLine(View view) {
        EditText lineIdBox = (EditText) findViewById(R.id.line_id);
        int lineId = Integer.valueOf(lineIdBox.getText().toString());
        EditText skillObstacleSequenceBox = (EditText) findViewById(R.id.skill_obstacle_sequence);
        String skillObstacleSequenceString = skillObstacleSequenceBox.getText().toString();

        ArrayList<SkillObstaclePair> skillObstacleSequence = parseSkillObstacleSequence(skillObstacleSequenceString);
        if (skillObstacleSequence == null) {
            System.out.printf("The skill/obstance sequence cannot be parsed. %n");
            return;
        }
        LoaderManager loaderManager = getLoaderManager();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_LINE_ID, new Integer(lineId));
        bundle.putSerializable(EXTRA_SKILL_OBSTACLE_SEQUENCE, skillObstacleSequence);
        loaderManager.restartLoader(SAVE_LINE_LOADER_ID, bundle, this); // Real loader, which should deliver.
    }

    public void backToMain(View view) {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(mainActivityIntent);
    }

    public void onPause() {
        super.onPause();
    }

    public void onResume() {
        super.onResume();
    }

    public void onStop() {
        super.onStop();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    private ArrayList<SkillObstaclePair> parseSkillObstacleSequence(String input) {
        String strippedInput = input.replaceAll("\\s", "");
        String chunkDelimiterRegex = "(?<=\\)),(?=\\()";
        String[] chunks = strippedInput.split(chunkDelimiterRegex);
        ArrayList<SkillObstaclePair> parsed = new ArrayList<SkillObstaclePair>();
        for (int i = 0; i < chunks.length; i++) {
            if (!SkillObstaclePair.isValidSyntax(chunks[i])) {
                return null;
            }
            SkillObstaclePair skillObstaclePair = SkillObstaclePair.parse(chunks[i]);
            parsed.add(skillObstaclePair);
        }
        return parsed;
    }

    private Button findSavingButton() {
        return (Button) findViewById(R.id.save_line_button);
    }

}
