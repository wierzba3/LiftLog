package com.liftlog.backend;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by James Wierzba on 2/4/2015.
 */
public class SessionAPI
{

    public SessionAPI()
    {
        lifts = new ArrayList<LiftAPI>();
    }

    private long id;
    private long date;
    private ArrayList<LiftAPI> lifts;
    /**
     * This variable having a value greater than 1 indicates that this is the i'th instance of session that has the same date.
     * (Same day of year, not same millisecond value.
     */
    private int sequenceNum;

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public ArrayList<LiftAPI> getLifts()
    {
        return lifts;
    }

    public void setLifts(ArrayList<LiftAPI> lifts)
    {
        this.lifts = lifts;
    }

    public long getDate()
    {
        return date;
    }

    public void setDate(long date)
    {
        this.date = date;
    }

    public int getSequenceNum()
    {
        return sequenceNum;
    }

    public void setSequenceNum(int sequenceNum)
    {
        this.sequenceNum = sequenceNum;
    }

    @Override
    public String toString()
    {
        if (id < 0) return "< New Session >";

        Date dateObj = new Date(date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("E, MMM dd yyyy");
        String result = dateFormat.format(dateObj);
        if(sequenceNum > 0)
        {
            result += " (" + sequenceNum + ")";
        }
        return result;
    }

    public static final Comparator<SessionAPI> byDateAsc = new Comparator<SessionAPI>()
    {
        @Override
        public int compare(SessionAPI lhs, SessionAPI rhs)
        {
            if(lhs.getDate() < rhs.getDate()) return -1;
            else if(lhs.getDate() > rhs.getDate()) return 1;
            else return 0;
        }
    };
    public static final Comparator<SessionAPI> byDateDesc = new Comparator<SessionAPI>()
    {
        @Override
        public int compare(SessionAPI lhs, SessionAPI rhs)
        {
            if(lhs.getDate() < rhs.getDate()) return 1;
            else if(lhs.getDate() > rhs.getDate()) return -1;
            else return 0;
        }
    };
    public static final Comparator<SessionAPI> byID = new Comparator<SessionAPI>()
    {
        @Override
        public int compare(SessionAPI lhs, SessionAPI rhs)
        {
            if(lhs.getId() < rhs.getId()) return -1;
            else if(lhs.getId() > rhs.getId()) return 1;
            else return 0;
        }
    };

    /**
     * Calculate the Sessions that have the same day of year date value, and assign it's sequenceNum value.
     * @param sessions The List of sessions.
     */
    public static void computeDuplicateDays(List<SessionAPI> sessions)
    {
        if(sessions == null || sessions.size() == 0) return;

        Collections.sort(sessions, byDateAsc);


        Map<Long, Integer> instanceCount = new HashMap<Long, Integer>();

        Integer value;
        for(SessionAPI session : sessions)
        {
            long date = session.getDate();
            long day = date / (1000 * 60 * 60 * 24);
            value = instanceCount.get(day);
            if(value == null)
            {
                instanceCount.put(day, 1);
            }
            else
            {
                instanceCount.put(day, value + 1);
            }
        }

        SessionAPI session;
        for(int i = sessions.size() - 1; i >= 0; i--)
        {
            session = sessions.get(i);
            long date = session.getDate();
            long day = date / (1000 * 60 * 60 * 24);
            value = instanceCount.get(day);
            //if there is more than one Session with the same day value
            if(value != null && value > 1)
            {
                session.setSequenceNum(value);
                instanceCount.put(day, value - 1);
            }
        }
    }


}
