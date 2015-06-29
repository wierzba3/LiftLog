package wierzba.james.liftlog;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;

/**
 *
 * Application TODO
 * - Relevant labels for Lift and Session ListView
 * - New Session option
 * - Exercise table in db
 * - Change passing of ids from String to long
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
     * @param view
     */
    public void startAddLift(View view)
    {
        Intent intent = new Intent(this, ViewLift.class);
        this.startActivity(intent);
    }

    /**
     * Start the activity to browse previous workout sessions
     * @param view
     */
    public void startBrowseSessions(View view)
    {
        Intent intent = new Intent(this, BrowseSessions.class);
        this.startActivity(intent);
    }


    /**
     * Start the activity to browse previous workout sessions
     * @param view
     */
    public void startBrowseExercises(View view)
    {
        Intent intent = new Intent(this, BrowseExercises.class);
        this.startActivity(intent);
    }

    private void loadLocalUserData()
    {

    }


}
