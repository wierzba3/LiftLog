package com.liftlog;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.liftlog.common.Util;
import com.liftlog.data.DataAccessObject;
import com.liftlog.models.Exercise;
import com.liftlog.models.Lift;
import com.liftlog.models.Session;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BestLiftActivity extends AppCompatActivity
{

    private static final String LOG_TAG = "BestLiftActivity";

    private DataAccessObject dao;

    private Spinner spnExercise;
    private EditText txtReps;
    private TextView lblResult;
    private TextView lblResultExercise;
    private TextView lblDate;
    private static final String SPN_SET_ANY = "Any";
    private Spinner spnSets;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_best_lift);

        dao = new DataAccessObject(this);

        createContents();
    }



    private void createContents()
    {

        ActionBar actionBar = this.getActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.show();
        }

        spnExercise = (Spinner) findViewById(R.id.spn_exercise_best);
        txtReps = (EditText) findViewById(R.id.txt_reps_best);
        lblResult = (TextView) findViewById(R.id.lbl_result);
        lblResultExercise = (TextView) findViewById(R.id.lbl_result_exercise);
        lblDate = (TextView) findViewById(R.id.lbl_date);
        spnSets = (Spinner) findViewById(R.id.pck_sets);

        /*
            ArrayAdapter<Exercise> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, exerciseList);
            adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            spnExercise.setAdapter(adapter);
         */
        ArrayList<String> spnSetItemList = new ArrayList<String>();
        spnSetItemList.add(SPN_SET_ANY);
        for(int i = 1; i <= 100; i++)
        {
            spnSetItemList.add(String.valueOf(i));
        }
        ArrayAdapter<String> spnSetAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spnSetItemList);
        spnSetAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spnSets.setAdapter(spnSetAdapter);


        loadExercises();
    }

    private void loadExercises()
    {
        List<Exercise> exercises = dao.selectExercises(false);
        if(exercises != null && !exercises.isEmpty())
        {
            Collections.sort(exercises, Exercise.byNameDummyLast);

            ArrayAdapter<Exercise> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, exercises);
            adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            spnExercise.setAdapter(adapter);
        }
        else
        {
            Toast.makeText(this, "No exercises exist to search for.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Handle "Calculate" button click
     * @param view
     */
    public void doSearch(View view)
    {
        lblResultExercise.setText("");
        lblResult.setText("");
        lblDate.setText("");

        Exercise exercise = (Exercise) spnExercise.getSelectedItem();
        if (exercise == null)
        {
            Toast.makeText(this, "Unknown exercise.", Toast.LENGTH_SHORT).show();
            return;
        }

        int reps;
        try
        {
            reps = Integer.parseInt(txtReps.getText().toString());
        } catch (NumberFormatException e)
        {
            Toast.makeText(this, "Invalid reps.", Toast.LENGTH_SHORT).show();
            return;
        }

        int sets = -1;
        /*
            try to parse the selected sets item, and ignore the possible exception,
            as this exception would only happen from the selected item being "Any",
            which means sets should be set to -1 (which it already is)
        */
        try
        {
            String setsItem = (String) spnSets.getSelectedItem();
            sets = Integer.parseInt(setsItem);
        } catch(NumberFormatException ex) {}

        Lift lift = dao.selectBestLift(exercise.getId(), reps, sets);
        String msg = "Best " + reps + " rep set: " ;
        if (lift == null)
        {
            lblResult.setText("No lift found");
            return;
        }

        double w = lift.getWeight();
        msg += (w == Math.ceil(w) ? String.valueOf((int)w) : String.valueOf(w));


        lblResultExercise.setText(exercise.getName());
        lblResult.setText(msg);

        Session session = dao.selectSession(lift.getSessionId());
        if(session != null)
        {
            String date = Util.DATE_FORMAT_SHORT.print(session.getDate());
            lblDate.setText("Performed on " + date);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
    }

    protected void onRestoreInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }




    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        //TODO create a_m menu for this activity
//       inflater.inflate(R.menu.menu_view_session, menu);
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
            case R.id.home:
                break;
        }

        return super.onOptionsItemSelected(item);
    }



}
