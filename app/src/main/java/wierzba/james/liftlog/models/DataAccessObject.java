package wierzba.james.liftlog.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jwierzba on 6/8/2015.
 */
public class DataAccessObject extends SQLiteOpenHelper
{

    public static final String DB_NAME = "LiftLog.db";

    public static final String LIFT_TABLE_NAME = "lifts";
    public static final String LIFT_COLUMN_PK = "id";
    public static final String LIFT_COLUMN_SESSION_FK = "session_id";
    public static final String LIFT_COLUMN_DATE = "date";
    public static final String LIFT_COLUMN_DATE_CREATED = "date_created";
    public static final String LIFT_COLUMN_LIFT_NAME = "lift_name";
    public static final String LIFT_COLUMN_WEIGHT = "weight";
    public static final String LIFT_COLUMN_REPS = "reps";
    public static final String LIFT_COLUMN_SETS = "sets";
    public static final String LIFT_TABLE_CREATE_QUERY =
            "CREATE TABLE " + LIFT_TABLE_NAME
                    + " ("
                    + LIFT_COLUMN_PK + " INTEGER PRIMARY KEY, "
                    + LIFT_COLUMN_DATE_CREATED + " INTEGER, "
                    + LIFT_COLUMN_DATE + " INTEGER, "
                    + LIFT_COLUMN_LIFT_NAME + " TEXT, "
                    + LIFT_COLUMN_WEIGHT + " INTEGER, "
                    + LIFT_COLUMN_REPS + " INTEGER, "
                    + LIFT_COLUMN_SETS + " INTEGER"
                    +  ")";

    public static final String SESSION_TABLE_NAME = "session";
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

    //TODO use this
    public static final String QUERY_JOIN  = "SELECT * FROM " + SESSION_TABLE_NAME + " as a," + LIFT_TABLE_NAME + " as b" +
            " WHERE a." + SESSION_COLUMN_PK + " = b." + LIFT_COLUMN_PK;

    public DataAccessObject(Context context)
    {
        super(context, DB_NAME , null, 7);
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

    public void insert(Lift lift)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
//        values.put(LIFT_COLUMN_ID, lift.getTime());
        values.put(LIFT_COLUMN_DATE_CREATED, System.currentTimeMillis());
        values.put(LIFT_COLUMN_DATE, lift.getTime());
        values.put(LIFT_COLUMN_LIFT_NAME, lift.getExerciseName());
        values.put(LIFT_COLUMN_WEIGHT, lift.getWeight());
        values.put(LIFT_COLUMN_REPS, lift.getReps());
        values.put(LIFT_COLUMN_SETS, lift.getSets());
        db.insert(LIFT_TABLE_NAME, null, values);
    }

    public Cursor getData()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM " + LIFT_TABLE_NAME, null );
        return res;
    }

    /**
     * Select all training sessions
     * @return
     */
    public List<Session> selectSessions()
    {
        ArrayList<Session> result = new ArrayList<Session>();

        SQLiteDatabase db = this.getReadableDatabase();
        String qry = "SELECT * FROM " + SESSION_TABLE_NAME;
        Cursor cursor = db.rawQuery(qry, null);

        if(cursor == null || cursor.getCount() > 0) return result;

        cursor.moveToFirst();
        boolean hasNext = true;
        while(hasNext)
        {
            Session session = new Session();

            int date = cursor.getInt(cursor.getColumnIndex(DataAccessObject.SESSION_COLUMN_DATE));
            session.setDate(date);

            result.add(session);

            hasNext = cursor.moveToNext();
        }

        return result;
    }


    public void deleteAll()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String qry = "DELETE FROM " + LIFT_TABLE_NAME;
        db.execSQL(qry);
    }


}
