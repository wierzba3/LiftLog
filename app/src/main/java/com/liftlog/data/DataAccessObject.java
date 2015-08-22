package com.liftlog.data;

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

import com.liftlog.backend.myApi.model.ExerciseAPI;
import com.liftlog.models.Lift;
import com.liftlog.models.Exercise;
import com.liftlog.models.Session;

/**
 * Created by James Wierzba on 6/8/2015.
 */
public class DataAccessObject extends SQLiteOpenHelper
{

//    public static Map<Long, Exercise> exerciseMap = new HashMap<>();

    public enum RecordState
    {

        UNCHANGED(0),
        MODIFIED(1),
        DELETED(2),
        NEW(3),
        UNKNOWN(4);
        ;

        RecordState(int value)
        {
            this.value = value;
        }

        private int value;

        public int getValue()
        {
            return value;
        }

        public static RecordState fromValue(int value)
        {
            for(RecordState state : RecordState.values())
            {
                if(state.getValue() == value) return state;
            }
            return null;
        }


    }


    public static final String DB_NAME = "Liftlog.db";

    public static final String LIFT_TABLE_NAME = "lifts";
    public static final String LIFT_COLUMN_PK = "id";
    public static final String LIFT_COLUMN_SESSION_FK = "session_id";
    public static final String LIFT_COLUMN_EXERCISE_FK = "exercise_id";
    public static final String LIFT_COLUMN_DATE_CREATED = "date_created";
    public static final String LIFT_COLUMN_WEIGHT = "weight";
    public static final String LIFT_COLUMN_REPS = "reps";
    public static final String LIFT_COLUMN_SETS = "sets";
    public static final String LIFT_COLUMN_UNIT = "unit";
    public static final String LIFT_COLUMN_STATE = "state";
    /**
     * boolean field, 0 = false, 1 = true
     */
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
                    + LIFT_COLUMN_WARMUP + " INTEGER, "
                    + LIFT_COLUMN_STATE + " INTEGER"
                    + ")";


    public static final String SESSION_TABLE_NAME = "sessions";
    public static final String SESSION_COLUMN_PK = "id";
    public static final String SESSION_COLUMN_DATE = "date";
    public static final String SESSION_COLUMN_DATE_CREATED = "date_created";
    //TODO
    public static final String SESSION_COLUMN_STATE = "state";
    public static final String SESSION_TABLE_CREATE_QUERY =
            "CREATE TABLE " + SESSION_TABLE_NAME
                    + " ("
                    + SESSION_COLUMN_PK + " INTEGER PRIMARY KEY, "
                    + SESSION_COLUMN_DATE + " INTEGER, "
                    + SESSION_COLUMN_DATE_CREATED + " INTEGER, "
                    + SESSION_COLUMN_STATE + " INTEGER"
                    + ")";


    public static final String EXERCISE_TABLE_NAME = "exercises";
    public static final String EXERCISE_COLUMN_PK = "id";
    public static final String EXERCISE_COLUMN_NAME = "name";
    public static final String EXERCISE_COLUMN_DESCRIPTION = "description";
    public static final String EXERCISE_COLUMN_VALID = "valid";
    public static final String EXERCISE_COLUMN_DATE = "date_created";
    //TODO
    public static final String EXERCISE_COLUMN_STATE = "state";
    public static final String EXERCISE_TABLE_CREATE_QUERY =
            "CREATE TABLE " + EXERCISE_TABLE_NAME
                    + " ("
                    + EXERCISE_COLUMN_PK + " INTEGER PRIMARY KEY, "
                    + EXERCISE_COLUMN_NAME + " TEXT, "
                    + EXERCISE_COLUMN_DESCRIPTION + " TEXT, "
                    + EXERCISE_COLUMN_VALID + " INTEGER DEFAULT 1, "
                    + EXERCISE_COLUMN_DATE + " INTEGER, "
                    + EXERCISE_COLUMN_STATE + " INTEGER"
                    + ")";


    private static final String LOG_TAG = "LiftLog";

//    private static DataAccessObject instance;
//    public synchronized static DataAccessObject getInstance(Context context)
//    {
//        if(instance == null)
//        {
//            instance = new DataAccessObject(context);
//        }
//        return instance;
//    }

