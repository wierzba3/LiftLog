package com.liftlog;

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

import wierzba.james.liftlog.R;

import com.liftlog.db.DataAccessObject;
import com.liftlog.components.ExerciseInputDialog;
import com.liftlog.models.Exercise;
import com.liftlog.models.Lift;

//public class ViewLift extends Activity {
public class ViewLift extends AppCompatActivity implements ExerciseInputDialog.ExerciseInputDialogListener {

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

    Spinner spnExercise;
    //    RadioButton rbtnWarmup;
    //    NumberPicker pckWeight;
    EditText txtWeight;
    NumberPicker pckReps;
    NumberPicker pckSets;

    Button btnSave;

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
            txtWeight.setText(String.valueOf(0));
            pckReps.setValue(5);
            pckSets.setValue(1);
//            rbtnWarmup.setChecked(false);
        }
        else {
            Lift lift = dao.selectLift(id);
            txtWeight.setText(String.valueOf(lift.getWeight()));
            pckReps.setValue(lift.getReps());
            pckSets.setValue(lift.getSets());

            //find and select the exercise matching the id
            long exerciseId = lift.getExerciseId();
            setSelectedExercise(exerciseId);


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


    private void loadExercises()
    {
        exercises = dao.selectExercises();
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
     * Launch a ExerciseInputDialog
     */
    public void doNewExercise(View view)
    {
        Exercise exercise = new Exercise();
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
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.action_delete:
                doDelete(this.getCurrentFocus());
                break;
            case R.id.home:
                //Automatically handled by the action bar.
                break;
            case R.id.action_settings:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    int previousExercisePos = 0;
    private void createContents()
    {
        //initialize control references
        spnExercise = (Spinner) findViewById(R.id.spn_exercise);
        pckSets = (NumberPicker) findViewById(R.id.pck_sets);
//        rbtnWarmup = (RadioButton) findViewById(R.id.rbtn_warmup);
//        pckWeight = (NumberPicker) findViewById(R.id.pck_weight);
        txtWeight = (EditText) findViewById(R.id.txt_weight);
        pckReps = (NumberPicker) findViewById(R.id.pck_reps);
        pckSets = (NumberPicker) findViewById(R.id.pck_sets);
        btnSave = (Button) findViewById(R.id.btn_save);


        openOrCreateDatabase(DataAccessObject.DB_NAME, MODE_PRIVATE, null);

        pckReps.setMinValue(1);
        pckReps.setMaxValue(100);
        pckReps.setWrapSelectorWheel(false);

        pckSets.setMinValue(0);
        pckSets.setMaxValue(100);
        pckSets.setWrapSelectorWheel(false);

        spnExercise.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
//                Exercise selectedExercise = (Exercise) parent.getItemAtPosition(position);
//
//                if(selectedExercise == null) return;
//                if(selectedExercise.getId() == -1)
//                {
//                    ViewLift.this.doNewExercise();
//                }
//                //remember the last actual exercise that was selected
//                else previousExercisePos = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            //do nothing
            }
        });
    }

    /**
     * Handle Submit button onClick
     * @param view The view
     */
    public void doSave(View view)
    {
//        boolean isWarmup = rbtnWarmup.isChecked();
        int weight = Integer.parseInt(txtWeight.getText().toString());

        int reps = pckReps.getValue();
        int sets = pckSets.getValue();

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
        lift.setSets(sets);
        lift.setReps(reps);
        lift.setWarmup(false);

        if(liftId == -1)
        {
            //insert new Lift
            dao.insert(lift);
        }
        else
        {
            //update existing lift
            lift.setId(liftId);
            boolean ret =  dao.update(lift);
            Log.d(LOG_TAG, "" + ret);
        }

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
            Toast.makeText(ViewLift.this, "Can't delete. This Lift has not been created yet.", Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Delete Lift")
                .setMessage("Are you sure you want to delete this lift?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (!dao.deleteLift(liftId)) {
                            Toast.makeText(ViewLift.this, "Error deleting lift.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        //successfully deleted
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
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
        //this should always be true (we don't allow editing from ViewLift context)
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
}
