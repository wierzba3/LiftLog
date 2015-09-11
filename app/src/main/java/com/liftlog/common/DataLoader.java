package com.liftlog.common;

import android.content.Context;

import com.liftlog.data.DataAccessObject;
import com.liftlog.models.Exercise;
import com.liftlog.models.Lift;
import com.liftlog.models.Session;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.Days;
import org.joda.time.MutableDateTime;
import org.joda.time.field.MillisDurationField;

/**
 * Created by root on 9/5/15.
 */
public class DataLoader
{


    private static long SQUAT_ID;
    private static long BENCH_ID;
    private static long DEADLIFT_ID;
    private static long BTNP_ID;
    private static long RDL_ID;
    private static long SEATED_CALVES_ID;
    private static long PULLUPS_ID;
    private static long DIPS_ID;
    private static long OHP_ID;
    private static long CHINUPS_ID;
    private static long HAM_CURLES_ID;

    //TODO
    public static void reload(Context ctx)
    {
        DataAccessObject dao = new DataAccessObject(ctx);
        dao.clearExerciseTable();
        dao.clearLiftsTable();
        dao.clearSessionsTable();

        Exercise squat = new Exercise();
        squat.setNew(true);
        squat.setName("Squat");
        SQUAT_ID = dao.insert(squat);

        Exercise bench = new Exercise();
        bench.setNew(true);
        bench.setName("Bench");
        BENCH_ID = dao.insert(bench);

        Exercise deadlift = new Exercise();
        deadlift.setNew(true);
        deadlift.setName("Deadlift");
        DEADLIFT_ID = dao.insert(deadlift);

        Exercise btnp = new Exercise();
        btnp.setNew(true);
        btnp.setName("BTNP");
        BTNP_ID = dao.insert(btnp);

        Exercise rdl = new Exercise();
        rdl.setNew(true);
        rdl.setName("RDL");
        RDL_ID = dao.insert(rdl);

        Exercise seatedCalves = new Exercise();
        seatedCalves.setNew(true);
        seatedCalves.setName("Seated Calves");
        SEATED_CALVES_ID = dao.insert(seatedCalves);

        Exercise pullups = new Exercise();
        pullups.setNew(true);
        pullups.setName("Pullups");
        PULLUPS_ID = dao.insert(pullups);

        Exercise dips = new Exercise();
        dips.setNew(true);
        dips.setName("Dips");
        DIPS_ID = dao.insert(dips);

        Exercise ohp = new Exercise();
        ohp.setNew(true);
        ohp.setName("OHP");
        OHP_ID = dao.insert(ohp);

        Exercise chinup = new Exercise();
        chinup.setNew(true);
        chinup.setName("Chinup");
        CHINUPS_ID = dao.insert(chinup);

        Exercise hamCurls = new Exercise();
        hamCurls.setNew(true);
        hamCurls.setName("Hamstring Curls (1 leg)");
        HAM_CURLES_ID = dao.insert(hamCurls);

        Session s04_22_2015 = new Session();
        s04_22_2015.setNew(true);
        s04_22_2015.setDate(toMillis(04, 22, 2015));

        long sessionId;

        Session s04_23_2015 = new Session();
        s04_23_2015.setNew(true);
        s04_23_2015.setDate(toMillis(04, 24, 2015));
        sessionId = dao.insert(s04_23_2015);
        dao.insert(createLift(sessionId, SQUAT_ID, 410, 5, 1));
        dao.insert(createLift(sessionId, DEADLIFT_ID, 470, 5, 1));
        dao.insert(createLift(sessionId, CHINUPS_ID, 235, 9, 3));
        dao.insert(createLift(sessionId, SEATED_CALVES_ID, 200, 19, 3));
        dao.insert(createLift(sessionId, HAM_CURLES_ID, 80, 10, 1));
        dao.insert(createLift(sessionId, HAM_CURLES_ID, 65, 15, 3));

        Session s04_25_2015 = new Session();
        s04_25_2015.setNew(true);
        s04_25_2015.setDate(toMillis(04, 25, 2015));
        sessionId = dao.insert(s04_25_2015);


        Session s04_26_2015 = new Session();


        Session s04_29_2015 = new Session();


        Session s04_30_2015 = new Session();


        Session s05_02_2015 = new Session();


        Session s05_03_2015 = new Session();


        Session s05_05_2015 = new Session();


        Session s05_08_2015 = new Session();


        Session s05_09_2015 = new Session();


        Session s05_10_2015 = new Session();


        Session s05_13_2015 = new Session();


        Session s05_14_2015 = new Session();


        Session s05_16_2015 = new Session();


        Session s05_17_2015 = new Session();


        Session s05_20_2015 = new Session();


        Session s05_21_2015 = new Session();


        Session s05_23_2015 = new Session();


        Session s05_24_2015 = new Session();


        Session s05_27_2015 = new Session();


        Session s05_28_2015 = new Session();


        Session s05_30_2015 = new Session();


        Session s05_31_2015 = new Session();


        Session s06_02_2015 = new Session();


        Session s06_06_2015 = new Session();


        Session s06_09_2015 = new Session();


        Session s06_10_2015 = new Session();


        Session s06_15_2015 = new Session();


        Session s06_16_2015 = new Session();


        Session s06_19_2015 = new Session();


        Session s06_20_2015 = new Session();


        Session s06_21_2015 = new Session();


        Session s06_22_2015 = new Session();


        Session s06_25_2015 = new Session();


        Session s06_27_2015 = new Session();


        Session s06_28_2015 = new Session();


        Session s06_30_2015 = new Session();


        Session s07_02_2015 = new Session();


        Session s07_03_2015 = new Session();


        Session s07_05_2015 = new Session();


        Session s07_06_2015 = new Session();


        Session s07_09_2015 = new Session();


        Session s07_10_2015 = new Session();


        Session s07_12_2015 = new Session();


        Session s07_13_2015 = new Session();


        Session s07_15_2015 = new Session();


        Session s07_17_2015 = new Session();


        Session s07_18_2015 = new Session();


        Session s07_20_2015 = new Session();


        Session s07_21_2015 = new Session();


        Session s07_23_2015 = new Session();


        Session s07_24_2015 = new Session();


        Session s07_26_2015 = new Session();


        Session s07_27_2015 = new Session();


        Session s07_30_2015 = new Session();


        Session s08_01_2015 = new Session();


        Session s08_02_2015 = new Session();


        Session s08_05_2015 = new Session();


        Session s08_08_2015 = new Session();


        Session s08_11_2015 = new Session();


        Session s08_23_2015 = new Session();


        Session s08_24_2015 = new Session();


        Session s08_26_2015 = new Session();


        Session s08_27_2015 = new Session();


        Session s08_29_2015 = new Session();


        Session s08_30_2015 = new Session();


        Session s09_01_2015 = new Session();


        Session s09_02_2015 = new Session();


        Session s09_04_2015 = new Session();


        Session s09_05_2015 = new Session();
    }

    //DateTime(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minuteOfHour)
    private DateTime createDate(int month, int day, int year)
    {
        return new DateTime(year, month, day, 12, 0);
    }


    MutableDateTime epoch = new MutableDateTime(0);
    private static long toMillis(int month, int day, int year)
    {
        DateTime dt = new DateTime(year, month, day, 12, 0);
        return dt.getMillis();
    }

    private static Lift createLift(long sessionId, long exerciseId, int weight, int reps, int sets)
    {
        Lift lift = new Lift();
        lift.setSessionId(sessionId);
        lift.setExerciseId(exerciseId);
        lift.setWeight(weight);
        lift.setReps(reps);
        lift.setSets(sets);
        return lift;
    }


}
