package com.liftlog;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
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

import com.liftlog.common.Util;
import com.liftlog.components.DateInputDialog;
import com.liftlog.models.Exercise;
import com.liftlog.models.Lift;
import com.liftlog.data.DataAccessObject;
import com.liftlog.models.Session;

import org.joda.time.DateTime;

public class ViewSessionActivity extends AppCompatActivity implements  DateInputDialog.DateInputDialogListener
{
//public class ViewSessionActivity extends Activity {

    /**
     * The key for this intent's extended data: the id of the session (-1 if new instance)
     */
    public static final String SESSION_ID_KEY = "session_id";

    private static final String LOG_TAG = "ViewSession";

    private DataAccessObject dao;

    private ListView listLifts;
    private ExpandableListView exListLifts;
    private LiftExpendableListAdapter extListLiftsAdapter;
    private TextView lblEmpty;

    private long sessionId = -1;

    public static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_session);

        dao = new DataAccessObject(this);

        Intent intent = getIntent();
        sessionId = intent.getLongExtra(SESSION_ID_KEY, -1l);

        createContents();
        loadSession(sessionId, false);
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
        exListLifts.setOnChildClickListener(new ExpandableListView.OnChildClickListener()
        {
            @Override
            public boolean onChildClick(ExpandableListView parent, View view, int i, int j, long id)
            {
                int index = parent.getFlatListPosition(ExpandableListView.getPackedPositionForChild(i, j));
                parent.setItemChecked(index, true);
                Lift lift = extListLiftsAdapter.getElements().get(i).getLifts().get(j);
                doAdd(lift.getId());
                return true;
            }
        });
        lblEmpty = (TextView) findViewById(R.id.lbl_empty_session);


    }

    private void loadSession(long id, boolean oneAdded)
    {
        int scrollPos = exListLifts.getFirstVisiblePosition();

        Map<Long, Boolean> isExpanded = new HashMap<Long, Boolean>();
        if(exListLifts.getExpandableListAdapter() != null)
        {
            int cnt = exListLifts.getExpandableListAdapter().getGroupCount();
            for (int i = 0; i < exListLifts.getExpandableListAdapter().getGroupCount(); i++)
            {
                if (exListLifts.getItemAtPosition(i) == null) continue;
                LiftGroupElement elem = (LiftGroupElement) exListLifts.getExpandableListAdapter().getGroup(i);
                isExpanded.put(elem.getExercise().getId(), exListLifts.isGroupExpanded(i));
            }
        }

        Session session = dao.selectSession(sessionId);
        ArrayList<Lift> lifts;
        if (session == null)
        {
            lifts = new ArrayList<Lift>();
        }
        else
        {
            lifts = session.getLifts();
            DateTime dt = new DateTime(session.getDate());
            setTitle(Util.DATE_FORMAT_SHORT.print(dt));
        }
        if(lifts == null || lifts.isEmpty())
        {
            lblEmpty.setText("No lifts have been added");
            exListLifts.setAdapter((LiftExpendableListAdapter)null);
            return;
        }
        lblEmpty.setText("");
        Collections.sort(lifts);


        Map<Long, Exercise> exerciseMap = dao.selectExerciseMap(false);

        //dummy lift for < Add New > option
        Lift emptyLift = new Lift();
        emptyLift.setId(-1);
        lifts.add(0, emptyLift);

//        LiftArrayAdapter liftArrayAdapter = new LtArrayAdapter(this, R.id.lbl_lift, lifts);
//        listLifts.setAdapter(liftArrayAdapter);if

        extListLiftsAdapter = new LiftExpendableListAdapter(this, lifts, exerciseMap);
        exListLifts.setAdapter(extListLiftsAdapter);

        for(int i = 0; i < extListLiftsAdapter.getGroupCount(); i++)
        {
            LiftGroupElement elem = (LiftGroupElement) extListLiftsAdapter.getGroup(i);
            if(elem == null) continue;
            Boolean wasExpanded = isExpanded.get(elem.getExercise().getId());
            if(wasExpanded == null) exListLifts.expandGroup(i);
            else if(wasExpanded) exListLifts.expandGroup(i);
            else exListLifts.collapseGroup(i);

        }

        if(oneAdded) scrollPos++;
        exListLifts.setSelection(scrollPos);

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
     * Handle Add action event
     * @param view
     */
    public void doAdd(View view)
    {
        doAdd(-1);
    }

    /**
     * Add a_m new Lift
     *
     * @param liftId The id of the lift (-1 for a_m new Lift)
     */
    private void doAdd(long liftId)
    {
        Intent intent = new Intent(ViewSessionActivity.this, ViewLiftActivity.class);
        intent.putExtra(ViewLiftActivity.LIFT_ID_KEY, liftId);
        intent.putExtra(ViewLiftActivity.SESSION_ID_KEY, sessionId);
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
                .setIcon(R.drawable.ic_warning_blue_24dp)

                .setTitle("Delete Session")
                .setMessage("Are you sure you want to delete this Session? All associated Lifts will also be deleted.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (!dao.deleteSession(sessionId))
                        {
                            Toast.makeText(ViewSessionActivity.this, "Error deleting session.", Toast.LENGTH_SHORT).show();
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
            loadSession(sessionId, false);
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
        // as you specify a_m parent activity in AndroidManifest.xml.
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
            case R.id.action_about_session:
                Util.launchAboutWebsiteIntent(this);
                break;
            case R.id.action_note:
                editNote();
                break;
            case R.id.action_edit_date:
                editDate();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void editDate()
    {
        Session session = dao.selectSession(sessionId);
        DateInputDialog dialog = DateInputDialog.newInstance(session.getDate());
        FragmentManager fm = getSupportFragmentManager();
//        DialogFragment dialogFragment = new DialogFragment();
//        dialog.setTargetFragment(dialog, REQUEST_CODE);

        dialog.show(fm, "DateInputDialog");
    }

    private void editNote()
    {
        String note = dao.selectNote(sessionId);

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.session_note_input_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText txtNoteInput = (EditText) promptsView.findViewById(R.id.txt_note_input);

//        txtNoteInput.requestFocus();
        if(note != null)
        {
            txtNoteInput.setText(note);
        }

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                Session session = dao.selectSession(sessionId);
                                if (session == null)
                                {
                                    Toast.makeText(ViewSessionActivity.this, "Error updating note", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                String txt = txtNoteInput.getText().toString();
                                session.setNote(txt);
                                if (!dao.update(session))
                                {
                                    Toast.makeText(ViewSessionActivity.this, "Error updating note", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

        //set the text caret to the end of the Note text
        txtNoteInput.setSelection(txtNoteInput.getText().length());
    }


    /**
     * Handle the Save action of the edit date dialog.
     * @param dialog The event sender
     * @param date The selected date
     */
    @Override
    public void onDialogSaveClick(DialogFragment dialog, long date)
    {
        Session session = dao.selectSession(sessionId);
        if(session == null)
        {
            Toast.makeText(this, "Error updating Session date. code=1", Toast.LENGTH_LONG).show();
            return;
        }
        session.setDate(date);
        if(dao.update(session))
        {
            setTitle(Util.DATE_FORMAT_SHORT.print(date));
        }
        else
        {
            Toast.makeText(this, "Error updating Session date. code=2", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDialogCancelClick(DialogFragment dialog)
    {

    }

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
                    lifts = new ArrayList<>();
                }
                lifts.add(lift);

                map.put(lift.getExerciseId(), lifts);
            }

            //assign sequence numbers to each list of lifts for each exercise
            for(List<Lift> lifts : map.values())
            {
                Lift.assignSequenceNums(lifts);
            }

			LiftGroupElement uncategorized = null;
			Exercise dummy = new Exercise();
			dummy.setId(-1l);
			dummy.setName("?");
			for(long exerciseId : map.keySet())
            {

				List<Lift> lifts = map.get(exerciseId);
				if(lifts == null || lifts.size() == 0) continue;

                Exercise exercise = exerciseMap.get(exerciseId);
				if(exerciseId == -1 || exercise == null)
				{
					uncategorized = new LiftGroupElement();
					uncategorized.setLifts(lifts);
					uncategorized.setExercise(dummy);
					elements.add(uncategorized);
				}
				else
				{
					LiftGroupElement element = new LiftGroupElement();
					element.setExercise(exercise);
					element.setLifts(lifts);
					elements.add(element);
				}
			}

			Collections.sort(elements, comparator);
		}

//        private Comparator<List<Lift>> liftsComparator = new Comparator<List<Lift>>(){
//            @Override
//            public int compare(List<Lift> l1, List<Lift> l2)
//            {
//                if((l1 == null || l1.size() == 0) && (l2 == null || l2.size() == 0)) return 0;
//                else if(l1 == null || l1.size() == 0) return -1;
//                else if(l2 == null || l2.size() == 0) return 1;
//
//				int result = 0;
//				long minDate = Long.MAX_VALUE;
//				for(Lift lift : l1)
//				{
//					if(lift.getDateCreated() < minDate)
//					{
//						minDate = lift.getDateCreated();
//						result = -1;
//					}
//				}
//				for(Lift lift : l2)
//				{
//					if(lift.getDateCreated() < minDate)
//					{
//						minDate = lift.getDateCreated();
//						result = 1;
//					}
//				}
//                return result;
//            }
//        };

        private Comparator<LiftGroupElement> comparator = new Comparator<LiftGroupElement>(){
            @Override
            public int compare(LiftGroupElement e1, LiftGroupElement e2)
            {
                List<Lift> l1 = e1.getLifts();
                List<Lift> l2 = e2.getLifts();
                if((l1 == null || l1.size() == 0) && (l2 == null || l2.size() == 0)) return 0;
                else if(l1 == null || l1.size() == 0) return -1;
                else if(l2 == null || l2.size() == 0) return 1;
                if(e1.getExercise().getId() == -1) return 1;
                if(e2.getExercise().getId() == -1) return -1;

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

        public List<LiftGroupElement> getElements()
        {
            return elements;
        }

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
        public View getChildView(final int i, final int j, boolean b, View view, ViewGroup viewGroup)
        {
            if (view == null)
            {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = vi.inflate(R.layout.lift_item, viewGroup, false);
            }

            //final Lift lift = liftLists.get(i).get(j);
			final Lift lift = elements.get(i).getLifts().get(j);

            final TextView lblLift = (TextView) view.findViewById(R.id.lbl_lift);
            lblLift.setText(lift.toString());

            ImageButton btnCopy = (ImageButton) view.findViewById(R.id.btn_copy);
            btnCopy.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(dao.insert(lift) < 0)
                    {
                        Toast.makeText(ViewSessionActivity.this, "Error copying lift.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    loadSession(sessionId, true);
                }
            });

            final View viewRef = view;
            lblLift.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
//                    viewRef.setBackgroundResource(R.drawable.list_click_background);
                    viewRef.setBackgroundColor(getResources().getColor(R.color.material_blue_200));
                    doAdd(lift.getId());
                }
            });



            return view;
        }
    }



}
