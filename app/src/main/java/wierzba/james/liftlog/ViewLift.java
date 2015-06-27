package wierzba.james.liftlog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import wierzba.james.liftlog.models.Exercise;
import wierzba.james.liftlog.models.Lift;


public class ViewLift extends ActionBarActivity {

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

    private double weightIncrement = 5.0;

    Spinner spnExercise;
    RadioButton rbtnWarmup;
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
        String liftIdString = intent.getStringExtra(LIFT_ID_KEY);
        String sessionIdString = intent.getStringExtra(SESSION_ID_KEY);
        try
        {
            liftId = Long.parseLong(liftIdString);
            sessionId = Long.parseLong(sessionIdString);
        }
        catch(Exception ex)
        {
            Log.d(LOG_TAG, "AddLift intent extended data is badly formatted: session_id");
        }



        Log.d(LOG_TAG, "LIFT_ID_KEY=" + liftIdString);

        createContents();
        loadLift(liftId);
    }

    private void loadLift(long id)
    {
        if(id < 0)
        {
            txtWeight.setText(String.valueOf(0));
            pckReps.setValue(5);
            pckSets.setValue(5);
            rbtnWarmup.setChecked(false);
        }
        else {
            Lift lift = dao.selectLift(id);
            txtWeight.setText(String.valueOf(lift.getWeight()));
            pckReps.setValue(lift.getReps());
            pckSets.setValue(lift.getSets());
            rbtnWarmup.setChecked(lift.isWarmup());
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_lift, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void createContents(){
        Spinner spinner = (Spinner) findViewById(R.id.spn_exercise);

        String[] staticExercises = {
                Exercise.Squat().getName(),
                Exercise.BenchPress().getName(),
                Exercise.Deadlift().getName(),
                Exercise.Press().getName()
        };
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, staticExercises);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);

        openOrCreateDatabase(DataAccessObject.DB_NAME, MODE_PRIVATE, null);


        //initialize control references
        spnExercise = (Spinner) findViewById(R.id.spn_exercise);
        pckSets = (NumberPicker) findViewById(R.id.pck_sets);
        rbtnWarmup = (RadioButton) findViewById(R.id.rbtn_warmup);
//        pckWeight = (NumberPicker) findViewById(R.id.pck_weight);
        txtWeight = (EditText) findViewById(R.id.txt_weight);
        pckReps = (NumberPicker) findViewById(R.id.pck_reps);
        pckSets = (NumberPicker) findViewById(R.id.pck_sets);
        btnSave = (Button) findViewById(R.id.btn_save);

//        txtDay = (EditText) findViewById(R.id.txt_day);
//
//        int numValues = 400;
//        String[] weightValues = new String[numValues];
//        double weightValue = 0;
//        for(int i = 0; i < numValues; i++)
//        {
//            weightValue += weightIncrement;
//            weightValues[i] = String.valueOf(weightValue);
//        }
//        pckWeight.setMinValue(1);
//        pckWeight.setMaxValue(numValues);
//        pckWeight.setWrapSelectorWheel(false);
//        pckWeight.setDisplayedValues(weightValues);

        pckReps.setMinValue(0);
        pckReps.setMaxValue(100);
        pckReps.setWrapSelectorWheel(false);

        pckSets.setMinValue(0);
        pckSets.setMaxValue(100);



        pckSets.setWrapSelectorWheel(false);
    }

    /**
     * Handle Submit button onClick
     * @param view
     */
    public void doSave(View view)
    {
        String exercise = spnExercise.getSelectedItem().toString();
        boolean isWarmup = rbtnWarmup.isChecked();
        int weight = Integer.parseInt(txtWeight.getText().toString());

        int reps = pckReps.getValue();
        int sets = pckSets.getValue();

        //TODO get actual id
        int exerciseId = 5;

        Lift lift = new Lift();
        lift.setExerciseId(exerciseId);
        lift.setSessionId(sessionId);
        lift.setWeight(weight);
        lift.setSets(sets);
        lift.setReps(reps);
        lift.setWarmup(isWarmup);

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
     * @param view
     */
    public void doDelete(View view)
    {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Deleting Lift")
                .setMessage("Are you sure you want to delete this lift?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (liftId == -1)
                        {
                            Toast.makeText(ViewLift.this, "Can't delete. This Lift has not been created yet.", Toast.LENGTH_LONG);
                            return;
                        }
                        if(!dao.deleteLift(liftId))
                        {
                            Toast.makeText(ViewLift.this, "Error deleting lift.", Toast.LENGTH_LONG);
                            return;
                        }
                        //successfully deleted
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }


}
