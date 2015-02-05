package wierzba.james.liftlog.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by James Wierzba on 2/4/2015.
 */
public class TrainingSession {

    public TrainingSession()
    {
        lifts = new ArrayList<Lift>();
    }

    private List<Lift> lifts;



}
