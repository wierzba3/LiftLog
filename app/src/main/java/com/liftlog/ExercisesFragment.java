package com.liftlog;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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


public class ExercisesFragment extends Fragment implements ExerciseInputDialog.ExerciseInputDialogListener
{

    public static final String LOG_TAG = "liftlog.BrowseExercises";

    public static final int REQUEST_CODE = 1;

    ListView listExercises;

    DataAccessObject dao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_browse_exercises);
        View view = inflater.inflate(R.layout.fragment_exercises, container, false);

        dao = new DataAccessObject(super.getActivity());

        createContents(view);
        loadExercises();

        return view;
    }

    private void createContents(View view)
    {
        setHasOptionsMenu(true);

        listExercises = (ListView) view.findViewById(R.id.list_exercises_fragment);

        ActionBar actionBar = super.getActivity().getActionBar();
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
        if(exercises == null)
        {
            return;
        }

        List<Exercise> exerciseList = new ArrayList<>(exercises.values());
        ArrayAdapter<Exercise> adapter = new ArrayAdapter<Exercise>(super.getActivity(), android.R.layout.simple_list_item_1, exerciseList);
        listExercises.setAdapter(adapter);
    }


    private void doAdd(long id)
    {

        Exercise exercise = new Exercise();
        exercise.setId(id);

        ExerciseInputDialog dialog = ExerciseInputDialog.newInstance(exercise);
        dialog.setTargetFragment(this, REQUEST_CODE);
        dialog.show(getFragmentManager().beginTransaction(), "ExerciseInputDialog");

    }

    public void doEdit(Exercise exercise)
    {
        ExerciseInputDialog dialog = ExerciseInputDialog.newInstance(exercise);
        dialog.setTargetFragment(this, REQUEST_CODE);
        dialog.show(getFragmentManager().beginTransaction(), "ExerciseInputDialog");
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_exercises, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
                Toast.makeText(super.getActivity(), "Error updating exercise.", Toast.LENGTH_SHORT).show();
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

        new AlertDialog.Builder(super.getActivity())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Delete Exercise")
                .setMessage(msg)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        if(!dao.deleteExercise(exercise.getId()))
                        {
                            Toast.makeText(ExercisesFragment.super.getActivity(), "Error deleting exercise.", Toast.LENGTH_SHORT).show();
                            Log.d(LOG_TAG, "Error deleting exercise. id=" + exercise.getId() + "\tname=" + exercise.getName());
                        }

                        loadExercises();
                    }

                })
                .setNegativeButton("No", null)
                .show();





    }

}
