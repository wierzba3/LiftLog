package com.liftlog.data;

import android.app.backup.BackupManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.liftlog.R;
import com.liftlog.common.Util;
import com.liftlog.models.Category;
import com.liftlog.models.Lift;
import com.liftlog.models.Exercise;
import com.liftlog.models.Session;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;

/**
 * Created by James Wierzba on 6/8/2015.
 */
public class DataAccessObject extends SQLiteOpenHelper
{

//    public static Map<Long, Exercise> exerciseMap = new HashMap<>();

    @Deprecated
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
    private static final String DB_COPY_NAME = "Liftlog-bkp.db";
    private static final String DB_BACKUP_PREFERENCE_KEY = "last_update";
    /** Day frequency to backup the db */
    private static final int dbBackupFrequency = 1;

    private static final String DB_BACKUP_FILE_PREFIX = "LiftLogBackup";

    public static final String LIFT_TABLE_NAME = "lifts";
    public static final String LIFT_COLUMN_PK = "id";
    public static final String LIFT_COLUMN_SESSION_FK = "session_id";
    public static final String LIFT_COLUMN_EXERCISE_FK = "exercise_id";
    public static final String LIFT_COLUMN_DATE_CREATED = "date_created";
    public static final String LIFT_COLUMN_WEIGHT = "weight";
    public static final String LIFT_COLUMN_REPS = "reps";
    public static final String LIFT_COLUMN_SETS = "sets";
    public static final String LIFT_COLUMN_RPE = "rpe";
    public static final String LIFT_COLUMN_UNIT = "unit";
    //    public static final String LIFT_COLUMN_STATE = "state";
    public static final String LIFT_COLUMN_NEW = "is_new";
    public static final String LIFT_COLUMN_MODIFIED = "is_modified";
    public static final String LIFT_COLUMN_DELETED = "is_deleted";
    public static final String LIFT_COLUMN_WARMUP = "warmup";
    public static final String LIFT_TABLE_CREATE_QUERY =
            "CREATE TABLE " + LIFT_TABLE_NAME
                    + " ("
                    + LIFT_COLUMN_PK + " INTEGER PRIMARY KEY, "
                    + LIFT_COLUMN_SESSION_FK + " INTEGER, "
                    + LIFT_COLUMN_EXERCISE_FK + " INTEGER, "
                    + LIFT_COLUMN_DATE_CREATED + " INTEGER, "
                    + LIFT_COLUMN_WEIGHT + " REAL, "
                    + LIFT_COLUMN_REPS + " INTEGER, "
                    + LIFT_COLUMN_SETS + " INTEGER, "
                    + LIFT_COLUMN_RPE + " REAL, "
                    + LIFT_COLUMN_UNIT + " TEXT, "
                    + LIFT_COLUMN_WARMUP + " INTEGER, "
                    + LIFT_COLUMN_NEW + " INTEGER DEFAULT 1, "
                    + LIFT_COLUMN_MODIFIED + " INTEGER DEFAULT 0, "
                    + LIFT_COLUMN_DELETED + " INTEGER DEFAULT 0"
                    + ")";


    public static final String SESSION_TABLE_NAME = "sessions";
    public static final String SESSION_COLUMN_PK = "id";
    public static final String SESSION_COLUMN_DATE = "date";
    public static final String SESSION_COLUMN_DATE_CREATED = "date_created";
    //    public static final String SESSION_COLUMN_STATE = "state";
    public static final String SESSION_COLUMN_NOTE = "note";
    public static final String SESSION_COLUMN_NEW = "is_new";
    public static final String SESSION_COLUMN_MODIFIED = "is_modified";
    public static final String SESSION_COLUMN_DELETED = "is_deleted";
    public static final String SESSION_TABLE_CREATE_QUERY =
            "CREATE TABLE " + SESSION_TABLE_NAME
                    + " ("
                    + SESSION_COLUMN_PK + " INTEGER PRIMARY KEY, "
                    + SESSION_COLUMN_DATE + " INTEGER, "
                    + SESSION_COLUMN_DATE_CREATED + " INTEGER, "
                    + SESSION_COLUMN_NOTE + " TEXT, "
                    + SESSION_COLUMN_NEW + " INTEGER DEFAULT 1, "
                    + SESSION_COLUMN_MODIFIED + " INTEGER DEFAULT 0, "
                    + SESSION_COLUMN_DELETED + " INTEGER DEFAULT 0"
                    + ")";


    public static final String EXERCISE_TABLE_NAME = "exercises";
    public static final String EXERCISE_COLUMN_PK = "id";
    public static final String EXERCISE_COLUMN_CATEGORY_FK = "category_id";
    public static final String EXERCISE_COLUMN_NAME = "name";
    public static final String EXERCISE_COLUMN_DESCRIPTION = "description";
    public static final String EXERCISE_COLUMN_VALID = "valid";
    public static final String EXERCISE_COLUMN_DATE_CREATED = "date_created";
    public static final String EXERCISE_COLUMN_NEW = "is_new";
    public static final String EXERCISE_COLUMN_MODIFIED = "is_modified";
    public static final String EXERCISE_COLUMN_DELETED = "is_deleted";
    public static final String EXERCISE_TABLE_CREATE_QUERY =
            "CREATE TABLE " + EXERCISE_TABLE_NAME
                    + " ("
                    + EXERCISE_COLUMN_PK + " INTEGER PRIMARY KEY, "
                    + EXERCISE_COLUMN_CATEGORY_FK + " INTEGER, "
                    + EXERCISE_COLUMN_NAME + " TEXT, "
                    + EXERCISE_COLUMN_DESCRIPTION + " TEXT, "
                    + EXERCISE_COLUMN_VALID + " INTEGER DEFAULT 1, "
                    + EXERCISE_COLUMN_DATE_CREATED + " INTEGER, "
                    + EXERCISE_COLUMN_NEW + " INTEGER DEFAULT 1, "
                    + EXERCISE_COLUMN_MODIFIED + " INTEGER DEFAULT 0, "
                    + EXERCISE_COLUMN_DELETED + " INTEGER DEFAULT 0"
                    + ")";

    public static String CATEGORY_TABLE_NAME = "category";
    public static String CATEGORY_COLUMN_PK = "id";
    public static String CATEGORY_COLUMN_NAME = "name";
    public static String CATEGORY_COLUMN_DATE_CREATED = "date_created";
    public static final String CATEGORY_COLUMN_NEW = "is_new";
    public static final String CATEGORY_COLUMN_MODIFIED = "is_modified";
    public static final String CATEGORY_COLUMN_DELETED = "is_deleted";
    public static final String CATEGORY_TABLE_CREATE_QUERY =
            "CREATE TABLE " + CATEGORY_TABLE_NAME
                    + " ("
                    + CATEGORY_COLUMN_PK + " INTEGER PRIMARY KEY, "
                    + CATEGORY_COLUMN_NAME + " TEXT, "
                    + CATEGORY_COLUMN_DATE_CREATED + " INTEGER, "
                    + EXERCISE_COLUMN_NEW + " INTEGER DEFAULT 1, "
                    + EXERCISE_COLUMN_MODIFIED + " INTEGER DEFAULT 0, "
                    + EXERCISE_COLUMN_DELETED + " INTEGER DEFAULT 0"
                    + ")";

