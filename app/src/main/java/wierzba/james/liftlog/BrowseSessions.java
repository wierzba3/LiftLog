package wierzba.james.liftlog;

import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import wierzba.james.liftlog.models.DataAccessObject;
import wierzba.james.liftlog.models.Session;


public class BrowseSessions extends ActionBarActivity {

    ListView listSessions;

    DataAccessObject dao;

    private static final String LOG_TAG = "LiftLog.BrowseSessions";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_sessions);

        dao = new DataAccessObject(this);

        createContents();


    }

    private void createContents()
    {
        try {
            insertDummySessions();

            listSessions = (ListView) findViewById(R.id.list_sessions);
            List<Session> sessions = dao.selectSessions();
            Log.d(LOG_TAG, "sessions.size()=" + sessions.size());

            ArrayList<String> sessionLabels = new ArrayList<String>();

            for (Session session : sessions) sessionLabels.add(String.valueOf(session.getDate()));

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sessionLabels);

            listSessions.setAdapter(adapter);

            listSessions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {
                    Intent intent = new Intent(BrowseSessions.this, ViewSession.class);
                    String text = ((TextView)view).getText().toString();
                    intent.putExtra(ViewSession.ID_ARGUMENT_KEY, text);
                    startActivity(intent);
                }
            });
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_browse_sessions, menu);
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


    private void insertDummySessions()
    {
        dao.clearSessionTable();

        for(int i = 0; i < 10; i++)
        {
            Session session = new Session();
            session.setDate(i + 1);
            dao.insert(session);
        }
    }

}
