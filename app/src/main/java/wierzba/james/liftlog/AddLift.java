package wierzba.james.liftlog;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.Spinner;

import wierzba.james.liftlog.models.Exercise;
import wierzba.james.liftlog.models.Lift;

/**
 * TODO
 * - Launch DatePickerDialog onClick of the date textbox
 */
public class AddLift extends ActionBarActivity {

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
    NumberPicker pckWeight;
    NumberPicker pckReps;
    NumberPicker pckSets;

    EditText txtDay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lift);
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
            liftId = -1;
            Log.d(LOG_TAG, "AddLift intent extended data is badly formatted: session_id");
        }



        Log.d(LOG_TAG, "LIFT_ID_KEY=" + liftIdString);

        createContents();
        loadLift(liftId);
    }

    private void loadLift(long id)
    {
        Lift lift = dao.selectLift(id);
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
        pckWeight = (NumberPicker) findViewById(R.id.pck_weight);
        pckReps = (NumberPicker) findViewById(R.id.pck_reps);
        pckSets = (NumberPicker) findViewById(R.id.pck_sets);
        txtDay = (EditText) findViewById(R.id.txt_day);

        int numValues = 400;
        String[] weightValues = new String[numValues];
        double weightValue = 0;
        for(int i = 0; i < numValues; i++)
        {
            weightValue += weightIncrement;
            weightValues[i] = String.valueOf(weightValue);
        }
        pckWeight.setMinValue(1);
        pckWeight.setMaxValue(numValues);
        pckWeight.setWrapSelectorWheel(false);
        pckWeight.setDisplayedValues(weightValues);

        pckReps.setMinValue(0);
        pckReps.setMaxValue(100);
        pckReps.setWrapSelectorWheel(false);

        pckSets.setMinValue(0);
        pckSets.setMaxValue(100);



        pckSets.setWrapSelectorWheel(false);
    }

    /**
     * Handle Submit button onClick
     */
    public void doSubmit(View view)
    {


        try {

//            int day = Integer.parseInt(txtDay.getText().toString());
            String exercise = spnExercise.getSelectedItem().toString();
            boolean isWarmup = rbtnWarmup.isSelected();
            int weight = pckWeight.getValue();
            int reps = pckReps.getValue();
            int sets = pckSets.getValue();

            //TODO get actual id
            int exerciseId = 5;

            Lift lift = new Lift();
            lift.setExerciseId(exerciseId);
            lift.setSessionId(sessionId);
//            lift.setDate(day);
            lift.setWeight(weight);
            lift.setSets(sets);
            lift.setReps(reps);

            long id = dao.insert(lift);

            Log.d(LOG_TAG, "inserted lift object, id=" + id);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(LOG_TAG, e.getMessage());
        }



    }


}
