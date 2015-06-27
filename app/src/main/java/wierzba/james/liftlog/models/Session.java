package wierzba.james.liftlog.models;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import wierzba.james.liftlog.DataAccessObject;

/**
 * Created by James Wierzba on 2/4/2015.
 */
public class Session {

    public Session()
    {
        lifts = new ArrayList<Lift>();
    }

    private long id;
    private long date;
    private ArrayList<Lift> lifts;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ArrayList<Lift> getLifts() {
        return lifts;
    }

    public void setLifts(ArrayList<Lift> lifts) {
        this.lifts = lifts;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public String toString()
    {
        if(id < 0) return "< New Session >";
        Date dateObj = new Date(date);
        return dateObj.toString();
    }


}
