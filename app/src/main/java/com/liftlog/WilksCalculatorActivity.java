package com.liftlog;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.liftlog.data.DataAccessObject;

public class WilksCalculatorActivity extends AppCompatActivity
{
//public class ViewSessionActivity extends Activity {

    /**
     * The key for this intent's extended data: the id of the session (-1 if new instance)
     */
    public static final String EXERCISE_ID_KEY = "exercise_id";

    private static final String LOG_TAG = "WilksCalculatorActivity";

    private DataAccessObject dao;

    private RadioButton rbtnLB;
    private RadioButton rbtnKG;
    private RadioButton rbtnMale;
    private RadioButton rbtnFemale;
    private EditText txtSquat;
    private EditText txtBench;
    private EditText txtDeadlift;
    private EditText txtBodyweight;
    private TextView lblWilks;

    //Wilks formula coefficients for males
    private final double A_M = -216.0475144;
    private final double B_M = 16.2606339;
    private final double C_M = -0.002388645;
    private final double D_M = -0.00113732;
    private final double E_M = 7.01863 * Math.pow(10, -6);
    private final double F_M = -1.291 * Math.pow(10, -8);

    //Wilks formula coefficients for females
    private final double A_F = 594.31747775582;
    private final double B_F = -27.23842536447;
    private final double C_F = 0.82112226871;
    private final double D_F = -0.00930733913;
    private final double E_F = 0.00004731582;
    private final double F_F = -0.00000009054;

    //Value to multiple kg by to get lb
    private final double KILO_LB_RATIO = 2.20462;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wilks_calc);

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

        rbtnLB = (RadioButton) findViewById(R.id.rbtn_lb);
        rbtnKG = (RadioButton) findViewById(R.id.rbtn_kg);
        rbtnMale = (RadioButton) findViewById(R.id.rbtn_male);
        rbtnFemale = (RadioButton) findViewById(R.id.rbtn_female);
        txtSquat = (EditText) findViewById(R.id.txt_squat);
        txtBench = (EditText) findViewById(R.id.txt_bench);
        txtDeadlift = (EditText) findViewById(R.id.txt_deadlift);
        txtBodyweight = (EditText) findViewById(R.id.txt_bodyweight);
        lblWilks = (TextView) findViewById(R.id.lbl_wilks);

//        RadioGroup rgUnits = new RadioGroup(this);
//        rgUnits.addView(rbtnKG);
//        rgUnits.addView(rbtnLB);
//
//        RadioGroup rgSex = new RadioGroup(this);
//        rgSex.addView(rbtnMale);
//        rgSex.addView(rbtnFemale);
    }

    /**
     * Handle "Calculate" button click
     * @param view
     */
    public void doCalculateWilks(View view)
    {
        String squatString = txtSquat.getText().toString();
        String benchString = txtBench.getText().toString();
        String deadliftString = txtDeadlift.getText().toString();
        //bodyweight value
        String bodyweightString = txtBodyweight.getText().toString();

        double squatValue = 0.0, benchValue = 0.0, deadliftValue = 0.0, x = 0.0;
        try
        {
            squatValue = Double.parseDouble(squatString);
        }
        catch(NumberFormatException ex) {}
        try
        {
            benchValue = Double.parseDouble(benchString);
        }
        catch(NumberFormatException ex) {}
        try
        {
            deadliftValue = Double.parseDouble(deadliftString);
        }
        catch(NumberFormatException ex) {}
        try
        {
            x = Double.parseDouble(bodyweightString);
        }
        catch(NumberFormatException ex)
        {
            Toast.makeText(this, "Enter a_m valid bodyweight.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(rbtnLB.isChecked())
        {
            //convert to pounds
            squatValue /= KILO_LB_RATIO;
            benchValue /= KILO_LB_RATIO;
            deadliftValue /= KILO_LB_RATIO;
            x /= KILO_LB_RATIO;
        }

        /*
        bx = b * x
        cx2 = c * pow(x,2)
        dx3 = d * pow(x,3)
        ex4 = e * pow(x,4)
        fx5 = f * pow(x,5)
        coeff = 500 / ( a_m + ( bx ) + (cx2) + (dx3) + (ex4) + (fx5) )
         */

        double coeff = 0;
        if(rbtnMale.isChecked())
        {
            coeff = 500 / ( A_M + (B_M * x) + (C_M * Math.pow(x, 2)) + (D_M * Math.pow(x, 3)) + (E_M * Math.pow(x, 4)) + (F_M * Math.pow(x, 5)) );
        }
        else
        {
            coeff = 500 / ( A_F + (B_F * x) + (C_F * Math.pow(x, 2)) + (D_F * Math.pow(x, 3)) + (E_F * Math.pow(x, 4)) + (F_F * Math.pow(x, 5)) );
        }

        double squatWilks = coeff * squatValue;
        double benchWilks = coeff * benchValue;
        double deadliftWilks = coeff * deadliftValue;
        double wilks = squatWilks + benchWilks + deadliftWilks;

        lblWilks.setText(String.format("%.2f", wilks));
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
