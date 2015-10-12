package com.liftlog;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.liftlog.data.DataAccessObject;
import com.liftlog.models.Exercise;
import com.liftlog.models.Lift;
import com.liftlog.models.Session;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ViewHistoryActivity extends AppCompatActivity
{
//public class ViewSessionActivity extends Activity {

    /**
     * The key for this intent's extended data: the id of the session (-1 if new instance)
     */
    public static final String EXERCISE_ID_KEY = "exercise_id";

    private static final String LOG_TAG = "LiftLog.ViewHistoryActivity";

    private DataAccessObject dao;

    private ExpandableListView exListHistory;
    private TextView lblEmpty;

    private long exerciseId = -1;
    private String exerciseName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_history);

        dao = new DataAccessObject(this);

        Intent intent = getIntent();
        exerciseId = intent.getLongExtra(EXERCISE_ID_KEY, -1l);

        createContents();

        //no exercise was provided, prompt for user to choose an exercise
        if(exerciseId == -1)
        {
            promptForExercise();
            return;
        }
        else
        {
            Exercise exercise = dao.selectExercise(exerciseId);
            if(exercise == null)
            {
                Toast.makeText(this, "Error: Exercise does not exist.", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            exerciseName = exercise.getName();
            this.setTitle(exerciseName);
        }

        loadLifts(exerciseId);
    }

    private void promptForExercise()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Exercise");

        final Spinner input = new Spinner(this);

        final List<Exercise> exercises = dao.selectExercises(false);
        if(exercises == null)
        {
            Toast.makeText(this, "Error loading exercises.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if(exercises.size() == 0)
        {
            Toast.makeText(this, "No exercises exist to view.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        ArrayAdapter<Exercise> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, exercises);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        input.setAdapter(spinnerAdapter);

        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Exercise exercise = (Exercise) input.getSelectedItem();
                if (exercise != null)
                {
                    ViewHistoryActivity.this.exerciseId = exercise.getId();
                    ViewHistoryActivity.this.exerciseName = exercise.getName();
                    loadLifts(exercise.getId());
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                finish();
            }
        });
        //if user exits dialog, exit the activity because there is no chosen exercise history to display
        builder.setOnKeyListener(new DialogInterface.OnKeyListener()
        {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent)
            {
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK)
                {
                    finish();
                }
                return false;
            }
        });
        builder.show();
    }

    private void createContents()
    {

        ActionBar actionBar = this.getActionBar();
//        if(actionBar == null) actionBar =
        if (actionBar != null)
        {
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.show();
        }


        exListHistory = (ExpandableListView) findViewById(R.id.exList_history);
        lblEmpty = (TextView) findViewById(R.id.lbl_empty);

    }

    private void loadLifts(long exerciseId)
    {
        List<Session> sessions = dao.selectSessionsByExercise(exerciseId);
        if(sessions == null || sessions.isEmpty())
        {
            lblEmpty.setText("No history to display");
            return;
        }

        HistoryExpendableListAdapter liftExpandableAdapter = new HistoryExpendableListAdapter(this, sessions);
        try {
            exListHistory.setAdapter(liftExpandableAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for(int i = 0; i < liftExpandableAdapter.getGroupCount(); i++)
        {
            exListHistory.expandGroup(i);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        bundle.putLong(EXERCISE_ID_KEY, exerciseId);
    }

    protected void onRestoreInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        long id = bundle.getLong(EXERCISE_ID_KEY, -1);
        if (id == -1)
        {
            Log.d(LOG_TAG, "Error restoring session.");
            finish();
            return;
        }
        exerciseId = id;
    }

    /**
     * Add a new Lift
     *
     * @param liftId The id of the lift (-1 for a new Lift)
     */
    private void doEditLift(long liftId, long sessionId)
    {
        Intent intent = new Intent(ViewHistoryActivity.this, ViewLiftActivity.class);
        intent.putExtra(ViewLiftActivity.LIFT_ID_KEY, liftId);
        intent.putExtra(ViewLiftActivity.SESSION_ID_KEY, sessionId);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1)
        {
            if(resultCode == RESULT_OK)
            {
                long id = data.getLongExtra(EXERCISE_ID_KEY, -1);
                String result = data.getStringExtra("result");
            }
            if (resultCode == RESULT_CANCELED)
            {
                //Write your code if there's no result
            }
        }
    }


//    private void doDelete()
//    {
//        if (exer == -1)
//        {
//            //this should never happen
//            Toast.makeText(this, "Error attempting to delete session.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        new AlertDialog.Builder(this)
//                .setIcon(android.R.drawable.ic_dialog_alert)
//                .setTitle("Delete Session")
//                .setMessage("Are you sure you want to delete this Session? All associated Lifts will also be deleted.")
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
//                {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which)
//                    {
//                        //can I get the Session object from somewhere previous in the lifecycle ?
//                        Session session = dao.selectSession(sessionId);
//                        session.setDeleted(true);
//                        if (!dao.update(session))
//                        {
//                            Toast.makeText(ViewHistoryActivity.this, "Error deleting session.", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                        //successfully deleted
//                        finish();
//                    }
//
//                })
//                .setNegativeButton("No", null)
//                .show();
//    }


    @Override
    protected void onResume()
    {
        super.onResume();
        Intent intent = getIntent();
        exerciseId = intent.getLongExtra(EXERCISE_ID_KEY, -1l);
        if (exerciseId > -1)
        {
            loadLifts(exerciseId);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        //TODO create a menu for this activity
//       inflater.inflate(R.menu.menu_view_session, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.home:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

//

    /**
        Encapsulates the elements of the ExpandableListView (Lifts categorized by the exercise type)
     */
    private class HistoryGroupElement
    {

        public HistoryGroupElement()
        {
        }

        private long id;
        private DateTime date;
        private List<Lift> lifts;

        public void setId(long value)
        {
            id = value;
        }
        public long getId()
        {
            return id;
        }


        public void setLifts(List<Lift> value)
        {
            lifts = value;
        }
        public List<Lift> getLifts()
        {
            return lifts;
        }

        public DateTime getDate()
        {
            return date;
        }
        public void setDate(DateTime value)
        {
            date = value;
        }

    }


    private class HistoryExpendableListAdapter extends BaseExpandableListAdapter
    {

        public HistoryExpendableListAdapter(Context ctx, List<Session> sessions)
        {
            elements = new ArrayList<HistoryGroupElement>();

            if(sessions == null) return;

            for(Session session : sessions)
            {
                if(session == null) continue;
                HistoryGroupElement element = new HistoryGroupElement();
                DateTime date = new DateTime(session.getDate());

                element.setId(session.getId());
                element.setDate(date);
                element.setLifts(session.getLifts());
                Collections.sort(element.getLifts(), Lift.byDateCreated);
                elements.add(element);
            }

            Collections.sort(elements, comparator);
        }


        private Comparator<List<Lift>> liftsComparator = new Comparator<List<Lift>>(){
            @Override
            public int compare(List<Lift> l1, List<Lift> l2)
            {
                if((l1 == null || l1.size() == 0) && (l2 == null || l2.size() == 0)) return 0;
                else if(l1 == null || l1.size() == 0) return -1;
                else if(l2 == null || l2.size() == 0) return 1;

                int result = 0;
                long minDate = Long.MAX_VALUE;
                for(Lift lift : l1)
                {
                    if(lift.getDateCreated() < minDate)
                    {
                        minDate = lift.getDateCreated();
                        result = -1;
                    }
                }
                for(Lift lift : l2)
                {
                    if(lift.getDateCreated() < minDate)
                    {
                        minDate = lift.getDateCreated();
                        result = 1;
                    }
                }
                return result;
            }
        };

        private Comparator<HistoryGroupElement> comparator = new Comparator<HistoryGroupElement>(){
            @Override
            public int compare(HistoryGroupElement e1, HistoryGroupElement e2)
            {
                long d1 = e1.getDate().getMillis();
                long d2 = e2.getDate().getMillis();
                if(d1 < d2) return 1;
                else if(d1 > d2) return -1;
                else return 0;
            }
        };

        private List<HistoryGroupElement> elements;

        @Override
        public boolean isChildSelectable(int i, int i1)
        {
            return true;
        }

        @Override
        public int getGroupCount()
        {
            return elements.size();
        }

        @Override
        public int getChildrenCount(int i)
        {
            HistoryGroupElement e = elements.get(i);
            if(e == null || e.getLifts() == null) return 0;
            return e.getLifts().size();
        }

        @Override
        public Object getGroup(int i)
        {
            return elements.get(i);
        }

        @Override
        public Object getChild(int i, int j)
        {
            return elements.get(i).getLifts().get(j);
        }

        @Override
        public long getGroupId(int i)
        {
            return elements.get(i).getDate().getMillis();
        }

        @Override
        public long getChildId(int i, int j)
        {
            return elements.get(i).getLifts().get(j).getId();
        }

        @Override
        public boolean hasStableIds()
        {
            return false;
        }

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup)
        {
            if (view == null)
            {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = vi.inflate(R.layout.lift_group_item, viewGroup, false);
            }

            //Exercise exercise = exercises.get(i);
            DateTime date = elements.get(i).getDate();
            DateTime dt = new DateTime(date);
            DateTimeFormatter dtf = DateTimeFormat.forPattern("E, MMM dd yyyy");
            String dateString = dtf.print(dt);
            TextView lblExercise = (TextView) view.findViewById(R.id.lbl_lift_group);
            lblExercise.setText(dateString);


            return view;
        }

        @Override
        public View getChildView(int i, int j, boolean b, View view, ViewGroup viewGroup)
        {
            if (view == null)
            {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = vi.inflate(R.layout.history_item, viewGroup, false);
            }

            final Lift lift = elements.get(i).getLifts().get(j);

            TextView lblLift = (TextView) view.findViewById(R.id.lbl_lift_history);
            lblLift.setText(lift.toString());

            lblLift.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    doEditLift(lift.getId(), lift.getSessionId());
                }
            });

            return view;
        }
    }

}
