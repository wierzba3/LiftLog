package wierzba.james.liftlog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import wierzba.james.liftlog.models.Exercise;
import wierzba.james.liftlog.models.Lift;
import wierzba.james.liftlog.models.Session;

/**
 * Created by jwierzba on 6/8/2015.
 */
public class DataAccessObject extends SQLiteOpenHelper
{

    //TODO
    public static Map<Integer, Exercise> exerciseMap;

    public static final String DB_NAME = "LiftLog.db";

    public static final String LIFT_TABLE_NAME = "lifts";
    public static final String LIFT_COLUMN_PK = "id";
    public static final String LIFT_COLUMN_SESSION_FK = "session_id";
    public static final String LIFT_COLUMN_EXERCISE_FK = "exercise_id";
//    public static final String LIFT_COLUMN_DATE = "date";
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

    private static final String LOG_TAG = "liftlog.DataAccessObject";

    public DataAccessObject(Context context)
    {
        super(context, DB_NAME , null, 11);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(LIFT_TABLE_CREATE_QUERY);
        db.execSQL(SESSION_TABLE_CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + LIFT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SESSION_TABLE_NAME);
        onCreate(db);
    }

    public long insert(Lift lift)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LIFT_COLUMN_EXERCISE_FK, lift.getExerciseId());
        values.put(LIFT_COLUMN_SESSION_FK, lift.getSessionId());
        values.put(LIFT_COLUMN_DATE_CREATED, now());
//        values.put(LIFT_COLUMN_DATE, lift.getDate());
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
        values.put(SESSION_COLUMN_DATE_CREATED, now());
        long id = db.insert(SESSION_TABLE_NAME, null, values);
        return id;
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
            int date = cursor.getInt(cursor.getColumnIndex(SESSION_COLUMN_DATE));
            session.setId(id);
            session.setDate(date);

            result.add(session);

            hasNext = cursor.moveToNext();
        }

        return result;
    }

    public Session selectSession(long id)
    {
        //column alias' to eliminate ambiguity in the join
//        String liftDateAlias = "lift_date";
//        String sessionDateAlias = "session_date";

        //TODO: this query throws an error
        /*
        SELECT (b.id, b.exercise_id, b.date as lift_date, b.weight, b.reps, b.sets, a.date as session_date)
        FROM sessions as a,
        lifts as b
        WHERE a.id = b.session_id AND a.id = 1
         */
        String qry  = "SELECT " +
                "b." + LIFT_COLUMN_PK + ", " +
                "b." + LIFT_COLUMN_SESSION_FK + ", " +
                "b." + LIFT_COLUMN_EXERCISE_FK + ", " +
//                "b." + LIFT_COLUMN_DATE + " as " + liftDateAlias + ", " +
                "b." + LIFT_COLUMN_WEIGHT + ", " +
                "b." + LIFT_COLUMN_REPS + ", " +
                "b." + LIFT_COLUMN_SETS + ", " +
                "b." + LIFT_COLUMN_WARMUP + ", " +
                "a." + SESSION_COLUMN_DATE + //" as " + sessionDateAlias +
                "" +
                " FROM " + SESSION_TABLE_NAME + " as a," + LIFT_TABLE_NAME + " as b" +
                " WHERE a." + SESSION_COLUMN_PK + " = b." + LIFT_COLUMN_SESSION_FK +
                " AND a." + SESSION_COLUMN_PK + " = " + id;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(qry, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int cnt = cursor.getCount();
        if(cursor == null || cursor.getCount() == 0) return null;

        Session result = new Session();
        int sessionDate = -1;

        boolean hasNext = cursor.moveToFirst();
        while(hasNext)
        {
            sessionDate = cursor.getInt(cursor.getColumnIndex(SESSION_COLUMN_DATE));

            Lift lift = new Lift();

            long liftId = cursor.getLong(cursor.getColumnIndex(LIFT_COLUMN_PK));
//            int date = cursor.getInt(cursor.getColumnIndex(liftDateAlias));
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

        result.setId(id);
        result.setDate(sessionDate);

        return result;
    }

    public Lift selectLift(long id)
    {
        Lift result = new Lift();

        SQLiteDatabase db = this.getReadableDatabase();
        String qry = "SELECT * FROM " + LIFT_TABLE_NAME;
        Cursor cursor = db.rawQuery(qry, null);
        if(cursor == null || cursor.getCount() < 1) return null;

        cursor.moveToFirst();



        return result;
    }


    public void clearLiftsTable()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String qry = "DELETE FROM " + LIFT_TABLE_NAME;
        db.execSQL(qry);
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

    /**
     * Get current date represented as seconds since the epoch.
     * @return The current date
     */
    private int now()
    {
        return (int)(System.currentTimeMillis() / 1000);
    }


    //tester method
    public void insertDummySessions()
    {
        clearSessionsTable();

        for(int i = 0; i < 10; i++)
        {
            Session session = new Session();
            session.setDate(i + 1);
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
        String qryLift = "SELECT * FROM " + LIFT_TABLE_NAME;
        String qrySession = "SELECT * FROM " + SESSION_TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursorLift = db.rawQuery(qryLift, null);
        Cursor cursorSession = db.rawQuery(qrySession, null);

        int liftCnt = cursorLift.getCount();
        int sessionCnt = cursorSession.getCount();

        boolean hasNext;
        hasNext = cursorLift.moveToFirst();
        while(hasNext)
        {
            int exerciseId = cursorLift.getInt(cursorLift.getColumnIndex(LIFT_COLUMN_EXERCISE_FK));
        }

        hasNext = cursorSession.moveToFirst();
        while(hasNext)
        {
            int id = cursorSession.getInt(cursorSession.getColumnIndex(SESSION_COLUMN_PK));
        }

    }


}
