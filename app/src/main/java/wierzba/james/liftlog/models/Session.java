package wierzba.james.liftlog.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by James Wierzba on 2/4/2015.
 */
public class Session {

    public Session()
    {
        lifts = new ArrayList<Lift>();
    }

    private List<Lift> lifts;
    private int date;

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
