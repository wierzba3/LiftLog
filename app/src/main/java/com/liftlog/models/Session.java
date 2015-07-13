package com.liftlog.models;

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
public class Session
{

    public Session()
    {
        lifts = new ArrayList<Lift>();
    }

    private long id;
    private long date;
    private ArrayList<Lift> lifts;
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

    public ArrayList<Lift> getLifts()
    {
        return lifts;
    }

    public void setLifts(ArrayList<Lift> lifts)
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

    public static final Comparator<Session> byDateAsc = new Comparator<Session>()
    {
        @Override
        public int compare(Session lhs, Session rhs)
        {
            if(lhs.getDate() < rhs.getDate()) return -1;
            else if(lhs.getDate() > rhs.getDate()) return 1;
            else return 0;
        }
    };
    public static final Comparator<Session> byDateDesc = new Comparator<Session>()
    {
        @Override
        public int compare(Session lhs, Session rhs)
        {
            if(lhs.getDate() < rhs.getDate()) return 1;
            else if(lhs.getDate() > rhs.getDate()) return -1;
            else return 0;
        }
    };
    public static final Comparator<Session> byID = new Comparator<Session>()
    {
        @Override
        public int compare(Session lhs, Session rhs)
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
    public static void computeDuplicateDays(List<Session> sessions)
    {
        if(sessions == null || sessions.size() == 0) return;

        Collections.sort(sessions, byDateAsc);


        Map<Long, Integer> instanceCount = new HashMap<Long, Integer>();

        Integer value;
        for(Session session : sessions)
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

        Session session;
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
