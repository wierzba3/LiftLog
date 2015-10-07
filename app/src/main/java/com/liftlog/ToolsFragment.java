package com.liftlog;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.InputType;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.liftlog.components.ExerciseInputDialog;
import com.liftlog.data.DataAccessObject;
import com.liftlog.models.Category;
import com.liftlog.models.Exercise;
import com.liftlog.models.Lift;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


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
     *
     * @param view
     */
    public void launchViewHistory(View view)
    {
        Intent intent = new Intent(super.getActivity(), ViewHistory.class);
        super.startActivity(intent);
    }


    /**
     *
     * @param view
     */
    public void launch1RMCalculator(View view)
    {
        //TODO launch 1RM calculator
        Toast.makeText(super.getActivity(), "Not implemented yet.", Toast.LENGTH_LONG).show();
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
                launchViewHistory(view);
            }
        });
        Button btn1RMCalc = (Button) view.findViewById(R.id.btn_1rm_calc);
        btn1RMCalc.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                launch1RMCalculator(view);
            }
        });
    }

//    private void initListView(View view)
//    {
//        listTools = (ListView) view.findViewById(R.id.list_tools);
//
//        final List<Tool> tools = new ArrayList<Tool>();
//        tools.add(Tool.VIEW_HISTORY);
////        tools.add(Tool.BACKUP);
//        ToolArrayAdapter adapter = new ToolArrayAdapter(super.getActivity(), android.R.layout.simple_list_item_1, tools);
//        listTools.setAdapter(adapter);
//        listTools.setOnItemClickListener(new AdapterView.OnItemClickListener()
//        {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
//            {
//                Tool tool = tools.get(i);
//                //TODO launch the activity
//                switch (tool)
//                {
//                    case VIEW_HISTORY:
//                        launchViewHistory();
//                        break;
////                    case BACKUP:
////                        Toast.makeText(ToolsFragment.super.getActivity(), "not implemented", Toast.LENGTH_SHORT).show();
////                        break;
//                }
//            }
//        });
//    }

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
//            case R.id.action_add_exercise:
////                doAddExercise(-1);
//                promptExerciseOrCategory();
//                break;
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
