package wierzba.james.liftlog;

import android.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import wierzba.james.liftlog.models.Exercise;


public class BrowseExercises extends AppCompatActivity {

    ListView listExercises;

    DataAccessObject dao;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_exercises);

        dao = new DataAccessObject(this);

        createContents();
    }

    private void createContents()
    {
        listExercises = (ListView) findViewById(R.id.list_exercises);

        ActionBar actionBar = this.getActionBar();
//        if(actionBar == null) actionBar =
        if(actionBar != null)
        {
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.show();
        }
    }

    private void loadExercises()
    {
        List<Exercise> exercises = dao.selectExercises();
        if(exercises == null || exercises.size() < 1)
        {
            return;
        }

        ArrayAdapter<Exercise> adapter = new ArrayAdapter<Exercise>(this, android.R.layout.simple_list_item_1, exercises);
        listExercises.setAdapter(adapter);
    }


    private void doAdd()
    {
        Toast.makeText(this, "...add exercise...", Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_browse_exercises, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id)
        {
            case R.id.action_add_exercise:
                doAdd();
                break;
        }

        return super.onOptionsItemSelected(item);
    }




}
