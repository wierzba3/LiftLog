package com.liftlog;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import wierzba.james.liftlog.R;

import com.liftlog.common.DataAccessObject;
import com.liftlog.components.DateInputDialog;
import com.liftlog.components.ExerciseInputDialog;
import com.liftlog.models.Exercise;
import com.liftlog.models.Lift;
import com.liftlog.models.Session;


public class SessionsFragment extends Fragment implements  DateInputDialog.DateInputDialogListener
{

    ListView listSessions;

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

        createContents(view);
        loadSessions();

        return view;
    }

    private void createContents(View view)
    {

        setHasOptionsMenu(true);

        ActionBar actionBar = super.getActivity().getActionBar();
//        if(actionBar == null) actionBar =
        if (actionBar != null)
        {
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.show();
        }

        listSessions = (ListView) view.findViewById(R.id.list_sessions_fragment);


        listSessions.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id)
            {
                Intent intent = new Intent(SessionsFragment.super.getActivity(), ViewSession.class);
                Session session = (Session) parent.getItemAtPosition(position);
                long sessionId = session.getId();
                //if id is -1, the user selected the <New session> dummy item
                if (sessionId == -1)
                {
                    doAdd();
                    return;
                }
                intent.putExtra(ViewSession.SESSION_ID_KEY, sessionId);
                startActivity(intent);
            }
        });


    }

    private void doAdd()
    {
        DateInputDialog dialog = DateInputDialog.newInstance();
        dialog.setTargetFragment(SessionsFragment.this, REQUEST_CODE);
        dialog.show(getFragmentManager().beginTransaction(), "DateInputDialog");
    }


    private void loadSessions()
    {
//            dao.insertDummySessions();
//        dao.test();
//        dao.clearSessionsTable();

        List<Session> sessions = dao.selectSessions();
        Session.computeDuplicateDays(sessions);
        Collections.sort(sessions, Session.byDateDesc);

        Session dummySession = new Session();
        dummySession.setId(-1);
        sessions.add(0, dummySession);

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



}
