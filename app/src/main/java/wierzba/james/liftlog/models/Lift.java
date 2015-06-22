package wierzba.james.liftlog.models;

import android.database.Cursor;

import wierzba.james.liftlog.DataAccessObject;

/**
 * Created by James Wierzba on 2/4/2015.
 */
public class Lift {

    public Lift()
    {

    }

    public Lift(long id, int exerciseId, int sessionId, int day, int weight, int sets, int reps, Unit unit)
    {
        this.id = id;
        this.exerciseId = exerciseId;
        this.sessionId = sessionId;
        this.date = day;
        this.weight = weight;
        this.sets = sets;
        this.reps = reps;
        this.unit = unit;
    }

    public enum Unit
    {
        LB,
        KG;
    }
    /** Primary Key ID */
    private long id;
    private long exerciseId;
    private long sessionId;
    private long date;
    private int weight;
    private Unit unit;
    private int sets;
    private int reps;

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(long exerciseId) {
        this.exerciseId = exerciseId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

//    public long getDate() {
//        return date;
//    }
//
//    public void setDate(long date) {
//        this.date = date;
//    }




}
