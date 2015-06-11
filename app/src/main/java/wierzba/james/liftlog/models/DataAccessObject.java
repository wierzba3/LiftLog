package wierzba.james.liftlog.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jwierzba on 6/8/2015.
 */
public class DataAccessObject extends SQLiteOpenHelper
{

    public static final String DB_NAME = "LiftLog.db";
    public static final String LIFT_TABLE_NAME = "lifts";
    public static final String LIFT_COLUMN_ID = "id";
    public static final String LIFT_COLUMN_LIFT_NAME = "lift_name";
    public static final String LIFT_COLUMN_WEIGHT = "weight";
    public static final String LIFT_COLUMN_REPS = "reps";
    public static final String LIFT_COLUMN_SETS = "sets";

    public DataAccessObject(Context context)
    {
        super(context, DB_NAME , null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String qry;
        qry = "create table " + LIFT_TABLE_NAME
                + " ("
                + LIFT_COLUMN_ID + ","
                + LIFT_COLUMN_LIFT_NAME + ","
                + LIFT_COLUMN_WEIGHT + ","
                + LIFT_COLUMN_REPS + ","
                + LIFT_COLUMN_SETS
                +  ")";
        db.execSQL(qry);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + LIFT_TABLE_NAME);
        onCreate(db);
    }

    public void insert(int day, String liftName, int weight, int reps, int sets)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LIFT_COLUMN_ID, day);
        values.put(LIFT_COLUMN_LIFT_NAME, liftName);
        values.put(LIFT_COLUMN_WEIGHT, weight);
        values.put(LIFT_COLUMN_REPS, reps);
        values.put(LIFT_COLUMN_SETS, sets);
        db.insert(LIFT_TABLE_NAME, null, values);
    }

    public Cursor getData()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + LIFT_TABLE_NAME, null );
        return res;
    }

    public void deleteAll()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String qry = "DELETE FROM " + LIFT_TABLE_NAME;
        db.execSQL(qry);
    }


}
