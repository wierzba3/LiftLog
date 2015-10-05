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
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


//        listLifts = (ListView) findViewById(R.id.list_lifts);
        exListLifts = (ExpandableListView) findViewById(R.id.exList_lifts);


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
            String lbl = session.toString();
            setTitle(session.toString());
        }
        Collections.sort(lifts);

        Map<Long, Exercise> exerciseMap = dao.selectExerciseMap(false);

        //dummy lift for < Add New > option
        Lift emptyLift = new Lift();
        emptyLift.setId(-1);
        lifts.add(0, emptyLift);

//        LiftArrayAdapter liftArrayAdapter = new LtArrayAdapter(this, R.id.lbl_lift, lifts);
//        listLifts.setAdapter(liftArrayAdapter);if

        LiftExpendableListAdapter liftExpandableAdapter = new LiftExpendableListAdapter(this, lifts, exerciseMap);
        exListLifts.setAdapter(liftExpandableAdapter);

        for(int i = 0; i < liftExpandableAdapter.getGroupCount(); i++)
        {
            exListLifts.expandGroup(i);
        }
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
//        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1)
        {
            if(resultCode == RESULT_OK)
            {
                long id = data.getLongExtra(SESSION_ID_KEY, -1);
                String result = data.getStringExtra("result");
            }
            if (resultCode == RESULT_CANCELED)
            {
                //Write your code if there's no result
            }
        }
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
        Intent intent = getIntent();
        sessionId = intent.getLongExtra(SESSION_ID_KEY, -1l);
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
        }

        return super.onOptionsItemSelected(item);
    }

//

	/**
		Encapsulates the elements of the ExpandableListView (Lifts categorized by the exercise type)
	*/
    private class LiftGroupElement
    {

        public LiftGroupElement()
        {
        }


        private Exercise exercise;
        private List<Lift> lifts;
		
		public void setLifts(List<Lift> value)
		{
			lifts = value;
		}
		public List<Lift> getLifts()
		{
			return lifts;
		}
		
		public Exercise getExercise()
		{
			return exercise;
		}
		public void setExercise(Exercise value)
		{
			exercise = value;
		}

    }


    private class LiftExpendableListAdapter extends BaseExpandableListAdapter
    {
		
		//TODO use this constructor instead
		public LiftExpendableListAdapter(Context ctx, List<Lift> allLifts, Map<Long, Exercise> exerciseMap)
		{
			elements = new ArrayList<LiftGroupElement>();
			
            if(allLifts == null) return;
			
			//map the exercise ID to the list of lifts whose ID is equal to it
            Map<Long, List<Lift>> map = new HashMap<Long, List<Lift>>();
            for(Lift lift : allLifts)
            {
                if(lift.getId() < 0) continue;
                List<Lift> lifts = map.get(lift.getExerciseId());

                if(lifts == null)
                {
                    lifts = new ArrayList<Lift>();
                }
                lifts.add(lift);

                map.put(lift.getExerciseId(), lifts);
            }
			
			LiftGroupElement uncategorized = null;
			Exercise dummy = new Exercise();
			dummy.setId(-1l);
			dummy.setName("Uncategorized");
			for(long exerciseId : map.keySet())
            {
				
				List<Lift> lifts = map.get(exerciseId);
				if(lifts == null || lifts.size() == 0) continue;
				if(exerciseId == -1)
				{
					uncategorized = new LiftGroupElement();
					uncategorized.setLifts(lifts);
					uncategorized.setExercise(dummy);
					elements.add(uncategorized);
				}
				else
				{
					LiftGroupElement element = new LiftGroupElement();
					Exercise exercise = exerciseMap.get(exerciseId);
					element.setExercise(exercise);
					element.setLifts(lifts);
					elements.add(element);
				}
			}
			
			Collections.sort(elements, comparator);
		}
		
        //public LiftExpendableListAdapter(Context ctx, List<Lift> allLifts, Map<Long, Exercise> exerciseMap)
//{
//    exercises = new ArrayList<Exercise>();
//    liftLists = new ArrayList<List<Lift>>();
//
//    if(allLifts == null) return;
//    Collections.sort(allLifts);
//
//    Map<Long, List<Lift>> map = new HashMap<Long, List<Lift>>();
//    for(Lift lift : allLifts)
//    {
//        if(lift.getId() < 0) continue;
//        List<Lift> lifts = map.get(lift.getExerciseId());
//
//        if(lifts == null)
//
//        {
//
//            lifts = new ArrayList<Lift>();
//
//        }
//
//        lifts.add(lift);
//
//        map.put(lift.getExerciseId(), lifts);
//    }
//
//    List<Lift> unknownLifts = new ArrayList<Lift>();
//
//    for(long exerciseId : map.keySet())
//    {
//
//        List<Lift> lifts = map.get(exerciseId);
//
//        Exercise exercise = exerciseMap.get(exerciseId);
//
//        if(exercise == null)
//
//        {
//
//            unknownLifts.addAll(lifts);
//
//            break;
//
//        }
//
//        Collections.sort(lifts);
//
//        liftLists.add(lifts);
//
//    }
//
//    Collections.sort(liftLists, liftsComparator);
//    for(List<Lift> lifts : liftLists)
//    {
//        if(lifts != null && !lifts.isEmpty())
//        {
//            long exerciseId = lifts.get(0).getExerciseId();
//            Exercise exercise = exerciseMap.get(exerciseId);
//            exercises.add(exercise);
//        }
//    }
//
//    if(unknownLifts.size() > 0)
//
//    {
//
//        Exercise unknown = new Exercise();
//        unknown.setName("Unknown");
//
//        exercises.add(unknown);
//
//        liftLists.add(unknownLifts);
//    }
//
//
//}

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
		
        private Comparator<LiftGroupElement> comparator = new Comparator<LiftGroupElement>(){
            @Override
            public int compare(LiftGroupElement e1, LiftGroupElement e2)
            {
                List<Lift> l1 = e1.getLifts();
                List<Lift> l2 = e2.getLifts();
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
		

        //private List<List<Lift>> liftLists;
        //private List<Exercise> exercises;
        private List<LiftGroupElement> elements;

        @Override
        public boolean isChildSelectable(int i, int i1)
        {
            return true;
        }

        @Override
        public int getGroupCount()
        {
            //return exercises.size();
			return elements.size();
        }

        @Override
        public int getChildrenCount(int i)
        {
            //List<Lift> lifts = liftLists.get(i);
            //if(lifts == null) return 0;
            //return lifts.size();
			LiftGroupElement e = elements.get(i);
			if(e == null || e.getLifts() == null) return 0;
			return e.getLifts().size();
        }

        @Override
        public Object getGroup(int i)
        {
            //return exercises.get(i);
			return elements.get(i);
        }

        @Override
        public Object getChild(int i, int j)
        {
			//return liftLists.get(i).get(j);
			return elements.get(i).getLifts().get(j);
        }

        @Override
        public long getGroupId(int i)
        {
            //return exercises.get(i).getId();
			return elements.get(i).getExercise().getId();
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
			Exercise exercise = elements.get(i).getExercise();
            TextView lblExercise = (TextView) view.findViewById(R.id.lbl_lift_group);
            lblExercise.setText(exercise.getName());


            return view;
        }

        @Override
        public View getChildView(int i, int j, boolean b, View view, ViewGroup viewGroup)
        {
            if (view == null)
            {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = vi.inflate(R.layout.lift_item, viewGroup, false);
            }

            //final Lift lift = liftLists.get(i).get(j);
			final Lift lift = elements.get(i).getLifts().get(j);

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
            lblLift.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    doAdd(lift.getId());
                }
            });

            return view;
        }
    }

