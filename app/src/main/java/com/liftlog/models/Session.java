package com.liftlog.models;

import com.liftlog.data.DataAccessObject;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.MutableDateTime;

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
    private boolean isNew;
    private boolean isModified;
    private boolean isDeleted;
    /**
     * This variable having a value greater than 1 indicates that this is the i'th instance of session that has the same date.
     * (Same day of year, not same millisecond value.)
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

        //clear out instanceCount so that subsequent calls don't increment instanceCount fields more than once
        for(Session session : sessions)
        {
            session.setSequenceNum(0);
        }

        MutableDateTime epoch = new MutableDateTime(0);
        DateTime dateObj;
        Integer value;
//        for(Session session : sessions)
        for(int i = 0; i < sessions.size(); i++)
        {
            Session session = sessions.get(i);

            long date = session.getDate();
            dateObj = new DateTime(date);

            //days since epoch
            long days = Days.daysBetween(epoch, dateObj).getDays();
//            long day = date / (1000 * 60 * 60 * 24);

            value = instanceCount.get(days);
            //if there is already a day mapped to the day, add to it's sequence num
            if(value == null)
            {
                DateTime dt = new DateTime(1l);
                instanceCount.put(days, 1);
            }
            else
            {
                instanceCount.put(days, value + 1);
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

    public static Session findInList(List<Session> sessions, long id)
    {
        if(sessions == null || sessions.isEmpty()) return null;
        for(Session session : sessions)
        {
            if(session.getId() == id)
            {
                return session;
            }
        }
        return null;
    }



    @Override
    public boolean equals(Object o)
    {
        if(o == null) return false;
        if(!(o instanceof Session)) return false;
        Session that = (Session)o;
        return id == that.getId();

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

}
