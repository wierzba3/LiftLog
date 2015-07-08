package com.liftlog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;

import wierzba.james.liftlog.R;

/**
 *  TODO
 *
 * Implement now:
 * - Action bar icon buttons on bottom of MainActivity to navigate to the Primary screens (BrowseSessions, BrowseExercises, ...)
 * - Option to <Add new> Exercise from Add Lift ComboBox
 * - Group Lifts in Session by the exercise, then after selecting exercise, show the individual lifts.
 *      Some tree-like structure?
 * - Add increment set button on the lifts ListView on ViewSession
 * - Tabular view of lifts
 * - Decide what to do when the user deletes an exercise that is referenced by 1 or more lifts
 *      I think the best option would be to not delete it, but change the name to "undefined" or something
 *      and offer the user the choice to re-define it when it is shown as "undefined"
 *
 *
 * Bugs:
 * - Remove "delete" button on ExerciseInputDialog when the user is entering a new exercise
 *
 *
 * Implement in the future:
 * - Programmable training routines. Define rules that the user can set for an exercise.
 *      Display planned lifts separately from the completed lifts in the sessions.
 *      e.g. repeat selected lift every M/W/F, increase weight each day/week
 * - Hi-scores. Users can submit their video of lifts to be reviewed and then entered in high scores.
 *
 */

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "LiftLog";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Util.copyDbFile();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
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

    /**
     * Start the activity to add a lift
     * @param view The view
     */
    public void startAddLift(View view)
    {
        Intent intent = new Intent(this, ViewLift.class);
        this.startActivity(intent);
    }

    /**
     * Start the activity to browse previous workout sessions
     * @param view The view
     */
    public void startBrowseSessions(View view)
    {
        Intent intent = new Intent(this, BrowseSessions.class);
        this.startActivity(intent);
    }


    /**
     * Start the activity to browse previous workout sessions
     * @param view The view
     */
    public void startBrowseExercises(View view)
    {
        Intent intent = new Intent(this, BrowseExercises.class);
        this.startActivity(intent);
    }




}
