package com.liftlog.data.mysql;

import android.util.Log;

import com.liftlog.models.Exercise;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by James Wierzba on 8/16/15.
 *
 * Utillity class to handle data marshalling from remote mysql database to the android application
 */
public class MySQLController
{
    public static final String LOG_TAG = "LiftLog";

    //Exercise table/column names
    public static final String EXERCISE_REMOTE_TABLE_NAME = "exercises";
    public static final String EXERCISE_REMOTE_COLUMN_USERNAME = "username";
    public static final String EXERCISE_REMOTE_COLUMN_PK = "id";
    public static final String EXERCISE_REMOTE_COLUMN_NAME = "name";
    public static final String EXERCISE_REMOTE_COLUMN_DESCRIPTION = "description";
    public static final String EXERCISE_REMOTE_COLUMN_VALID = "valid";
    public static final String EXERCISE_REMOTE_COLUMN_DATE = "date_created";

    public static List<Exercise> selectExercises(Connection conn, String username)
    {
        List<Exercise> result = new ArrayList<Exercise>();

        String qry = "SELECT * FROM exercises WHERE username = '" + username + "'";
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
            return null;
        }

        return result;
    }



}
