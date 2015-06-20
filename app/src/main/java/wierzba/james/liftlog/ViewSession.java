package wierzba.james.liftlog;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class ViewSession extends ActionBarActivity {

    /**
     * The key for the data containing the session_id that is passed in via putExtra(key, value)
     */
    public static final String ID_ARGUMENT_KEY = "session_id";

    private static final String LOG_TAG = "LiftLog.ViewSession";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_session);

        Intent intent  = getIntent();
        String idString = intent.getStringExtra(ID_ARGUMENT_KEY);
        Log.d(LOG_TAG,"ID_ARGUMENT_KEY=" + idString);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_session, menu);
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
}
