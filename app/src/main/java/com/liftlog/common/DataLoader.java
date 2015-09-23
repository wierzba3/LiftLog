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

import java.util.Map;

/**
 * Created by root on 9/5/15.
 */
public class DataLoader
{

	private static DataAccessObject dao;

    private static long SQUAT_ID;
    private static long BENCH_ID;
	private static long BENCH_P_ID;
    private static long DEADLIFT_ID;
	private static long BB_ROW_ID;
    private static long BTNP_ID;
    private static long RDL_ID;
    private static long SEATED_CALVES_ID;
    private static long PULLUPS_ID;
    private static long DIPS_ID;
    private static long OHP_ID;
    private static long CHINUPS_ID;
    private static long HAMSTRING_CURLS_ID;
	private static long SUMO_DL_DOH_BL_ID;
	private static long HAMMER_CURL_ID;
	
    //TODO
    public static void reload(Context ctx)
    {
        dao = new DataAccessObject(ctx);
        dao.clearExerciseTable();
        dao.clearLiftsTable();
        dao.clearSessionsTable();

		Map<Long, Exercise> exerciseMap = dao.selectExercises(false);
	
	
		//TODO make sure these lift names match the ones in the app db
        SQUAT_ID = findOrInsert(exerciseMap, "Squat");
        BENCH_ID = findOrInsert(exerciseMap, "Bench(T)");
		BENCH_P_ID = findOrInsert(exerciseMap, "Bench(P)");  
        DEADLIFT_ID = findOrInsert(exerciseMap, "Deadlift");
		BB_ROW_ID = findOrInsert(exerciseMap, "BB Row");
        BTNP_ID = findOrInsert(exerciseMap, "BTNP");
        RDL_ID = findOrInsert(exerciseMap, "RDL");
        SEATED_CALVES_ID = findOrInsert(exerciseMap, "Seated Calve");
        PULLUPS_ID = findOrInsert(exerciseMap, "Pullup");
        DIPS_ID = findOrInsert(exerciseMap, "Dip");
        OHP_ID = findOrInsert(exerciseMap, "OHP");
		CHINUPS_ID = findOrInsert(exerciseMap, "Chinup");
		HAMSTRING_CURLS_ID  = findOrInsert(exerciseMap, "Hamstring Curl");
		SUMO_DL_DOH_BL_ID = findOrInsert(exerciseMap, "Sumo DL(DOH)(BL)");
		HAMMER_CURL_ID = findOrInsert(exerciseMap, "Hammer Curl");

        long sessionId;

		Session s4_22_2015 = new Session();
		s4_22_2015.setNew(true);
		s4_22_2015.setDate(toMillis(4, 22, 2015));
		sessionId = dao.insert(s4_22_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 287, 3, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 140, 13, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 155, 3, 3));

