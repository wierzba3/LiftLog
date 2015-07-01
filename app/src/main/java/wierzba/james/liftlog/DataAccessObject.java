package wierzba.james.liftlog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wierzba.james.liftlog.models.Exercise;
import wierzba.james.liftlog.models.Lift;
import wierzba.james.liftlog.models.Session;
import wierzba.james.liftlog.wierzba.james.liftlog.utils.Util;

/**
 * Created by jwierzba on 6/8/2015.
 */
public class DataAccessObject extends SQLiteOpenHelper
{

    //TODO
    public static Map<Integer, Exercise> exerciseMap = new HashMap<>();

    public static final String DB_NAME = "LiftLog.db";

    public static final String LIFT_TABLE_NAME = "lifts";
    public static final String LIFT_COLUMN_PK = "id";
    public static final String LIFT_COLUMN_SESSION_FK = "session_id";
    public static final String LIFT_COLUMN_EXERCISE_FK = "exercise_id";
    /** The date created. Represented as seconds since epoch. */
    public static final String LIFT_COLUMN_DATE_CREATED = "date_created";
    public static final String LIFT_COLUMN_WEIGHT = "weight";
    public static final String LIFT_COLUMN_REPS = "reps";
    public static final String LIFT_COLUMN_SETS = "sets";
    public static final String LIFT_COLUMN_UNIT = "unit";
    /** boolean field, 0 = false, 1 = true */
    public static final String LIFT_COLUMN_WARMUP = "warmup";
    public static final String LIFT_TABLE_CREATE_QUERY =
            "CREATE TABLE " + LIFT_TABLE_NAME
                    + " ("
                    + LIFT_COLUMN_PK + " INTEGER PRIMARY KEY, "
                    + LIFT_COLUMN_SESSION_FK + " INTEGER, "
                    + LIFT_COLUMN_EXERCISE_FK + " INTEGER, "
                    + LIFT_COLUMN_DATE_CREATED + " INTEGER, "
                    + LIFT_COLUMN_WEIGHT + " INTEGER, "
                    + LIFT_COLUMN_REPS + " INTEGER, "
                    + LIFT_COLUMN_SETS + " INTEGER, "
                    + LIFT_COLUMN_UNIT + " TEXT, "
                    + LIFT_COLUMN_WARMUP + " INTEGER"
                    +  ")";


    public static final String SESSION_TABLE_NAME = "sessions";
    public static final String SESSION_COLUMN_PK = "id";
    public static final String SESSION_COLUMN_DATE = "date";
    public static final String SESSION_COLUMN_DATE_CREATED = "date_created";
    public static final String SESSION_TABLE_CREATE_QUERY =
            "CREATE TABLE " + SESSION_TABLE_NAME
                    + " ("
                    + SESSION_COLUMN_PK + " INTEGER PRIMARY KEY, "
                    + SESSION_COLUMN_DATE + " INTEGER, "
                    + SESSION_COLUMN_DATE_CREATED + " INTEGER"
                    +  ")";


    //TODO
    public static final String EXERCISE_TABLE_NAME = "exercises";
    public static final String EXERCISE_COLUMN_PK = "id";
    public static final String EXERCISE_COLUMN_NAME = "name";
    public static final String EXERCISE_COLUMN_DESCRIPTION = "description";
    public static final String EXERCISE_COLUMN_DATE = "date";
    public static final String EXERCISE_TABLE_CREATE_QUERY =
            "CREATE TABLE " + EXERCISE_TABLE_NAME
                    + " ("
                    + EXERCISE_COLUMN_PK + " INTEGER PRIMARY KEY, "
                    + EXERCISE_COLUMN_NAME + " TEXT, "
                    + EXERCISE_COLUMN_DESCRIPTION + " TEXT, "
                    + EXERCISE_COLUMN_DATE + " INTEGER"
                    +  ")";


    private static final String LOG_TAG = "liftlog.DataAccessObject";

    public DataAccessObject(Context context)
    {
        super(context, DB_NAME , null, 12);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(LIFT_TABLE_CREATE_QUERY);
        db.execSQL(SESSION_TABLE_CREATE_QUERY);
        db.execSQL(EXERCISE_TABLE_CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + LIFT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SESSION_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + EXERCISE_TABLE_NAME);
        onCreate(db);
    }

