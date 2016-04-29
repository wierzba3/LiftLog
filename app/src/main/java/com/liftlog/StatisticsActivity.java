package com.liftlog;

import android.graphics.Typeface;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.liftlog.common.Util;
import com.liftlog.data.DataAccessObject;
import com.liftlog.models.Exercise;
import com.liftlog.models.Lift;
import com.liftlog.models.Session;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsActivity extends AppCompatActivity
{

    private static final String LOG_TAG = "StatisticsActivity";

    private DataAccessObject dao;
    private TableLayout tbl;

    private Spinner spnStatisticChoice;
    private Spinner spnExercise;

    private Shape border;

    private enum StatisticChoice
    {
        WEEKLY_VOLUME("Volume per week");

        StatisticChoice(String display)
        {
            this.display = display;
        }

        private String display;

        @Override
        public String toString()
        {
            return display;
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        dao = new DataAccessObject(this);

        createContents();
    }


    private void createContents()
    {
        tbl = (TableLayout) findViewById(R.id.tbl_statistics);

        spnExercise = (Spinner) findViewById(R.id.spn_exercise_statistics);

        Map<Long, Exercise> exercises = dao.selectExerciseMap(false);
        if(exercises != null)
        {
            List<Exercise> exerciseList = new ArrayList<>(exercises.values());
            Collections.sort(exerciseList, Exercise.byNameDummyLast);

            ArrayAdapter<Exercise> adapterExercise = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, exerciseList);
            adapterExercise.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            spnExercise.setAdapter(adapterExercise);
        }

        spnStatisticChoice = (Spinner) findViewById(R.id.spn_statistic_choice);
        ArrayAdapter<StatisticChoice> adapterStatChoice = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, StatisticChoice.values());
        adapterStatChoice.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spnStatisticChoice.setAdapter(adapterStatChoice);


    }



    public void onCalculateClick(View view)
    {
        //TODO populate table with data
        //see tutorial http://www.warriorpoint.com/blog/2009/07/01/android-creating-tablerow-rows-inside-a-tablelayout-programatically/

        Object selectedExerciseObject = spnExercise.getSelectedItem();
        if(selectedExerciseObject == null)
        {
            Toast.makeText(this, "Error: no exercise selected.", Toast.LENGTH_SHORT).show();
            return;
        }
        Exercise selectedExercise = (Exercise) selectedExerciseObject;
        List<StatisticRowItem> rowItems = selectVolumePerWeek(selectedExercise.getId());
        Collections.sort(rowItems, Collections.<StatisticRowItem>reverseOrder());
        populateTableLayout(rowItems);

    }

    private void populateTableLayout(List<StatisticRowItem> rowItems)
    {
        tbl.removeAllViewsInLayout();
        if(rowItems == null || rowItems.isEmpty()) return;

        //add header column to TableLayout:
        TableRow headerRow = new TableRow(this);
        headerRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        headerRow.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView txtHeaderCol1 = new TextView(this);
        txtHeaderCol1.setText("Dates");
        txtHeaderCol1.setTypeface(null, Typeface.BOLD);
        txtHeaderCol1.setGravity(Gravity.CENTER_HORIZONTAL);
        txtHeaderCol1.setBackgroundResource(R.drawable.border_2dp);
        headerRow.addView(txtHeaderCol1);

        TextView txtHeaderCol2 = new TextView(this);
        txtHeaderCol2.setText("Total Volume");
        txtHeaderCol2.setTypeface(null, Typeface.BOLD);
        txtHeaderCol2.setGravity(Gravity.CENTER_HORIZONTAL);
        txtHeaderCol2.setBackgroundResource(R.drawable.border_2dp);
        headerRow.addView(txtHeaderCol2);

        tbl.addView(headerRow);

        //add new line to TableLayout:
        final View vlineHeader = new View(this);
        vlineHeader.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 2));
        vlineHeader.setBackgroundColor(getResources().getColor(R.color.material_blue_300));
        tbl.addView(vlineHeader);

        for(StatisticRowItem rowItem : rowItems)
        {
            //add a new row to the TableLayout
            TableRow row = new TableRow(this);
            row.setGravity(Gravity.CENTER_HORIZONTAL);

            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));

            TextView txtCol1 = new TextView(this);
            txtCol1.setText(rowItem.getItem1());
            txtCol1.setBackgroundResource(R.drawable.border_1dp);
            txtCol1.setGravity(Gravity.CENTER_HORIZONTAL);
            row.addView(txtCol1);

            TextView txtCol2 = new TextView(this);
            txtCol2.setText(rowItem.getItem2());
            txtCol2.setGravity(Gravity.CENTER_HORIZONTAL);
            txtCol2.setBackgroundResource(R.drawable.border_1dp);
            row.addView(txtCol2);

            tbl.addView(row);

            //add a new line to the TableLayout:
            final View vline = new View(this);

            vline.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 2));
            vline.setBackgroundColor(getResources().getColor(R.color.material_blue_300));
            tbl.addView(vline);
        }
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
        //I've created an algorithm in my eclipse test project, TODO copy it over here
        for(Session session : sessions)
        {

        }

        MutableDateTime dt = MutableDateTime.now();
        dt.setHourOfDay(0);
        dt.setMinuteOfDay(0);
        dt.setSecondOfDay(0);
        //decrement day until dt.getDayOfWeek == 1 (for a maximum of 7 iterations to prevent an infinite loop... I don't trust the library!
        for(int i = 0; i < 8; i++)
        {
            if(dt.getDayOfWeek() == 1) break;
            dt.addDays(-1);
        }

        Map<DateTime, Integer> weeklyVolumeMap = new HashMap<>();
        //we know that the Sessions are sorted chronologically in descending order
        //List<Session> sessions = generateTestSessions();
        int currentTotal = 0;
        for(int i = 0; i < sessions.size(); i++)
        {
            Session session = sessions.get(i);
            //System.out.println("iterate over session " + DATE_FORMAT_SHORT.print(new DateTime(session.millis)));
            //we moved passed this week into the previous week
            if(session.getDate()  < dt.getMillis())
            {
                //add the accumulated total to the map for this week
                weeklyVolumeMap.put(new DateTime(dt.getMillis()), currentTotal);
                //shift dt backwards one week
                dt.addDays(-7);
                //reset accumulated total volume
                currentTotal = 0;
            }
            //add all of the volume to the current total
            for(Lift lift : session.getLifts())
            {
                currentTotal += (lift.getReps() * lift.getWeight());
            }
        }
        //add the remainder of the sessions in the last week
        if(currentTotal > 0) weeklyVolumeMap.put(new DateTime(dt.getMillis()), currentTotal);

        List<StatisticRowItem> result = new ArrayList<>();
        for(Map.Entry<DateTime, Integer> entry : weeklyVolumeMap.entrySet())
        {
            StatisticRowItem rowItem = new StatisticRowItem();
            DateTime dt1 = entry.getKey();
            DateTime dt2 = dt1.plusDays(6);
            String dateStr = Util.DATE_FORMAT_SHORT.print(dt1) + " - " + Util.DATE_FORMAT_SHORT.print(dt2);
            rowItem.setItem1(dateStr);
            rowItem.setItem2(String.valueOf(entry.getValue()));
            rowItem.setId(dt1.getMillis());
            result.add(rowItem);
        }

        return result;
    }



    private class StatisticRowItem implements Comparable<StatisticRowItem>
    {
        private long id;
        private String item1;
        private String item2;

        public void setId(long newValue) { id = newValue; }
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
        public long getId()
        {
            return id;
        }


        @Override
        public int compareTo(StatisticRowItem other)
        {
            if(id < other.getId()) return -1;
            else if(id > other.getId()) return 1;
            else return 0;
        }

    }


}