    public DataAccessObject(Context context)
    {
        super(context, DB_NAME, null, 21);
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
        List<Session> sessions = null;
        List<Lift> lifts = null;
        Map<Long, Exercise> exercises = null;
        try
        {
            sessions = selectSessions(db, 0, Integer.MAX_VALUE);
            lifts = selectLifts(db);
            exercises = selectExercises(db);
        } catch (Exception ex)
        {
            ex.printStackTrace();
            Log.d(LOG_TAG, ex.getMessage());
        }

        db.execSQL("DROP TABLE IF EXISTS " + LIFT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SESSION_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + EXERCISE_TABLE_NAME);
        onCreate(db);

        if (sessions != null) for (Session session : sessions) insert(db, session);
        if (lifts != null) for (Lift lift : lifts) insert(db, lift);
        if (exercises != null) for (Exercise exercise : exercises.values()) insert(db, exercise);
    }

    public long insert(Lift lift)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return insert(db, lift);
    }

    public long insert(SQLiteDatabase db, Lift lift)
    {
        ContentValues values = new ContentValues();
        values.put(LIFT_COLUMN_EXERCISE_FK, lift.getExerciseId());
        values.put(LIFT_COLUMN_SESSION_FK, lift.getSessionId());
        values.put(LIFT_COLUMN_DATE_CREATED, System.currentTimeMillis());
        values.put(LIFT_COLUMN_WEIGHT, lift.getWeight());
        values.put(LIFT_COLUMN_REPS, lift.getReps());
        values.put(LIFT_COLUMN_SETS, lift.getSets());
        values.put(LIFT_COLUMN_UNIT, lift.getUnit() == null ? "lb" : lift.getUnit().toString().toUpperCase());
        values.put(LIFT_COLUMN_STATE, RecordState.UNCHANGED.getValue());
        long id = db.insert(LIFT_TABLE_NAME, null, values);
        return id;
    }


    public long insert(Exercise exercise)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return insert(db, exercise);
    }

    public long insert(SQLiteDatabase db, Exercise exercise)
    {
        ContentValues values = new ContentValues();
        if(exercise.getId() > 0)
        {
            values.put(EXERCISE_COLUMN_PK, exercise.getId());
        }
        values.put(EXERCISE_COLUMN_NAME, exercise.getName());
        values.put(EXERCISE_COLUMN_DESCRIPTION, exercise.getDescription());
        values.put(EXERCISE_COLUMN_VALID, exercise.isValid() ? 1 : 0);
        values.put(EXERCISE_COLUMN_DATE, System.currentTimeMillis());
        values.put(EXERCISE_COLUMN_STATE,  exercise.getState().getValue());
        long id = db.insert(EXERCISE_TABLE_NAME, null, values);
        return id;
    }

    public long insert(ExerciseAPI exercise)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return insert(db, exercise);
    }

    public long insert(SQLiteDatabase db, ExerciseAPI exercise)
    {
        ContentValues values = new ContentValues();
        values.put(EXERCISE_COLUMN_NAME, exercise.getName());
        values.put(EXERCISE_COLUMN_DESCRIPTION, exercise.getDescription());
        values.put(EXERCISE_COLUMN_DATE, System.currentTimeMillis());
        long id = db.insert(EXERCISE_TABLE_NAME, null, values);
        return id;
    }

    public boolean update(Exercise exercise)
    {
        if (exercise == null || exercise.getId() == -1) return false;
        SQLiteDatabase db = this.getWritableDatabase();
//        String qry = "UPDATE " + EXERCISE_TABLE_NAME +
//                " SET " + EXERCISE_COLUMN_NAME + " = " + exercise.getName() + ", " +
//                EXERCISE_COLUMN_DESCRIPTION + " = " + exercise.getDescription()  +
//                " WHERE " + EXERCISE_COLUMN_PK + " = " + exercise.getId();
        ContentValues values = new ContentValues();
        values.put(EXERCISE_COLUMN_NAME, exercise.getName());
        values.put(EXERCISE_COLUMN_DESCRIPTION, exercise.getDescription());
        values.put(EXERCISE_COLUMN_VALID, exercise.isValid() ? 1 : 0);
        values.put(EXERCISE_COLUMN_DATE, System.currentTimeMillis());
        values.put(EXERCISE_COLUMN_STATE, exercise.getState().getValue());

        try
        {
            db.update(EXERCISE_TABLE_NAME, values, EXERCISE_COLUMN_PK + " = " + exercise.getId(), null);
//            data.execSQL(qry);
        } catch (SQLException e)
        {
            Log.d(LOG_TAG, e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public long insert(Session session)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return insert(db, session);
    }

    public long insert(SQLiteDatabase db, Session session)
    {
        ContentValues values = new ContentValues();
        values.put(SESSION_COLUMN_DATE, session.getDate());
        values.put(SESSION_COLUMN_DATE_CREATED, System.currentTimeMillis());
        values.put(SESSION_COLUMN_STATE, session.getState().getValue());
        long id = db.insert(SESSION_TABLE_NAME, null, values);
        return id;
    }

    public boolean update(Lift lift)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LIFT_COLUMN_WEIGHT, lift.getWeight());
        values.put(LIFT_COLUMN_REPS, lift.getReps());
        values.put(LIFT_COLUMN_SETS, lift.getSets());
        values.put(LIFT_COLUMN_EXERCISE_FK, lift.getExerciseId());
        values.put(LIFT_COLUMN_SESSION_FK, lift.getSessionId());
        values.put(LIFT_COLUMN_STATE, lift.getState().getValue());
        try
        {
            db.update(LIFT_TABLE_NAME, values, LIFT_COLUMN_PK + " = " + lift.getId(), null);
        } catch (SQLException e)
        {
            Log.d(LOG_TAG, e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public Exercise selectExercise(long id)
    {
        Exercise result = null;

        SQLiteDatabase db = this.getReadableDatabase();
        String qry = "SELECT * FROM " + EXERCISE_TABLE_NAME +
                " WHERE " + EXERCISE_COLUMN_PK + " = " + id;
        Cursor cursor = db.rawQuery(qry, null);
        if (cursor == null)
        {
            return null;
        }
        cursor.moveToFirst();

        String name = cursor.getString(cursor.getColumnIndex(EXERCISE_COLUMN_NAME));
        String desc = cursor.getString(cursor.getColumnIndex(EXERCISE_COLUMN_DESCRIPTION));
        int valid = cursor.getInt(cursor.getColumnIndex(EXERCISE_COLUMN_VALID));
        RecordState state = RecordState.fromValue(cursor.getInt(cursor.getColumnIndex(EXERCISE_COLUMN_STATE)));

        result = new Exercise(state);
        result.setId(id);
        result.setName(name);
        result.setValid(valid == 1 ? true : false);
        result.setDescription(desc);

        return result;
    }


    public Map<Long, Exercise> selectExercises()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        return selectExercises(db);
    }

    public Map<Long, Exercise> selectExercises(SQLiteDatabase db)
    {
//        ArrayList<Exercise> result = new ArrayList<Exercise>();
        HashMap<Long, Exercise> result = new HashMap<>();
        String qry = "SELECT * FROM " + EXERCISE_TABLE_NAME;
        Cursor cursor = db.rawQuery(qry, null);

        if (cursor == null)
        {
            return null;
        }

        boolean hasNext = cursor.moveToFirst();
        while (hasNext)
        {

            long id = cursor.getLong(cursor.getColumnIndex(EXERCISE_COLUMN_PK));
            String name = cursor.getString(cursor.getColumnIndex(EXERCISE_COLUMN_NAME));
            String desc = cursor.getString(cursor.getColumnIndex(EXERCISE_COLUMN_DESCRIPTION));
            int valid = cursor.getInt(cursor.getColumnIndex(EXERCISE_COLUMN_VALID));
            RecordState state = RecordState.fromValue(cursor.getInt(cursor.getColumnIndex(EXERCISE_COLUMN_STATE)));

            Exercise exercise = new Exercise(state);
            exercise.setId(id);
            exercise.setName(name);
            exercise.setValid(valid == 1 ? true : false);
            exercise.setDescription(desc);

            result.put(id, exercise);

            hasNext = cursor.moveToNext();
        }

        return result;
    }

    public List<Lift> selectLifts()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        return selectLifts(db);
    }

    public List<Lift> selectLifts(SQLiteDatabase db)
    {
        ArrayList<Lift> result = new ArrayList<Lift>();
        String qry = "SELECT * FROM " + LIFT_TABLE_NAME;
        Cursor cursor = db.rawQuery(qry, null);
        if (cursor == null || cursor.getCount() == 0)
        {
            return result;
        }

        cursor.moveToFirst();
        boolean hasNext = true;
        while (hasNext)
        {


            long id = cursor.getLong(cursor.getColumnIndex(LIFT_COLUMN_PK));
            long exerciseId = cursor.getLong(cursor.getColumnIndex(LIFT_COLUMN_EXERCISE_FK));
            long sessionId = cursor.getLong(cursor.getColumnIndex(LIFT_COLUMN_SESSION_FK));
            int weight = cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_WEIGHT));
            int sets = cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_SETS));
            int reps = cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_REPS));
            int warmup = cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_WARMUP));
            RecordState state = RecordState.fromValue(cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_STATE)));
            long dateCreated = cursor.getLong(cursor.getColumnIndex(LIFT_COLUMN_DATE_CREATED));

            Lift lift = new Lift(state);
            lift.setId(id);
            lift.setExerciseId(exerciseId);
            lift.setSessionId(sessionId);
            lift.setWeight(weight);
            lift.setSets(sets);
            lift.setReps(reps);
            lift.setWarmup(warmup == 1 ? true : false);
            lift.setDateCreated(dateCreated);

            hasNext = cursor.moveToNext();
        }

        return result;
    }


    public List<Session> selectSessions()
    {
        try
        {
            SQLiteDatabase db = this.getReadableDatabase();
            return selectSessions(db);
        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Select all training sessions
     *
     * @return A list of all Sessions
     */
    public List<Session> selectSessions(SQLiteDatabase db)
    {
        return selectSessions(db, 0, Integer.MAX_VALUE);
    }

    public List<Session> selectSessions(SQLiteDatabase db, int startDate, int endDate)
    {
        ArrayList<Session> result = null;
        Cursor cursor = null;
        try
        {
            result = new ArrayList<Session>();
            String qry = "SELECT * FROM " + SESSION_TABLE_NAME;
            cursor = db.rawQuery(qry, null);
            if (cursor == null || cursor.getCount() == 0)
            {
                return result;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        cursor.moveToFirst();
        boolean hasNext = true;
        while (hasNext)
        {

            long id = cursor.getInt(cursor.getColumnIndex(SESSION_COLUMN_PK));
            long date = cursor.getLong(cursor.getColumnIndex(SESSION_COLUMN_DATE));
            RecordState state = RecordState.fromValue(cursor.getInt(cursor.getColumnIndex(SESSION_COLUMN_STATE)));

            Session session = new Session(state);
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
        Cursor cursorSession = db.rawQuery(qrySession, null);
        if (cursorSession == null)
        {
            return null;
        }

        cursorSession.moveToFirst();


        long date = cursorSession.getLong(cursorSession.getColumnIndex(SESSION_COLUMN_DATE));
        RecordState state = RecordState.fromValue(cursorSession.getInt(cursorSession.getColumnIndex(SESSION_COLUMN_STATE)));

        Session result = new Session(state);
        result.setId(id);
        result.setDate(date);

        String qryLifts = "SELECT " +
                "b." + LIFT_COLUMN_PK + ", " +
                "b." + LIFT_COLUMN_SESSION_FK + ", " +
                "b." + LIFT_COLUMN_EXERCISE_FK + ", " +
                "b." + LIFT_COLUMN_WEIGHT + ", " +
                "b." + LIFT_COLUMN_REPS + ", " +
                "b." + LIFT_COLUMN_SETS + ", " +
                "b." + LIFT_COLUMN_DATE_CREATED + ", " +
                "b." + LIFT_COLUMN_WARMUP + ", " +
                "b." + LIFT_COLUMN_STATE + ", " +
                "a." + SESSION_COLUMN_DATE  +
                " FROM " + SESSION_TABLE_NAME + " as a," + LIFT_TABLE_NAME + " as b" +
                " WHERE a." + SESSION_COLUMN_PK + " = b." + LIFT_COLUMN_SESSION_FK +
                " AND a." + SESSION_COLUMN_PK + " = " + id;


        Cursor cursor = null;
        try
        {
            cursor = db.rawQuery(qryLifts, null);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        boolean hasNext = cursor.moveToFirst();
        while (hasNext)
        {



            long liftId = cursor.getLong(cursor.getColumnIndex(LIFT_COLUMN_PK));
            long exerciseId = cursor.getLong(cursor.getColumnIndex(LIFT_COLUMN_EXERCISE_FK));
            long sessionId = cursor.getLong(cursor.getColumnIndex(LIFT_COLUMN_SESSION_FK));
            int weight = cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_WEIGHT));
            int reps = cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_REPS));
            int sets = cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_SETS));
            int warmup = cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_WARMUP));
            long dateCreated = cursor.getLong(cursor.getColumnIndex(LIFT_COLUMN_DATE_CREATED));
            RecordState liftState = RecordState.fromValue(cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_STATE)));

            Lift lift = new Lift(liftState);
            lift.setId(liftId);
            lift.setExerciseId(exerciseId);
            lift.setSessionId(sessionId);
            lift.setWeight(weight);
            lift.setReps(reps);
            lift.setSets(sets);
            lift.setWarmup(warmup == 1 ? true : false);
            lift.setDateCreated(dateCreated);

            result.getLifts().add(lift);

            hasNext = cursor.moveToNext();
        }

        Map<Long, Exercise> exercises = selectExercises();
        for (Lift lift : result.getLifts())
        {
            long exerciseId = lift.getExerciseId();
            Exercise exercise = exercises.get(exerciseId);
            if (exercise != null) lift.setExerciseName(exercise.getName());
        }

        return result;
    }

    public Lift selectLift(long id)
    {


        SQLiteDatabase db = this.getReadableDatabase();
        String qry = "SELECT * FROM " + LIFT_TABLE_NAME +
                " WHERE " + LIFT_COLUMN_PK + " = " + id;
        Cursor cursor = db.rawQuery(qry, null);
        if (cursor == null)
        {
            return null;
        }

        cursor.moveToFirst();

        long exerciseId = cursor.getLong(cursor.getColumnIndex(LIFT_COLUMN_EXERCISE_FK));
        long sessionId = cursor.getLong(cursor.getColumnIndex(LIFT_COLUMN_SESSION_FK));
        int weight = cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_WEIGHT));
        int sets = cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_SETS));
        int reps = cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_REPS));
        int warmup = cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_WARMUP));
        long dateCreated = cursor.getLong(cursor.getColumnIndex(LIFT_COLUMN_DATE_CREATED));
        RecordState state = RecordState.fromValue(cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_STATE)));

        Lift result = new Lift(state);
        result.setId(id);
        result.setExerciseId(exerciseId);
        result.setSessionId(sessionId);
        result.setWeight(weight);
        result.setSets(sets);
        result.setReps(reps);
        result.setWarmup(warmup == 1 ? true : false);
        result.setDateCreated(dateCreated);

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
        try
        {
            this.getWritableDatabase().execSQL(qry);
        } catch (SQLException e)
        {
            e.printStackTrace();
            Log.d(LOG_TAG, "Error removing session id=" + id + ":" + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Delete the Session matching the id. Also, delete all Lifts related to the Session.
     *
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
        } catch (SQLException e)
        {
            e.printStackTrace();
            Log.d(LOG_TAG, "Error removing session id=" + id + ":" + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Delete the Exercise matching the id.
     *
     * @param id The id of the session
     * @return True on success
     */
    public boolean deleteExercise(long id)
    {
        String qry = "DELETE FROM " + EXERCISE_TABLE_NAME +
                " WHERE " + EXERCISE_COLUMN_PK + " = " + id;
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL(qry);
        } catch (SQLException e)
        {
            e.printStackTrace();
            Log.d(LOG_TAG, "Error removing exercise id=" + id + ":" + e.getMessage());
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
        for (int i = 0; i < 10; i++)
        {
            Session session = new Session(RecordState.UNKNOWN);
            session.setState(RecordState.NEW);
            session.setDate(System.currentTimeMillis() - (millisPerDay * i));
            insert(session);
        }
    }

    //tester method
    public void insertDummyLifts()
    {
        clearLiftsTable();


        for (long i = 0; i < 20; i++)
        {
            Lift lift = new Lift(RecordState.UNKNOWN);
            lift.setState(RecordState.UNCHANGED);
            lift.setSessionId(3l);
            lift.setExerciseId(i);
            lift.setWeight(315);
            lift.setSets(3);
            lift.setReps(5);
            lift.setDateCreated(System.currentTimeMillis());
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
