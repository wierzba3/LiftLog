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
import android.widget.DatePicker;

import com.liftlog.models.Exercise;

import java.util.Calendar;
import java.util.Date;

import com.liftlog.R;

import org.joda.time.DateTime;

/**
 * Created by James Wierzba on 7/12/2015.
 */
public class DateInputDialog extends DialogFragment
{

    public static DateInputDialog newInstance()
    {
        DateInputDialog dialog = new DateInputDialog();

        Bundle bundle = new Bundle();
        dialog.setArguments(bundle);

        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        //the new custom view
        final View customView = inflater.inflate(R.layout.fragment_date_input_dialog, null);
        builder.setView(customView);

        final DatePicker pckDate = (DatePicker) customView.findViewById(R.id.pckdate_input_dialog);


        // Add action buttons
        builder.setNegativeButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                int day = pckDate.getDayOfMonth();
                int month = pckDate.getMonth() + 1;
                int year = pckDate.getYear();

                //Calendar cal = Calendar.getInstance();
                //cal.set(year, month, day);
				//long date = cal.getTimeInMillis();
				
				DateTime dt = new DateTime(year, month, day, 12, 0);
				long date = dt.getMillis();
                

                mListener.onDialogSaveClick(DateInputDialog.this, date);
            }
        });

        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                mListener.onDialogCancelClick(DateInputDialog.this);
            }
        });

        return builder.create();
    }

    /**
     * Cast the caller to a DateInputDialogListener to communicate events back.
     * The class of the calling Fragment/Activity must implement DateInputDialogListener
     * @param activity The calling activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Fragment callingFragment = getTargetFragment();
        //if called from a fragment
        if(callingFragment != null)
        {
            mListener = (DateInputDialogListener) getTargetFragment();
        }
        //else called from an activity
        else
        {
            mListener = (DateInputDialogListener) activity;
        }
    }

    /**
     * The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it.
     */
    public interface DateInputDialogListener
    {
        /**
         *
         * @param dialog The event sender
         * @param date The selected date
         */
        public void onDialogSaveClick(DialogFragment dialog, long date);

        /**
         *
         * @param dialog The event sender
         */
        public void onDialogCancelClick(DialogFragment dialog);

    }
    private DateInputDialogListener mListener;

}
