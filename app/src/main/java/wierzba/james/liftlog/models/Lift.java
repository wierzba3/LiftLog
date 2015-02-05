package wierzba.james.liftlog.models;

/**
 * Created by James Wierzba on 2/4/2015.
 */
public class Lift {

    public Lift(String exercise, int sets, int reps){
        this.exercise = exercise;
        this.sets = sets;
        this.reps = reps;
    }

    private String exercise;
    private int sets;
    private int reps;

    public String getExercise() {
        return exercise;
    }

    public void setExercise(String exercise) {
        this.exercise = exercise;
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
}