		Session s4_23_2015 = new Session();
		s4_23_2015.setNew(true);
		s4_23_2015.setDate(toMillis(4, 23, 2015));
		sessionId = dao.insert(s4_23_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 230, 9, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 65, 15, 3));

		Session s4_25_2015 = new Session();
		s4_25_2015.setNew(true);
		s4_25_2015.setDate(toMillis(4, 25, 2015));
		sessionId = dao.insert(s4_25_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 267, 5, 6));
		dao.insert(createLift(sessionId, SQUAT_ID, 140, 14, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 135, 7, 7));
		dao.insert(createLift(sessionId, SQUAT_ID, 35, 13, 3));

		Session s4_26_2015 = new Session();
		s4_26_2015.setNew(true);
		s4_26_2015.setDate(toMillis(4, 26, 2015));
		sessionId = dao.insert(s4_26_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 375, 5, 5));
		dao.insert(createLift(sessionId, SQUAT_ID, 365, 5, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 230, 10, 2));
		dao.insert(createLift(sessionId, SQUAT_ID, 80, 10, 2));

		Session s4_29_2015 = new Session();
		s4_29_2015.setNew(true);
		s4_29_2015.setDate(toMillis(4, 29, 2015));
		sessionId = dao.insert(s4_29_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 290, 3, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 140, 15, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 160, 3, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 35, 14, 3));

		Session s4_30_2015 = new Session();
		s4_30_2015.setNew(true);
		s4_30_2015.setDate(toMillis(4, 30, 2015));
		sessionId = dao.insert(s4_30_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 315, 5, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 230, 10, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 80, 11, 3));

		Session s5_2_2015 = new Session();
		s5_2_2015.setNew(true);
		s5_2_2015.setDate(toMillis(5, 2, 2015));
		sessionId = dao.insert(s5_2_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 270, 5, 6));
		dao.insert(createLift(sessionId, SQUAT_ID, 145, 10, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 115, 10, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 35, 15, 3));

		Session s5_3_2015 = new Session();
		s5_3_2015.setNew(true);
		s5_3_2015.setDate(toMillis(5, 3, 2015));
		sessionId = dao.insert(s5_3_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 380, 5, 5));
		dao.insert(createLift(sessionId, SQUAT_ID, 315, 6, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 235, 5, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 80, 12, 3));

		Session s5_5_2015 = new Session();
		s5_5_2015.setNew(true);
		s5_5_2015.setDate(toMillis(5, 5, 2015));
		sessionId = dao.insert(s5_5_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 292, 3, 2));
		dao.insert(createLift(sessionId, SQUAT_ID, 145, 11, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 120, 10, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 35, 15, 2));

		Session s5_8_2015 = new Session();
		s5_8_2015.setNew(true);
		s5_8_2015.setDate(toMillis(5, 8, 2015));
		sessionId = dao.insert(s5_8_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 315, 7, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 235, 6, 3));

		Session s5_9_2015 = new Session();
		s5_9_2015.setNew(true);
		s5_9_2015.setDate(toMillis(5, 9, 2015));
		sessionId = dao.insert(s5_9_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 272, 5, 4));
		dao.insert(createLift(sessionId, SQUAT_ID, 145, 12, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 40, 10, 3));

		Session s5_10_2015 = new Session();
		s5_10_2015.setNew(true);
		s5_10_2015.setDate(toMillis(5, 10, 2015));
		sessionId = dao.insert(s5_10_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 385, 5, 4));
		dao.insert(createLift(sessionId, SQUAT_ID, 315, 8, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 235, 7, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 80, 13, 3));

		Session s5_13_2015 = new Session();
		s5_13_2015.setNew(true);
		s5_13_2015.setDate(toMillis(5, 13, 2015));
		sessionId = dao.insert(s5_13_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 295, 3, 2));
		dao.insert(createLift(sessionId, SQUAT_ID, 145, 13, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 40, 11, 3));

		Session s5_14_2015 = new Session();
		s5_14_2015.setNew(true);
		s5_14_2015.setDate(toMillis(5, 14, 2015));
		sessionId = dao.insert(s5_14_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 425, 3, 2));
		dao.insert(createLift(sessionId, SQUAT_ID, 325, 5, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 235, 8, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 80, 14, 3));

		Session s5_16_2015 = new Session();
		s5_16_2015.setNew(true);
		s5_16_2015.setDate(toMillis(5, 16, 2015));
		sessionId = dao.insert(s5_16_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 275, 5, 5));
		dao.insert(createLift(sessionId, SQUAT_ID, 145, 13, 2));
		dao.insert(createLift(sessionId, SQUAT_ID, 40, 12, 3));

		Session s5_17_2015 = new Session();
		s5_17_2015.setNew(true);
		s5_17_2015.setDate(toMillis(5, 17, 2015));
		sessionId = dao.insert(s5_17_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 350, 5, 5));
		dao.insert(createLift(sessionId, SQUAT_ID, 325, 6, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 235, 8, 4));
		dao.insert(createLift(sessionId, SQUAT_ID, 80, 15, 3));

		Session s5_20_2015 = new Session();
		s5_20_2015.setNew(true);
		s5_20_2015.setDate(toMillis(5, 20, 2015));
		sessionId = dao.insert(s5_20_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 297, 3, 2));
		dao.insert(createLift(sessionId, SQUAT_ID, 145, 14, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 40, 13, 3));

		Session s5_21_2015 = new Session();
		s5_21_2015.setNew(true);
		s5_21_2015.setDate(toMillis(5, 21, 2015));
		sessionId = dao.insert(s5_21_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 430, 3, 2));
		dao.insert(createLift(sessionId, SQUAT_ID, 325, 7, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 240, 5, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 95, 10, 3));

		Session s5_23_2015 = new Session();
		s5_23_2015.setNew(true);
		s5_23_2015.setDate(toMillis(5, 23, 2015));
		sessionId = dao.insert(s5_23_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 277, 5, 5));
		dao.insert(createLift(sessionId, SQUAT_ID, 145, 15, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 45, 10, 3));

		Session s5_24_2015 = new Session();
		s5_24_2015.setNew(true);
		s5_24_2015.setDate(toMillis(5, 24, 2015));
		sessionId = dao.insert(s5_24_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 355, 5, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 325, 8, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 240, 6, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 95, 8, 3));

		Session s5_27_2015 = new Session();
		s5_27_2015.setNew(true);
		s5_27_2015.setDate(toMillis(5, 27, 2015));
		sessionId = dao.insert(s5_27_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 300, 3, 2));
		dao.insert(createLift(sessionId, SQUAT_ID, 145, 16, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 45, 11, 3));

		Session s5_28_2015 = new Session();
		s5_28_2015.setNew(true);
		s5_28_2015.setDate(toMillis(5, 28, 2015));
		sessionId = dao.insert(s5_28_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 335, 5, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 240, 7, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 95, 9, 3));

		Session s5_30_2015 = new Session();
		s5_30_2015.setNew(true);
		s5_30_2015.setDate(toMillis(5, 30, 2015));
		sessionId = dao.insert(s5_30_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 280, 5, 5));
		dao.insert(createLift(sessionId, SQUAT_ID, 150, 10, 3));

		Session s5_31_2015 = new Session();
		s5_31_2015.setNew(true);
		s5_31_2015.setDate(toMillis(5, 31, 2015));
		sessionId = dao.insert(s5_31_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 355, 5, 5));
		dao.insert(createLift(sessionId, SQUAT_ID, 335, 5, 4));
		dao.insert(createLift(sessionId, SQUAT_ID, 240, 8, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 95, 10, 3));

		Session s6_2_2015 = new Session();
		s6_2_2015.setNew(true);
		s6_2_2015.setDate(toMillis(6, 2, 2015));
		sessionId = dao.insert(s6_2_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 302, 3, 2));
		dao.insert(createLift(sessionId, SQUAT_ID, 150, 11, 3));

		Session s6_6_2015 = new Session();
		s6_6_2015.setNew(true);
		s6_6_2015.setDate(toMillis(6, 6, 2015));
		sessionId = dao.insert(s6_6_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 255, 5, 4));
		dao.insert(createLift(sessionId, SQUAT_ID, 150, 12, 3));

		Session s6_9_2015 = new Session();
		s6_9_2015.setNew(true);
		s6_9_2015.setDate(toMillis(6, 9, 2015));
		sessionId = dao.insert(s6_9_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 185, 5, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 150, 13, 3));

		Session s6_10_2015 = new Session();
		s6_10_2015.setNew(true);
		s6_10_2015.setDate(toMillis(6, 10, 2015));
		sessionId = dao.insert(s6_10_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 245, 5, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 95, 12, 3));

		Session s6_15_2015 = new Session();
		s6_15_2015.setNew(true);
		s6_15_2015.setDate(toMillis(6, 15, 2015));
		sessionId = dao.insert(s6_15_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 245, 5, 6));
		dao.insert(createLift(sessionId, SQUAT_ID, 150, 14, 3));

		Session s6_16_2015 = new Session();
		s6_16_2015.setNew(true);
		s6_16_2015.setDate(toMillis(6, 16, 2015));
		sessionId = dao.insert(s6_16_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 345, 6, 5));
		dao.insert(createLift(sessionId, SQUAT_ID, 335, 6, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 245, 6, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 65, 12, 3));

		Session s6_19_2015 = new Session();
		s6_19_2015.setNew(true);
		s6_19_2015.setDate(toMillis(6, 19, 2015));
		sessionId = dao.insert(s6_19_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 300, 3, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 150, 15, 3));

		Session s6_20_2015 = new Session();
		s6_20_2015.setNew(true);
		s6_20_2015.setDate(toMillis(6, 20, 2015));
		sessionId = dao.insert(s6_20_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 335, 7, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 245, 7, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 65, 13, 3));

		Session s6_21_2015 = new Session();
		s6_21_2015.setNew(true);
		s6_21_2015.setDate(toMillis(6, 21, 2015));
		sessionId = dao.insert(s6_21_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 250, 6, 5));
		dao.insert(createLift(sessionId, SQUAT_ID, 155, 10, 3));

		Session s6_22_2015 = new Session();
		s6_22_2015.setNew(true);
		s6_22_2015.setDate(toMillis(6, 22, 2015));
		sessionId = dao.insert(s6_22_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 350, 6, 5));
		dao.insert(createLift(sessionId, SQUAT_ID, 335, 8, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 245, 8, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 65, 14, 3));

		Session s6_25_2015 = new Session();
		s6_25_2015.setNew(true);
		s6_25_2015.setDate(toMillis(6, 25, 2015));
		sessionId = dao.insert(s6_25_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 305, 2, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 155, 11, 3));

		Session s6_27_2015 = new Session();
		s6_27_2015.setNew(true);
		s6_27_2015.setDate(toMillis(6, 27, 2015));
		sessionId = dao.insert(s6_27_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 340, 5, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 250, 5, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 65, 15, 3));

		Session s6_28_2015 = new Session();
		s6_28_2015.setNew(true);
		s6_28_2015.setDate(toMillis(6, 28, 2015));
		sessionId = dao.insert(s6_28_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 255, 6, 6));
		dao.insert(createLift(sessionId, SQUAT_ID, 155, 11, 2));

		Session s6_30_2015 = new Session();
		s6_30_2015.setNew(true);
		s6_30_2015.setDate(toMillis(6, 30, 2015));
		sessionId = dao.insert(s6_30_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 355, 6, 6));
		dao.insert(createLift(sessionId, SQUAT_ID, 340, 6, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 250, 5, 3));

		Session s7_2_2015 = new Session();
		s7_2_2015.setNew(true);
		s7_2_2015.setDate(toMillis(7, 2, 2015));
		sessionId = dao.insert(s7_2_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 307, 2, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 155, 12, 2));

		Session s7_3_2015 = new Session();
		s7_3_2015.setNew(true);
		s7_3_2015.setDate(toMillis(7, 3, 2015));
		sessionId = dao.insert(s7_3_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 340, 7, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 250, 6, 2));

		Session s7_5_2015 = new Session();
		s7_5_2015.setNew(true);
		s7_5_2015.setDate(toMillis(7, 5, 2015));
		sessionId = dao.insert(s7_5_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 260, 6, 6));
		dao.insert(createLift(sessionId, SQUAT_ID, 155, 12, 3));

		Session s7_6_2015 = new Session();
		s7_6_2015.setNew(true);
		s7_6_2015.setDate(toMillis(7, 6, 2015));
		sessionId = dao.insert(s7_6_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 360, 6, 6));
		dao.insert(createLift(sessionId, SQUAT_ID, 340, 8, 3));

		Session s7_9_2015 = new Session();
		s7_9_2015.setNew(true);
		s7_9_2015.setDate(toMillis(7, 9, 2015));
		sessionId = dao.insert(s7_9_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 310, 2, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 155, 12, 2));

		Session s7_10_2015 = new Session();
		s7_10_2015.setNew(true);
		s7_10_2015.setDate(toMillis(7, 10, 2015));
		sessionId = dao.insert(s7_10_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 345, 5, 4));
		dao.insert(createLift(sessionId, SQUAT_ID, 250, 6, 3));

		Session s7_12_2015 = new Session();
		s7_12_2015.setNew(true);
		s7_12_2015.setDate(toMillis(7, 12, 2015));
		sessionId = dao.insert(s7_12_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 265, 6, 4));
		dao.insert(createLift(sessionId, SQUAT_ID, 155, 13, 2));

		Session s7_13_2015 = new Session();
		s7_13_2015.setNew(true);
		s7_13_2015.setDate(toMillis(7, 13, 2015));
		sessionId = dao.insert(s7_13_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 365, 6, 6));
		dao.insert(createLift(sessionId, SQUAT_ID, 345, 6, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 250, 6, 2));

		Session s7_15_2015 = new Session();
		s7_15_2015.setNew(true);
		s7_15_2015.setDate(toMillis(7, 15, 2015));
		sessionId = dao.insert(s7_15_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 312, 2, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 155, 13, 3));

		Session s7_17_2015 = new Session();
		s7_17_2015.setNew(true);
		s7_17_2015.setDate(toMillis(7, 17, 2015));
		sessionId = dao.insert(s7_17_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 345, 7, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 250, 7, 2));

		Session s7_18_2015 = new Session();
		s7_18_2015.setNew(true);
		s7_18_2015.setDate(toMillis(7, 18, 2015));
		sessionId = dao.insert(s7_18_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 275, 4, 6));
		dao.insert(createLift(sessionId, SQUAT_ID, 155, 13, 2));

		Session s7_20_2015 = new Session();
		s7_20_2015.setNew(true);
		s7_20_2015.setDate(toMillis(7, 20, 2015));
		sessionId = dao.insert(s7_20_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 385, 5, 5));
		dao.insert(createLift(sessionId, SQUAT_ID, 345, 8, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 250, 7, 3));

		Session s7_21_2015 = new Session();
		s7_21_2015.setNew(true);
		s7_21_2015.setDate(toMillis(7, 21, 2015));
		sessionId = dao.insert(s7_21_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 315, 2, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 155, 14, 3));

		Session s7_23_2015 = new Session();
		s7_23_2015.setNew(true);
		s7_23_2015.setDate(toMillis(7, 23, 2015));
		sessionId = dao.insert(s7_23_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 435, 3, 2));
		dao.insert(createLift(sessionId, SQUAT_ID, 350, 5, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 250, 7, 2));

		Session s7_24_2015 = new Session();
		s7_24_2015.setNew(true);
		s7_24_2015.setDate(toMillis(7, 24, 2015));
		sessionId = dao.insert(s7_24_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 280, 4, 5));
		dao.insert(createLift(sessionId, SQUAT_ID, 155, 14, 2));

		Session s7_26_2015 = new Session();
		s7_26_2015.setNew(true);
		s7_26_2015.setDate(toMillis(7, 26, 2015));
		sessionId = dao.insert(s7_26_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 395, 4, 5));
		dao.insert(createLift(sessionId, SQUAT_ID, 350, 6, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 250, 8, 3));

		Session s7_27_2015 = new Session();
		s7_27_2015.setNew(true);
		s7_27_2015.setDate(toMillis(7, 27, 2015));
		sessionId = dao.insert(s7_27_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 305, 3, 2));
		dao.insert(createLift(sessionId, SQUAT_ID, 155, 15, 2));

		Session s7_30_2015 = new Session();
		s7_30_2015.setNew(true);
		s7_30_2015.setDate(toMillis(7, 30, 2015));
		sessionId = dao.insert(s7_30_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 445, 2, 2));
		dao.insert(createLift(sessionId, SQUAT_ID, 350, 7, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 250, 7, 3));

		Session s8_1_2015 = new Session();
		s8_1_2015.setNew(true);
		s8_1_2015.setDate(toMillis(8, 1, 2015));
		sessionId = dao.insert(s8_1_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 295, 3, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 155, 15, 3));

		Session s8_2_2015 = new Session();
		s8_2_2015.setNew(true);
		s8_2_2015.setDate(toMillis(8, 2, 2015));
		sessionId = dao.insert(s8_2_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 405, 3, 4));
		dao.insert(createLift(sessionId, SQUAT_ID, 350, 8, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 250, 7, 2));

		Session s8_5_2015 = new Session();
		s8_5_2015.setNew(true);
		s8_5_2015.setDate(toMillis(8, 5, 2015));
		sessionId = dao.insert(s8_5_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 315, 1, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 135, 12, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 195, 8, 3));

		Session s8_8_2015 = new Session();
		s8_8_2015.setNew(true);
		s8_8_2015.setDate(toMillis(8, 8, 2015));
		sessionId = dao.insert(s8_8_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 305, 1, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 135, 12, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 195, 8, 3));

		Session s8_11_2015 = new Session();
		s8_11_2015.setNew(true);
		s8_11_2015.setDate(toMillis(8, 11, 2015));
		sessionId = dao.insert(s8_11_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 225, 5, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 275, 1, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 132, 12, 3));

		Session s8_23_2015 = new Session();
		s8_23_2015.setNew(true);
		s8_23_2015.setDate(toMillis(8, 23, 2015));
		sessionId = dao.insert(s8_23_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 235, 5, 5));
		dao.insert(createLift(sessionId, SQUAT_ID, 155, 10, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 95, 8, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 195, 10, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 35, 12, 3));

		Session s8_24_2015 = new Session();
		s8_24_2015.setNew(true);
		s8_24_2015.setDate(toMillis(8, 24, 2015));
		sessionId = dao.insert(s8_24_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 325, 5, 5));
		dao.insert(createLift(sessionId, SQUAT_ID, 315, 5, 2));
		dao.insert(createLift(sessionId, SQUAT_ID, 240, 6, 3));

		Session s8_26_2015 = new Session();
		s8_26_2015.setNew(true);
		s8_26_2015.setDate(toMillis(8, 26, 2015));
		sessionId = dao.insert(s8_26_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 160, 10, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 95, 9, 3));

		Session s8_27_2015 = new Session();
		s8_27_2015.setNew(true);
		s8_27_2015.setDate(toMillis(8, 27, 2015));
		sessionId = dao.insert(s8_27_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 345, 5, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 235, 5, 3));

		Session s8_29_2015 = new Session();
		s8_29_2015.setNew(true);
		s8_29_2015.setDate(toMillis(8, 29, 2015));
		sessionId = dao.insert(s8_29_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 240, 5, 5));
		dao.insert(createLift(sessionId, SQUAT_ID, 165, 10, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 95, 9, 2));

		Session s8_30_2015 = new Session();
		s8_30_2015.setNew(true);
		s8_30_2015.setDate(toMillis(8, 30, 2015));
		sessionId = dao.insert(s8_30_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 330, 5, 5));
		dao.insert(createLift(sessionId, SQUAT_ID, 225, 10, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 90, 13, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 235, 5, 2));

		Session s9_1_2015 = new Session();
		s9_1_2015.setNew(true);
		s9_1_2015.setDate(toMillis(9, 1, 2015));
		sessionId = dao.insert(s9_1_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 170, 10, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 95, 10, 2));
		dao.insert(createLift(sessionId, SQUAT_ID, 240, 8, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 35, 13, 5));

		Session s9_2_2015 = new Session();
		s9_2_2015.setNew(true);
		s9_2_2015.setDate(toMillis(9, 2, 2015));
		sessionId = dao.insert(s9_2_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 430, 2, 2));
		dao.insert(createLift(sessionId, SQUAT_ID, 345, 6, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 235, 6, 3));

		Session s9_4_2015 = new Session();
		s9_4_2015.setNew(true);
		s9_4_2015.setDate(toMillis(9, 4, 2015));
		sessionId = dao.insert(s9_4_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 245, 5, 5));
		dao.insert(createLift(sessionId, SQUAT_ID, 175, 10, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 95, 10, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 90, 12, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 240, 8, 2));
		dao.insert(createLift(sessionId, SQUAT_ID, 35, 14, 3));

		Session s9_5_2015 = new Session();
		s9_5_2015.setNew(true);
		s9_5_2015.setDate(toMillis(9, 5, 2015));
		sessionId = dao.insert(s9_5_2015);
		dao.insert(createLift(sessionId, SQUAT_ID, 335, 5, 5));
		dao.insert(createLift(sessionId, SQUAT_ID, 230, 10, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 90, 14, 3));
		dao.insert(createLift(sessionId, SQUAT_ID, 235, 6, 2));
		
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

    private static Exercise fromName(Map<Long, Exercise> exerciseMap, String name)
    {
        if(exerciseMap == null || name == null) return null;
        for(long key : exerciseMap.keySet())
        {
            Exercise exercise = exerciseMap.get(key);
            if(exercise != null && name.equalsIgnoreCase(exercise.getName()))
            {
                return exercise;
            }
        }
        return null;
    }

    private static long findOrInsert(Map<Long, Exercise> exerciseMap, String name)
    {
        long id = -1;
        Exercise e = fromName(exerciseMap, "Squat");
        if(e == null)
        {
            e = new Exercise();
            e.setNew(true);
            e.setName(name);
            id = dao.insert(e);
        }
        else id = e.getId();
        return id;
    }
       


}
