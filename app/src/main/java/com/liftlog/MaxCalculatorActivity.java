package com.liftlog;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.liftlog.data.DataAccessObject;
import com.liftlog.models.Exercise;
import com.liftlog.models.Lift;
import com.liftlog.models.Session;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MaxCalculatorActivity extends AppCompatActivity
{
//public class ViewSessionActivity extends Activity {

    /**
     * The key for this intent's extended data: the id of the session (-1 if new instance)
     */
    public static final String EXERCISE_ID_KEY = "exercise_id";

    private static final String LOG_TAG = "MaxCalculatorActivity";

    private DataAccessObject dao;

    private EditText txtWeight;
    private EditText txtReps;
    private TextView lblMax;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_max_calc);

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

        txtWeight = (EditText) findViewById(R.id.txt_weight_calc);
        txtReps = (EditText) findViewById(R.id.txt_reps_calc);
        lblMax = (TextView) findViewById(R.id.lbl_max);
    }

    /**
     * Handle "Calculate" button click
     * @param view
     */
    public void doCalculate(View view)
    {
        String weightValue = txtWeight.getText().toString().trim();
        double weight;
        try
        {
            weight = Double.parseDouble(weightValue);
        } catch (NumberFormatException e)
        {
            Toast.makeText(this, "Invalid Weight value: " + weightValue, Toast.LENGTH_SHORT).show();
            return;
        }

        String repsValue = txtReps.getText().toString().trim();
        int reps;
        try
        {
            reps = Integer.parseInt(repsValue);
        } catch (NumberFormatException e)
        {
            Toast.makeText(this, "Invalid Reps value: " + repsValue, Toast.LENGTH_SHORT).show();
            return;
        }

//        double max = weight / (1.0278 - (0.0278 * reps));
        double max = (weight * 0.033 * reps) + weight;
        lblMax.setText(String.valueOf(Math.round(max)));
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
