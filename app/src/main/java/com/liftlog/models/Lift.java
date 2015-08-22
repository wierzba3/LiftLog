package com.liftlog.models;

import com.liftlog.backend.myApi.model.LiftAPI;
import com.liftlog.data.DataAccessObject;

/**
 * Created by James Wierzba on 2/4/2015.
 */
public class Lift implements Comparable<Lift> {


    public Lift(DataAccessObject.RecordState state)
    {
        this.state = state;
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
    private String exerciseName;
    private int weight;
    private Unit unit;
    private int sets;
    private int reps;
    private boolean isWarmup;
    private long dateCreated;
    private DataAccessObject.RecordState state;

    public long getSessionId()
    {
        return sessionId;
    }

    public void setSessionId(long sessionId)
    {
        this.sessionId = sessionId;
    }

    public long getExerciseId()
    {
        return exerciseId;
    }

    public void setExerciseId(long exerciseId)
    {
        this.exerciseId = exerciseId;
    }

    public String getExerciseName()
    {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName)
    {
        this.exerciseName = exerciseName;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public int getSets()
    {
        return sets;
    }

    public void setSets(int sets)
    {
        this.sets = sets;
    }

    public int getReps()
    {
        return reps;
    }

    public void setReps(int reps)
    {
        this.reps = reps;
    }

    public Unit getUnit()
    {
        return unit;
    }

    public void setUnit(Unit unit)
    {
        this.unit = unit;
    }

    public int getWeight()
    {
        return weight;
    }

    public void setWeight(int weight)
    {
        this.weight = weight;
    }

    public boolean isWarmup()
    {
        return isWarmup;
    }

    public void setWarmup(boolean isWarmup)
    {
        this.isWarmup = isWarmup;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public DataAccessObject.RecordState getState()
    {
        return state;
    }

    public void setState(DataAccessObject.RecordState state)
    {
        this.state = state;
    }

    //    public long getDate() {
//        return date;
//    }
//
//    public void setDate(long date) {
//        this.date = date;
//    }



    //default sort by date
    @Override
    public int compareTo(Lift other)
    {
        if(other == null) return 1;
        if(dateCreated > other.getDateCreated()) return 1;
        else if(dateCreated < other.getDateCreated()) return -1;
        else return 0;
    }

//    @Override
//    public int compareTo(Lift other)
//    {
//        if(other == null) return 1;
//        //if the names are not equal, sort by name
//        if(exerciseName != null && !exerciseName.equals(other.getExerciseName()))
//        {
//            return exerciseName.compareTo(other.getExerciseName());
//        }
//        //else sort by the weight
//        else
//        {
//            if(weight > other.getWeight()) return 1;
//            else if(weight < other.getWeight()) return -1;
//            else return 0;
//        }
//    }

    @Override
    public String toString()
    {
        if(id == -1) return "< Add Lift >";
//        Exercise exercise = DataAccessObject.exerciseMap.get(exerciseId);
        return (exerciseName != null ? exerciseName : "?") + " " + weight + " x " + reps + " x " + sets;
    }

    @Override
    public boolean equals(Object o)
    {
        if(o == null) return false;
        if(o instanceof Lift)
        {
            Lift that = (Lift)o;
            return id == that.getId();
        }
        else if(o instanceof LiftAPI)
        {
            LiftAPI that = (LiftAPI)o;
            return id == that.getId();
        }
        else return false;
    }


}
