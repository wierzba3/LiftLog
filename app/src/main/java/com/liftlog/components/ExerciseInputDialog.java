package com.liftlog.components;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.liftlog.common.DataAccessObject;
import wierzba.james.liftlog.R;
import com.liftlog.models.Exercise;


public class ExerciseInputDialog extends DialogFragment {

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

        final EditText txtName = (EditText) customView.findViewById(R.id.txt_name);
        final EditText txtDesc = (EditText) customView.findViewById(R.id.txt_description);

        if(currentExercise != null)
        {
            txtName.setText(currentExercise.getName());
            txtDesc.setText(currentExercise.getDescription());
        }

        //Save is the "Negative" button because this is always the left-most button.
        builder.setView(customView)
                // Add action buttons
                .setNegativeButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String name = txtName.getText().toString();
                        String desc = txtDesc.getText().toString();

                        if (currentExercise == null) currentExercise = new Exercise();
                        currentExercise.setId(exerciseId);
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

                    if (currentExercise == null) currentExercise = new Exercise();
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
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
    public interface NoticeDialogListener {
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
    private NoticeDialogListener mListener;

}
