package wierzba.james.liftlog;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import wierzba.james.liftlog.models.Session;
import wierzba.james.liftlog.wierzba.james.liftlog.utils.Util;


public class BrowseSessions extends AppCompatActivity {

    ListView listSessions;

    DataAccessObject dao;

    private static final String LOG_TAG = "LiftLog.BrowseSessions";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_sessions);

        dao = new DataAccessObject(this);

        createContents();
        loadSessions();
    }

    private void createContents()
    {
        listSessions = (ListView) findViewById(R.id.list_sessions);



        listSessions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(BrowseSessions.this, ViewSession.class);
                Session session = (Session) parent.getItemAtPosition(position);
                long sessionId = session.getId();
                if(sessionId == -1)
                {
                    //TODO prompt user for date option to create new session
                    long now = System.currentTimeMillis();
                    session.setDate(now);
                    sessionId = dao.insert(session);
                    if(sessionId == -1)
                    {
                        Toast.makeText(BrowseSessions.this, "", Toast.LENGTH_LONG).show();
                        Log.d(LOG_TAG, "Error inserting new session.");
                        return;
                    }
                }
                intent.putExtra(ViewSession.SESSION_ID_KEY, sessionId);
                startActivity(intent);
            }
        });



    }

    private void loadSessions()
    {
//            dao.insertDummySessions();
//        dao.test();
//        dao.clearSessionsTable();

        List<Session> sessions = dao.selectSessions();
        Session dummySession = new Session();
        dummySession.setId(-1);
        sessions.add(0, dummySession);

//            ArrayList<String> sessionLabels = new ArrayList<String>();
//            for (Session session : sessions) sessionLabels.add(String.valueOf(session.getDate()));

        ArrayAdapter<Session> adapter = new ArrayAdapter<Session>(this, android.R.layout.simple_list_item_1, sessions);
        listSessions.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_browse_sessions, menu);
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


    @Override
    public void onResume()
    {
        super.onResume();
        loadSessions();
    }


}
