package wierzba.james.liftlog.models;

/**
 * Created by James Wierzba on 2/4/2015.
 */
public class Lift {

    public Lift() { }

    public Lift(int day, String exerciseName, int weight, int sets, int reps, Unit unit)
    {
        this.time = day;
        this.exerciseName = exerciseName;
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
    /** The time the lift was performed. Value is number of days since the epoch. */
    private int sessionId;
    private long time;
    private String exerciseName;
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

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
