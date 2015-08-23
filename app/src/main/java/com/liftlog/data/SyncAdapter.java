package com.liftlog.data;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.liftlog.backend.myApi.MyApi;
import com.liftlog.backend.myApi.model.ExerciseAPI;
import com.liftlog.backend.myApi.model.LiftAPI;
import com.liftlog.backend.myApi.model.SessionAPI;
import com.liftlog.data.mysql.MySQLController;
import com.liftlog.models.Exercise;
import com.liftlog.models.Lift;
import com.liftlog.models.Session;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by James Wierzba on 7/25/2015.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter
{
    public static final String LOG_TAG = "LiftLog";

    private DataAccessObject dao;
    private static MyApi myApiService = null;

    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize)
    {
        super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        dao = new DataAccessObject(context);

    }

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs)
    {
        super(context, autoInitialize, allowParallelSyncs);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */

    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult)
    {
        Log.d(LOG_TAG, "HI from onPerformSync");
        //TODO sync to database server

        try
        {

            Class.forName("com.mysql.jdbc.Driver").newInstance();
        }
        catch (Exception ex)
        {
            Log.d(LOG_TAG, "Exception: " + ex.getMessage());
            return;
        }

        Connection conn = null;
        try
        {
            conn = DriverManager.getConnection("jdbc:mysql://sql5.freesqldatabase.com/sql586870?" + "user=sql586870&password=kS8*lJ8!");

            // Do something with the Connection
            Log.d(LOG_TAG, "success connecting");

        }
        catch (SQLException ex)
        {
            Log.d(LOG_TAG, "SQLException: " + ex.getMessage());
            return;
        }

        syncExercises(conn, "jamesmw129@gmail.com");





        try
        {
            conn.close();
        }
        catch(SQLException e)
        {
            Log.d(LOG_TAG, "Error closing mysql connection.");
        }
    }


    private void syncExercises(Connection conn, String username)
    {
        Map<Long, Exercise> localExercises = dao.selectExercises(true);
        for(Exercise localExercise : localExercises.values())
        {
            boolean ret;
            switch(localExercise.getState())
            {

                case NEW:

                    //insert the exercise into the remote database
                    try
                    {
                        ret = MySQLController.insert(conn, username, localExercise);
                    }catch(SQLException ex)
                    {
                        Log.d(LOG_TAG, "error trying to insert new remote exercise from local: " + ex.getMessage());
                        break;
                    }
                    //mark the local record as up-to-date
                    localExercise.setState(DataAccessObject.RecordState.UNCHANGED);
                    dao.update(localExercise);

                    break;
                case MODIFIED:
                    try
                    {
                        ret = MySQLController.update(conn, username, localExercise);
                    }catch(SQLException ex)
                    {
                        Log.d(LOG_TAG, "error trying to update remote exercise from local: " + ex.getMessage());
                        break;
                    }
                    //remote exercise was successfully updated, so mark it as UNCHANGED in the local db
                    localExercise.setState(DataAccessObject.RecordState.UNCHANGED);
                    dao.update(localExercise);
                    break;
                case DELETED:
                    Log.d(LOG_TAG, "DELETED exercise: " + localExercise.getName());
                    try
                    {
                         ret = MySQLController.deleteExercise(conn, username, localExercise.getId());
                    }catch(SQLException ex)
                    {
                        Log.d(LOG_TAG, "error trying to delete remote exercise from local: " + ex.getMessage());
                        break;
                    }

                    //remote exercise was successfully removed, so fully remove it in the local db
                    dao.deleteExercise(localExercise.getId());

                    break;
            }
        }

        /*
         * Now that we have updated the changes in the local db, select all remote exercises
          * and update the local db accordingly, treating the remote db as the authority
         */
        Map<Long, Exercise> remoteExercises = MySQLController.selectExercises(conn, username);
        for(Exercise remoteExercise : remoteExercises.values())
        {
            Exercise matchingLocalExercise = localExercises.get(remoteExercise.getId());
            if(matchingLocalExercise == null)
            {
                dao.insert(remoteExercise);
            }
            else
            {
                if(!matchingLocalExercise.equals(remoteExercise))
                {
                    dao.update(remoteExercise);
                }
            }
        }



        getContext().getContentResolver().notifyChange(Uri.EMPTY , null, false);

    }



}
