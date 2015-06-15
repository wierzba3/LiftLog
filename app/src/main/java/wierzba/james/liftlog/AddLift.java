package wierzba.james.liftlog;

import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.Spinner;

import java.util.Date;

import wierzba.james.liftlog.models.DataAccessObject;
import wierzba.james.liftlog.models.Exercise;
import wierzba.james.liftlog.models.Lift;


public class AddLift extends ActionBarActivity {

    public static final String LOG_TAG = "LiftLog";

    private DataAccessObject dao;

    private double weightIncrement = 1.0;

    Spinner spnExercise;
    RadioButton rbtnWarmup;
    NumberPicker pckWeight;
    NumberPicker pckReps;
    NumberPicker pckSets;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lift);

        createContents();
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
        dao = new DataAccessObject(this);

        //initialize control references
        spnExercise = (Spinner) findViewById(R.id.spn_exercise);
        pckSets = (NumberPicker) findViewById(R.id.pck_sets);
        rbtnWarmup = (RadioButton) findViewById(R.id.rbtn_warmup);
        pckWeight = (NumberPicker) findViewById(R.id.pck_weight);
        pckReps = (NumberPicker) findViewById(R.id.pck_reps);
        pckSets = (NumberPicker) findViewById(R.id.pck_sets);


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

            int day = 3;//Integer.parseInt(txtDay.getText().toString());
            String exercise = spnExercise.getSelectedItem().toString();
            boolean isWarmup = rbtnWarmup.isSelected();
            int weight = pckWeight.getValue();
            int reps = pckReps.getValue();
            int sets = pckSets.getValue();


            Lift lift = new Lift();
            lift.setTime(day);
            lift.setExerciseName(exercise);
            lift.setWeight(weight);
            lift.setSets(sets);
            lift.setReps(reps);

            dao.insert(lift);

            Cursor cursor = dao.getData();
            cursor.moveToFirst();
            int i = 1;
            do
            {
                //TODO get time from user
                long time = System.currentTimeMillis();
                String l = cursor.getString(cursor.getColumnIndex(DataAccessObject.LIFT_COLUMN_LIFT_NAME));
                String w = cursor.getString(cursor.getColumnIndex(DataAccessObject.LIFT_COLUMN_WEIGHT));
                String s = cursor.getString(cursor.getColumnIndex(DataAccessObject.LIFT_COLUMN_SETS));
                String r = cursor.getString(cursor.getColumnIndex(DataAccessObject.LIFT_COLUMN_REPS));
                Log.d(LOG_TAG, "-------------\n record " + i);
                Date d = new Date(time);
                Log.d(LOG_TAG, d.toString());
                Log.d(LOG_TAG, l);
                Log.d(LOG_TAG, w);
                Log.d(LOG_TAG, s);
                Log.d(LOG_TAG, r);
                i++;
            }
            while(cursor.moveToNext());
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(LOG_TAG, e.getMessage());
        }



    }


}
