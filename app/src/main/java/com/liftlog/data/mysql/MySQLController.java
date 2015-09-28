package com.liftlog.data.mysql;

import android.util.Log;

import com.liftlog.data.DataAccessObject;
import com.liftlog.models.Exercise;
import com.liftlog.models.Lift;
import com.liftlog.models.Session;

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

        String qry = "SELECT * FROM " + EXERCISE_REMOTE_TABLE_NAME
                + " WHERE " + EXERCISE_REMOTE_COLUMN_USERNAME + " = '" + username + "'";
        try
        {

            ResultSet rs = conn.createStatement().executeQuery(qry);

            boolean hasNext = rs.first();
            while(hasNext)
            {
                long id = rs.getLong(rs.findColumn(EXERCISE_REMOTE_COLUMN_PK));
                String name = rs.getString(rs.findColumn(EXERCISE_REMOTE_COLUMN_NAME));
                String desc = rs.getString(rs.findColumn(EXERCISE_REMOTE_COLUMN_DESCRIPTION));
                int valid = rs.getInt(rs.findColumn(EXERCISE_REMOTE_COLUMN_VALID));
                long dateCreated  = rs.getLong(rs.findColumn(EXERCISE_REMOTE_COLUMN_DATE));

                Exercise exercise = new Exercise();
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
        long id = exercise.getId();
        String name = exercise.getName();
        String desc = exercise.getDescription();
        int valid = exercise.isValid() ? 1 : 0;

        String qry = "UPDATE " + EXERCISE_REMOTE_TABLE_NAME + " SET "
                + EXERCISE_REMOTE_COLUMN_NAME + " = '" + name + "',"
                + EXERCISE_REMOTE_COLUMN_DESCRIPTION + " = '" + desc + "',"
                + EXERCISE_REMOTE_COLUMN_VALID + " = " + valid
                + " WHERE "
                + EXERCISE_REMOTE_COLUMN_PK + " = " + id
                + " AND "
                + EXERCISE_REMOTE_COLUMN_USERNAME + " = '" + username + "'";

        return conn.createStatement().execute(qry);
    }

    //BEGIN Session methods
    public static final String SESSION_REMOTE_TABLE_NAME = "sessions";
    public static final String SESSION_REMOTE_COLUMN_USERNAME = "username";
    public static final String SESSION_REMOTE_COLUMN_PK = "id";
    public static final String SESSION_REMOTE_COLUMN_DATE = "session_date";
    public static final String SESSION_REMOTE_COLUMN_DATE_CREATED = "date_created";
    public static boolean insert(Connection conn, String username, Session session) throws SQLException
    {
        long id = session.getId();
        long date = session.getDate();

        String qry = "INSERT INTO " + SESSION_REMOTE_TABLE_NAME
                + "("
                + SESSION_REMOTE_COLUMN_USERNAME + ","
                + SESSION_REMOTE_COLUMN_PK + ","
                + SESSION_REMOTE_COLUMN_DATE + ","
                + SESSION_REMOTE_COLUMN_DATE_CREATED
                + ") "
                + " VALUES ("
                + "'" + username + "',"
                + id + ","
                + date + ","
                + System.currentTimeMillis()
                + ")";

        return conn.createStatement().execute(qry);
    }
    public static boolean update(Connection conn, String username, Session session) throws SQLException
    {
        long id = session.getId();
        long date = session.getDate();

        String qry = "UPDATE " + SESSION_REMOTE_TABLE_NAME + " SET "
                + SESSION_REMOTE_COLUMN_DATE + " = '" + date
                + " WHERE "
                + SESSION_REMOTE_COLUMN_PK + " = " + id
                + " AND "
                + SESSION_REMOTE_COLUMN_USERNAME + " = '" + username + "'";

        return conn.createStatement().execute(qry);
    }
    public static boolean deleteSession(Connection conn, String username, long id) throws SQLException
    {
        String qry = "DELETE FROM "
                + SESSION_REMOTE_TABLE_NAME
                + " WHERE "
                + SESSION_REMOTE_COLUMN_PK + " = " + id
                + " AND " + SESSION_REMOTE_COLUMN_USERNAME + " = '" + username + "'";

        return conn.createStatement().execute(qry);
    }
    public static List<Session> selectSessions(Connection conn, String username)
    {
        List<Session> result = new ArrayList<>();

        String qry = "SELECT * FROM " + SESSION_REMOTE_TABLE_NAME
                + " WHERE " + SESSION_REMOTE_COLUMN_USERNAME + " = '" + username + "'";
        try
        {
            ResultSet rs = conn.createStatement().executeQuery(qry);

            boolean hasNext = rs.first();
            while(hasNext)
            {
                long id = rs.getLong(rs.findColumn(SESSION_REMOTE_COLUMN_PK));
                long date  = rs.getLong(rs.findColumn(SESSION_REMOTE_COLUMN_DATE));

                Session session = new Session();
                session.setId(id);
                session.setDate(date);
                result.add(session);

                hasNext = rs.next();
            }

        } catch (SQLException e)
        {
            Log.d(LOG_TAG, "SQLException: " + e.getMessage());
            return null;
        }

        return result;
    }

    //END Session methods



    //BEGIN Lift methods
    public static final String LIFT_REMOTE_TABLE_NAME = "lifts";
    public static final String LIFT_REMOTE_COLUMN_USERNAME = "username";
    public static final String LIFT_REMOTE_COLUMN_PK = "id";
    public static final String LIFT_REMOTE_COLUMN_SESSION_FK = "session_id";
    public static final String LIFT_REMOTE_COLUMN_EXERCISE_FK = "exercise_id";
    public static final String LIFT_REMOTE_COLUMN_DATE_CREATED = "date_created";
    public static final String LIFT_REMOTE_COLUMN_WEIGHT = "weight";
    public static final String LIFT_REMOTE_COLUMN_REPS = "reps";
    public static final String LIFT_REMOTE_COLUMN_SETS = "sets";
    public static final String LIFT_REMOTE_COLUMN_UNIT = "unit";
    public static final String LIFT_REMOTE_COLUMN_WARMUP = "warmup";
    public static boolean insert(Connection conn, String username, Lift lift) throws SQLException
    {
        long id = lift.getId();
        long sessionId = lift.getSessionId();
        long exerciseId = lift.getExerciseId();
        double weight = lift.getWeight();
        int reps = lift.getReps();
        int sets = lift.getSets();
        String unit = lift.getUnit() == null ? "lb" : lift.getUnit().toString().toUpperCase();
        int warmup = lift.isWarmup() ? 1 : 0;


        String qry = "INSERT INTO " + LIFT_REMOTE_TABLE_NAME
                + "("
                + LIFT_REMOTE_COLUMN_USERNAME + ","
                + LIFT_REMOTE_COLUMN_PK + ","
                + LIFT_REMOTE_COLUMN_SESSION_FK + ","
                + LIFT_REMOTE_COLUMN_EXERCISE_FK + ","
                + LIFT_REMOTE_COLUMN_DATE_CREATED + ","
                + LIFT_REMOTE_COLUMN_WEIGHT + ","
                + LIFT_REMOTE_COLUMN_REPS + ","
                + LIFT_REMOTE_COLUMN_SETS + ","
                + LIFT_REMOTE_COLUMN_UNIT + ","
                + LIFT_REMOTE_COLUMN_WARMUP
                + ") "
                + " VALUES ("
                + "'" + username + "',"
                + id + ","
                + sessionId + ","
                + exerciseId + ","
                + System.currentTimeMillis() + ","
                + weight + ","
                + reps + ","
                + sets + ","
                + "'" + unit + "',"
                + warmup
                + ")";

        return conn.createStatement().execute(qry);
    }
    public static boolean update(Connection conn, String username, Lift lift) throws SQLException
    {
        long id = lift.getId();
        long sessionId = lift.getSessionId();
        long exerciseId = lift.getExerciseId();
        double weight = lift.getWeight();
        int reps = lift.getReps();
        int sets = lift.getSets();
        String unit = lift.getUnit() == null ? "lb" : lift.getUnit().toString().toUpperCase();
        int warmup = lift.isWarmup() ? 1 : 0;

        String qry = "UPDATE " + LIFT_REMOTE_TABLE_NAME + " SET "
                + LIFT_REMOTE_COLUMN_SESSION_FK + " = " + sessionId + ","
                + LIFT_REMOTE_COLUMN_EXERCISE_FK + " = " + exerciseId + ","
                + LIFT_REMOTE_COLUMN_WEIGHT + " = " + weight + ","
                + LIFT_REMOTE_COLUMN_REPS + " = " + reps + ","
                + LIFT_REMOTE_COLUMN_SETS + " = " + sets + ","
                + LIFT_REMOTE_COLUMN_UNIT + " = '" + unit + "',"
                + LIFT_REMOTE_COLUMN_WARMUP + " = " + warmup
                + " WHERE "
                + LIFT_REMOTE_COLUMN_PK + " = " + id
                + " AND "
                + LIFT_REMOTE_COLUMN_USERNAME + " = '" + username + "'";

        return conn.createStatement().execute(qry);
    }
    public static boolean deleteLift(Connection conn, String username, long id) throws SQLException
    {
        String qry = "DELETE FROM "
                + LIFT_REMOTE_TABLE_NAME
                + " WHERE "
                + LIFT_REMOTE_COLUMN_PK + " = " + id
                + " AND " + LIFT_REMOTE_COLUMN_USERNAME + " = '" + username + "'";

        return conn.createStatement().execute(qry);
    }
    public static List<Lift> selectLifts(Connection conn, String username)
    {
        List<Lift> result = new ArrayList<>();

        String qry = "SELECT * FROM " + LIFT_REMOTE_TABLE_NAME
                + " WHERE " + LIFT_REMOTE_COLUMN_USERNAME + " = '" + username + "'";
        try
        {
            ResultSet rs = conn.createStatement().executeQuery(qry);

            boolean hasNext = rs.first();
            while(hasNext)
            {
                long id = rs.getLong(rs.findColumn(LIFT_REMOTE_COLUMN_PK));
                long sessionId = rs.getLong(rs.findColumn(LIFT_REMOTE_COLUMN_SESSION_FK));
                long exerciseId = rs.getLong(rs.findColumn(LIFT_REMOTE_COLUMN_EXERCISE_FK));
                int weight = rs.getInt(rs.findColumn(LIFT_REMOTE_COLUMN_WEIGHT));
                int reps = rs.getInt(rs.findColumn(LIFT_REMOTE_COLUMN_REPS));
                int sets = rs.getInt(rs.findColumn(LIFT_REMOTE_COLUMN_SETS));
                String unit = rs.getString(rs.findColumn(LIFT_REMOTE_COLUMN_UNIT));
                int warmup = rs.getInt(rs.findColumn(LIFT_REMOTE_COLUMN_WARMUP));

                Lift lift = new Lift();
                lift.setId(id);
                lift.setSessionId(sessionId);
                lift.setExerciseId(exerciseId);
                lift.setWeight(weight);
                lift.setReps(reps);
                lift.setSets(sets);
                lift.setUnit(Lift.Unit.valueOf(unit));
                lift.setWarmup(warmup == 1);
                result.add(lift);

                hasNext = rs.next();
            }

        } catch (SQLException e)
        {
            Log.d(LOG_TAG, "SQLException: " + e.getMessage());
            return null;
        }

        return result;
    }
    //END lift methods

}
