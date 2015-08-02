package com.liftlog;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;

import com.liftlog.R;


/**
 * TODO
 * BUGS:
 * - SyncAdapter is not working, periodically or when called explicitly
 *
 *
 *
 *
 * Implement now:
 * - Implement Sync Adapter ( need to set-up data schema in google cloud sql database first...)
 *      Set up all the stub classes. Next: execute the SyncAdapter  https://developer.android.com/training/sync-adapters/running-sync-adapter.html
 * - Deploy backend to app engine
 *      https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints section 2.2
 *
 * - Group Lifts in Session by the exercise, then after selecting exercise, show the individual lifts.
 * Some tree-like structure?
 * - Decide what to do when the user deletes an exercise that is referenced by 1 or more lifts
 * add new boolean field to Exercise: valid, need to update database this allows the user
 * to "delete the lift" by us setting valid to false and name to "?" and the user can choose to re-define it if
 * there are existing lifts which reference that exercise
 *
 *
 *
 * Implement in the future:
 * - Tools
 * 1RM calculator
 * <p/>
 * - Programmable training routines. Define rules that the user can set for an exercise.
 * Display planned lifts separately from the completed lifts in the sessions.
 * e.g. repeat selected lift every M/W/F, increase weight each day/week
 * - Hi-scores. Users can submit their video of lifts to be reviewed and then entered in high scores.
 * - Settings
 * - "sort by" option on sessions/lifts
 * - Copy option for session
 * - Tabular view of lifts
 */

public class MainActivity extends AppCompatActivity
{

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

    private ContentResolver mResolver;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCustomPagerAdapter = new FragmentPagerAdapter();
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mCustomPagerAdapter);

        accountType = getString(R.string.accountType);
        mAccount = CreateSyncAccount(this);

        Log.d(LOG_TAG, "test");
        mResolver = getContentResolver();
        ContentResolver.setSyncAutomatically(mAccount, AUTHORITY, true);
        ContentResolver.addPeriodicSync(
                mAccount,
                AUTHORITY,
                Bundle.EMPTY,
                2
        );
        ContentResolver.requestSync(mAccount, AUTHORITY, Bundle.EMPTY);

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
            switch (position)
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

    /**
     * Create a new dummy account for the sync adapter
     *
     * @param context The application context
     */
    public Account CreateSyncAccount(Context context)
    {
        // Create the account type and default account
        Account newAccount = new Account(
                ACCOUNT, accountType);
        // Get an instance of the Android account manager
//        AccountManager accountManager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);
        AccountManager accountManager = AccountManager.get(this);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        Account[] accounts = accountManager.getAccountsByType(accountType);
        if (accounts.length == 0)
        {
            boolean ret = false;
            ret = accountManager.addAccountExplicitly(newAccount, null, null);
        }
        else
        {
            return accounts[0];
        }

//        if (ret)
//        {
//            /*
//             * If you don't set android:syncable="true" in
//             * in your <provider> element in the manifest,
//             * then call context.setIsSyncable(account, AUTHORITY, 1)
//             * here.
//             */
//        }
//        else
//        {
//            /*
//             * The account exists or some other error occurred. Log this, report it,
//             * or handle it internally.
//             */
//            Log.d(LOG_TAG, "Error creating account");
//            return null;
//        }

        return newAccount;
    }
}
