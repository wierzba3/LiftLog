package com.liftlog;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.liftlog.common.Util;
import com.liftlog.data.DataAccessObject;

import java.util.List;


public class ToolsFragment extends Fragment
{

    public static final String LOG_TAG = "liftlog.ToolsFragment";


    DataAccessObject dao;

    private ListView listTools;

    enum Tool
    {
        VIEW_HISTORY("View History"),
//        BACKUP("Data Backup")
        ;

        Tool(String name)
        {
            this.name = name;
        }

        private String name;

        public String getName()
        {
            return name;
        }


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_browse_exercises);
        View view = inflater.inflate(R.layout.fragment_tools, container, false);

        dao =  new DataAccessObject(super.getActivity());

        createContents(view);

        return view;
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState)
    {
        super.onViewStateRestored(savedInstanceState);

    }

    /**
     * Launch View History activity
     */
    public void launchViewHistory()
    {
        Intent intent = new Intent(super.getActivity(), ViewHistoryActivity.class);
        super.startActivity(intent);
    }


    /**
     * Launch 1-Rep Max Calculator activity
     */
    private void launch1RMCalculator()
    {
        Intent intent = new Intent(super.getActivity(), MaxCalculatorActivity.class);
        super.startActivity(intent);
    }

    /**
     * Launch Find Best Lifts activity
     */
    public void launchFindBestLifts()
    {
        Intent intent = new Intent(super.getActivity(), BestLiftActivity.class);
        super.startActivity(intent);
    }

    /**
     * Launch backup activity
     */
    public void launchBackup()
    {
        Intent intent = new Intent(super.getActivity(), DatabaseBackupActivity.class);
        super.startActivity(intent);
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

        Button btnViewHistory = (Button) view.findViewById(R.id.btn_view_history);
        btnViewHistory.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                launchViewHistory();
            }
        });

        Button btn1RMCalc = (Button) view.findViewById(R.id.btn_1rm_calc);
        btn1RMCalc.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                launch1RMCalculator();
            }
        });

        Button btnFindBestLifts = (Button) view.findViewById(R.id.btn_find_best_lifts);
        btnFindBestLifts.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                launchFindBestLifts();
            }
        });

        Button btnBackup = (Button) view.findViewById(R.id.btn_backup);
        btnBackup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                launchBackup();
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_tools, menu);
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

            case R.id.action_about_tools:
                Util.launchAboutWebsiteIntent(super.getActivity());
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    public class ToolArrayAdapter extends ArrayAdapter<Tool>
    {

        public ToolArrayAdapter(Context context, int textViewResourceId, List<Tool> tools) {
            super(context, textViewResourceId, tools);
            this.tools = tools;
        }

        List<Tool> tools;

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent)
        {
            View view = convertView;
            if (view == null) {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = vi.inflate(R.layout.tool_item, parent, false);
            }

            Tool tool = tools.get(position);

            TextView lblTool = (TextView) view.findViewById(R.id.lbl_tool);
            lblTool.setText(tool.getName());

            return view;
        }


        @Override
        public long getItemId(int position)
        {
           return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }

}
