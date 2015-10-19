package com.liftlog;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.liftlog.common.Util;
import com.liftlog.data.DataAccessObject;
import com.liftlog.components.ExerciseInputDialog;
import com.liftlog.models.Category;
import com.liftlog.models.Exercise;
import com.liftlog.models.Lift;



public class ExercisesFragment extends Fragment implements ExerciseInputDialog.ExerciseInputDialogListener
{

    public static final String LOG_TAG = "ExerciseFragment";

//    public static final int REQUEST_CODE = 1;

    //    private ListView listExercises;
    private ExpandableListView exListExercises;
    private ExerciseExpendableListAdapter exListAdapter;
    private TextView lblEmpty;

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
        if (actionBar != null)
        {
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.show();
        }

        lblEmpty = (TextView) view.findViewById(R.id.lbl_empty_exercises);

        exListExercises = (ExpandableListView) view.findViewById(R.id.exList_exercises);
        Map<Category, List<Exercise>> categoryMap = dao.selectCategoryMap(false);
        exListAdapter = new ExerciseExpendableListAdapter(super.getActivity(), categoryMap);
        exListExercises.setAdapter(exListAdapter);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab_add_exercise);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                addExerciseOrCategory();
            }
        });

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

//        Map<Long, Exercise> exercises = dao.selectExerciseMap(false);
//        if (exercises == null)
//        {
//            return;
//        }
//        List<Exercise> exerciseList = new ArrayList<>(exercises.values());
//        Exercise dummyExercise = new Exercise();
//        dummyExercise.setId(-1);
//        dummyExercise.setName("<Add New>");
//        exerciseList.add(dummyExercise);
//        Collections.sort(exerciseList, Exercise.byNameDummyFirst);
//        ArrayAdapter<Exercise> adapter = new ArrayAdapter<Exercise>(super.getActivity(), android.R.layout.simple_list_item_1, exerciseList);
//        listExercises.setAdapter(adapter);

        Map<Category, List<Exercise>> categoryMap = dao.selectCategoryMap(false);
        int exerciseCnt = 0;
        if(categoryMap != null)
        {
            for(List<Exercise> exercises : categoryMap.values())
            {
                if(exercises == null) continue;
                exerciseCnt += exercises.size();
            }
        }
        if(exerciseCnt == 0)
        {
            lblEmpty.setText("No exercises have been added");
            exListExercises.setAdapter((ExerciseExpendableListAdapter)null);
            return;
        }
        lblEmpty.setText("");

        exListAdapter = new ExerciseExpendableListAdapter(super.getActivity(), categoryMap);
        exListExercises.setAdapter(exListAdapter);

        expandListGroupItems();
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
        //filter doesn't work on some devices...
        //input.setFilters(new InputFilter[]{ Util.ALPHANUMERIC_FILTER });
        input.setSingleLine();
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String inputValue = input.getText().toString();
                if(inputValue == null || inputValue.isEmpty())
                {
                    Toast.makeText(ExercisesFragment.super.getActivity(), "Category name must not be empty.", Toast.LENGTH_LONG).show();
                    return;
                }
                if(dao.categoryExists(inputValue))
                {
                    Toast.makeText(ExercisesFragment.super.getActivity(), "Category name already exists.", Toast.LENGTH_LONG).show();
                    return;
                }
                addCategory(inputValue);
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

    private void doEditCategory()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(super.getActivity());
        builder.setTitle("Select Category");

        final Spinner input = new Spinner(super.getActivity());

        final List<Category> categories = dao.selectCategories(false);
        Collections.sort(categories);
        if(categories == null)
        {
            Toast.makeText(super.getActivity(), "Error loading categories.", Toast.LENGTH_LONG).show();
            return;
        }
        if(categories.size() == 0)
        {
            Toast.makeText(super.getActivity(), "No categories exist to edit.", Toast.LENGTH_LONG).show();
            return;
        }
        ArrayAdapter<Category> spinnerAdapter = new ArrayAdapter<>(super.getActivity(), android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        input.setAdapter(spinnerAdapter);

        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Category category = (Category) input.getSelectedItem();
                if (category != null)
                {
                    doEditCategory(category);
                }
            }
        });
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void doEditCategory(final Category category)
    {
        if(category == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(super.getActivity());
        builder.setTitle("Edit Category");

        // Set up the input
        final EditText input = new EditText(super.getActivity());
        input.setSingleLine();
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(category.getName());

        builder.setView(input);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                category.setName(input.getText().toString());
                boolean rVal = dao.update(category);
                if(rVal)
                {
                    ExercisesFragment.this.loadExercises();
                }
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
        builder.setNeutralButton("Delete", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                deleteCategory(category);
            }
        });
        builder.show();

    }

    private void deleteCategory(final Category category)
    {
        String msg = "Are you sure you want to delete category " + category.getName();
        new AlertDialog.Builder(super.getActivity())
//                .setIcon(android.R.drawable.ic_dialog_alert)
                .setIcon(R.drawable.ic_warning_blue_24dp)
                .setTitle("Delete Category")
                .setMessage(msg)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        //TODO CHECK THIS, IT IS CRASHING BECAUSE LISTVIEW IS STILL EXPECTING THE OBJECT!
                        if (category != null)
                        {
                            if (dao.deleteCategory(category.getId()))
                            {
                                ExercisesFragment.this.loadExercises();
                            }
                        }
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

    private void deleteExercise(final Exercise exercise)
    {
        //search all existing Lifts to check whether any of them reference this lift that the user wants to delete
        List<Lift> lifts = dao.selectLifts(false);

        String msg = "Are you sure you want to delete exercise " + exercise.getName() + "?";

        boolean found = false;
        if(lifts != null)
        {
            for (Lift lift : lifts)
            {
                if (lift.getExerciseId() == exercise.getId()) found = true;
            }
        }
        if(found)
        {
            msg += "\nNOTE: This exercise is currently used in the workout log. " +
                    "Deleting it will cause all referenced lifts to display \"?\" as the exercise.";
        }

        new AlertDialog.Builder(super.getActivity())
//                .setIcon(android.R.drawable.ic_dialog_alert)
                .setIcon(R.drawable.ic_warning_blue_24dp)
                .setTitle("Delete Exercise")
                .setMessage(msg)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dao.deleteExercise(exercise.getId());
                        ExercisesFragment.this.loadExercises();
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


    public void addCategory(String name)
    {
        Category category = new Category();
        category.setName(name);
        category.setNew(true);
        dao.insert(category);
        loadExercises();
    }

    public void addExerciseOrCategory()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(super.getActivity());
        builder.setTitle("Type to add");
        builder.setPositiveButton("Exercise", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                doAddExercise(-1);
            }
        });
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


    public void editExerciseOrCategory()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(super.getActivity());
        builder.setTitle("Type to edit");
        builder.setPositiveButton("Exercise", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                doEditExercise();
            }
        });
        builder.setNeutralButton("Category", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                doEditCategory();
            }
        });
        builder.show();
    }

    private void doEditExercise()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(super.getActivity());
        builder.setTitle("Select Exercise");

        final Spinner input = new Spinner(super.getActivity());

        final List<Exercise> exercises = dao.selectExercises(false);
        Collections.sort(exercises);
        if(exercises == null)
        {
            Toast.makeText(super.getActivity(), "Error loading exercises.", Toast.LENGTH_LONG).show();
            return;
        }
        if(exercises.size() == 0)
        {
            Toast.makeText(super.getActivity(), "No exercises exist to edit.", Toast.LENGTH_LONG).show();
            return;
        }
        ArrayAdapter<Exercise> spinnerAdapter = new ArrayAdapter<>(super.getActivity(), android.R.layout.simple_spinner_dropdown_item, exercises);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        input.setAdapter(spinnerAdapter);

        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Exercise exercise = (Exercise) input.getSelectedItem();
                if (exercise != null)
                {
                    doEditExercise(exercise);
                }
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

    public void doEditExercise(Exercise exercise)
    {
        ExerciseInputDialog dialog = ExerciseInputDialog.newInstance(exercise);
        dialog.setTargetFragment(this, ExerciseInputDialog.RequestType.DEFAULT.getValue());
        dialog.show(getFragmentManager().beginTransaction(), "ExerciseInputDialog");
//        loadExercises();
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
                addExerciseOrCategory();
                break;

            case R.id.action_edit_exercise:
                editExerciseOrCategory();
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
        //load exercises again to remove the background highlight color
        loadExercises();
    }


    public void onDialogDeleteClick(DialogFragment dialog, final Exercise exercise)
    {
        if (exercise == null || exercise.getId() == -1)
        {
            Log.d(LOG_TAG, "Error handling delete exercise button click. Exercise is null|empty");
            return;
        }
        deleteExercise(exercise);
    }


    private class ExerciseGroupElement
    {
        public ExerciseGroupElement()
        {

        }

        private Category category;
        private List<Exercise> exercises;

        public List<Exercise> getExercises()
        {
            return exercises;
        }
        public void setExercises(List<Exercise> exercises)
        {
            this.exercises = exercises;
        }
        public Category getCategory()
        {
            return category;
        }
        public void setCategory(Category category)
        {
            this.category = category;
        }
        public void addAll(List<Exercise> values)
        {
            if(values == null) return;
            if(exercises == null) exercises = new ArrayList<>();
            exercises.addAll(values);
        }

    }

    private class ExerciseExpendableListAdapter extends BaseExpandableListAdapter
    {
        public ExerciseExpendableListAdapter(Context ctx, Map<Category, List<Exercise>> categoryMap)
        {
//            categories = new ArrayList<Category>();
//            exerciseLists = new ArrayList<List<Exercise>>();
            elements = new ArrayList<>();

            if(categoryMap == null) return;
//            Collections.sort(allLifts);


//            Category uncategorized = null;
//            List<Exercise> uncategorizedExercises = null;
            ExerciseGroupElement uncategorizedElement = new ExerciseGroupElement();
            for(Category category : categoryMap.keySet())
            {
                if(category == null) continue;

                List<Exercise> exercises = categoryMap.get(category);
                if(exercises == null) exercises = new ArrayList<Exercise>();

                if(category.getId() < 1)
                {
                    uncategorizedElement.setCategory(category);
                    uncategorizedElement.addAll(exercises);
                    continue;
                }


                Collections.sort(exercises, Exercise.byNameDummyLast);

                //add the Category, List<Exercise> in parallel
//                categories.add(category);
//                exerciseLists.add(exercises);
                ExerciseGroupElement element = new ExerciseGroupElement();
                element.setCategory(category);
                element.setExercises(exercises);
                elements.add(element);
            }
            if(uncategorizedElement != null)
            {
                elements.add(uncategorizedElement);
            }

            Collections.sort(elements, comparator);
        }

        private List<ExerciseGroupElement> elements;

        public List<ExerciseGroupElement> getElements()
        {
            return elements;
        }


        private Comparator<ExerciseGroupElement> comparator = new Comparator<ExerciseGroupElement>(){
            @Override
            public int compare(ExerciseGroupElement e1, ExerciseGroupElement e2)
            {
                if((e1 == null) && (e2 == null)) return 0;
                else if(e1 == null) return -1;
                else if(e2 == null) return 1;
                if(e1.getCategory().getId() == -1) return 1;
                if(e2.getCategory().getId() == -1) return -1;
                return e1.getCategory().compareTo(e2.getCategory());
            }
        };



        @Override
        public boolean isChildSelectable(int i, int i1)
        {
            return true;
        }

        @Override
        public int getGroupCount()
        {
            return elements.size();
        }

        @Override
        public int getChildrenCount(int i)
        {
            List<Exercise> exercises = elements.get(i).getExercises();
            if(exercises == null) return 0;
            return exercises.size();
        }

        @Override
        public Object getGroup(int i)
        {
            return elements.get(i).getCategory();
        }

        @Override
        public Object getChild(int i, int j)
        {
            return elements.get(i).getExercises().get(j);
        }

        @Override
        public long getGroupId(int i)
        {
            return elements.get(i).getCategory().getId();
        }

        @Override
        public long getChildId(int i, int j)
        {
            return elements.get(i).getExercises().get(j).getId();
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

            Category category = elements.get(i).getCategory();
            TextView lblCategory = (TextView) view.findViewById(R.id.lbl_exercise_group);
            lblCategory.setText(category.getName());


            return view;
        }

        @Override
        public View getChildView(final int i, final int j, boolean b, View view, ViewGroup viewGroup)
        {
            if (view == null)
            {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = vi.inflate(R.layout.exercise_item, viewGroup, false);
            }

            final Exercise exercise = elements.get(i).getExercises().get(j);

            TextView lblExercise = (TextView) view.findViewById(R.id.lbl_exercise);
            lblExercise.setText(exercise.toString());

            final View viewRef = view;
            lblExercise.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    viewRef.setBackgroundColor(getResources().getColor(R.color.material_blue_200));
                    doEditExercise(exercise);
                }
            });

            return view;
        }
    }



}
