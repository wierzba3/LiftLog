package wierzba.james.liftlog;

import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import wierzba.james.liftlog.models.DataAccessObject;


public class BrowseSessions extends ActionBarActivity {

    ListView listSessions;

    DataAccessObject dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createContents();

        setContentView(R.layout.activity_browse_sessions);
    }

    private void createContents()
    {
        listSessions = (ListView) findViewById(R.id.list_sessions);

        Cursor cursor = dao.getData();
        boolean next = cursor.moveToFirst();
        while(next)
        {

            next = cursor.moveToNext();
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
}