    private static final String LOG_TAG = "LiftLog";

    public DataAccessObject(Context context) {
        super(context, DB_NAME, null, 32);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        try
        {
            db.execSQL(LIFT_TABLE_CREATE_QUERY);
            db.execSQL(SESSION_TABLE_CREATE_QUERY);
            db.execSQL(EXERCISE_TABLE_CREATE_QUERY);
            db.execSQL(CATEGORY_TABLE_CREATE_QUERY);
            insert(db, Exercise.Squat());
            insert(db, Exercise.BenchPress());
            insert(db, Exercise.Deadlift());
            insert(db, Exercise.Press());
        }
        catch (SQLException e)
        {
            Log.d(LOG_TAG, "Error creating tables: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        List<Session> sessions = null;
        List<Lift> lifts = null;
        Map<Long, Exercise> exercises = null;
        List<Category> categories = null;

        try {
            sessions = selectSessions(db, true);
            lifts = selectLifts(db, true);
            exercises = selectExerciseMap(db, true);
            categories = selectCategories(db, true);


            db.execSQL("DROP TABLE IF EXISTS " + LIFT_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + SESSION_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + EXERCISE_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + CATEGORY_TABLE_NAME);

            onCreate(db);
            //clear exercises table because onCreate adds some default ones
            clearExerciseTable(db);

            if (sessions != null)
            {
                for (Session session : sessions)
                {
                    long ret = insert(db, session);
                    System.out.println(ret);
                }
            }
            if (lifts != null)
            {
                for (Lift lift : lifts)
                {
                    long ret = insert(db, lift);
                    System.out.println(ret);
                }
            }
            if (exercises != null && exercises.size() > 0)
            {
                for (Exercise exercise : exercises.values())
                {
                    long ret = insert(db, exercise);
                    System.out.println(ret);
                }
            }
            if(categories != null)
            {
                for(Category category : categories)
                {
                    insert(db, category);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d(LOG_TAG, "exception in onUpgrade: " + ex.getMessage());
        }
    }


    //BEGIN LIFT METHODS

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
        values.put(LIFT_COLUMN_RPE, lift.getRPE());
        values.put(LIFT_COLUMN_UNIT, lift.getUnit() == null ? "lb" : lift.getUnit().toString().toUpperCase());
        values.put(LIFT_COLUMN_NEW, lift.isNew() ? 1 : 0);
        values.put(LIFT_COLUMN_MODIFIED, lift.isModified() ? 1 : 0);
        values.put(LIFT_COLUMN_DELETED, lift.isDeleted() ? 1 : 0);
        return db.insert(LIFT_TABLE_NAME, null, values);
    }
    public boolean update(Lift lift)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LIFT_COLUMN_WEIGHT, lift.getWeight());
        values.put(LIFT_COLUMN_REPS, lift.getReps());
        values.put(LIFT_COLUMN_SETS, lift.getSets());
        values.put(LIFT_COLUMN_RPE, lift.getRPE());
        values.put(LIFT_COLUMN_EXERCISE_FK, lift.getExerciseId());
        values.put(LIFT_COLUMN_SESSION_FK, lift.getSessionId());
        values.put(LIFT_COLUMN_NEW, lift.isNew() ? 1 : 0);
        values.put(LIFT_COLUMN_MODIFIED, lift.isModified() ? 1 : 0);
        values.put(LIFT_COLUMN_DELETED, lift.isDeleted() ? 1 : 0);
        try
        {
            db.update(LIFT_TABLE_NAME, values, LIFT_COLUMN_PK + " = " + lift.getId(), null);
        } catch (SQLException e)
        {
            Log.d(LOG_TAG, "exception in update(Lift): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public List<Lift> selectLifts(boolean includeDeleted)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        return selectLifts(db, includeDeleted);
    }
    public List<Lift> selectLifts(SQLiteDatabase db, boolean includeDeleted)
    {
        ArrayList<Lift> result = new ArrayList<Lift>();

        String qry = "SELECT * FROM " + LIFT_TABLE_NAME;
        if(!includeDeleted)
        {
            qry += " WHERE " + LIFT_COLUMN_DELETED + " != 1";
        }

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
            double weight = cursor.getDouble(cursor.getColumnIndex(LIFT_COLUMN_WEIGHT));
            int sets = cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_SETS));
            int reps = cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_REPS));
            
            //need to verify the RPE column exists first, as this field was added to the database as an update
            //the user may be in the process of upgrading and migrating the database
            int rpeColIndex = cursor.getColumnIndex(LIFT_COLUMN_RPE);
            double rpe = 0;
            if(rpeColIndex > -1)
            {
                rpe = cursor.getDouble(rpeColIndex);   
            }
            
            int warmup = cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_WARMUP));
            long dateCreated = cursor.getLong(cursor.getColumnIndex(LIFT_COLUMN_DATE_CREATED));
            boolean isNew = (cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_NEW)) == 1);
            boolean isModified = (cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_MODIFIED)) == 1);
            boolean isDeleted = (cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_DELETED)) == 1);

            Lift lift = new Lift();
            lift.setId(id);
            lift.setExerciseId(exerciseId);
            lift.setSessionId(sessionId);
            lift.setWeight(weight);
            lift.setSets(sets);
            lift.setReps(reps);
            lift.setRPE(rpe);
            lift.setWarmup(warmup == 1);
            lift.setDateCreated(dateCreated);
            lift.setNew(isNew);
            lift.setModified(isModified);
            lift.setDeleted(isDeleted);
            result.add(lift);

            hasNext = cursor.moveToNext();
        }

        cursor.close();

        return result;
    }
