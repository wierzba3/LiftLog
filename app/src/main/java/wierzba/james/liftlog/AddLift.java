package wierzba.james.liftlog;

import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import wierzba.james.liftlog.models.DataAccessObject;
import wierzba.james.liftlog.models.Exercise;


public class AddLift extends ActionBarActivity {

    private DataAccessObject dao;

    public static final String LOG_TAG = "LiftLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lift);

        openOrCreateDatabase(DataAccessObject.DB_NAME, MODE_PRIVATE, null);
        dao = new DataAccessObject(this);

        Cursor c = dao.getData();
        Log.d(LOG_TAG, "db size = " + c.getCount());
        dao.deleteAll();
        c = dao.getData();
        Log.d(LOG_TAG, "db size after deleteAll = " + c.getCount());
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
    }

    /**
     * Handle Submit button onClick
     */
    public void doSubmit(View view)
    {


        try {
            EditText txtDay = (EditText) findViewById(R.id.txt_day);
            Spinner spnExercise = (Spinner) findViewById(R.id.spn_exercise);
            RadioButton rbtnWarmup = (RadioButton) findViewById(R.id.rbtn_warmup);
            EditText txtWeight = (EditText) findViewById(R.id.txt_weight);
            EditText txtSets = (EditText) findViewById(R.id.txt_sets);
            EditText txtReps = (EditText) findViewById(R.id.txt_reps);

            int day = Integer.parseInt(txtDay.getText().toString());
            String exercise = spnExercise.getSelectedItem().toString();
            boolean isWarmup = rbtnWarmup.isSelected();
            int weight = Integer.parseInt(txtWeight.getText().toString());
            int sets = Integer.parseInt(txtSets.getText().toString());
            int reps = Integer.parseInt(txtReps.getText().toString());

            dao.insert(day, exercise, weight, sets, reps);

            Cursor cursor = dao.getData();
            cursor.moveToFirst();
            int i = 1;
            do {
                String id = cursor.getString(cursor.getColumnIndex(DataAccessObject.LIFT_COLUMN_ID));
                String l = cursor.getString(cursor.getColumnIndex(DataAccessObject.LIFT_COLUMN_LIFT_NAME));
                String w = cursor.getString(cursor.getColumnIndex(DataAccessObject.LIFT_COLUMN_WEIGHT));
                String s = cursor.getString(cursor.getColumnIndex(DataAccessObject.LIFT_COLUMN_SETS));
                String r = cursor.getString(cursor.getColumnIndex(DataAccessObject.LIFT_COLUMN_REPS));
                Log.d(LOG_TAG, "-------------\n record " + i);
                Log.d(LOG_TAG, id);
                Log.d(LOG_TAG, l);
                Log.d(LOG_TAG, w);
                Log.d(LOG_TAG, s);
                Log.d(LOG_TAG, r);
                i++;
            }while(cursor.moveToNext());
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(LOG_TAG, e.getMessage());
        }



    }


}
