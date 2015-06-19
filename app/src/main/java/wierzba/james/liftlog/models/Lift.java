package wierzba.james.liftlog.models;

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
    /** The date the lift was performed. Value is number of days since the epoch. */
    private long id;
    private int exerciseId;
    private int sessionId;
    private long date;
    private int weight;
    private Unit unit;
    private int sets;
    private int reps;

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public int getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(int exerciseId) {
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

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