//    public List<Lift> selectLiftsByExercise(long exerciseId)
//    {
//        SQLiteDatabase db = this.getReadableDatabase();
//        return selectLiftsByExercise(db, exerciseId);
//    }
//    public List<Lift> selectLiftsByExercise(SQLiteDatabase db, long exerciseId)
//    {
//        List<Lift> result = null;
//        String qry = "SELECT * FROM " + LIFT_TABLE_NAME
//                + " WHERE " + LIFT_COLUMN_EXERCISE_FK + " = " + exerciseId
//                + " AND " + LIFT_COLUMN_DELETED + " != 1";
//        Cursor cursor = db.rawQuery(qry, null);
//        if (cursor == null)
//        {
//            return null;
//        }
//
//        boolean hasNext = cursor.moveToFirst();
//        while(hasNext)
//        {
//            long id = cursor.getLong(cursor.getColumnIndex(LIFT_COLUMN_PK));
//            long sessionId = cursor.getLong(cursor.getColumnIndex(LIFT_COLUMN_SESSION_FK));
//            double weight = cursor.getDouble(cursor.getColumnIndex(LIFT_COLUMN_WEIGHT));
//            int sets = cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_SETS));
//            int reps = cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_REPS));
//            int warmup = cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_WARMUP));
//            long dateCreated = cursor.getLong(cursor.getColumnIndex(LIFT_COLUMN_DATE_CREATED));
//            boolean isNew = (cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_NEW)) == 1);
//            boolean isModified = (cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_MODIFIED)) == 1);
//            boolean isDeleted = (cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_DELETED)) == 1);
//
//            Lift lift = new Lift();
//            lift.setId(id);
//            lift.setExerciseId(exerciseId);
//            lift.setSessionId(sessionId);
//            lift.setWeight(weight);
//            lift.setSets(sets);
//            lift.setReps(reps);
//            lift.setWarmup(warmup == 1 ? true : false);
//            lift.setDateCreated(dateCreated);
//            lift.setNew(isNew);
//            lift.setModified(isModified);
//            lift.setDeleted(isDeleted);
//            result.add(lift);
//
//            hasNext = cursor.moveToNext();
//        }
//
//        cursor.close();
//        return result;
//    }

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
        double weight = cursor.getDouble(cursor.getColumnIndex(LIFT_COLUMN_WEIGHT));
        int sets = cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_SETS));
        int reps = cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_REPS));
            
        //need to verify the RPE column exists first, as this field was added to the database as an update
        //the user may be in the process of upgrading and migrating the database
        int rpeColIndex = cursor.getColumnIndex(LIFT_COLUMN_RPE);
        double rpe = 0;
        if(rpeColIndex > -1)
        {
            rpe = cursor.getDouble(rpeColIndex);   
        }
            
        int warmup = cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_WARMUP));
        long dateCreated = cursor.getLong(cursor.getColumnIndex(LIFT_COLUMN_DATE_CREATED));
        boolean isNew = (cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_NEW)) == 1);
        boolean isModified = (cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_MODIFIED)) == 1);
        boolean isDeleted = (cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_DELETED)) == 1);

        Lift result = new Lift();
        result.setId(id);
        result.setExerciseId(exerciseId);
        result.setSessionId(sessionId);
        result.setWeight(weight);
        result.setSets(sets);
        result.setReps(reps);
        result.setRPE(rpe);
        result.setWarmup(warmup == 1 ? true : false);
        result.setDateCreated(dateCreated);
        result.setNew(isNew);
        result.setModified(isModified);
        result.setDeleted(isDeleted);

        return result;
    }

    public Lift selectBestLift(long exerciseId, int reps)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        return selectBestLift(db, exerciseId, reps, -1);
    }
    public Lift selectBestLift(long exerciseId, int reps, int sets)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        return selectBestLift(db, exerciseId, reps, sets);
    }

    /**
     *
     * @param db The SQLiteDatabase object to read from
     * @param exerciseId The ID of the exercise
     * @param reps The number of reps
     * @param sets The number of sets (optional, a value <= 0 signifies any
     * @return The Lift with the highest weight that matches the requested reps, and sets (if specified).
     *  Returns null if no Lift matches.
     */
    private Lift selectBestLift(SQLiteDatabase db, long exerciseId, int reps, int setsArg)
    {
        String qry = "SELECT * FROM " + LIFT_TABLE_NAME
                + " WHERE " + LIFT_COLUMN_EXERCISE_FK + " = " + exerciseId
                + " AND " + LIFT_COLUMN_REPS + " = " + reps;
        if(setsArg > 0)
        {
            qry += " AND " + LIFT_COLUMN_SETS + " = " + setsArg;
        }
        qry += " ORDER BY " + LIFT_COLUMN_WEIGHT + " DESC LIMIT 1";

        Cursor cursor = db.rawQuery(qry, null);
        if(cursor == null || cursor.getCount() == 0)
        {
            return null;
        }
        cursor.moveToFirst();

        long id = cursor.getLong(cursor.getColumnIndex(LIFT_COLUMN_PK));
        long sessionId = cursor.getLong(cursor.getColumnIndex(LIFT_COLUMN_SESSION_FK));
        double weight = cursor.getDouble(cursor.getColumnIndex(LIFT_COLUMN_WEIGHT));
        int sets = cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_SETS));
            
        //need to verify the RPE column exists first, as this field was added to the database as an update
        //the user may be in the process of upgrading and migrating the database
        int rpeColIndex = cursor.getColumnIndex(LIFT_COLUMN_RPE);
        double rpe = 0;
        if(rpeColIndex > -1)
        {
            rpe = cursor.getDouble(rpeColIndex);   
        }
            
        int warmup = cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_WARMUP));
        long dateCreated = cursor.getLong(cursor.getColumnIndex(LIFT_COLUMN_DATE_CREATED));
        boolean isNew = (cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_NEW)) == 1);
        boolean isModified = (cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_MODIFIED)) == 1);
        boolean isDeleted = (cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_DELETED)) == 1);

        Lift result = new Lift();
        result.setId(id);
        result.setExerciseId(exerciseId);
        result.setSessionId(sessionId);
        result.setWeight(weight);
        result.setSets(sets);
        result.setReps(reps);
        result.setRPE(rpe);
        result.setWarmup(warmup == 1 ? true : false);
        result.setDateCreated(dateCreated);
        result.setNew(isNew);
        result.setModified(isModified);
        result.setDeleted(isDeleted);

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
            Log.d(LOG_TAG, "Error removing lift id=" + id + ":" + e.getMessage());
            return false;
        }
        return true;
    }

    //END LIFT METHODS


    //BEGIN CATEGORY METHODS
    public Map<Category, List<Exercise>> selectCategoryMap(boolean includeDeleted)
    {
        return selectCategoryMap(this.getReadableDatabase(), includeDeleted);
    }
    public Map<Category, List<Exercise>> selectCategoryMap(SQLiteDatabase db, boolean includeDeleted)
    {
        String qryCategory = "SELECT * FROM " + CATEGORY_TABLE_NAME;
        if(!includeDeleted)
        {
            qryCategory += " WHERE " + CATEGORY_COLUMN_DELETED + " != 1";
        }

        Map<Category, List<Exercise>> result = new HashMap<Category, List<Exercise>>();

        Cursor cursorCategory = null;
        try
        {
            cursorCategory = db.rawQuery(qryCategory, null);
        } catch (Exception e)
        {
            Log.d(LOG_TAG, "Error selecting all categories: " + e.getMessage());
            return null;
        }

        if(cursorCategory != null)
        {
            boolean hasNext = cursorCategory.moveToFirst();
            while(hasNext)
            {
                long id = cursorCategory.getLong(cursorCategory.getColumnIndex(CATEGORY_COLUMN_PK));
                String name = cursorCategory.getString(cursorCategory.getColumnIndex(CATEGORY_COLUMN_NAME));
                int isNew = cursorCategory.getInt(cursorCategory.getColumnIndex(CATEGORY_COLUMN_NAME));
                int isModified = cursorCategory.getInt(cursorCategory.getColumnIndex(CATEGORY_COLUMN_MODIFIED));
                int isDeleted = cursorCategory.getInt(cursorCategory.getColumnIndex(CATEGORY_COLUMN_DELETED));

                Category category = new Category();
                category.setId(id);
                category.setName(name);
                category.setNew(isNew == 1);
                category.setModified(isModified == 1);
                category.setDeleted(isDeleted == 1);
                result.put(category, new ArrayList<Exercise>());

                hasNext = cursorCategory.moveToNext();
            }
        }
        Category dummyCategory = new Category();
        dummyCategory.setName("Uncategorized");
        dummyCategory.setId(-1l);
        result.put(dummyCategory, new ArrayList<Exercise>());

        String qryExercises = "SELECT * FROM " + EXERCISE_TABLE_NAME;
        if(!includeDeleted)
        {
            qryExercises += " WHERE " + EXERCISE_COLUMN_DELETED + " != 1";
        }

        Cursor cursorExercises = db.rawQuery(qryExercises, null);
        if(cursorExercises != null)
        {
            boolean hasNext = cursorExercises.moveToFirst();
            while(hasNext)
            {
                long id = cursorExercises.getLong(cursorExercises.getColumnIndex(EXERCISE_COLUMN_PK));
                long categoryId = cursorExercises.getLong(cursorExercises.getColumnIndex(EXERCISE_COLUMN_CATEGORY_FK));
                String name = cursorExercises.getString(cursorExercises.getColumnIndex(EXERCISE_COLUMN_NAME));
                String desc = cursorExercises.getString(cursorExercises.getColumnIndex(EXERCISE_COLUMN_DESCRIPTION));
                int valid = cursorExercises.getInt(cursorExercises.getColumnIndex(EXERCISE_COLUMN_VALID));
                boolean isNew = (cursorExercises.getInt(cursorExercises.getColumnIndex(EXERCISE_COLUMN_NEW)) == 1);
                boolean isModified = (cursorExercises.getInt(cursorExercises.getColumnIndex(EXERCISE_COLUMN_MODIFIED)) == 1);
                boolean isDeleted = (cursorExercises.getInt(cursorExercises.getColumnIndex(EXERCISE_COLUMN_DELETED)) == 1);

                Exercise exercise = new Exercise();
                exercise.setId(id);
                exercise.setCategoryId(categoryId);
                exercise.setName(name);
                exercise.setValid(valid == 1);
                exercise.setDescription(desc);
                exercise.setNew(isNew);
                exercise.setModified(isModified);
                exercise.setDeleted(isDeleted);

                boolean hasCategory = false;
                for(Category categoryKey : result.keySet())
                {
                    if(categoryKey.getId() == categoryId)
                    {
                        List<Exercise> exercises = result.get(categoryKey);
                        if(exercise == null) exercises = new ArrayList<Exercise>();
                        exercises.add(exercise);
                        result.put(categoryKey, exercises);
                        hasCategory = true;
                    }
                }
                //no matching category found, put in "Uncategorized"
                if(!hasCategory)
                {
                    result.get(dummyCategory).add(exercise);
                }

                hasNext = cursorExercises.moveToNext();
            }
        }
        return result;
    }
    public long insert(Category category)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return insert(db, category);
    }
    public long insert(SQLiteDatabase db, Category category)
    {
        ContentValues values = new ContentValues();
        values.put(CATEGORY_COLUMN_NAME, category.getName());
        values.put(CATEGORY_COLUMN_NEW, category.isNew() ? 1 : 0);
        values.put(CATEGORY_COLUMN_MODIFIED, category.isModified() ? 1 : 0);
        values.put(CATEGORY_COLUMN_DELETED, category.isDeleted() ? 1 : 0);
        return db.insert(CATEGORY_TABLE_NAME, null, values);
    }
    public boolean update(Category category)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CATEGORY_COLUMN_NAME, category.getName());
        values.put(CATEGORY_COLUMN_NEW, category.isNew() ? 1 : 0);
        values.put(CATEGORY_COLUMN_MODIFIED, category.isModified() ? 1 : 0);
        values.put(CATEGORY_COLUMN_DELETED, category.isDeleted() ? 1 : 0);
        try
        {
            int rVal = db.update(CATEGORY_TABLE_NAME, values, CATEGORY_COLUMN_PK + " = " + category.getId(), null);
            return rVal > 0;
        } catch (SQLException e)
        {
            Log.d(LOG_TAG, "exception in update(Category): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public List<Category> selectCategories(boolean includeDeleted)
    {
        return selectCategories(this.getReadableDatabase(), includeDeleted);
    }
    public List<Category> selectCategories(SQLiteDatabase db, boolean includeDeleted)
    {
        try
        {
            ArrayList<Category> result = new ArrayList<Category>();

            String qry = "SELECT * FROM " + CATEGORY_TABLE_NAME;
            if(!includeDeleted)
            {
                qry += " WHERE " + CATEGORY_COLUMN_DELETED + " != 1";
            }

            Cursor cursor = db.rawQuery(qry, null);
            if (cursor == null || cursor.getCount() == 0)
            {
                return result;
            }

            boolean hasNext = cursor.moveToFirst();
            while (hasNext)
            {

                Category category = new Category();
                long id = cursor.getLong(cursor.getColumnIndex(CATEGORY_COLUMN_PK));
                String name = cursor.getString(cursor.getColumnIndex(CATEGORY_COLUMN_NAME));
                int isNew = cursor.getInt(cursor.getColumnIndex(CATEGORY_COLUMN_NAME));
                int isModified = cursor.getInt(cursor.getColumnIndex(CATEGORY_COLUMN_MODIFIED));
                int isDeleted = cursor.getInt(cursor.getColumnIndex(CATEGORY_COLUMN_DELETED));

                category.setId(id);
                category.setName(name);
                category.setNew(isNew == 1);
                category.setModified(isModified == 1);
                category.setDeleted(isDeleted == 1);
                result.add(category);

                hasNext = cursor.moveToNext();
            }

            cursor.close();

            return result;
        } catch (SQLiteException e)
        {
            Log.d(LOG_TAG, "Erorr selecting all categores: " + e.getMessage());
            return null;
        }
    }
    public Category selectCategory(long id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String qry = "SELECT * FROM " + CATEGORY_TABLE_NAME +
                " WHERE " + CATEGORY_COLUMN_PK + " = " + id;
        Cursor cursor = db.rawQuery(qry, null);
        if (cursor == null)
        {
            return null;
        }

        cursor.moveToFirst();

        String name = cursor.getString(cursor.getColumnIndex(CATEGORY_COLUMN_NAME));
        int isNew = cursor.getInt(cursor.getColumnIndex(CATEGORY_COLUMN_NAME));
        int isModified = cursor.getInt(cursor.getColumnIndex(CATEGORY_COLUMN_MODIFIED));
        int isDeleted = cursor.getInt(cursor.getColumnIndex(CATEGORY_COLUMN_DELETED));

        Category result = new Category();
        result.setId(id);
        result.setName(name);
        result.setNew(isNew == 1);
        result.setModified(isModified == 1);
        result.setDeleted(isDeleted == 1);

        return result;
    }
    public boolean categoryExists(String name)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        return categoryExists(db, name);
    }
    public boolean categoryExists(SQLiteDatabase db, String name)
    {
        long num = DatabaseUtils.queryNumEntries(db, CATEGORY_TABLE_NAME, "name=?", new String[]{name});
        return num > 0;
    }
    public boolean deleteCategory(long id)
    {
        String qry = "DELETE FROM " + CATEGORY_TABLE_NAME +
                " WHERE " + CATEGORY_COLUMN_PK + " = " + id;

        ContentValues values = new ContentValues();
        values.put(EXERCISE_COLUMN_CATEGORY_FK, 0l);
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            //delete the category
            db.execSQL(qry);
            //update exercises whose category_id was referencing this category
            int rVal = db.update(EXERCISE_TABLE_NAME, values, EXERCISE_COLUMN_CATEGORY_FK + " = " + id, null);
            return rVal > 0;
        } catch (SQLException e)
        {
            e.printStackTrace();
            Log.d(LOG_TAG, "Error removing category id=" + id + ":" + e.getMessage());
            return false;
        }
    }
    public void clearCategoryTable()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String qry = "DELETE FROM " + CATEGORY_TABLE_NAME;
        db.execSQL(qry);
    }

    //END CATEGORY METHODS


    //BEGIN EXERCISE METHODS

    public long insert(Exercise exercise)
    {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            return insert(db, exercise);
        } catch (Exception e)
        {
            e.printStackTrace();
            return -1;
        }
    }
    public long insert(SQLiteDatabase db, Exercise exercise)
    {
        ContentValues values = new ContentValues();
        if(exercise.getId() > 0)
        {
            values.put(EXERCISE_COLUMN_PK, exercise.getId());
        }
        values.put(EXERCISE_COLUMN_CATEGORY_FK, exercise.getCategoryId());
        values.put(EXERCISE_COLUMN_NAME, exercise.getName());
        values.put(EXERCISE_COLUMN_DESCRIPTION, exercise.getDescription());
        values.put(EXERCISE_COLUMN_VALID, exercise.isValid() ? 1 : 0);
        values.put(EXERCISE_COLUMN_DATE_CREATED, System.currentTimeMillis());
        values.put(EXERCISE_COLUMN_NEW, exercise.isNew() ? 1 : 0);
        values.put(EXERCISE_COLUMN_MODIFIED, exercise.isModified() ? 1 : 0);
        values.put(EXERCISE_COLUMN_DELETED, exercise.isDeleted() ? 1 : 0);
        long id = db.insert(EXERCISE_TABLE_NAME, null, values);
        return id;
    }
    //    public long insert(SQLiteDatabase db, ExerciseAPI exercise)
