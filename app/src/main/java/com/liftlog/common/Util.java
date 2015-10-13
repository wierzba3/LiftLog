package com.liftlog.common;

import android.text.InputFilter;
import android.text.Spanned;

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

    public static final String ALPHANUMERIC_DIGITS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
    public static final String ALPHANUMERIC_REGEX = "[A-Za-z0-9]*";

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("E, MMM dd yyyy");

    /**
     * Custom InputFilter for allowing only whitespace, alphanumeric characters
     */
    public static final InputFilter ALPHANUMERIC_FILTER = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            if (source != null && !source.toString().trim().matches(ALPHANUMERIC_REGEX))
            {
                return "";
            }
            return null;
        }
    };

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





}
