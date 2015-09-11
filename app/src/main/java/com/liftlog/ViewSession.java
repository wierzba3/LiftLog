package com.liftlog;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
//import android.support.v7.app.ActionBar;
//import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import com.liftlog.models.Exercise;
import com.liftlog.models.Lift;
import com.liftlog.data.DataAccessObject;
import com.liftlog.models.Session;

public class ViewSession extends AppCompatActivity
{
//public class ViewSession extends Activity {

    /**
     * The key for this intent's extended data: the id of the session (-1 if new instance)
     */
    public static final String SESSION_ID_KEY = "session_id";

    private static final String LOG_TAG = "LiftLog.ViewSession";

    private DataAccessObject dao;

    private ListView listLifts;
    private ExpandableListView exListLifts;

    private long sessionId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_session);

        dao = new DataAccessObject(this);

        Intent intent = getIntent();
        sessionId = intent.getLongExtra(SESSION_ID_KEY, -1l);

//        Log.d(LOG_TAG, "LIFT_ID_KEY=" + sessionId);


        createContents();
        loadSession(sessionId);
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


        listLifts = (ListView) findViewById(R.id.list_lifts);
//        exListLifts = (ExpandableListView) findViewById(R.id.exList_lifts);



//        listLifts.setOnItemClickListener(new AdapterView.OnItemClickListener()
//        {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position,
//                                    long id)
//            {
//                Lift lift = (Lift) parent.getItemAtPosition(position);
//                doAdd(lift.getId());
//            }
//        });


    }

    private void loadSession(long id)
    {

        Session session = dao.selectSession(sessionId);

        ArrayList<Lift> lifts;
        if (session == null)
        {
            lifts = new ArrayList<Lift>();
        }
        else
        {
            lifts = session.getLifts();
        }
        Collections.sort(lifts);

        Map<Long, Exercise> exerciseMap = dao.selectExercises(false);

        //dummy lift for < Add New > option
        Lift emptyLift = new Lift();
        emptyLift.setId(-1);
        lifts.add(0, emptyLift);

        LiftArrayAdapter liftArrayAdapter = new LiftArrayAdapter(this, R.id.lbl_lift, lifts);
        listLifts.setAdapter(liftArrayAdapter);

//        LiftExpendableListAdapter liftExpandableAdapter = new LiftExpendableListAdapter(this, lifts, exerciseMap);
//        exListLifts.setAdapter(liftExpandableAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        bundle.putLong(SESSION_ID_KEY, sessionId);
    }

    protected void onRestoreInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        long id = bundle.getLong(SESSION_ID_KEY, -1);
        if (id == -1)
        {
            Log.d(LOG_TAG, "Error restoring session.");
            finish();
            return;
        }
        sessionId = id;
    }

    /**
     * Add a new Lift
     *
     * @param liftId The id of the lift (-1 for a new Lift)
     */
    private void doAdd(long liftId)
    {
        Intent intent = new Intent(ViewSession.this, ViewLift.class);
        intent.putExtra(ViewLift.LIFT_ID_KEY, liftId);
        intent.putExtra(ViewLift.SESSION_ID_KEY, sessionId);
        startActivity(intent);
    }


    private void doDelete()
    {
        if (sessionId == -1)
        {
            //this should never happen
            Toast.makeText(this, "Error attempting to delete session.", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Delete Session")
                .setMessage("Are you sure you want to delete this Session? All associated Lifts will also be deleted.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        //can I get the Session object from somewhere previous in the lifecycle ?
                        Session session = dao.selectSession(sessionId);
                        session.setDeleted(true);
                        if (!dao.update(session))
                        {
                            Toast.makeText(ViewSession.this, "Error deleting session.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //successfully deleted
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        if (sessionId > -1)
        {
            loadSession(sessionId);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
//       inflater.inflate(R.menu.menu_view_session, menu);
        inflater.inflate(R.menu.menu_view_session, menu);
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
            case R.id.action_delete:
                doDelete();
                break;
            case R.id.action_add_lift:
                doAdd(-1);
                break;
            case R.id.home:
                break;
            case R.id.action_settings:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private class LiftArrayAdapter extends ArrayAdapter<Lift>
    {

        private ArrayList<Lift> items;

        public LiftArrayAdapter(Context context, int textViewResourceId, ArrayList<Lift> items)
        {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View v = convertView;
            if (v == null)
            {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.lift_item, parent, false);
            }
//            v.setMinimumHeight(150);

            final Lift lift = items.get(position);

            TextView lblLift = (TextView) v.findViewById(R.id.lbl_lift);
            ImageButton btnIncrement = (ImageButton) v.findViewById(R.id.btn_increment);

            if (lift != null)
            {
                if (lblLift != null)
                {
                    lblLift.setText(lift.toString());
                }

                //remove the increment button in the <New Lift> dummy item
                if (lift.getId() == -1)
                {
                    btnIncrement.setVisibility(View.GONE);
                }
            }
            else return null;

            lblLift.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    doAdd(lift.getId());
                }
            });
            btnIncrement.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    lift.setSets(lift.getSets() + 1);
                    dao.update(lift);
                    loadSession(sessionId);
                }
            });

            return v;
        }

    }

    private class LiftExpendableListAdapter extends BaseExpandableListAdapter
    {
        public LiftExpendableListAdapter(Context ctx, List<Lift> allLifts, Map<Long, Exercise> exerciseMap)
        {
            Map<Long, List<Lift>> map = new HashMap<Long, List<Lift>>();

            for(Lift lift : allLifts)

            {

                List<Lift> lifts = map.get(lift.getExerciseId());

                if(lifts == null)

                {

                    lifts = new ArrayList<Lift>();

                }

                lifts.add(lift);

                map.put(lift.getExerciseId(), lifts);

            }



            exercises = new ArrayList<Exercise>();

            liftLists = new ArrayList<List<Lift>>();

            List<Lift> unknownLifts = new ArrayList<Lift>();

            for(long exerciseId : map.keySet())

            {

                List<Lift> lifts = map.get(exerciseId);

                Exercise exercise = exerciseMap.get(exerciseId);

                if(exercise == null)

                {

                    unknownLifts.addAll(lifts);

                    break;

                }

                Collections.sort(lifts);

                exercises.add(exercise);

                liftLists.add(lifts);

            }

            if(unknownLifts.size() > 0)

            {

                Exercise unknown = new Exercise();
                unknown.setName("Unknown");

                exercises.add(unknown);

                liftLists.add(unknownLifts);

            }
        }

        private List<List<Lift>> liftLists;
        private List<Exercise> exercises;

        @Override
        public boolean isChildSelectable(int i, int i1)
        {
            return true;
        }

        @Override
        public int getGroupCount()
        {
            return exercises.size();
        }

        @Override
        public int getChildrenCount(int i)
        {
            int result = 0;
            for (List<Lift> lifts : liftLists)
            {
                if (lifts != null)
                {
                    result += lifts.size();
                }
            }
            return result;
        }

        @Override
        public Object getGroup(int i)
        {
            return exercises.get(i);
        }

        @Override
        public Object getChild(int i, int j)
        {
            return liftLists.get(i).get(j);
        }

        @Override
        public long getGroupId(int i)
        {
            return exercises.get(i).getId();
        }

        @Override
        public long getChildId(int i, int j)
        {
            return liftLists.get(i).get(j).getId();
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

            Exercise exercise = exercises.get(i);
            TextView lblExercise = (TextView) view.findViewById(R.id.lbl_lift_group);
            lblExercise.setText(exercise.getName());

            return null;
        }

        @Override
        public View getChildView(int i, int j, boolean b, View view, ViewGroup viewGroup)
        {
            if (view == null)
            {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = vi.inflate(R.layout.lift_item, viewGroup, false);
            }

            final Lift lift = liftLists.get(i).get(j);


            TextView lblLift = (TextView) view.findViewById(R.id.lbl_lift);
            lblLift.setText(lift.toString());

            ImageButton btnIncrement = (ImageButton) view.findViewById(R.id.btn_increment);
            btnIncrement.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    lift.setSets(lift.getSets() + 1);
                    dao.update(lift);
                    loadSession(sessionId);
                }
            });

            return view;
        }
    }

}
