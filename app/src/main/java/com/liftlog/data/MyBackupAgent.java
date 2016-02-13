package com.liftlog.data;

import android.app.backup.BackupAgentHelper;
import android.app.backup.FileBackupHelper;

import java.io.File;

/**
 * Created by James Wierzba on 10/13/2015.
 */
public class MyBackupAgent extends BackupAgentHelper
{

    @Override
    public void onCreate()
    {
        FileBackupHelper dbs = new FileBackupHelper(this, DataAccessObject.DB_NAME);
        addHelper("dbs", dbs);
    }

    @Override
    public File getFilesDir()
    {
        File path = getDatabasePath(DataAccessObject.DB_NAME);
        return path.getParentFile();
    }
}