//    {
//        ContentValues values = new ContentValues();
//        values.put(EXERCISE_COLUMN_NAME, exercise.getName());
//        values.put(EXERCISE_COLUMN_DESCRIPTION, exercise.getDescription());
//        values.put(EXERCISE_COLUMN_DATE_CREATED, System.currentTimeMillis());
//        long id = db.insert(EXERCISE_TABLE_NAME, null, values);
//        return id;
//    }
    public boolean update(Exercise exercise)
    {
        if (exercise == null || exercise.getId() == -1) return false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EXERCISE_COLUMN_CATEGORY_FK, exercise.getCategoryId());
        values.put(EXERCISE_COLUMN_NAME, exercise.getName());
        values.put(EXERCISE_COLUMN_DESCRIPTION, exercise.getDescription());
        values.put(EXERCISE_COLUMN_VALID, exercise.isValid() ? 1 : 0);
        values.put(EXERCISE_COLUMN_DATE_CREATED, System.currentTimeMillis());
        values.put(EXERCISE_COLUMN_NEW, exercise.isNew() ? 1 : 0);
        values.put(EXERCISE_COLUMN_MODIFIED, exercise.isModified() ? 1 : 0);
        values.put(EXERCISE_COLUMN_DELETED, exercise.isDeleted() ? 1 : 0);
        try
        {
            db.update(EXERCISE_TABLE_NAME, values, EXERCISE_COLUMN_PK + " = " + exercise.getId(), null);
//            data.execSQL(qry);
        } catch (SQLException e)
        {
            Log.d(LOG_TAG, "exception in update(Exercise exercise): " + e.getMessage());
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

        long categoryId = cursor.getLong(cursor.getColumnIndex(EXERCISE_COLUMN_CATEGORY_FK));
        String name = cursor.getString(cursor.getColumnIndex(EXERCISE_COLUMN_NAME));
        String desc = cursor.getString(cursor.getColumnIndex(EXERCISE_COLUMN_DESCRIPTION));
        int valid = cursor.getInt(cursor.getColumnIndex(EXERCISE_COLUMN_VALID));
        boolean isNew = (cursor.getInt(cursor.getColumnIndex(EXERCISE_COLUMN_NEW)) == 1);
        boolean isModified = (cursor.getInt(cursor.getColumnIndex(EXERCISE_COLUMN_MODIFIED)) == 1);
        boolean isDeleted = (cursor.getInt(cursor.getColumnIndex(EXERCISE_COLUMN_DELETED)) == 1);

        result = new Exercise();
        result.setId(id);
        result.setCategoryId(categoryId);
        result.setName(name);
        result.setValid(valid == 1);
        result.setDescription(desc);
        result.setNew(isNew);
        result.setModified(isModified);
        result.setDeleted(isDeleted);

        cursor.close();

        return result;
    }
    public List<Exercise> selectExercises(boolean includeDeleted)
    {
        Map<Long, Exercise> exerciseMap = selectExerciseMap(includeDeleted);
        return new ArrayList<>(exerciseMap.values());
    }
    public List<Exercise> selectExercises(SQLiteDatabase db, boolean includeDeleted)
    {
        Map<Long, Exercise> exerciseMap = selectExerciseMap(db, includeDeleted);
        return new ArrayList<>(exerciseMap.values());
    }
    public Map<Long, Exercise> selectExerciseMap(boolean includeDeleted)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        return selectExerciseMap(db, includeDeleted);
    }
    public Map<Long, Exercise> selectExerciseMap(SQLiteDatabase db, boolean includeDeleted)
    {
//        ArrayList<Exercise> result = new ArrayList<Exercise>();
        HashMap<Long, Exercise> result = new HashMap<>();
        String qry = "SELECT * FROM " + EXERCISE_TABLE_NAME;
        if(!includeDeleted)
        {
            qry += " WHERE " + EXERCISE_COLUMN_DELETED + " != 1";
        }
        Cursor cursor = db.rawQuery(qry, null);

        if (cursor == null)
        {
            return null;
        }

        boolean hasNext = cursor.moveToFirst();
        while (hasNext)
        {

            long id = cursor.getLong(cursor.getColumnIndex(EXERCISE_COLUMN_PK));
            long categoryId = cursor.getLong(cursor.getColumnIndex(EXERCISE_COLUMN_CATEGORY_FK));
            String name = cursor.getString(cursor.getColumnIndex(EXERCISE_COLUMN_NAME));
            String desc = cursor.getString(cursor.getColumnIndex(EXERCISE_COLUMN_DESCRIPTION));
            int valid = cursor.getInt(cursor.getColumnIndex(EXERCISE_COLUMN_VALID));
            boolean isNew = (cursor.getInt(cursor.getColumnIndex(EXERCISE_COLUMN_NEW)) == 1);
            boolean isModified = (cursor.getInt(cursor.getColumnIndex(EXERCISE_COLUMN_MODIFIED)) == 1);
            boolean isDeleted = (cursor.getInt(cursor.getColumnIndex(EXERCISE_COLUMN_DELETED)) == 1);

            Exercise exercise = new Exercise();
            exercise.setId(id);
            exercise.setCategoryId(categoryId);
            exercise.setName(name);
            exercise.setValid(valid == 1 ? true : false);
            exercise.setDescription(desc);
            exercise.setNew(isNew);
            exercise.setModified(isModified);
            exercise.setDeleted(isDeleted);

            result.put(id, exercise);

            hasNext = cursor.moveToNext();
        }

        return result;
    }

    public long exerciseCount()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        return exerciseCount(db);
    }
    public long exerciseCount(SQLiteDatabase db)
    {
        return DatabaseUtils.queryNumEntries(db, EXERCISE_TABLE_NAME);
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

    /**
     * Clear all of the Exercises in the database
     */
    public void clearExerciseTable()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        clearExerciseTable(db);
    }

    /**
     * {@link #clearExerciseTable()
     */
    public void clearExerciseTable(SQLiteDatabase db)
    {
//        SQLiteDatabase db = this.getWritableDatabase();
        String qry = "DELETE FROM " + EXERCISE_TABLE_NAME;
        db.execSQL(qry);
    }

    //END EXERCISE METHODS




    //BEGIN SESSION METHODS

    public boolean update(Session session)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SESSION_COLUMN_DATE, session.getDate());
        String note = session.getNote();
        values.put(SESSION_COLUMN_NOTE, note == null ? "" : note);
        values.put(SESSION_COLUMN_NEW, session.isNew() ? 1 : 0);
        values.put(SESSION_COLUMN_MODIFIED, session.isModified() ? 1 : 0);
        values.put(SESSION_COLUMN_DELETED, session.isDeleted() ? 1 : 0);
        int ret = -1;
        try
        {
            ret = db.update(SESSION_TABLE_NAME, values, SESSION_COLUMN_PK + " = " + session.getId(), null);
        } catch (SQLException e)
        {
            Log.d(LOG_TAG, "exception in update(Session): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        return ret > 0;
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
        values.put(SESSION_COLUMN_NEW, session.isNew() ? 1 : 0);
        values.put(SESSION_COLUMN_MODIFIED, session.isModified() ? 1 : 0);
        values.put(SESSION_COLUMN_DELETED, session.isDeleted() ? 1 : 0);
        values.put(SESSION_COLUMN_NOTE, session.getNote() == null ? "" : session.getNote());
        long id = db.insert(SESSION_TABLE_NAME, null, values);
        dataChanged();return id;
    }
    public List<Session> selectSessions(boolean includeDeleted)
    {
        try
        {
            SQLiteDatabase db = this.getReadableDatabase();
            return selectSessions(db, includeDeleted);
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
    public List<Session> selectSessions(SQLiteDatabase db, boolean includeDeleted)
    {
        return selectSessions(db, 0, Integer.MAX_VALUE, includeDeleted);
    }



    /**
     *
     * @param startDate
     * @param endDate
     * @param includeDeleted
     * @return
     */
    public List<Session> selectSessions(int startDate, int endDate, boolean includeDeleted)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        return selectSessions(db, startDate, endDate, includeDeleted);
    }

    /**
     *
     * {@link #selectSessions(int, int, boolean)
     */
    public List<Session> selectSessions(SQLiteDatabase db, int startDate, int endDate, boolean includeDeleted)
    {
        ArrayList<Session> result = null;
        Cursor cursor = null;
        try
        {
            result = new ArrayList<Session>();
            String qry = "SELECT * FROM " + SESSION_TABLE_NAME;
            if(!includeDeleted)
            {
                qry += " WHERE " + SESSION_COLUMN_DELETED + " != 1";
            }
            cursor = db.rawQuery(qry, null);

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        if (cursor == null || cursor.getCount() == 0)
        {
            return result;
        }

        cursor.moveToFirst();
        boolean hasNext = true;
        while (hasNext)
        {

            long id = cursor.getInt(cursor.getColumnIndex(SESSION_COLUMN_PK));
            long date = cursor.getLong(cursor.getColumnIndex(SESSION_COLUMN_DATE));
            int noteColIndex = cursor.getColumnIndex(SESSION_COLUMN_NOTE);
            String note = "";
            if(noteColIndex > -1)
            {
                note = cursor.getString(noteColIndex);
            }
            boolean isNew = (cursor.getInt(cursor.getColumnIndex(SESSION_COLUMN_NEW)) == 1);
            boolean isModified = (cursor.getInt(cursor.getColumnIndex(SESSION_COLUMN_MODIFIED)) == 1);
            boolean isDeleted = (cursor.getInt(cursor.getColumnIndex(SESSION_COLUMN_DELETED)) == 1);

            Session session = new Session();
            session.setId(id);
            session.setDate(date);
            session.setNote(note);
            session.setNew(isNew);
            session.setModified(isModified);
            session.setDeleted(isDeleted);

            result.add(session);

            hasNext = cursor.moveToNext();
        }

        cursor.close();

        return result;
    }

    /**
     * Select all Sessions which contain the specified Exercise
     * @param exerciseId The ID of the Exercise to select the Sessions by
     * @return A list of Sessions
     */
    public List<Session> selectSessionsByExercise(long exerciseId)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        return selectSessionsByExercise(db, exerciseId);
    }
    public List<Session> selectSessionsByExercise(SQLiteDatabase db, long exerciseId)
    {
        String qry = "SELECT " +
                "b." + LIFT_COLUMN_PK + ", " +
                "b." + LIFT_COLUMN_SESSION_FK + ", " +
                "b." + LIFT_COLUMN_EXERCISE_FK + ", " +
                "b." + LIFT_COLUMN_WEIGHT + ", " +
                "b." + LIFT_COLUMN_REPS + ", " +
                "b." + LIFT_COLUMN_SETS + ", " +
                "b." + LIFT_COLUMN_RPE + ", " +
                "b." + LIFT_COLUMN_DATE_CREATED + ", " +
                "b." + LIFT_COLUMN_WARMUP + ", " +
                "a." + SESSION_COLUMN_NOTE + ", " +
                "a." + SESSION_COLUMN_DATE  +
                " FROM " + SESSION_TABLE_NAME + " as a," + LIFT_TABLE_NAME + " as b" +
                " WHERE a." + SESSION_COLUMN_PK + " = b." + LIFT_COLUMN_SESSION_FK +
                " AND b." + LIFT_COLUMN_EXERCISE_FK + " = " + exerciseId;


        Cursor cursor = null;
        try
        {
            cursor = db.rawQuery(qry, null);
        } catch (SQLiteException e)
        {
            e.printStackTrace();
            return null;
        }
        if(cursor == null)
        {
            return null;
        }

        //use a map to avoid re-creating Session objects because the returned dataset is a table of lifts with sessionId
        Map<Long, Session> sessions = new HashMap<Long, Session>();
        boolean hasNext = cursor.moveToFirst();
        while(hasNext)
        {
            long sessionId = cursor.getLong(cursor.getColumnIndex(LIFT_COLUMN_SESSION_FK));
            long date = cursor.getLong(cursor.getColumnIndex(SESSION_COLUMN_DATE));
            String note = cursor.getString(cursor.getColumnIndex(SESSION_COLUMN_NOTE));
            Session session = sessions.get(sessionId);
            if(session == null)
            {
                session = new Session();
                session.setId(sessionId);
                session.setDate(date);
                session.setNote(note);
            }


            long liftId = cursor.getLong(cursor.getColumnIndex(LIFT_COLUMN_PK));
            double weight = cursor.getDouble(cursor.getColumnIndex(LIFT_COLUMN_WEIGHT));
            int reps = cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_REPS));
            int sets = cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_SETS));
            
            //need to verify the RPE column exists first, as this field was added to the database as an update
            //the user may be in the process of upgrading and migrating the database
            int rpeColIndex = cursor.getColumnIndex(LIFT_COLUMN_RPE);
            double rpe = 0;
            if(rpeColIndex > -1)
            {
                rpe = cursor.getDouble(rpeColIndex);   
            }
            
            int warmup = cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_WARMUP));
            long dateCreated = cursor.getLong(cursor.getColumnIndex(LIFT_COLUMN_DATE_CREATED));


            Lift lift = new Lift();
            lift.setId(liftId);
            lift.setExerciseId(exerciseId);
            lift.setSessionId(sessionId);
            lift.setWeight(weight);
            lift.setReps(reps);
            lift.setSets(sets);
            lift.setRPE(rpe);
            lift.setWarmup(warmup == 1);
            lift.setDateCreated(dateCreated);

            session.getLifts().add(lift);

            sessions.put(sessionId, session);

            hasNext = cursor.moveToNext();
        }

        cursor.close();
        return new ArrayList<>(sessions.values());
    }

    /**
     * Select the Session with the associated id, along with the associated Lifts
     * @param id The id of the session
     * @return A populated Session object
     */
    public Session selectSession(long id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String qrySession = "SELECT * FROM " + SESSION_TABLE_NAME
                + " WHERE " + SESSION_COLUMN_PK + " = " + id;
        Cursor cursorSession = db.rawQuery(qrySession, null);
        if (cursorSession == null)
        {
            return null;
        }

        cursorSession.moveToFirst();


        long date = cursorSession.getLong(cursorSession.getColumnIndex(SESSION_COLUMN_DATE));
        int noteColIndex = cursorSession.getColumnIndex(SESSION_COLUMN_NOTE);
        String note = "";
        if(noteColIndex > -1)
        {
            note = cursorSession.getString(noteColIndex);
        }
        boolean isSessionNew = (cursorSession.getInt(cursorSession.getColumnIndex(SESSION_COLUMN_NEW)) == 1);
        boolean isSessionModified = (cursorSession.getInt(cursorSession.getColumnIndex(SESSION_COLUMN_MODIFIED)) == 1);
        boolean isSessionDeleted = (cursorSession.getInt(cursorSession.getColumnIndex(SESSION_COLUMN_DELETED)) == 1);

        Session result = new Session();
        result.setId(id);
        result.setDate(date);
        result.setNote(note);
        result.setNew(isSessionNew);
        result.setModified(isSessionModified);
        result.setDeleted(isSessionDeleted);


        String qryLifts = "SELECT " +
                "b." + LIFT_COLUMN_PK + ", " +
                "b." + LIFT_COLUMN_SESSION_FK + ", " +
                "b." + LIFT_COLUMN_EXERCISE_FK + ", " +
                "b." + LIFT_COLUMN_WEIGHT + ", " +
                "b." + LIFT_COLUMN_REPS + ", " +
                "b." + LIFT_COLUMN_SETS + ", " +
                "b." + LIFT_COLUMN_RPE + ", " +
                "b." + LIFT_COLUMN_DATE_CREATED + ", " +
                "b." + LIFT_COLUMN_WARMUP + ", " +
                "b." + LIFT_COLUMN_NEW + ", " +
                "b." + LIFT_COLUMN_MODIFIED + ", " +
                "b." + LIFT_COLUMN_DELETED + ", " +
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
            boolean isDeleted = (cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_DELETED)) == 1);