//    private class LiftArrayAdapter extends ArrayAdapter<Lift>
//    {
//
//        private ArrayList<Lift> items;
//
//        public LiftArrayAdapter(Context context, int textViewResourceId, ArrayList<Lift> items)
//        {
//            super(context, textViewResourceId, items);
//            this.items = items;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent)
//        {
//            View v = convertView;
//            if (v == null)
//            {
//                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                v = vi.inflate(R.layout.lift_item, parent, false);
//            }
////            v.setMinimumHeight(150);
//
//            final Lift lift = items.get(position);
//
//            TextView lblLift = (TextView) v.findViewById(R.id.lbl_lift);
//            ImageButton btnIncrement = (ImageButton) v.findViewById(R.id.btn_increment);
//
//            if (lift != null)
//            {
//                if (lblLift != null)
//                {
//                    lblLift.setText(lift.toString());
//                }
//
//                //remove the increment button in the <New Lift> dummy item
//                if (lift.getId() == -1)
//                {
//                    btnIncrement.setVisibility(View.GONE);
//                }
//            }
//            else return null;
//
//            lblLift.setOnClickListener(new View.OnClickListener()
//            {
//                @Override
//                public void onClick(View v)
//                {
//                    doAdd(lift.getId());
//                }
//            });
//            btnIncrement.setOnClickListener(new View.OnClickListener()
//            {
//                @Override
//                public void onClick(View v)
//                {
//                    lift.setSets(lift.getSets() + 1);
//                    dao.update(lift);
//                    loadSession(sessionId);
//                }
//            });
//
//            return v;
//        }
//
//    }

}
