package com.liftlog.data;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
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
        Log.d(LOG_TAG, "hello from onPerformSync");
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
            System.out.println("success");

        }
        catch (SQLException ex)
        {
            Log.d(LOG_TAG, "SQLException: " + ex.getMessage());
            return;
        }

        syncExercises(conn);






    }



    //remote db exercise table/column names
    public static final String EXERCISE_REMOTE_TABLE_NAME = "exercises";
    public static final String EXERCISE_REMOTE_COLUMN_USERNAME = "username";
    public static final String EXERCISE_REMOTE_COLUMN_PK = "id";
    public static final String EXERCISE_REMOTE_COLUMN_NAME = "name";
    public static final String EXERCISE_REMOTE_COLUMN_DESCRIPTION = "description";
    public static final String EXERCISE_REMOTE_COLUMN_VALID = "valid";
    public static final String EXERCISE_REMOTE_COLUMN_DATE = "date_created";
    private void syncExercises(Connection conn)
    {
        Map<Long, Exercise> localExercises = dao.selectExercises();

        List<Exercise> newlyAddedExercises = new ArrayList<Exercise>();
        List<Exercise> modifiedExercises = new ArrayList<Exercise>();
        List<Exercise> deletedExercises = new ArrayList<Exercise>();

        for(Exercise localExercise : localExercises.values())
        {
            switch(localExercise.getState())
            {
                case NEW:
                    newlyAddedExercises.add(localExercise);
                    break;
                case MODIFIED:
                    modifiedExercises.add(localExercise);
                    break;
                case DELETED:
                    deletedExercises.add(localExercise);
                    break;
            }
        }

        //TODO update remote db with the modified, new, and deleted exercises

        String qry = "SELECT * FROM exercises WHERE username = 'jamesmw129@gmail.com'";
        try
        {

            ResultSet rs = conn.createStatement().executeQuery(qry);

            boolean hasNext = rs.first();
            if(hasNext) Log.d(LOG_TAG, "empty");
            while(hasNext)
            {
                //TODO
                long id;
                String name;
                String desc;
                int valid;
                long dateCreated;

                

                hasNext = rs.next();
            }

        } catch (SQLException e)
        {
            Log.d(LOG_TAG, "SQLException: " + e.getMessage());
            return;
        }
    }



}
