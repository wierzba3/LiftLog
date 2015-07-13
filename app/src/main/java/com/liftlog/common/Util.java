package com.liftlog.common;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * Created by James Wierzba on 6/21/2015.
 */
public class Util {



    public static void copyDbFile()
    {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite())
            {
                String currentDBPath = "//data/data/wierzba.james.liftlog/databases/LiftLog.db";
                String backupDBPath = "C:/Users/cdewz/Desktop/LiftLog_backup.db";
                File currentDB = new File(currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        }
        catch (Exception e)
        {

        }
    }

//    /**
//     * Get current date represented as seconds since the epoch.
//     * @return The current date
//     */
//    public static int now()
//    {
//        return (int)(System.currentTimeMillis() / 1000);
//    }

}
