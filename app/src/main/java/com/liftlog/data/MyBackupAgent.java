package com.liftlog.data;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.app.backup.FileBackupHelper;
import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.IOException;

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

    @Override
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) throws IOException
    {
        super.onBackup(oldState, data, newState);
    }

    @Override
    public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState) throws IOException
    {
        super.onRestore(data, appVersionCode, newState);
    }
}
