package com.liftlog;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.liftlog.data.DataAccessObject;
import com.liftlog.components.ExerciseInputDialog;
import com.liftlog.models.Category;
import com.liftlog.models.Exercise;
import com.liftlog.models.Lift;



public class ExercisesFragment extends Fragment implements ExerciseInputDialog.ExerciseInputDialogListener
{

    public static final String LOG_TAG = "liftlog.BrowseExercises";

//    public static final int REQUEST_CODE = 1;

//    private ListView listExercises;
    private ExpandableListView exListExercises;
    private ExerciseExpendableListAdapter exListAdapter;

    DataAccessObject dao;

    private enum InputChoice
    {
        EXERCISE,
        CATEGORY;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_browse_exercises);
        View view = inflater.inflate(R.layout.fragment_exercises, container, false);

        dao =  new DataAccessObject(super.getActivity());

        createContents(view);
        loadExercises();

        return view;
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState)
    {
        super.onViewStateRestored(savedInstanceState);

    }


    private void createContents(View view)
    {
        setHasOptionsMenu(true);




        ActionBar actionBar = super.getActivity().getActionBar();
//        if(actionBar == null) actionBar =
        if (actionBar != null)
        {
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.show();
        }

//        listExercises = (ListView) view.findViewById(R.id.list_exercises_fragment);
//        listExercises.setOnItemClickListener(new AdapterView.OnItemClickListener()
//        {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
//            {
//                Exercise exercise = (Exercise) parent.getItemAtPosition(position);
//                doEdit(exercise);
//            }
//
//        });


        exListExercises = (ExpandableListView) view.findViewById(R.id.exList_exercises);
        Map<Category, List<Exercise>> categoryMap = dao.selectCategoryMap(false);
        exListAdapter = new ExerciseExpendableListAdapter(super.getActivity(), categoryMap);
        exListExercises.setAdapter(exListAdapter);


    }

    public void expandListGroupItems()
    {
        if(exListAdapter == null || exListExercises == null) return;
        for(int i = 0; i < exListAdapter.getGroupCount(); i++)
        {
            exListExercises.expandGroup(i);
        }
    }


    public void loadExercises()
    {
        if( super.getActivity() == null)
        {
            //TODO figure out why this is returning null. Perhaps implement MainActivity as a singleton?
            return;
        }

        Map<Long, Exercise> exercises = dao.selectExerciseMap(false);
        if (exercises == null)
        {
            return;
        }

        List<Exercise> exerciseList = new ArrayList<>(exercises.values());
        Exercise dummyExercise = new Exercise();
        dummyExercise.setId(-1);
        dummyExercise.setName("<Add New>");
        exerciseList.add(dummyExercise);
        Collections.sort(exerciseList, Exercise.byNameDummyFirst);

//        ArrayAdapter<Exercise> adapter = new ArrayAdapter<Exercise>(super.getActivity(), android.R.layout.simple_list_item_1, exerciseList);
//        listExercises.setAdapter(adapter);

        Map<Category, List<Exercise>> categoryMap = dao.selectCategoryMap(false);
        ExerciseExpendableListAdapter exListAdapter = new ExerciseExpendableListAdapter(super.getActivity(), categoryMap);
        exListExercises.setAdapter(exListAdapter);
    }


    private void doAddExercise(long id)
    {
        Exercise exercise = new Exercise();
        exercise.setNew(true);
        exercise.setId(id);

        ExerciseInputDialog dialog = ExerciseInputDialog.newInstance(exercise);
        dialog.setTargetFragment(this, ExerciseInputDialog.RequestType.DEFAULT.getValue());
        dialog.show(getFragmentManager().beginTransaction(), "ExerciseInputDialog");

    }


    private void doAddCategory()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(super.getActivity());
        builder.setTitle("New Category Name");

        // Set up the input
        final EditText input = new EditText(super.getActivity());
        input.setSingleLine();

        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String categoryInput = input.getText().toString();
                addCategory(categoryInput);
                ExercisesFragment.this.loadExercises();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void addCategory(String name)
    {
        Category category = new Category();
        category.setName(name);
        category.setNew(true);
        dao.insert(category);
    }



    public void promptExerciseOrCategory()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(super.getActivity());
        builder.setTitle("Type to add");
        builder.setPositiveButton("Exercise", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //do nothing
                doAddExercise(-1);
            }
        });
