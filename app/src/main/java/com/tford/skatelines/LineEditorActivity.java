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
    private Spinner skillSpinner;
    private ObstacleAdapter obstacleAdapter;
    private Spinner obstacleSpinner;
    private Spinner spinner;

    private static final int SAVE_LINE_LOADER_ID = 1;
    private static final String EXTRA_LINE_ID = "line_id";
    private static final String EXTRA_SKILL_OBSTACLE_SEQUENCE = "skill_obstacle_sequence";
    private static final String EXTRA_LINE_DESCRIPTION = "line_description";

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
        skillSpinner = (Spinner) findViewById(R.id.skill_spinner);
        skillAdapter = new SkillAdapter(this, new ArrayList<Skill>());
        skillSpinner.setAdapter(skillAdapter);

        // Set up Obstacle Spinner
        obstacleSpinner = (Spinner) findViewById(R.id.obstacle_spinner);
        obstacleAdapter = new ObstacleAdapter(this, new ArrayList<Obstacle>());
        obstacleSpinner.setAdapter(obstacleAdapter);

        // Init Loader.
        getLoaderManager().initLoader(SAVE_LINE_LOADER_ID, new Bundle(), this); // Empty loader should never deliver.
    }

    @Override
    public Loader<LineEditorData> onCreateLoader(int id, Bundle args) {
        Long lineId = (Long) args.getSerializable(EXTRA_LINE_ID);
        ArrayList<SkillObstaclePair> skillObstacleSequence = (ArrayList<SkillObstaclePair>) args.getSerializable(EXTRA_SKILL_OBSTACLE_SEQUENCE);
        String lineDescription = (String) args.getSerializable(EXTRA_LINE_DESCRIPTION);
        //SaveLineLoader lineSaveLoader = new SaveLineLoader(this, lineId, skillObstacleSequence);
        SaveLineLoader lineSaveLoader = new SaveLineLoader(this, lineId, lineDescription, skillObstacleSequence);
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

        // Extract line id.
        EditText lineIdBox = (EditText) findViewById(R.id.line_id);
        String lineIdString = lineIdBox.getText().toString();
        long lineId = 0L; // Code for "create new"
        if (lineIdString != null && lineIdString.length() >0) {
            lineId = Long.valueOf(lineIdString);
        }

        // Extract line description
        EditText lineDescriptionBox = (EditText) findViewById(R.id.line_description);
        String lineDescriptionString = lineDescriptionBox.getText().toString();

        // Extract Skill obstacle sequence
        EditText skillObstacleSequenceBox = (EditText) findViewById(R.id.skill_obstacle_sequence);
        String skillObstacleSequenceString = skillObstacleSequenceBox.getText().toString();
        ArrayList<SkillObstaclePair> skillObstacleSequence = parseSkillObstacleSequence(skillObstacleSequenceString);
        if (skillObstacleSequence == null) {
            System.out.printf("The skill/obstance sequence cannot be parsed. %n");
            return;
        }
        LoaderManager loaderManager = getLoaderManager();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_LINE_ID, new Long(lineId));
        bundle.putSerializable(EXTRA_LINE_DESCRIPTION, lineDescriptionString);
        bundle.putSerializable(EXTRA_SKILL_OBSTACLE_SEQUENCE, skillObstacleSequence);
        loaderManager.restartLoader(SAVE_LINE_LOADER_ID, bundle, this); // Real loader, which should deliver.
    }

    public void backToMain(View view) {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(mainActivityIntent);
    }

    public void addSkillObstaclePair(View view) {
        System.out.println("Inside addSkillObstaclePair, got called!");
        Skill selectedSkill = (Skill) skillSpinner.getSelectedItem();
        Obstacle selectedObstacle = (Obstacle) obstacleSpinner.getSelectedItem();
        System.out.printf("The selected skill is %d %n", selectedSkill.getId());
        System.out.printf("The selected obstacle is %d %n", selectedObstacle.getId());
        String skillObstaclePair = String.format(
                "(%d,%d)",
                selectedSkill.getId(),
                selectedObstacle.getId()
        );
        EditText skillObstacleSequenceBox = (EditText) findViewById(R.id.skill_obstacle_sequence);
        String skillObstacleSequenceString = skillObstacleSequenceBox.getText().toString();
        if (skillObstacleSequenceString.length() > 0) {
            skillObstacleSequenceString += "," + skillObstaclePair;
        } else {
            skillObstacleSequenceString += skillObstaclePair;
        }
        skillObstacleSequenceBox.setText(skillObstacleSequenceString);
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
