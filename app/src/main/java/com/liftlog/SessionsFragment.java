package com.liftlog;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
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

import com.liftlog.data.DataAccessObject;
import com.liftlog.components.DateInputDialog;
import com.liftlog.models.Exercise;
import com.liftlog.models.Lift;
import com.liftlog.models.Session;

import org.joda.time.DateTime;
import org.joda.time.Months;


public class SessionsFragment extends Fragment implements  DateInputDialog.DateInputDialogListener
{

    private ListView listSessions;
    private TextView lblEmpty;

    DataAccessObject dao;

    private static final String LOG_TAG = "LiftLog.BrowseSessions";

    public static final int REQUEST_CODE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_browse_sessions);
        View view = inflater.inflate(R.layout.fragment_sessions, container, false);

        dao = new DataAccessObject(super.getActivity());
        //dao.test();

        createContents(view);
        loadSessions();

        return view;
    }

    private void createContents(View view)
    {

        setHasOptionsMenu(true);

        ActionBar actionBar = super.getActivity().getActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.show();
        }

        lblEmpty = (TextView) view.findViewById(R.id.lbl_empty_sessions);

        listSessions = (ListView) view.findViewById(R.id.list_sessions_fragment);
        listSessions.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id)
            {
                Intent intent = new Intent(SessionsFragment.super.getActivity(), ViewSessionActivity.class);
                Session session = (Session) parent.getItemAtPosition(position);
                long sessionId = session.getId();
                //if id is -1, the user selected the <New session> dummy item
                if (sessionId == -1)
                {
                    doAdd();
                    return;
                }

                view.setBackgroundColor(getResources().getColor(R.color.material_blue_200));
//                view.setBackgroundResource(R.drawable.list_click_background);

                intent.putExtra(ViewSessionActivity.SESSION_ID_KEY, sessionId);
                startActivity(intent);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab_add_session);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                doAdd();
            }
        });
    }

    private void doAdd()
    {
        DateInputDialog dialog = DateInputDialog.newInstance();
        dialog.setTargetFragment(SessionsFragment.this, REQUEST_CODE);
        dialog.show(getFragmentManager().beginTransaction(), "DateInputDialog");
    }


    public void loadSessions()
    {
        if(super.getActivity() == null)
        {
            //TODO figure out why this is returning null. Perhaps implement MainActivity as a singleton?
            return;
        }

        List<Session> sessions = dao.selectSessions(false);
        if(sessions == null || sessions.isEmpty())
        {
            lblEmpty.setText("No sessions have been added");
            listSessions.setAdapter(null);
            return;
        }
        lblEmpty.setText("");

        Session.computeDuplicateDays(sessions);
        Collections.sort(sessions, Session.byDateDesc);

//        Session dummySession = new Session();
//        dummySession.setId(-1);
//        sessions.add(0, dummySession);

//            ArrayList<String> sessionLabels = new ArrayList<String>();
//            for (Session session : sessions) sessionLabels.add(String.valueOf(session.getDate()));

        ArrayAdapter<Session> adapter = new ArrayAdapter<Session>(super.getActivity(), android.R.layout.simple_list_item_1, sessions);

        listSessions.setAdapter(adapter);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_sessions, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
            case R.id.action_add_session:
                doAdd();
                break;
        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onResume()
    {
        super.onResume();
        loadSessions();
    }

    @Override
    public void onDialogCancelClick(DialogFragment dialog)
    {

    }

    @Override
    public void onDialogSaveClick(DialogFragment dialog, long date)
    {
        Session session = new Session();
        session.setNew(true);
        long sessionId;
        session.setDate(date);
        sessionId = dao.insert(session);
        if (sessionId == -1)
        {
            Toast.makeText(SessionsFragment.super.getActivity(), "", Toast.LENGTH_LONG).show();
            Log.d(LOG_TAG, "Error inserting new session.");
            return;
        }

        loadSessions();
    }

    private class SessionGroupElement
    {
        public SessionGroupElement() {}

        private List<Session> sessions;
        private DateTime date;

        public List<Session> getSessions()
        {
            return sessions;
        }

        public void setSessions(List<Session> sessions)
        {
            this.sessions = sessions;
        }

        public DateTime getDateTime()
        {
            return date;
        }

        public void setDateTime(DateTime date)
        {
            this.date = date;
        }
    }

    private class SessionsExpendableListAdapter extends BaseExpandableListAdapter
    {
        public SessionsExpendableListAdapter(Context ctx, List<Session> allSessions)
        {
            elements = new ArrayList<SessionGroupElement>();

            if(allSessions == null) return;



            Collections.sort(elements, comparator);
        }

        private Comparator<SessionGroupElement> comparator = new Comparator<SessionGroupElement>(){
            @Override
            public int compare(SessionGroupElement e1, SessionGroupElement e2)
            {
                //TODO
                return 0;
            }
        };

        private List<SessionGroupElement> elements;
        public List<SessionGroupElement> getElements()
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
            return elements.size();
        }

        @Override
        public int getChildrenCount(int i)
        {
            SessionGroupElement e = elements.get(i);
            if(e == null || e.getSessions() == null) return 0;
            return e.getSessions().size();
        }

        @Override
        public Object getGroup(int i)
        {
            return elements.get(i);
        }

        @Override
        public Object getChild(int i, int j)
        {
            //return liftLists.get(i).get(j);
            return elements.get(i).getSessions().get(j);
        }

        @Override
        public long getGroupId(int i)
        {
            //return exercises.get(i).getId();
            return elements.get(i).getDateTime().getMonthOfYear();
        }

        @Override
        public long getChildId(int i, int j)
        {
            return elements.get(i).getSessions().get(j).getId();
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
                LayoutInflater vi = (LayoutInflater) SessionsFragment.super.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = vi.inflate(R.layout.session_group_item, viewGroup, false);
            }

            //TODO

            return view;
        }

        @Override
        public View getChildView(final int i, final int j, boolean b, View view, ViewGroup viewGroup)
        {
            if (view == null)
            {
                LayoutInflater vi = (LayoutInflater) SessionsFragment.super.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = vi.inflate(R.layout.session_item, viewGroup, false);
            }

            //TODO

            return view;
        }
    }

    public class SessionListAdapter extends ArrayAdapter<Session>
    {

        public SessionListAdapter(Context context, int resource, List<Session> elements)
        {
            super(context, resource, elements);
            this.elements = elements;
        }

        private List<Session> elements;

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {

            View view = convertView;

            if (view == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                view = vi.inflate(R.layout.session_item, null);
            }

            Session session = getItem(position);

            TextView lblSession = (TextView) view.findViewById(R.id.lbl_session);

            return view;
        }

    }

}
