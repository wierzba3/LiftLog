package com.liftlog;

import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.CollapsibleActionView;
import android.view.View;
import android.widget.TableLayout;

import com.liftlog.data.DataAccessObject;
import com.liftlog.models.Lift;
import com.liftlog.models.Session;

import org.joda.time.DateTime;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsActivity extends AppCompatActivity
{

    private DataAccessObject dao;
    private TableLayout tbl;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
    }


    private void createContents()
    {
        tbl = (TableLayout) findViewById(R.id.tbl_statistics);
    }



    private void onCalculateClick(View view)
    {
        //TODO populate table with data
        //see tutorial http://www.warriorpoint.com/blog/2009/07/01/android-creating-tablerow-rows-inside-a-tablelayout-programatically/


    }


    private List<StatisticRowItem> selectVolumePerWeek(long exerciseId)
    {
        //List<Lift> allLifts = dao.selectLifts(true);

        //select all sessions and lifts for the specified exercise
        List<Session> sessions = dao.selectSessionsByExercise(exerciseId);
        if(sessions == null || sessions.isEmpty()) return null;

        //sort chronologically starting from the most recent
        Collections.sort(sessions, Session.byDateDesc);

        //Map of ["week of" date -> list of sessions occuring during that week]
        Map<DateTime, List<Session>> weeklySessions = new HashMap<>();


        //TODO need to map each session in a list to the week it is in
        //creating and testing algorithm in separate environment would be easiest
        for(Session session : sessions)
        {

        }

        return null;
    }



    private class StatisticRowItem
    {
        private String item1;
        private String item2;

        public void setItem1(String newValue)
        {
            item1 = newValue;
        }
        public void setItem2(String newValue)
        {
            item2 = newValue;
        }

        public String getItem1()
        {
            return item1;
        }

        public String getItem2()
        {
            return item2;
        }



    }


}
