package com.liftlog;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.liftlog.common.Util;
import com.liftlog.models.Lift;
import com.liftlog.data.DataAccessObject;
import com.liftlog.components.ExerciseInputDialog;
import com.liftlog.models.Exercise;
import com.liftlog.models.RPEScale;

//public class ViewLiftActivity extends Activity {
public class ViewLiftActivity extends AppCompatActivity implements ExerciseInputDialog.ExerciseInputDialogListener {

    private static final String LOG_TAG = "LiftLog.AddLift";



    /**
     * The key for this intent's extended data: the id of the lift (-1 if new instance)
     */
    public static final String LIFT_ID_KEY = "lift_id";
    /**
     * The key for this intent's extended data: the id of this session
     */
    public static final String SESSION_ID_KEY = "session_id";

    private long sessionId = -1;
    private long liftId = -1;

    private DataAccessObject dao;

    private Map<Long, Exercise> exercises;

    private static long prevSelected = -1;
    private static int prevReps = -1;

    private Spinner spnExercise;
    //    RadioButton rbtnWarmup;
    //    NumberPicker pckWeight;
    private EditText txtWeight;
    private NumberPicker pckReps;
//    private NumberPicker pckSets;
    private Spinner spnRPE;

    private Button btnSave;
    private Button btnViewHistory;

//    EditText txtDay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_lift);
        dao = new DataAccessObject(this);

        Intent intent = this.getIntent();
        liftId  = intent.getLongExtra(LIFT_ID_KEY, -1l);
        sessionId = intent.getLongExtra(SESSION_ID_KEY, -1l);

//        Log.d(LOG_TAG, "LIFT_ID_KEY=" + liftIdString);

        createContents();
        loadExercises();
        loadLift(liftId);
    }

    private void loadLift(long id)
    {
        if(id < 0)
        {
            //txtWeight.setText(String.valueOf(0));
            pckReps.setValue(5);
//            pckSets.setValue(1);
            if(prevSelected > -1)
            {
                setSelectedExercise(prevSelected);
            }
            if(prevReps > -1)
            {
                pckReps.setValue(prevReps);
            }

        }
        else {
            Lift lift = dao.selectLift(id);
            double w = lift.getWeight();
            String weightValue = (w == Math.floor(w) ? String.valueOf((int) w) : String.valueOf(w));
            txtWeight.setText(weightValue);
            pckReps.setValue(lift.getReps());
//            pckSets.setValue(lift.getSets());

            //find and select the exercise matching the id
            long exerciseId = lift.getExerciseId();
            setSelectedExercise(exerciseId);
            
            double rpe = lift.getRPE();
            setSelectedRPE(rpe);

            //focus on weight EditText and set caret to end
            txtWeight.requestFocus();
            int endPos = txtWeight.getText().length();
            txtWeight.setSelection(endPos);
//            rbtnWarmup.setChecked(lift.isWarmup());
        }

    }

    private void setSelectedExercise(long exerciseId)
    {
        int cnt = spnExercise.getAdapter().getCount();
        Exercise exercise;
        for(int i = 0; i < cnt; i++)
        {
            exercise = (Exercise) spnExercise.getItemAtPosition(i);
            if(exercise != null && exercise.getId() == exerciseId)
            {
                spnExercise.setSelection(i);
                return;
            }
        }
        spnExercise.setSelection(0);
    }
    
    private void setSelectedRPE(double rpeValue)
    {
        int cnt = spnRPE.getAdapter().getCount();
        RPEScale rpe;
        int unspecifiedIndex = 0;
        for(int i = 0; i < cnt; i++)
        {
            rpe = (RPEScale) spnRPE.getItemAtPosition(i);
            if(rpe == RPEScale.DEFAULT)
            {
                unspecifiedIndex = i;
            }
            if(rpe != null && rpe.getValue() == rpeValue)
            {
                spnRPE.setSelection(i);
                return;
            }
        }
        
        //no match found, set to the "Unspecified" default item
        spnRPE.setSelection(unspecifiedIndex);
    }


    private void loadExercises()
    {
        exercises = dao.selectExerciseMap(false);
        if(exercises != null)
        {
            List<Exercise> exerciseList = new ArrayList<>(exercises.values());
            //dummy Exercise object for <Add New>
//            Exercise dummyExercise = new Exercise();
//            dummyExercise.setId(-1);
//            dummyExercise.setName("<Add New>");
//            exerciseList.add(dummyExercise);
            Collections.sort(exerciseList, Exercise.byNameDummyLast);

            ArrayAdapter<Exercise> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, exerciseList);
            adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            spnExercise.setAdapter(adapter);


        }
        else
        {
            Log.d(LOG_TAG, "Error loading exercises.");
        }
    }



    /**
     * Launch a_m ExerciseInputDialog
     */
    public void doNewExercise(View view)
    {
        Exercise exercise = new Exercise();
        exercise.setNew(true);
        exercise.setId(-1);

        ExerciseInputDialog dialog = ExerciseInputDialog.newInstance(exercise);
//        dialog.setTargetFragment(this, ExerciseInputDialog.RequestType.DEFAULT.getValue());
        dialog.show(this.getSupportFragmentManager(), "ExerciseInputDialog");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_lift, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a_m parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id)
        {
            case R.id.action_delete:
                doDelete(this.getCurrentFocus());
                break;
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_view_history:
                doLaunchViewHistory();
                break;
            case R.id.action_about_viewlift:
                Util.launchAboutWebsiteIntent(this);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void doLaunchViewHistory()
    {
        Intent intent = new Intent(this, ViewHistoryActivity.class);
        Exercise selectedExercise = (Exercise) spnExercise.getSelectedItem();
        if(selectedExercise == null)
        {
            Toast.makeText(this, "Error loading exercise history.", Toast.LENGTH_LONG).show();
            return;
        }
        intent.putExtra(ViewHistoryActivity.EXERCISE_ID_KEY, selectedExercise.getId());
        super.startActivity(intent);
    }


    int previousExercisePos = 0;
    private void createContents()
    {
        ActionBar actionBar = this.getActionBar();
//        if(actionBar == null) actionBar =
        if (actionBar != null)
        {
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.show();
        }

        btnViewHistory = (Button) findViewById(R.id.btn_view_history);
        btnViewHistory.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                doLaunchViewHistory();
            }
        });

        //initialize control references
        spnExercise = (Spinner) findViewById(R.id.spn_exercise);
