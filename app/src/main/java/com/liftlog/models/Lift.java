package com.liftlog.models;

import java.util.Comparator;
import java.util.List;

/**
 * Created by James Wierzba on 2/4/2015.
 */
public class Lift implements Comparable<Lift> {


    public Lift()
    {
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
    private double weight;
    private Unit unit;
    private int sets;
    private int reps;
    private boolean isWarmup;
    private long dateCreated;
    private boolean isNew;
    private boolean isModified;
    private boolean isDeleted;

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

    public double getWeight()
    {
        return weight;
    }

    public void setWeight(double weight)
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

    public boolean isNew()
    {
        return isNew;
    }

    public void setNew(boolean isNew)
    {
        this.isNew = isNew;
    }

    public boolean isModified()
    {
        return isModified;
    }

    public void setModified(boolean isModified)
    {
        this.isModified = isModified;
    }

    public boolean isDeleted()
    {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted)
    {
        this.isDeleted = isDeleted;
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

    public static Comparator<Lift> byDateCreated = new Comparator<Lift>()
    {
        @Override
        public int compare(Lift l1, Lift l2) {
            return l1.compareTo(l2);
        }
    };

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

    /**
     * Search the argument list for a lift element whose ID matches the argument id
     * @param lifts The list of Lifts
     * @param id The id to search for
     * @return The Lift that was found, null otherwise
     */
    public static Lift findInList(List<Lift> lifts, long id)
    {
        if(lifts == null || lifts.isEmpty()) return null;
        for(Lift lift : lifts)
        {
            if(lift.getId() == id)
            {
                return lift;
            }
        }
        return null;
    }

    @Override
    public String toString()
    {
        if(id == -1) return "< Add Lift >";
//        Exercise exercise = DataAccessObject.exerciseMap.get(exerciseId);
//        return (exerciseName != null ? exerciseName : "?") + " " + weight + " x " + reps + " x " + sets;
        String result = "";
        if(weight == Math.floor(weight))
        {
            result += (int)weight;
        }
        else
        {
            result += weight;
        }
        result += " x ";
        result += String.valueOf(reps) + (reps == 1 ? " rep" : " reps");
        result += " x ";
        result += String.valueOf(sets) + (sets == 1 ? " set" : " sets");
        return result;
    }

    @Override
    public boolean equals(Object o)
    {
        if(o == null) return false;
        if(!(o instanceof Lift)) return false;
        Lift that = (Lift)o;
        if(id != that.getId()) return false;
        if(exerciseId != that.getExerciseId()) return false;
        if(sessionId != that.getSessionId()) return false;
        if(sets != that.getSets()) return false;
        if(reps != that.getReps()) return false;
        if(weight != that.getWeight()) return false;
        if(isWarmup != that.isWarmup) return false;
        return true;
    }


}
