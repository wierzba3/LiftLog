package com.liftlog;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.liftlog.common.Util;
import com.liftlog.data.DataAccessObject;

import org.joda.time.DateTime;

public class DatabaseBackupActivity extends AppCompatActivity
{

    private static final String LOG_TAG = "DatabaseBackupActivity";

    private DataAccessObject dao;

    private TextView lblLastBackup;
    private Button btnRestoreBackup;
    private Button btnDeleteBackup;

    private static final String LABEL_NO_BACKUP = "never";
    private static final String LABEL_UNKNOWN_BACKUP = "unknown";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_backup);

        dao = new DataAccessObject(this);

        createContents();
    }

    private void createContents()
    {
        ActionBar actionBar = this.getActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.show();
        }

        lblLastBackup = (TextView) findViewById(R.id.lbl_last_backup);
        btnRestoreBackup = (Button) findViewById(R.id.btn_restore_backup);
        btnDeleteBackup = (Button) findViewById(R.id.btn_delete_backup);

        DateTime lastBackup = dao.getLastBackup(this);
        if(lastBackup == null)
        {
            lblLastBackup.setText(LABEL_NO_BACKUP);
            btnRestoreBackup.setEnabled(false);
            btnDeleteBackup.setEnabled(false);
        }
        else
        {
            lblLastBackup.setText(Util.DATE_FORMAT.print(lastBackup));
        }
    }

    /**
     * Handle "Create Backup" button click
     * @param view
     */
    public void doCreateBackup(View view)
    {
        DateTime dt = dao.getLastBackup(this);
        if(dt != null)
        {
            String dtStr = Util.DATE_FORMAT.print(dt);
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
//                    .setIcon(R.drawable.ic_warning_blue_24dp)
                    .setTitle("Restore Backup")
                    .setMessage("Are you sure you want to create a new backup?"
                            + "\nThe backup from " + dtStr + " will be replaced.")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            createBackup();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    return;
                                }

                            }
                    )
                    .show();
        }
        else
        {
            createBackup();
        }
    }

    private void createBackup()
    {
        dao.createBackupCopy(this);
        if(dao.hasBackup(this))
        {
            btnDeleteBackup.setEnabled(true);
            btnRestoreBackup.setEnabled(true);
//            int days  = dao.getLastBackup(this);
            DateTime dt = dao.getLastBackup(this);
            lblLastBackup.setText(Util.DATE_FORMAT.print(dt));
        }
    }


    /**
     * Handle "Restore Backup" button click
     * @param view
     */
    public void doRestoreBackup(View view)
    {
        DateTime dt = dao.getLastBackup(this);
        if(dt == null)
        {
            Toast.makeText(this, "No backup to restore.", Toast.LENGTH_SHORT).show();
            return;
        }
        String dtStr = Util.DATE_FORMAT.print(dt);
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
//                .setIcon(R.drawable.ic_warning_blue_24dp)
                .setTitle("Restore Backup")
                .setMessage("Are you sure you want to restore the backup from " + dtStr + "?\n"
                        + "All data since then will be permanently destroyed.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dao.restoreBackupCopy(DatabaseBackupActivity.this);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                return;
                            }

                        }
                )
                .show();
    }

    /**
     * Handle "Delete Backup" button click
     * @param view
     */
    public void doDeleteBackup(View view)
    {
//        DateTime dt = dao.getLastBackup(this);
        DateTime dt = dao.getLastBackup(this);
        if(dt == null)
        {
            Toast.makeText(this, "No backup to delete.", Toast.LENGTH_SHORT).show();
            return;
        }
        String dtStr = Util.DATE_FORMAT.print(dt);
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
//                .setIcon(R.drawable.ic_warning_blue_24dp)
                .setTitle("Restore Backup")
                .setMessage("Are you sure you want to delete the backup from " + dtStr)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (dao.deleteBackupCopy(DatabaseBackupActivity.this))
                        {
                            btnRestoreBackup.setEnabled(false);
                            btnDeleteBackup.setEnabled(false);
                            lblLastBackup.setText(LABEL_NO_BACKUP);
                        }
                        else
                        {
                            Toast.makeText(DatabaseBackupActivity.this,
                                    "Error deleting backup",
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                return;
                            }
                        }
                )
                .show();
    }


    @Override
    protected void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
    }

    protected void onRestoreInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        //TODO create a menu for this activity
//       inflater.inflate(R.menu.menu_view_session, menu);
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
            case R.id.home:
                break;
        }

        return super.onOptionsItemSelected(item);
    }



}
