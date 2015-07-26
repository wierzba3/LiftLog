package com.liftlog.models;

import java.util.Comparator;

/**
 * Created by James Wierzba on 2/8/2015.
 */
public class Exercise {


    public Exercise()
    {

    }

    public Exercise(long id, String name, String description){
        this.name = name;
        this.description = description;
    }

    private long id;
    private String name;
    private String description;
    private boolean valid = true;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isValid()
    {
        return valid;
    }

    public void setValid(boolean valid)
    {
        this.valid = valid;
    }

    public static final Exercise Squat(){
        String name = "Squat";
        String desc = "From rack with barbell at upper chest height, position bar high on back of shoulders and grasp barbell to sides. " +
                "Dismount bar from rack and stand with shoulder width stance. " +
                "Squat down by bending hips back while allowing knees to bend forward, " +
                "keeping back straight and knees pointed same direction as feet. " +
                "Descend until thighs are just past parallel to floor. Extend knees and hips until legs are straight. Return and repeat." +
                "Source: http://www.exrx.net/WeightExercises/Quadriceps/BBSquat.html";
        Exercise result = new Exercise(-1, name, desc);
        return result;
    }

    public static final Exercise BenchPress(){
        String name = "Bench Press";
        String desc = "Lie supine on bench. Dismount barbell from rack over upper chest using wide oblique overhand grip. " +
                "Lower weight to mid-chest. Press bar upward until arms are extended. Repeat.   " +
                "Source: http://www.exrx.net/WeightExercises/PectoralSternal/BBBenchPress.html";
        Exercise result = new Exercise(-1, name, desc);
        return result;
    }

    public static final Exercise Deadlift(){
        String name = "Deadlift";
        String desc = "With feet flat beneath bar, squat down and grasp bar with shoulder width or slightly wider overhand or mixed grip. " +
                "Lift bar by extending hips and knees to full extension. Pull shoulders back at top of lift if rounded. Return and repeat. " +
                "Source: http://www.exrx.net/WeightExercises/ErectorSpinae/BBDeadlift.html";
        Exercise result = new Exercise(-1, name, desc);
        return result;
    }

    public static final Exercise Press(){
        String name = "Overhead Press";
        String desc = "Grasp barbell from rack or clean barbell from floor with overhand grip, slightly wider than shoulder width. Position bar in front of neck. " +
                "Press bar upward until arms are extended overhead. Lower to front of neck and repeat. " +
                "Source: http://www.exrx.net/WeightExercises/DeltoidAnterior/BBMilitaryPress.html";
        Exercise result = new Exercise(-1, name, desc);
        return result;
    }

    /**
     * Compare by name, and place dummy "&lt;Add New&gt;" first
     */
    public static final Comparator<Exercise> byNameDummyFirst = new Comparator<Exercise>()
    {
        @Override
        public int compare(Exercise lhs, Exercise rhs)
        {
            if(lhs == null && rhs == null) return 0;
            if(lhs == null) return -1;
            if(rhs == null) return 1;
            if(lhs.getId() == -1) return -1;
            return lhs.getName().compareTo(rhs.getName());
        }
    };
    /**
     * Compare by name, and place dummy "&lt;Add New&gt;" last
     */
    public static final Comparator<Exercise> byNameDummyLast = new Comparator<Exercise>()
    {
        @Override
        public int compare(Exercise lhs, Exercise rhs)
        {
            if(lhs == null && rhs == null) return 0;
            if(lhs == null) return -1;
            if(rhs == null) return 1;
            if(lhs.getId() == -1) return 1;
            return lhs.getName().compareToIgnoreCase(rhs.getName());
        }
    };

    @Override
    public String toString()
    {
        return name;
    }



}