    public long insert(Lift lift)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LIFT_COLUMN_EXERCISE_FK, lift.getExerciseId());
        values.put(LIFT_COLUMN_SESSION_FK, lift.getSessionId());
        values.put(LIFT_COLUMN_DATE_CREATED, System.currentTimeMillis());
        values.put(LIFT_COLUMN_WEIGHT, lift.getWeight());
        values.put(LIFT_COLUMN_REPS, lift.getReps());
        values.put(LIFT_COLUMN_SETS, lift.getSets());
        values.put(LIFT_COLUMN_UNIT, lift.getUnit() == null ? "lb" : lift.getUnit().toString().toUpperCase());
        long id = db.insert(LIFT_TABLE_NAME, null, values);
        return id;
    }

    public long insert(Session session)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SESSION_COLUMN_DATE, session.getDate());
        values.put(SESSION_COLUMN_DATE_CREATED, System.currentTimeMillis());
        long id = db.insert(SESSION_TABLE_NAME, null, values);
        return id;
    }

    public boolean update(Lift lift)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String qry = "UPDATE " + LIFT_TABLE_NAME +
                " SET " + LIFT_COLUMN_WEIGHT + " = " + lift.getWeight() + ", " +
                LIFT_COLUMN_REPS + " = " + lift.getReps() + ", " +
                LIFT_COLUMN_SETS + " = " + lift.getSets() + ", " +
                LIFT_COLUMN_WARMUP + " = " + (lift.isWarmup() ? "1" : "0") +
                " WHERE " + LIFT_COLUMN_PK + " = " + lift.getId();
        try
        {
            db.execSQL(qry);
        }
        catch(SQLException e)
        {
            Log.d(LOG_TAG, e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public List<Exercise> selectExercises()
    {
        ArrayList<Exercise> result = new ArrayList<Exercise>();
        String qry = "SELECT * FROM " + EXERCISE_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(qry, null);

        if(cursor == null || cursor.getCount() < 1)
        {
            return null;
        }

        boolean hasNext = cursor.moveToFirst();
        while(hasNext)
        {
            Exercise exercise = new Exercise();

            String name = cursor.getString(cursor.getColumnIndex(EXERCISE_COLUMN_NAME));
            String desc = cursor.getString(cursor.getColumnIndex(EXERCISE_COLUMN_DESCRIPTION));

            exercise.setName(name);
            exercise.setDescription(desc);

            result.add(exercise);

            hasNext = cursor.moveToNext();
        }
        return result;
    }


    /**
     * Select all training sessions
     * @return A list of all Sessions
     */
    public List<Session> selectSessions()
    {
        return selectSessions(0, Integer.MAX_VALUE);
    }

    public List<Session> selectSessions(int startDate, int endDate)
    {
        ArrayList<Session> result = new ArrayList<Session>();

        SQLiteDatabase db = this.getReadableDatabase();
        String qry = "SELECT * FROM " + SESSION_TABLE_NAME;
        Cursor cursor = db.rawQuery(qry, null);
        int cnt = cursor.getCount();
        if(cursor == null || cursor.getCount() == 0)
        {

            return result;
        }

        cursor.moveToFirst();
        boolean hasNext = true;
        while(hasNext)
        {
            Session session = new Session();
            long id = cursor.getInt(cursor.getColumnIndex(SESSION_COLUMN_PK));
            long date = cursor.getLong(cursor.getColumnIndex(SESSION_COLUMN_DATE));
            session.setId(id);
            session.setDate(date);

            result.add(session);

            hasNext = cursor.moveToNext();
        }

        return result;
    }

    public Session selectSession(long id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String qrySession = "SELECT * FROM " + SESSION_TABLE_NAME;
        Cursor cursorSession =  db.rawQuery(qrySession, null);
        if(cursorSession == null || cursorSession.getCount() < 1)
        {
            return null;
        }

        cursorSession.moveToFirst();
        Session result = new Session();
        result.setId(id);
        result.setDate(cursorSession.getInt(cursorSession.getColumnIndex(SESSION_COLUMN_DATE)));

        String qryLifts  = "SELECT " +
                "b." + LIFT_COLUMN_PK + ", " +
                "b." + LIFT_COLUMN_SESSION_FK + ", " +
                "b." + LIFT_COLUMN_EXERCISE_FK + ", " +
                "b." + LIFT_COLUMN_WEIGHT + ", " +
                "b." + LIFT_COLUMN_REPS + ", " +
                "b." + LIFT_COLUMN_SETS + ", " +
                "b." + LIFT_COLUMN_WARMUP + ", " +
                "a." + SESSION_COLUMN_DATE + //" as " + sessionDateAlias +
                "" +
                " FROM " + SESSION_TABLE_NAME + " as a," + LIFT_TABLE_NAME + " as b" +
                " WHERE a." + SESSION_COLUMN_PK + " = b." + LIFT_COLUMN_SESSION_FK +
                " AND a." + SESSION_COLUMN_PK + " = " + id;


        Cursor cursor = null;
        try {
            cursor = db.rawQuery(qryLifts, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        int cnt = cursor.getCount();
//        if(cursor == null || cursor.getCount() == 0)
//        {
//            return null;
//        }


//        int sessionDate = -1;

        boolean hasNext = cursor.moveToFirst();
        while(hasNext)
        {

            Lift lift = new Lift();

            long liftId = cursor.getLong(cursor.getColumnIndex(LIFT_COLUMN_PK));
            long exerciseId = cursor.getLong(cursor.getColumnIndex(LIFT_COLUMN_EXERCISE_FK));
            long sessionId = cursor.getLong(cursor.getColumnIndex(LIFT_COLUMN_SESSION_FK));
            int weight = cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_WEIGHT));
            int reps = cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_REPS));
            int sets = cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_SETS));
            int warmup = cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_WARMUP));
            lift.setId(liftId);
            lift.setExerciseId(exerciseId);
            lift.setSessionId(sessionId);
            lift.setWeight(weight);
            lift.setReps(reps);
            lift.setSets(sets);
            lift.setWarmup(warmup == 1 ? true : false);


            result.getLifts().add(lift);

            hasNext = cursor.moveToNext();
        }

        return result;
    }

    public Lift selectLift(long id)
    {
        Lift result = new Lift();

        SQLiteDatabase db = this.getReadableDatabase();
        String qry = "SELECT * FROM " + LIFT_TABLE_NAME +
                " WHERE " + LIFT_COLUMN_PK + " = " + id;
        Cursor cursor = db.rawQuery(qry, null);
        if(cursor == null || cursor.getCount() < 1) return null;

        cursor.moveToFirst();

        long exerciseId = cursor.getLong(cursor.getColumnIndex(LIFT_COLUMN_EXERCISE_FK));
        long sessionId = cursor.getLong(cursor.getColumnIndex(LIFT_COLUMN_SESSION_FK));
        int weight = cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_WEIGHT));
        int sets = cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_SETS));
        int reps = cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_REPS));
        int warmup = cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_WARMUP));

        result.setId(id);
        result.setExerciseId(exerciseId);
        result.setSessionId(sessionId);
        result.setWeight(weight);
        result.setSets(sets);
        result.setReps(reps);
        result.setWarmup(warmup == 1 ? true : false);

        return result;
    }


    public void clearLiftsTable()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String qry = "DELETE FROM " + LIFT_TABLE_NAME;
        db.execSQL(qry);
    }

    public boolean deleteLift(long id)
    {
        String qry = "DELETE FROM " + LIFT_TABLE_NAME +
                " WHERE " + LIFT_COLUMN_PK + " = " + id;
        try {
            this.getWritableDatabase().execSQL(qry);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            Log.d(LOG_TAG, "Error removing session id=" + id + ":" + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Delete the Session matching the id. Also, delete all Lifts related to the Session.
     * @param id The id of the session
     * @return True on success
     */
    public boolean deleteSession(long id)
    {
        String qrySessions = "DELETE FROM " + SESSION_TABLE_NAME +
                " WHERE " + SESSION_COLUMN_PK + " = " + id;
        String qryLifts = "DELETE FROM " + LIFT_TABLE_NAME +
                " WHERE " + LIFT_COLUMN_SESSION_FK + " = " + id;
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL(qrySessions);
            db.execSQL(qryLifts);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            Log.d(LOG_TAG, "Error removing session id=" + id + ":" + e.getMessage());
            return false;
        }
        return true;
    }

    public void clearExerciseTable()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String qry = "DELETE FROM " + EXERCISE_TABLE_NAME;
        db.execSQL(qry);
    }

    public void clearSessionsTable()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String qry = "DELETE FROM " + SESSION_TABLE_NAME;
        db.execSQL(qry);
    }




    //tester method
    public void insertDummySessions()
    {
        clearSessionsTable();
        final long millisPerDay = 86400000;
        for(int i = 0; i < 10; i++)
        {
            Session session = new Session();
            session.setDate(System.currentTimeMillis() - (millisPerDay * i));
            insert(session);
        }
    }

    //tester method
    public void insertDummyLifts()
    {
        clearLiftsTable();


        for(int i = 0; i < 20; i++)
        {
            Lift lift = new Lift();
            lift.setSessionId(3);
            lift.setExerciseId(i);
            lift.setWeight(315);
            lift.setSets(3);
            lift.setReps(5);
            insert(lift);
        }
    }

    //tester method
    public void test()
    {
        String qry = "DELETE FROM " + SESSION_TABLE_NAME + " WHERE " + SESSION_COLUMN_DATE + " < 1000";
        this.getWritableDatabase().execSQL(qry);

    }


}