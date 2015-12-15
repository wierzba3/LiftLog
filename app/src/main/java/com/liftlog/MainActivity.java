package com.liftlog;


import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;

import android.net.Uri;

import com.liftlog.components.ExerciseInputDialog;
import com.liftlog.data.DataAccessObject;
import com.liftlog.models.Exercise;


/**
 * TODO
 * BUGS:
 *
 *
 * Implement now:
 * - Edit date on Session
 *      Date input dialog code
 *
 *
 * Implement in the future:
 * - Refactor DataAccessObject by extracting methods that create the model objects from cursors (lots of code repetition)
 * - Programmable training routines. Define rules that the user can set for an exercise.
 * Display planned lifts separately from the completed lifts in the sessions.
 * e.g. repeat selected lift every M/W/F, increase weight each day/week
 * - Implement DataBackup service
 *      http://developer.android.com/guide/topics/data/backup.html
 *
 * Publishing TODO:
 * - End User License Agreement (EULA)
 *
 * Version 2 log:
 * - New feature to add a note to a Session
 * - New option to edit the date of a Session
 * - Other bug fixes
 */

public class MainActivity extends AppCompatActivity implements ExerciseInputDialog.ExerciseInputDialogListener
{

    /** The key for the shared preferences value indicating if this application has been executed before */
    private static final String PREVIOUS_EXECUTION_PREFERENCE_KEY = "previous_execution";

    private DataAccessObject dao;

    private FragmentPagerAdapter mCustomPagerAdapter;
    private ViewPager mViewPager;

    public static final String LOG_TAG = "LiftLog";

    // Constants
    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = "com.liftlog.data.provider";
    // An account type, in the form of a domain name
    public String accountType;
    // The account name
    public static final String ACCOUNT = "dummyaccount";
    // Instance fields
    Account mAccount;

    //    private ContentResolver mResolver;
    private ContentObserver mObserver;

    public static final Uri DUMMY_URI = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority("com.liftlog").build();

    @Override
    public void onStart()
    {
        super.onStart();

        mObserver = new ContentObserver(new Handler(Looper.getMainLooper()))
        {
            public void onChange(boolean selfChange)
            {
                mCustomPagerAdapter.getExercisesFragment().loadExercises();
                mCustomPagerAdapter.getSessionsFragment().loadSessions();
            }
        };
        getContentResolver().registerContentObserver(DUMMY_URI, false, mObserver);


        ContentResolver.requestSync(mAccount, AUTHORITY, Bundle.EMPTY);
    }


    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCustomPagerAdapter = new FragmentPagerAdapter();
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mCustomPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }
            @Override
            public void onPageSelected(int position)
            {
                if(position == FragmentPagerAdapter.EXERCISES_INDEX)
                {
//                    mCustomPagerAdapter.getExercisesFragment().expandListGroupItems();
                }
                if(position == FragmentPagerAdapter.SESSIONS_INDEX)
                {
//                    mCustomPagerAdapter.getSessionsFragment().loadSessions();
//                    mCustomPagerAdapter.getSessionsFragment().expandListGroupItems();
                }
            }
            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });



        dao = new DataAccessObject(this);
        //checkFirstExecution();
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
//        if (id == R.id.action_settings)
//        {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Start the activity to add a lift
     *
     * @param view The view
     */
    public void startAddLift(View view)
    {
        Intent intent = new Intent(this, ViewLiftActivity.class);
        this.startActivity(intent);
    }

    /**
     * Check the shared preferences flag to determine if this is the first time running the app
     * If true, and also the Exercises and Categories tables are empty, add default records
     */
    private void checkFirstExecution()
    {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preferencesFileKey), Context.MODE_PRIVATE);
        int previouslyExecuted = sharedPref.getInt(PREVIOUS_EXECUTION_PREFERENCE_KEY, 0);
        if(previouslyExecuted == 1) return;

        long exerciseCount = dao.exerciseCount();
        if(exerciseCount == 0)
        {
            dao.insert(Exercise.Squat());
            dao.insert(Exercise.BenchPress());
            dao.insert(Exercise.Deadlift());
            dao.insert(Exercise.Press());
        }

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(PREVIOUS_EXECUTION_PREFERENCE_KEY, 1);
        editor.commit();
    }


    public class FragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter
    {
        final int PAGE_COUNT = 3;

        public final String[] PAGE_TITLES =
                {
                        "Log",
                        "Exercises",
                        "Tools"
                };

        public static final int SESSIONS_INDEX = 0;
        public static final int EXERCISES_INDEX = 1;
        public static final int TOOLS_INDEX = 2;

        private SessionsFragment sessionsFragment;
        private ExercisesFragment exercisesFragment;
        private ToolsFragment toolsFragment;

        public FragmentPagerAdapter()
        {
            super(getSupportFragmentManager());
            sessionsFragment = new SessionsFragment();
            exercisesFragment = new ExercisesFragment();
            toolsFragment = new ToolsFragment();
        }

        public SessionsFragment getSessionsFragment()
        {
            return sessionsFragment;
        }

        public ExercisesFragment getExercisesFragment()
        {
            return exercisesFragment;
        }

        public ToolsFragment getToolsFragment()
        {
            return toolsFragment;
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
            switch (position)
            {
                case SESSIONS_INDEX:
                    return sessionsFragment;
                case EXERCISES_INDEX:
                    return exercisesFragment;
                case TOOLS_INDEX:
                    return toolsFragment;
                default:
                    return null;
            }

        }
    }

//    /**
//     * Create a new dummy account for the sync adapter
//     *
//     * @param context The application context
//     */
//    public Account CreateSyncAccount(Context context)
//    {
//        // Create the account type and default account
//        Account newAccount = new Account(
//                ACCOUNT, accountType);
//        // Get an instance of the Android account manager
//        AccountManager accountManager = AccountManager.get(this);
//        /*
//         * Add the account and account type, no password or user data
//         * If successful, return the Account object, otherwise report an error.
//         */
//        Account[] accounts = accountManager.getAccountsByType(accountType);
//        if (accounts.length == 0)
//        {
//            boolean ret = false;
//            ret = accountManager.addAccountExplicitly(newAccount, null, null);
//        }
//        else
//        {
//            return accounts[0];
//        }
//
//
//        return newAccount;
//    }


    @Override
    public void onDialogSaveClick(DialogFragment dialog, Exercise exercise)
    {

    }

    @Override
    public void onDialogCancelClick(DialogFragment dialog)
    {

    }

    @Override
    public void onDialogDeleteClick(DialogFragment dialog, Exercise exercise)
    {

    }

    @Override
    public void handleDialogClose(DialogInterface dialog)
    {
        mCustomPagerAdapter.getExercisesFragment().clearSelectedExercises();
    }
}
