package wierzba.james.liftlog.models;

import android.database.Cursor;

import java.util.ArrayList;
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
    private int date;
    private List<Lift> lifts;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Lift> getLifts() {
        return lifts;
    }

    public void setLifts(List<Lift> lifts) {
        this.lifts = lifts;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }



}
