package com.liftlog;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import wierzba.james.liftlog.R;

import com.liftlog.common.DataAccessObject;
import com.liftlog.components.ExerciseInputDialog;

import com.liftlog.models.Exercise;


public class BrowseExercises extends AppCompatActivity implements ExerciseInputDialog.NoticeDialogListener {

    public static final String LOG_TAG = "liftlog.BrowseExercises";

    ListView listExercises;

    DataAccessObject dao;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_exercises);

        dao = new DataAccessObject(this);

        createContents();
        loadExercises();
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


        listExercises.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Exercise exercise = (Exercise) parent.getItemAtPosition(position);
                doEdit(exercise);
            }

        });
    }

    private void loadExercises()
    {
        Map<Long, Exercise> exercises = dao.selectExercises();
        if(exercises == null || exercises.size() < 1)
        {
            return;
        }

        List<Exercise> exerciseList = new ArrayList<>(exercises.values());
        ArrayAdapter<Exercise> adapter = new ArrayAdapter<Exercise>(this, android.R.layout.simple_list_item_1, exerciseList);
        listExercises.setAdapter(adapter);
    }


    private void doAdd(long id)
    {

        Exercise exercise = new Exercise();
        exercise.setId(id);

        ExerciseInputDialog dialog = ExerciseInputDialog.newInstance(exercise);
        dialog.show(getFragmentManager(), "ExerciseInputDialog");

    }

    public void doEdit(Exercise exercise)
    {
        ExerciseInputDialog dialog = ExerciseInputDialog.newInstance(exercise);
        dialog.show(getFragmentManager(), "ExerciseInputDialog");
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
                doAdd(-1);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDialogSaveClick(DialogFragment dialog, Exercise exercise)
    {
        if(exercise == null) return;
        long id = exercise.getId();
        if(id == -1)
        {
            dao.insert(exercise);
        }
        else
        {
            if(!dao.update(exercise))
            {
                Toast.makeText(this, "Error updating exercise.", Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG, "Error updating exercise.");
            }
        }

        //reload exercises
        loadExercises();
    }

    @Override
    public void onDialogCancelClick(DialogFragment dialog)
    {
        //do nothing
    }

    public void onDialogDeleteClick(DialogFragment dialog, final Exercise exercise)
    {
        if(exercise == null || exercise.getId() == -1)
        {
            Log.d(LOG_TAG, "Error handling delete exercise button click. Exercise is null|empty");
            return;
        }

        String msg = "Are you sure you want to delete this Exercise?";
        //TODO
        /*
        if(exercise references existing lifts)
        {
            msg = "Are you sure you want to delete this Exercise?\nThis Exercise is currently referenced by existing Lifts."
        }
         */

        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Delete Exercise")
                .setMessage(msg)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        if(!dao.deleteExercise(exercise.getId()))
                        {
                            Toast.makeText(BrowseExercises.this, "Error deleting exercise.", Toast.LENGTH_SHORT).show();
                            Log.d(LOG_TAG, "Error deleting exercise. id=" + exercise.getId() + "\tname=" + exercise.getName());
                        }

                    }

                })
                .setNegativeButton("No", null)
                .show();




        loadExercises();
    }

}
