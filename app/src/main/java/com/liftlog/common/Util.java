package com.liftlog.common;

import android.os.Environment;

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

    public static int getDaysSinceEpoch()
    {
        MutableDateTime epoch = new MutableDateTime(0);
        return Days.daysBetween(epoch, DateTime.now()).getDays();
    }


}
