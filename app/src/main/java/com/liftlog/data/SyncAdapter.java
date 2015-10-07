package com.liftlog.data;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;


import com.liftlog.MainActivity;
import com.liftlog.data.mysql.MySQLController;
import com.liftlog.models.Exercise;
import com.liftlog.models.Lift;
import com.liftlog.models.Session;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by James Wierzba on 7/25/2015.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter
{
    public static final String LOG_TAG = "LiftLog";

    private DataAccessObject dao;

    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize)
    {
        super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        dao = new DataAccessObject(context);

    }

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs)
    {
        super(context, autoInitialize, allowParallelSyncs);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */

    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult)
    {
        try {
            Log.d(LOG_TAG, "onPerformSync called");
            try {

                Class.forName("com.mysql.jdbc.Driver").newInstance();
            } catch (Exception ex) {
                Log.d(LOG_TAG, "Exception: " + ex.getMessage());
                return;
            }

            Connection conn = null;
            try {
                conn = DriverManager.getConnection("jdbc:mysql://sql5.freesqldatabase.com/sql586870?" + "user=sql586870&password=kS8*lJ8!");
            } catch (SQLException ex) {
                Log.d(LOG_TAG, "SQLException: " + ex.getMessage());
                return;
            }

            syncExercises(conn, "jamesmw129@gmail.com");
            syncSessions(conn, "jamesmw129@gmail.com");
            syncLifts(conn, "jamesmw129@gmail.com");

            //notify MainActivity that the database has changed
            getContext().getContentResolver().notifyChange(MainActivity.DUMMY_URI, null, false);

            try {
                conn.close();
            } catch (SQLException e) {
                Log.d(LOG_TAG, "Error closing mysql connection.");
            }
        } catch(Exception ex)
        {
            Log.d(LOG_TAG, "Exception in performSync: " + ex.getMessage());
            Log.d(LOG_TAG, "Stack trace:" + Arrays.toString(ex.getStackTrace()));
            return;
        }

    }


    /**
     * Sync the local sqlite database with the remote mysql database
     * Steps:
     * 1. Check the state of each record in the local database,
     *      updating any local changes to the remote db (NEW, MODIFIED, DELETED)
     * 2. After the local changes have been reflected in the remote db,
     *      select all records from the remote db and update all local records with the differences.
     *      This can be a cause of unexpected results, if the user makes local changes in the app,
     *      in addition to another device before doing a remote sync.
     * @param conn The connection to the remote mysql database
     * @param username The username of the current app user
     */
    private void syncExercises(Connection conn, String username)
    {
        Map<Long, Exercise> localExercises = dao.selectExerciseMap(true);
        for(Exercise localExercise : localExercises.values())
        {
            boolean ret;
//            switch(localExercise.getState())
//            {

            if(localExercise.isNew()) {

                //insert the exercise into the remote database
                try {
                    ret = MySQLController.insert(conn, username, localExercise);
                } catch (SQLException ex) {
                    Log.d(LOG_TAG, "error trying to insert new remote exercise from local: " + ex.getMessage());
                       /*
                            exit the sync method because if the record was not successfully added to remote db,
                            it will be deleted in a later step in this sync method
                         */
                    return;
                }
                //mark the local record as up-to-date
                localExercise.setNew(false);
                dao.update(localExercise);

            }
            else if(localExercise.isModified())
            {
                try {
                    ret = MySQLController.update(conn, username, localExercise);
                } catch (SQLException ex) {
                    Log.d(LOG_TAG, "error trying to update remote exercise from local: " + ex.getMessage());
                    break;
                }
                //remote exercise was successfully updated, so mark it as UNCHANGED in the local db
                localExercise.setModified(false);
                dao.update(localExercise);
            }
            else if(localExercise.isDeleted())
            {
                try {
                    ret = MySQLController.deleteExercise(conn, username, localExercise.getId());
                } catch (SQLException ex) {
                    Log.d(LOG_TAG, "error trying to delete remote exercise from local: " + ex.getMessage());
                    break;
                }

                //remote exercise was successfully removed, so fully remove it in the local db
                dao.deleteExercise(localExercise.getId());

            }
//            }
        }

        /*
         * Now that we have updated the changes in the local db, select all remote exercises
          * and update the local db accordingly, treating the remote db as the authority
         */
        Map<Long, Exercise> remoteExercises = MySQLController.selectExercises(conn, username);
        for(Exercise remoteExercise : remoteExercises.values())
        {
            Exercise matchingLocalExercise = localExercises.get(remoteExercise.getId());
            //if the local database does not contain the remote exercise, insert it
            if(matchingLocalExercise == null)
            {
                dao.insert(remoteExercise);
            }
            else
            {
                //if the remote exercise is different from the local record
                boolean b = matchingLocalExercise.equals(remoteExercise);
                if(!matchingLocalExercise.equals(remoteExercise))
                {
                    dao.update(remoteExercise);
                }
            }
        }


        for(Exercise localExercise : localExercises.values())
        {
            /*
                if our local database has a record that is not in the remote db, remove it
                (it should have been inserted as a NEW exercise in the first step of the sync algorithm.
                Ideally, this should never happen.
             */
            if(!remoteExercises.containsKey(localExercise.getId()))
            {
                dao.deleteExercise(localExercise.getId());
            }
        }

        getContext().getContentResolver().notifyChange(MainActivity.DUMMY_URI, null, false);

    }


    /**
     * Sync the local sqlite database with the remote mysql database
     * Steps:
     * 1. Check the state of each record in the local database,
     *      updating any local changes to the remote db (NEW, MODIFIED, DELETED)
     * 2. After the local changes have been reflected in the remote db,
     *      select all records from the remote db and update all local records with the differences.
     *      This can be a cause of unexpected results, if the user makes local changes in the app,
     *      in addition to another device before doing a remote sync.
     * @param conn The connection to the remote mysql database
     * @param username The username of the current app user
     */
    private void syncSessions(Connection conn, String username)
    {
        Log.d(LOG_TAG, "syncSessions");
        List<Session> localSessions = dao.selectSessions(true);
        Log.d(LOG_TAG, "localSessions size = " + (localSessions == null ? 0 : localSessions.size()));
        for(Session localSession : localSessions)
        {
            boolean ret;
            Log.d(LOG_TAG, "syncing session: " + localSession + " isNew=" + localSession.isNew());
            if(localSession.isNew())
            {
                //insert the session into the remote database
                try
                {
                    ret = MySQLController.insert(conn, username, localSession);
                }
                catch (SQLException ex)
                {
                    Log.d(LOG_TAG, "error trying to insert new remote session from local: " + ex.getMessage());
                        /*
                            exit the sync method because if the record was not successfully added to remote db,
                            it will be deleted in a later step in this sync method
                         */
                    return;
                }
                //mark the local record as up-to-date
                localSession.setNew(false);
                dao.update(localSession);

            }
            else if(localSession.isModified())
            {
                try
                {
                    ret = MySQLController.update(conn, username, localSession);
                }
                catch (SQLException ex) {
                    Log.d(LOG_TAG, "error trying to update remote session from local: " + ex.getMessage());
                    break;
                }
                //remote session was successfully updated, so mark it as UNCHANGED in the local db
                localSession.setModified(false);
                dao.update(localSession);
            }
            else if(localSession.isDeleted())
            {
                try
                {
                    ret = MySQLController.deleteSession(conn, username, localSession.getId());
                }
                catch (SQLException ex) {
                    Log.d(LOG_TAG, "error trying to delete remote session from local: " + ex.getMessage());
                    break;
                }

                //remote session was successfully removed, so fully remove it in the local db
                dao.deleteSession(localSession.getId());

            }


        }

        /*
            * Now that we have updated the changes in the local db, select all remote sessions
            * and update the local db accordingly, treating the remote db as the authority
        */
        List<Session> remoteSessions = MySQLController.selectSessions(conn, username);
        for(Session remoteSession : remoteSessions)
        {
            Session matchingLocalSession = Session.findInList(localSessions, remoteSession.getId());
            //if the local database does not contain the remote session, insert it
            if(matchingLocalSession == null)
            {
                dao.insert(remoteSession);
            }
            else
            {
                //if the remote session is different from the local record
                if(!matchingLocalSession.equals(remoteSession))
                {
                    dao.update(remoteSession);
                }
            }
        }

        for(Session localSession : localSessions)
        {
            /*
                if our local database has a record that is not in the remote db, remove it
                (it should have been inserted as a NEW session in the first step of the sync algorithm.
                Ideally, this should never happen.
             */
            if(Session.findInList(remoteSessions, localSession.getId()) == null)
            {
                dao.deleteSession(localSession.getId());
            }
        }


    }

    /**
     * Sync the local sqlite database with the remote mysql database
     * Steps:
     * 1. Check the state of each record in the local database,
     *      updating any local changes to the remote db (NEW, MODIFIED, DELETED)
     * 2. After the local changes have been reflected in the remote db,
     *      select all records from the remote db and update all local records with the differences.
     *      This can be a cause of unexpected results, if the user makes local changes in the app,
     *      in addition to another device before doing a remote sync.
     * @param conn The connection to the remote mysql database
     * @param username The username of the current app user
     */
    private void syncLifts(Connection conn, String username)
    {

        List<Lift> localLifts = dao.selectLifts(true);
        for(Lift localLift : localLifts)
        {
            boolean ret;
            if(localLift.isNew()) {
                Log.d(LOG_TAG, "inserting Lift from local");
                //insert the lift into the remote database
                try
                {
                    ret = MySQLController.insert(conn, username, localLift);
                }
                catch (SQLException ex)
                {
                    Log.d(LOG_TAG, "error trying to insert new remote lift from local: " + ex.getMessage());
                        /*
                            exit the sync method because if the record was not successfully added to remote db,
                            it will be deleted in a later step in this sync method
                         */
                    return;
                }
                //mark the local record as up-to-date
                localLift.setNew(false);
                dao.update(localLift);

            }
            else if(localLift.isModified())
            {
                try
                {
                    ret = MySQLController.update(conn, username, localLift);
                }
                catch (SQLException ex)
                {
                    Log.d(LOG_TAG, "error trying to update remote lift from local: " + ex.getMessage());
                    break;
                }
                //remote lift was successfully updated, so mark it as UNCHANGED in the local db
                localLift.setModified(false);
                dao.update(localLift);
            }
            else if(localLift.isDeleted())
            {
                try
                {
                    ret = MySQLController.deleteLift(conn, username, localLift.getId());
                }
                catch (SQLException ex)
                {
                    Log.d(LOG_TAG, "error trying to delete remote lift from local: " + ex.getMessage());
                    break;
                }

                //remote lift was successfully removed, so fully remove it in the local db
                dao.deleteLift(localLift.getId());
            }
        }

        /*
            * Now that we have updated the changes in the local db, select all remote lifts
            * and update the local db accordingly, treating the remote db as the authority
        */
        List<Lift> remoteLifts = MySQLController.selectLifts(conn, username);
        for(Lift remoteLift : remoteLifts)
        {
            Lift matchingLocalLift = Lift.findInList(localLifts, remoteLift.getId());
            //if the local database does not contain the remote lift, insert it
            if(matchingLocalLift == null)
            {
                dao.insert(remoteLift);
            }
            else
            {
                //if the remote lift is different from the local record
                if(!matchingLocalLift.equals(remoteLift))
                {
                    dao.update(remoteLift);
                }
            }
        }

        for(Lift localLift : localLifts)
        {
            /*
                if our local database has a record that is not in the remote db, remove it
                (it should have been inserted as a NEW lift in the first step of the sync algorithm.
                Ideally, this should never happen.
             */
            if(Lift.findInList(remoteLifts, localLift.getId()) == null)
            {
                dao.deleteLift(localLift.getId());
            }
        }
    }


}
