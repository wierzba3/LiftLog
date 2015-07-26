package com.liftlog;


import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.TextView;

import wierzba.james.liftlog.R;

/**
 * TODO

 * Implement now:
 * - Group Lifts in Session by the exercise, then after selecting exercise, show the individual lifts.
 *      Some tree-like structure?
 * - Implement Sync Adapter ( need to set-up db schema in google cloud sql database first...)
 * - Decide what to do when the user deletes an exercise that is referenced by 1 or more lifts
 *      add new boolean field to Exercise: valid, need to update database this allows the user
 *          to "delete the lift" by us setting valid to false and name to "?" and the user can choose to re-define it if
 *          there are existing lifts which reference that exercise
 *
 *
 *
 *
 * Implement in the future:
 * - Tools
 *      1RM calculator
 *
 * - Programmable training routines. Define rules that the user can set for an exercise.
 * Display planned lifts separately from the completed lifts in the sessions.
 * e.g. repeat selected lift every M/W/F, increase weight each day/week
 * - Hi-scores. Users can submit their video of lifts to be reviewed and then entered in high scores.
 * - Settings
 *   - "sort by" option on sessions/lifts
 * - Copy option for session
 * - Tabular view of lifts
 */

public class MainActivity extends AppCompatActivity
{

    private FragmentPagerAdapter mCustomPagerAdapter;
    private ViewPager mViewPager;

    public static final String LOG_TAG = "LiftLog";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCustomPagerAdapter = new FragmentPagerAdapter();
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mCustomPagerAdapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Start the activity to add a lift
     *
     * @param view The view
     */
    public void startAddLift(View view)
    {
        Intent intent = new Intent(this, ViewLift.class);
        this.startActivity(intent);
    }

//    /**
//     * Start the activity to browse previous workout sessions
//     *
//     * @param view The view
//     */
//    public void startBrowseSessions(View view)
//    {
//        Intent intent = new Intent(this, BrowseSessions.class);
//        this.startActivity(intent);
//    }


//    /**
//     * Start the activity to browse previous workout sessions
//     *
//     * @param view The view
//     */
//    public void startBrowseExercises(View view)
//    {
//        Intent intent = new Intent(this, BrowseExercises.class);
//        this.startActivity(intent);
//    }

    public class FragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter
    {
        final int PAGE_COUNT = 2;

        private final String[] PAGE_TITLES =
                {
                        "Log",
                        "Exercises"
                };

        public FragmentPagerAdapter()
        {
            super(getSupportFragmentManager());
        }

        @Override
        public int getCount()
        {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return PAGE_TITLES[position];
        }

        @Override
        public Fragment getItem(int position)
        {
            switch(position)
            {
                case 0:
                    return new SessionsFragment();
                case 1:
                    return new ExercisesFragment();
                default:
                    return null;
            }

        }
    }

//    public static class PageFragment extends Fragment
//    {
//        public static final String ARG_PAGE = "ARG_PAGE";
//
//        private int mPage;
//
//        public static PageFragment create(int page)
//        {
//            Bundle args = new Bundle();
//            args.putInt(ARG_PAGE, page);
//            PageFragment fragment = new PageFragment();
//            fragment.setArguments(args);
//            return fragment;
//        }
//
//        @Override
//        public void onCreate(Bundle savedInstanceState)
//        {
//            super.onCreate(savedInstanceState);
//            mPage = getArguments().getInt(ARG_PAGE);
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
//        {
//            View view = inflater.inflate(R.layout.fragment_demo, container, false);
//            TextView textView = (TextView) view;
//            textView.setText("Fragment #" + mPage);
//            return view;
//        }
//    }
}
