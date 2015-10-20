package com.liftlog.common;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

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
    public static final DateTimeFormatter DATE_FORMAT_MONTH = DateTimeFormat.forPattern("MMMM, yyyy");

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

    public static void launchAboutWebsiteIntent(Context ctx)
    {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ABOUT_URL));
        ctx.startActivity(browserIntent);
    }


}