//        builder.setNegativeButton("Exercise", new DialogInterface.OnClickListener()
//        {
//            @Override
//            public void onClick(DialogInterface dialog, int which)
//            {
//
//            }
//        });
        builder.setNeutralButton("Category", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                doAddCategory();
            }
        });
        builder.show();
    }

    public void doEdit(Exercise exercise)
    {
        ExerciseInputDialog dialog = ExerciseInputDialog.newInstance(exercise);
        dialog.setTargetFragment(this, ExerciseInputDialog.RequestType.DEFAULT.getValue());
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

        switch (id)
        {
            case R.id.action_add_exercise:
                promptExerciseOrCategory();
                break;

            case R.id.action_edit_exercise:
                //TODO prompt for edit/delete of Exercise|Category
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDialogSaveClick(DialogFragment dialog, Exercise exercise)
    {
        if (exercise == null) return;
        long id = exercise.getId();
        if (id == -1)
        {
            dao.insert(exercise);
        }
        else
        {
            if (!dao.update(exercise))
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
        if (exercise == null || exercise.getId() == -1)
        {
            Log.d(LOG_TAG, "Error handling delete exercise button click. Exercise is null|empty");
            return;
        }

        String msg = "Are you sure you want to delete this Exercise?";

        //search all existing Lifts to check whether any of them reference this lift that the user wants to delete
        List<Lift> lifts = dao.selectLifts(false);
        boolean found = false;
        for (Lift lift : lifts)
        {
            if (lift.getExerciseId() == exercise.getId()) found = true;
        }
        //exercise is referencing existing lifts, give relevant warning
        if (found)
        {
            msg = "Are you sure you want to delete this Exercise?\nThis Exercise is currently referenced by existing Lifts.";
            new AlertDialog.Builder(super.getActivity())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Delete Exercise")
                    .setMessage(msg)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
//                            if (!dao.deleteExercise(exercise.getId()))
//                            {
//                                Toast.makeText(ExercisesFragment.super.getActivity(), "Error deleting exercise.", Toast.LENGTH_SHORT).show();
//                                Log.d(LOG_TAG, "Error deleting exercise. id=" + exercise.getId() + "\tname=" + exercise.getName());
//                            }
                            exercise.setName("?");
                            exercise.setValid(false);
                            exercise.setDeleted(true);
                            dao.update(exercise);
                            loadExercises();
                        }

                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    return;
                                }

                            }
                    )
                    .show();
        }
        else
        //Exercise is not referencing existing lifts, give generic message.
        {
            new AlertDialog.Builder(super.getActivity())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Delete Exercise")
                    .setMessage(msg)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            exercise.setDeleted(true);
//                        if (!dao.deleteExercise(exercise.getId()))
                            if (!dao.update(exercise))
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


    private class ExerciseExpendableListAdapter extends BaseExpandableListAdapter
    {
        public ExerciseExpendableListAdapter(Context ctx, Map<Category, List<Exercise>> categoryMap)
        {
            categories = new ArrayList<Category>();
            exerciseLists = new ArrayList<List<Exercise>>();

            if(categoryMap == null) return;
//            Collections.sort(allLifts);


            Category uncategorized = null;
            List<Exercise> uncategorizedExercises = null;
            for(Category category : categoryMap.keySet())
            {
                List<Exercise> exercises = categoryMap.get(category);

                if(category.getId() == -1)
                {
                    uncategorized = category;
                    uncategorizedExercises = exercises;
                    continue;
                }


                Collections.sort(exercises, Exercise.byNameDummyLast);

                //add the Category, List<Exercise> in parallel
                categories.add(category);
                exerciseLists.add(exercises);
            }
            if(uncategorized != null)
            {
                categories.add(uncategorized);
                exerciseLists.add(uncategorizedExercises);
            }

        }

//        private Comparator<List<Lift>> liftsComparator = new Comparator<List<Lift>>(){
//            @Override
//            public int compare(List<Lift> l1, List<Lift> l2)
//            {
//                if((l1 == null || l1.size() == 0) && (l2 == null || l2.size() == 0)) return 0;
//                else if(l1 == null || l1.size() == 0) return -1;
//                else if(l2 == null || l2.size() == 0) return 1;
//                return l1.get(0).compareTo(l2.get(0));
//            }
//        };

        private List<List<Exercise>> exerciseLists;
        private List<Category> categories;

        @Override
        public boolean isChildSelectable(int i, int i1)
        {
            return true;
        }

        @Override
        public int getGroupCount()
        {
            return categories.size();
        }

        @Override
        public int getChildrenCount(int i)
        {
            List<Exercise> exercises = exerciseLists.get(i);
            if(exercises == null) return 0;
            return exercises.size();
        }

        @Override
        public Object getGroup(int i)
        {
            return categories.get(i);
        }

        @Override
        public Object getChild(int i, int j)
        {
            return exerciseLists.get(i).get(j);
        }

        @Override
        public long getGroupId(int i)
        {
            return categories.get(i).getId();
        }

        @Override
        public long getChildId(int i, int j)
        {
            return exerciseLists.get(i).get(j).getId();
        }

        @Override
        public boolean hasStableIds()
        {
            return false;
        }

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup)
        {
            if (view == null)
            {
                LayoutInflater vi = (LayoutInflater) ExercisesFragment.super.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = vi.inflate(R.layout.exercise_group_item, viewGroup, false);
            }

            Category category = categories.get(i);
            TextView lblCategory = (TextView) view.findViewById(R.id.lbl_exercise_group);
            lblCategory.setText(category.getName());


            return view;
        }

        @Override
        public View getChildView(int i, int j, boolean b, View view, ViewGroup viewGroup)
        {
            if (view == null)
            {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = vi.inflate(R.layout.exercise_item, viewGroup, false);
            }

            final Exercise exercise = exerciseLists.get(i).get(j);


            TextView lblExercise = (TextView) view.findViewById(R.id.lbl_exercise);
            lblExercise.setText(exercise.toString());

            lblExercise.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    doEdit(exercise);
                }
            });

            return view;
        }
    }


}
