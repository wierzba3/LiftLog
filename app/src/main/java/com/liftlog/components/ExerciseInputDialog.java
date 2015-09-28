package com.liftlog.components;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.liftlog.data.DataAccessObject;
import com.liftlog.R;
import com.liftlog.models.Category;
import com.liftlog.models.Exercise;

import java.util.List;


public class ExerciseInputDialog extends DialogFragment
{

    public enum RequestType
    {
        DEFAULT(0);

        private int value;

        RequestType(int value) { this.value = value; }
        public int getValue() { return value; }
    }

    public static ExerciseInputDialog newInstance(Exercise exercise)
    {
        ExerciseInputDialog dialog = new ExerciseInputDialog();

        Bundle bundle = new Bundle();
        long id = -1;
        if(exercise != null) id = exercise.getId();
        bundle.putLong(EXERCISE_ID_KEY, id);
        dialog.setArguments(bundle);

        return dialog;
    }

    public static final String EXERCISE_ID_KEY = "exercise_id";



    DataAccessObject dao;

    private long exerciseId = -1;
    private Exercise currentExercise = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        //the new custom view
        final View customView = inflater.inflate(R.layout.fragment_exercise_input_dialog, null);

        final Spinner spnCategory = (Spinner) customView.findViewById(R.id.spn_category);
        final EditText txtName = (EditText) customView.findViewById(R.id.txt_name);
        final EditText txtDesc = (EditText) customView.findViewById(R.id.txt_description);


        final List<Category> categories = dao.selectCategories(false);
        categories.add(Category.dummy);
        if(currentExercise != null)
        {
            txtName.setText(currentExercise.getName());
            txtDesc.setText(currentExercise.getDescription());

            if(currentExercise.getCategoryId() < 1) currentExercise.setCategoryId(-1l);

            //populate and select Category spinner:
            ArrayAdapter<Category> categoryItemAdapter = new ArrayAdapter<>(super.getActivity(), android.R.layout.simple_spinner_dropdown_item, categories);
            categoryItemAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            spnCategory.setAdapter(categoryItemAdapter);
            for(int i = 0; i < spnCategory.getAdapter().getCount(); i++)
            {
                Category category = (Category) spnCategory.getItemAtPosition(i);
                if(category != null && category.getId() == currentExercise.getCategoryId())
                {
                    spnCategory.setSelection(i);
                    break;
                }
            }
        }

        builder.setView(customView);

        // Add action buttons
        builder.setNegativeButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                long categoryId = -1;
                Category category = (Category) spnCategory.getSelectedItem();
                if(category != null)
                {
                    categoryId = category.getId();
                }

                String name = txtName.getText().toString();
                String desc = txtDesc.getText().toString();

                if (currentExercise == null)
                {
                    currentExercise = new Exercise();
                    currentExercise.setNew(true);
                }
                else if(currentExercise.getId() > 0)
                {
                    currentExercise.setModified(true);
                }
                currentExercise.setId(exerciseId);
                currentExercise.setCategoryId(categoryId);
                currentExercise.setName(name);
                currentExercise.setDescription(desc);

                mListener.onDialogSaveClick(ExerciseInputDialog.this, currentExercise);
            }
        });

        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mListener.onDialogCancelClick(ExerciseInputDialog.this);
            }
        });

        //if we are not adding a new exercise, show the delete button
        //Delete is the "Positive" button because it is always the right-most button
        if(currentExercise != null && currentExercise.getId() > -1)
        {
            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    String name = txtName.getText().toString();
                    String desc = txtDesc.getText().toString();

                    if (currentExercise == null)
                    {
                        currentExercise = new Exercise();
                    }
                    currentExercise.setDeleted(true);
                    currentExercise.setId(exerciseId);
                    currentExercise.setName(name);
                    currentExercise.setDescription(desc);

                    mListener.onDialogDeleteClick(ExerciseInputDialog.this, currentExercise);
                }
            });
        }
        return builder.create();
    }


    public ExerciseInputDialog()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        dao = new DataAccessObject(this.getActivity());

        Bundle bundle = this.getArguments();
        if(bundle != null && bundle.containsKey(EXERCISE_ID_KEY))
        {
            exerciseId = bundle.getLong(EXERCISE_ID_KEY);

            if(exerciseId != -1)
            {
                currentExercise = dao.selectExercise(exerciseId);
            }
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return null;//return inflater.inflate(R.layout.fragment_exercise_input_dialog, container, false);
    }

    /**
     * Cast the caller to a DateInputDialogListener to communicate events back.
     * The class of the calling Fragment/Activity must implement ExerciseInputDialogListener
     * @param activity The calling activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Fragment callingFragment = getTargetFragment();
        //if called from a fragment
        if(callingFragment != null)
        {
            mListener = (ExerciseInputDialogListener) getTargetFragment();
        }
        //else called from an activity
        else
        {
            mListener = (ExerciseInputDialogListener) activity;
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it.
     */
    public interface ExerciseInputDialogListener
    {
        /**
         *
         * @param dialog The event sender
         * @param exercise The currentExercise to update
         */
        public void onDialogSaveClick(DialogFragment dialog, Exercise exercise);

        /**
         *
         * @param dialog The event sender
         */
        public void onDialogCancelClick(DialogFragment dialog);

        /**
         *
         * @param dialog The event sender
         * @param exercise The currentExercise to delete
         */
        public void onDialogDeleteClick(DialogFragment dialog, Exercise exercise);
    }
    private ExerciseInputDialogListener mListener;

}
