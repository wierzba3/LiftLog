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
//    public static final String LIFT_COLUMN_ID = "id";
    public static final String LIFT_COLUMN_DATE_LIFT = "date_lift";
    public static final String LIFT_COLUMN_DATE_ENTERED = "date_entered";
    public static final String LIFT_COLUMN_LIFT_NAME = "lift_name";
    public static final String LIFT_COLUMN_WEIGHT = "weight";
    public static final String LIFT_COLUMN_REPS = "reps";
    public static final String LIFT_COLUMN_SETS = "sets";

    public DataAccessObject(Context context)
    {
        super(context, DB_NAME , null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String qry;
        qry = "CREATE TABLE " + LIFT_TABLE_NAME
                + " ("
                + LIFT_COLUMN_DATE_ENTERED + ","
                + LIFT_COLUMN_DATE_LIFT + ","
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

    public void insert(Lift lift)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
//        values.put(LIFT_COLUMN_ID, lift.getTime());
        values.put(LIFT_COLUMN_DATE_ENTERED, System.currentTimeMillis());
        values.put(LIFT_COLUMN_DATE_LIFT, lift.getTime());
        values.put(LIFT_COLUMN_LIFT_NAME, lift.getExerciseName());
        values.put(LIFT_COLUMN_WEIGHT, lift.getWeight());
        values.put(LIFT_COLUMN_REPS, lift.getReps());
        values.put(LIFT_COLUMN_SETS, lift.getSets());
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
