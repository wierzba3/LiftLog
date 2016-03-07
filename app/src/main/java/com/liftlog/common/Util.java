package com.liftlog.common;

import android.app.AlertDialog;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.liftlog.R;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by James Wierzba on 6/21/2015.
 */
public class Util
{

    public static final String ABOUT_URL = "http://wierzba3.github.io/LiftLog/";

    public static final String ALPHANUMERIC_DIGITS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
    public static final String ALPHANUMERIC_REGEX = "[A-Za-z0-9]*";

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("E, MMM dd yyyy");
    public static final DateTimeFormatter DATE_FORMAT_SHORT = DateTimeFormat.forPattern("MM/dd/yy");
    public static final DateTimeFormatter DATE_FORMAT_MONTH = DateTimeFormat.forPattern("MMMM, yyyy");

    private static BackupManager backupManager;

    /**
     * Calcuate the number of days since the epoch (exclusive)
     * @return The number of days since the epoch
     */
    public static int getDaysSinceEpoch()
    {
        MutableDateTime epoch = new MutableDateTime(0);
        return Days.daysBetween(epoch, DateTime.now()).getDays();
    }

    /**
     * Create a joda DateTime object from the number of day since the epoch
     * @param days The number of days since the epoch
     * @return A joda DateTime instance
     */
    public static DateTime dateFromDays(int days)
    {
        MutableDateTime dt = new MutableDateTime(0);
        //days+1 because the day value is derived from Days.daysBetween(epoch,<date>)
        //which is the number of whole days between (exclusive)
        dt.addDays(days+1);
        return dt.toDateTime();
    }


    public static void launchAboutWebsiteIntent(final Context ctx)
    {

        new AlertDialog.Builder(ctx)
                .setIcon(R.drawable.ic_warning_blue_24dp)

                .setTitle("View Online Help")
                .setMessage("Are you sure you want to open Online Help? A web page will be opened with your default browser.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ABOUT_URL));
                        ctx.startActivity(browserIntent);
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    public static void dataChanged(Context ctx)
    {
        if(backupManager ==  null)
        {
            backupManager = new BackupManager(ctx);
        }
        backupManager.dataChanged();
    }


}
