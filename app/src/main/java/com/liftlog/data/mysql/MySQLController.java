package com.liftlog.data.mysql;

import android.util.Log;

import com.liftlog.data.DataAccessObject;
import com.liftlog.models.Exercise;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static Map<Long, Exercise> selectExercises(Connection conn, String username)
    {
        Map<Long, Exercise> result = new HashMap<Long, Exercise>();

        String qry = "SELECT * FROM exercises WHERE username = '" + username + "'";
        try
        {

            ResultSet rs = conn.createStatement().executeQuery(qry);

            boolean hasNext = rs.first();
            if(hasNext) Log.d(LOG_TAG, "empty");
            while(hasNext)
            {
                long id = rs.getLong(rs.findColumn(EXERCISE_REMOTE_COLUMN_PK));
                String name = rs.getString(rs.findColumn(EXERCISE_REMOTE_COLUMN_NAME));
                String desc = rs.getString(rs.findColumn(EXERCISE_REMOTE_COLUMN_DESCRIPTION));
                int valid = rs.getInt(rs.findColumn(EXERCISE_REMOTE_COLUMN_VALID));
                long dateCreated  = rs.getLong(rs.findColumn(EXERCISE_REMOTE_COLUMN_DATE));

                Exercise exercise = new Exercise(DataAccessObject.RecordState.UNCHANGED);
                exercise.setId(id);
                exercise.setName(name);
                exercise.setDescription(desc);
                exercise.setValid(valid == 1 ? true : false);
                result.put(id, exercise);

                hasNext = rs.next();
            }

        } catch (SQLException e)
        {
            Log.d(LOG_TAG, "SQLException: " + e.getMessage());
            return null;
        }

        return result;
    }

    public static boolean deleteExercise(Connection conn, String username, long id) throws SQLException
    {
        String qry = "DELETE FROM "
                + EXERCISE_REMOTE_TABLE_NAME
                + " WHERE "
                + EXERCISE_REMOTE_COLUMN_PK + " = " + id
                + " AND " + EXERCISE_REMOTE_COLUMN_USERNAME + " = '" + username + "'";

        return conn.createStatement().execute(qry);
    }

    public static boolean insert(Connection conn, String username, Exercise exercise) throws SQLException
    {
        long id = exercise.getId();
        String name = exercise.getName();
        String desc = exercise.getDescription();
        int valid = exercise.isValid() ? 1 : 0;
        long date = System.currentTimeMillis();

        String qry = "INSERT INTO " + EXERCISE_REMOTE_TABLE_NAME
                + "("
                + EXERCISE_REMOTE_COLUMN_USERNAME + ","
                + EXERCISE_REMOTE_COLUMN_PK + ","
                + EXERCISE_REMOTE_COLUMN_NAME + ","
                + EXERCISE_REMOTE_COLUMN_DESCRIPTION + ","
                + EXERCISE_REMOTE_COLUMN_VALID + ","
                + EXERCISE_REMOTE_COLUMN_DATE
                + ") "
                + " VALUES ("
                + "'" + username + "',"
                + id + ","
                + "'" + name + "',"
                + "'" + desc + "',"
                + valid + ","
                + date
                + ")";
        return conn.createStatement().execute(qry);
    }

    public static boolean update(Connection conn, String username, Exercise exercise) throws SQLException
    {
        String qry = "";
        //TODO
        return conn.createStatement().execute(qry);
    }




}
