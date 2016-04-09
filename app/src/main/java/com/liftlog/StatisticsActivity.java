package com.liftlog;

import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TableLayout;

import com.liftlog.data.DataAccessObject;

public class StatisticsActivity extends AppCompatActivity
{

    private DataAccessObject dao;
    private TableLayout tbl;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
    }


    private void createContents()
    {
        tbl = (TableLayout) findViewById(R.id.tbl_statistics);
    }

    private void onCalculateClick(View view)
    {
        //TODO populate table with data
        //see tutorial http://www.warriorpoint.com/blog/2009/07/01/android-creating-tablerow-rows-inside-a-tablelayout-programatically/
    }


}