//        pckSets = (NumberPicker) findViewById(R.id.pck_sets);
        txtWeight = (EditText) findViewById(R.id.txt_weight);
        pckReps = (NumberPicker) findViewById(R.id.pck_reps);
        btnSave = (Button) findViewById(R.id.btn_save);
        spnRPE = (Spinner) findViewById(R.id.spn_rpe);

        openOrCreateDatabase(DataAccessObject.DB_NAME, MODE_PRIVATE, null);

        pckReps.setMinValue(1);
        pckReps.setMaxValue(100);
        pckReps.setWrapSelectorWheel(false);

//        pckSets.setMinValue(0);
//        pckSets.setMaxValue(100);
//        pckSets.setWrapSelectorWheel(false);

        spnExercise.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                Exercise selectedExercise = (Exercise) parent.getItemAtPosition(position);
                setViewHistoryButtonText(selectedExercise);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                //do nothing
            }
        });


        ArrayAdapter<RPEScale> rpeScaleArrayAdapter = new ArrayAdapter<RPEScale>(this, android.R.layout.simple_spinner_dropdown_item, RPEScale.values());
        rpeScaleArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spnRPE.setAdapter(rpeScaleArrayAdapter);

    }

    private void setViewHistoryButtonText(Exercise exercise)
    {
        if(exercise == null) return;
        String lbl;
        if(exercise.getName() == null) lbl = "View Exercise History";
        else lbl = "View " + exercise.getName() + " history";
        btnViewHistory.setText(lbl);
    }


    /**
     * Handle Submit button onClick
     * @param view The view
     */
    public void doSave(View view)
    {
//        boolean isWarmup = rbtnWarmup.isChecked();

        double weight = 0;
        try
        {
            weight = Double.parseDouble(txtWeight.getText().toString());
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        }

        int reps = pckReps.getValue();
//        int sets = pckSets.getValue();

        long exerciseId = -1;
        Exercise selectedExercise = (Exercise) spnExercise.getSelectedItem();
        if(selectedExercise != null)
        {
            exerciseId = selectedExercise.getId();
        }
        Lift lift = new Lift();
        lift.setExerciseId(exerciseId);
        lift.setSessionId(sessionId);
        lift.setWeight(weight);
//        lift.setSets(sets);
        lift.setSets(1);
        lift.setReps(reps);
        lift.setWarmup(false);


        RPEScale rpe = (RPEScale)spnRPE.getSelectedItem();
        if(rpe != null)
        {
            lift.setRPE(rpe.getValue());
        }


        if(liftId == -1)
        {
            //insert new Lift
            lift.setNew(true);
            long ret = dao.insert(lift);
            System.out.println();
        }
        else
        {
            //update existing lift
            lift.setId(liftId);
            boolean ret =  dao.update(lift);
            lift.setModified(true);
            Log.d(LOG_TAG, "" + ret);
        }

        prevSelected = exerciseId;
        prevReps = reps;
        this.finish();
    }

    /**
     * Handle Delete button click
     * @param view The view
     */
    public void doDelete(View view)
    {
        if (liftId == -1)
        {
            Toast.makeText(ViewLiftActivity.this, "Can't delete. This Lift has not been created yet.", Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this)
//                .setIcon(android.R.drawable.ic_dialog_alert)
                .setIcon(R.drawable.ic_warning_blue_24dp)
                .setTitle("Delete Lift")
                .setMessage("Are you sure you want to delete this lift?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (!dao.deleteLift(liftId)) {
                            Toast.makeText(ViewLiftActivity.this, "Error deleting lift.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        //successfully deleted
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

//    @Override
//    public void finish()
//    {
//        //super.finish();
//        Intent intent = new Intent();
//        intent.putExtra(ViewSessionActivity.SESSION_ID_KEY, sessionId);
//        setResult(RESULT_OK, intent);
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra(ViewSessionActivity.SESSION_ID_KEY, sessionId);
        setResult(RESULT_OK, intent);
    }

    /**
     * Handle Cancel button click
     * @param view The view
     */
    public void doCancel(View view)
    {
        finish();
    }

    @Override
    public void onDialogSaveClick(DialogFragment dialog, Exercise exercise)
    {
        if(exercise == null) return;
        long id = exercise.getId();
        //this should always be true (we don't allow editing from ViewLiftActivity context)
        if(id == -1)
        {
            long newExerciseId = dao.insert(exercise);
            loadExercises();
            setSelectedExercise(newExerciseId);
        }
        else spnExercise.setSelection(0);


    }

    @Override
    public void onDialogCancelClick(DialogFragment dialog)
    {
        spnExercise.setSelection(previousExercisePos);
    }

    @Override
    public void onDialogDeleteClick(DialogFragment dialog, Exercise exercise)
    {
        //do nothing
    }

    @Override
    public void handleDialogClose(DialogInterface dialog)
    {
        //do nothing
    }
}
