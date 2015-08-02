/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.liftlog.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.utils.SystemProperty;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

/**
 * An endpoint class we are exposing
 */
@Api(
        name = "myApi",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.liftlog.com",
                ownerName = "backend.liftlog.com",
                packagePath = ""
        )
)
public class MyEndpoint
{

    public static final String DB_NAME = "liftlog";

    public static final String LIFT_TABLE_NAME = "lifts";
    public static final String LIFT_COLUMN_USER = "username";
    public static final String LIFT_COLUMN_ID = "id";
    public static final String LIFT_COLUMN_SESSION_FK = "session_id";
    public static final String LIFT_COLUMN_EXERCISE_FK = "exercise_id";
    public static final String LIFT_COLUMN_DATE_CREATED = "date_created";
    public static final String LIFT_COLUMN_WEIGHT = "weight";
    public static final String LIFT_COLUMN_REPS = "reps";
    public static final String LIFT_COLUMN_SETS = "sets";
    public static final String LIFT_COLUMN_UNIT = "unit";
    public static final String LIFT_COLUMN_STATE = "state";
    public static final String LIFT_COLUMN_WARMUP = "warmup";



    public static final String SESSION_TABLE_NAME = "sessions";
    public static final String SESSION_COLUMN_USER = "username";
    public static final String SESSION_COLUMN_ID = "id";
    public static final String SESSION_COLUMN_DATE = "date";
    public static final String SESSION_COLUMN_DATE_CREATED = "date_created";

    public static final String EXERCISE_TABLE_NAME = "exercises";
    public static final String EXERCISE_COLUMN_USER = "username";
    public static final String EXERCISE_COLUMN_ID = "id";
    public static final String EXERCISE_COLUMN_NAME = "name";
    public static final String EXERCISE_COLUMN_DESCRIPTION = "description";
    public static final String EXERCISE_COLUMN_VALID = "valid";
    public static final String EXERCISE_COLUMN_DATE = "date_created";

    @ApiMethod(name = "selectSessions")
    public List<SessionAPI> selectSessions(@Named("username") String username)
    {
        List<SessionAPI> result = new ArrayList<SessionAPI>();
        try
        {
            Connection conn = getConnection();
            String qry = "SELECT * "
                    + " FROM " + DB_NAME + "." + SESSION_TABLE_NAME
                    + " WHERE " + SESSION_COLUMN_USER + " = " + username;
            ResultSet rs = conn.createStatement().executeQuery(qry);

            boolean hasNext = rs.first();
            while(hasNext)
            {
                result.add(parseSessionAPI(rs));
            }

        } catch (SQLException e)
        {
            e.printStackTrace();

            return null;
        }
        return result;
    }

    /**
     * Helper method to create Session bean from a ResultSet record
     * @param rs The result set
     * @return The created SessionAPI object
     */
    private SessionAPI parseSessionAPI(ResultSet rs)
    {
        SessionAPI result = new SessionAPI();
        //TODO
        return result;
    }

    @ApiMethod(name = "selectExercises")
    public List<ExerciseAPI> selectExercises(@Named("username") String username)
    {
        List<ExerciseAPI> result = new ArrayList<ExerciseAPI>();
        try
        {
            Connection conn = getConnection();
            String qry = "SELECT * "
                    + " FROM " + DB_NAME + "." + EXERCISE_TABLE_NAME
                    + " WHERE " + EXERCISE_COLUMN_USER + " = " + username;
            ResultSet rs = conn.createStatement().executeQuery(qry);

            boolean hasNext = rs.first();
            while(hasNext)
            {
                result.add(parseExerciseAPI(rs));
            }

        } catch (SQLException e)
        {
            e.printStackTrace();

            return null;
        }
        return result;
    }

    /**
     * Helper method to create Session bean from a ResultSet record
     * @param rs The result set
     * @return The created SessionAPI object
     */
    private ExerciseAPI parseExerciseAPI(ResultSet rs)
    {
        ExerciseAPI result = new ExerciseAPI();
        //TODO
        return result;
    }


    @ApiMethod(name = "selectLifts")
    public List<LiftAPI> selectLifts(@Named("username") String username)
    {
        List<LiftAPI> result = new ArrayList<LiftAPI>();
        try
        {
            Connection conn = getConnection();
            String qry = "SELECT * "
                    + " FROM " + DB_NAME + "." + LIFT_TABLE_NAME
                    + " WHERE " + LIFT_COLUMN_USER + " = " + username;
            ResultSet rs = conn.createStatement().executeQuery(qry);

            boolean hasNext = rs.first();
            while(hasNext)
            {
                result.add(parseLiftAPI(rs));
            }

        } catch (SQLException e)
        {
            e.printStackTrace();

            return null;
        }
        return result;
    }

    /**
     * Helper method to create Session bean from a ResultSet record
     * @param rs The result set
     * @return The created SessionAPI object
     */
    private LiftAPI parseLiftAPI(ResultSet rs)
    {
        LiftAPI result = new LiftAPI();
        //TODO
        return result;
    }

    private Connection getConnection() throws SQLException
    {
        String url = null;
        if (SystemProperty.environment.value() ==
                SystemProperty.Environment.Value.Production)
        {
            // Connecting from App Engine.
            // Load the class that provides the "jdbc:google:mysql://"
            // prefix.
            try
            {
                //use google mysql driver in prod environment
                Class.forName("com.mysql.jdbc.GoogleDriver");
            } catch (ClassNotFoundException e)
            {
                e.printStackTrace();
                return null;
            }
            url = "jdbc:google:mysql://liftlog-1016:liftlog-db1?user=root";
        }
        else
        {
            try
            {
                //use normal mysql driver in dev environment
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e)
            {
                e.printStackTrace();
                return null;
            }
            url = "jdbc:mysql://localhost:3306?user=root&password=a1b2c3d3";
        }
        return DriverManager.getConnection(url);
    }

}
