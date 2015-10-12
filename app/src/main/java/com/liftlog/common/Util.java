package com.liftlog.common;

import android.os.Environment;
import android.text.InputFilter;
import android.text.Spanned;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.MutableDateTime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * Created by James Wierzba on 6/21/2015.
 */
public class Util
{

    public static final String ALPHANUMERIC_DIGITS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890 \t";
    public static final String ALPHANUMERIC_REGEX = "[A-Za-z0-9]*";

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

    public static int getDaysSinceEpoch()
    {
        MutableDateTime epoch = new MutableDateTime(0);
        return Days.daysBetween(epoch, DateTime.now()).getDays();
    }


}
