package wierzba.james.liftlog;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import wierzba.james.liftlog.models.Lift;
import wierzba.james.liftlog.models.Session;


public class ViewSession extends ActionBarActivity {

    /**
     * The key for this intent's extended data: the id of the session (-1 if new instance)
     */
    public static final String SESSION_ID_KEY = "session_id";

    private static final String LOG_TAG = "LiftLog.ViewSession";

    private DataAccessObject dao;

    private ListView listLifts;

    private long sessionId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_session);

        dao = new DataAccessObject(this);

        Intent intent  = getIntent();
        String idString = intent.getStringExtra(SESSION_ID_KEY);
        try
        {
            sessionId = Long.parseLong(idString);
        }
        catch(NumberFormatException ex)
        {
            sessionId = -1;
            Log.d(LOG_TAG, "ViewSession intent extended data is badly formatted: session_id");
        }

        Log.d(LOG_TAG, "LIFT_ID_KEY=" + idString);


        createContents();
        loadSession(sessionId);
    }

    private void createContents()
    {
        listLifts = (ListView) findViewById(R.id.list_lifts);

        listLifts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(ViewSession.this, ViewLift.class);
                String text = ((TextView)view).getText().toString();
                intent.putExtra(ViewLift.LIFT_ID_KEY, text);
                intent.putExtra(ViewLift.SESSION_ID_KEY, sessionId);
                startActivity(intent);
            }
        });
    }

    private void loadSession(long id)
    {
        dao.insertDummyLifts();


        Session session = dao.selectSession(sessionId);


        if(session == null)
        {
            Toast.makeText(this, "error loading training session data", Toast.LENGTH_LONG);
        }

        List<Lift> lifts = session.getLifts();

        ArrayList<String> liftLabels = new ArrayList<String>();

        for(Lift lift : lifts) liftLabels.add("" + lift.getId());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, liftLabels);
        listLifts.setAdapter(adapter);
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