//            long date = cursor.getLong(cursor.getColumnIndex(SESSION_COLUMN_DATE));


            long liftId = cursor.getLong(cursor.getColumnIndex(LIFT_COLUMN_PK));
            long exerciseId = cursor.getLong(cursor.getColumnIndex(LIFT_COLUMN_EXERCISE_FK));
            long sessionId = cursor.getLong(cursor.getColumnIndex(LIFT_COLUMN_SESSION_FK));
            double weight = cursor.getDouble(cursor.getColumnIndex(LIFT_COLUMN_WEIGHT));
            int reps = cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_REPS));
            int sets = cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_SETS));
            
            //need to verify the RPE column exists first, as this field was added to the database as an update
            //the user may be in the process of upgrading and migrating the database
            int rpeColIndex = cursor.getColumnIndex(LIFT_COLUMN_RPE);
            double rpe = 0;
            if(rpeColIndex > -1)
            {
                rpe = cursor.getDouble(rpeColIndex);   
            }
            
            int warmup = cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_WARMUP));
            long dateCreated = cursor.getLong(cursor.getColumnIndex(LIFT_COLUMN_DATE_CREATED));
            boolean isNew = (cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_NEW)) == 1);
            boolean isModified = (cursor.getInt(cursor.getColumnIndex(LIFT_COLUMN_MODIFIED)) == 1);


            Lift lift = new Lift();
            lift.setId(liftId);
            lift.setExerciseId(exerciseId);
            lift.setSessionId(sessionId);
            lift.setWeight(weight);
            lift.setReps(reps);
            lift.setSets(sets);
            lift.setRPE(rpe);
            lift.setWarmup(warmup == 1);
            lift.setDateCreated(dateCreated);
            lift.setNew(isNew);
            lift.setModified(isModified);
            lift.setDeleted(isDeleted);
            result.getLifts().add(lift);

            hasNext = cursor.moveToNext();
        }

        Map<Long, Exercise> exercises = selectExerciseMap(false);
        for (Lift lift : result.getLifts())
        {
            long exerciseId = lift.getExerciseId();
            Exercise exercise = exercises.get(exerciseId);
            if (exercise != null) lift.setExerciseName(exercise.getName());
        }

        cursorSession.close();
        cursor.close();
        return result;
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
    public void clearSessionsTable()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String qry = "DELETE FROM " + SESSION_TABLE_NAME;
        db.execSQL(qry);
    }

    //END SESSION METHODS



    //BEGIN NOTE METHODS

    /**
     * Select the note with the associated session
     * @param sessionId The Session ID
     * @return The note
     */
    public String selectNote(long sessionId)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        return selectNote(db, sessionId);
    }

    /**
     *
     * {@link #selectNote(long)
     */
    public String selectNote(SQLiteDatabase db, long sessionId)
    {
        String qry = "SELECT " + SESSION_COLUMN_NOTE + " FROM " + SESSION_TABLE_NAME
                + " WHERE " + SESSION_COLUMN_PK + " = " + sessionId;
        Cursor cursor = db.rawQuery(qry, null);
        if(cursor == null || !cursor.moveToFirst())
        {
            return null;
        }

        int noteColIndex = cursor.getColumnIndex(SESSION_COLUMN_NOTE);
        String note = null;
        if(noteColIndex > -1)
        {
            note = cursor.getString(noteColIndex);
        }
        return note;
    }

    //END NOTE METHODS






















    //BEGIN DATABASE BACKUP METHODS
    public boolean hasBackup(Context ctx)
    {
        return getLastBackup(ctx) != null;
    }
    public DateTime getLastBackup(Context ctx)
    {
        File backupFile = new File(Environment.getExternalStorageDirectory(), DB_COPY_NAME);
        if(!backupFile.exists())
        {
            return null;
        }
        return new DateTime(backupFile.lastModified());

//        SharedPreferences sharedPref = ctx.getSharedPreferences(ctx.getString(R.string.preferencesFileKey), Context.MODE_PRIVATE);
//        int lastUpdate = sharedPref.getInt(DB_BACKUP_PREFERENCE_KEY, -1);
//        return lastUpdate;
//        return Util.dateFromDays(lastUpdate);
    }
    public void createBackupCopy(Context ctx)
    {
        SharedPreferences sharedPref = ctx.getSharedPreferences(ctx.getString(R.string.preferencesFileKey), Context.MODE_PRIVATE);
        int daysSinceEpoch = Util.getDaysSinceEpoch();

//        int lastUpdate = sharedPref.getInt(DB_BACKUP_PREFERENCE_KEY, -1);
//        int diff = daysSinceEpoch - lastUpdate;
//        if(lastUpdate != -1 && diff < dbBackupFrequency)
//        {
//            //if not enough time has passed, don't backup the db
//            return;
//        }

        try
        {
            final String inFileName = "/data/data/com.liftlog/databases/" + DB_NAME;
            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            File dbCopyFile = new File(Environment.getExternalStorageDirectory(), DB_COPY_NAME);

            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(dbCopyFile);

            // Transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer))>0){
                output.write(buffer, 0, length);
            }

            // Close the streams
            output.flush();
            output.close();
            fis.close();

            //update the date of last backup
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(DB_BACKUP_PREFERENCE_KEY, daysSinceEpoch);
            editor.commit();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            Log.d(LOG_TAG, "IOException in createBackupCopy(): " + e.getMessage());
            return;
        }
    }

    public boolean restoreBackupCopy(Context ctx)
    {
        try
        {
            File srcFile = new File(Environment.getExternalStorageDirectory(), DB_COPY_NAME);
            FileInputStream fis = new FileInputStream(srcFile);

            if (!srcFile.exists())
            {
                Toast.makeText(ctx, "DB backup not found", Toast.LENGTH_SHORT).show();
            }
            final String destFileName = "/data/data/com.liftlog/databases/" + DB_NAME;
            OutputStream output = new FileOutputStream(destFileName);

            // Transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer))>0){
                output.write(buffer, 0, length);
            }

            // Close the streams
            output.flush();
            output.close();
            fis.close();
            return true;
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteBackupCopy(Context ctx)
    {
        try
        {
            File srcFile = new File(Environment.getExternalStorageDirectory(), DB_COPY_NAME);
            return srcFile.delete();
        } catch (Exception e)
        {
            Log.d(LOG_TAG, "Error in DataAccessObject.deleteBackupCopy(Context): " + e.getMessage());
            return false;
        }
    }


    private void dataChanged()
    {
    }

    //END DATABASE BACKUP METHODS



    public void test()
    {
        try
        {
            MutableDateTime mdt = new MutableDateTime();
            mdt.setYear(2015);
            for(int i = 1; i <= 12; i++)
            {
                for(int j = 1; j <= 20; j++)
                {
                    mdt.setMonthOfYear(i);
                    mdt.setDayOfMonth(j);
                    Session session = new Session();
                    session.setDate(mdt.getMillis());
                    insert(session);
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }


    public void test2()
    {
        List<Exercise> exercises = selectExercises(true);
        for(Exercise exercise : exercises)
        {
            if(exercise.getDescription() != null && exercise.getDescription().length() > 20)
            {
                deleteExercise(exercise.getId());
            }
        }
    }






}
