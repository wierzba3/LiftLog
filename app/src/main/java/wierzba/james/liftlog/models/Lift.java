package wierzba.james.liftlog.models;

/**
 * Created by James Wierzba on 2/4/2015.
 */
public class Lift {

    public Lift(Exercise exercise, int sets, int reps){
        this.exercise = exercise;
        this.sets = sets;
        this.reps = reps;
    }

    private Exercise exercise;
    private int sets;
    private int reps;

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
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
