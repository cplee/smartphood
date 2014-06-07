package com.nektos.smartphood;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class MainActivity extends TabActivity {

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, TodayActivity.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("today").setIndicator("Today",
                          null/*res.getDrawable(R.drawable.ic_tab_today)*/)
                      .setContent(intent);
        tabHost.addTab(spec);
        tabHost.getTabWidget().getChildAt(0).getLayoutParams().height=50;

        // Do the same for the other tabs
        intent = new Intent().setClass(this, GoalActivity.class);
        spec = tabHost.newTabSpec("goal").setIndicator("Goal",
                          null/*res.getDrawable(R.drawable.ic_tab_goal)*/)
                      .setContent(intent);
        tabHost.addTab(spec);
        tabHost.getTabWidget().getChildAt(1).getLayoutParams().height=50;
        
        intent = new Intent().setClass(this, ProgressActivity.class);
        spec = tabHost.newTabSpec("progess").setIndicator("Measure",
                          null/*res.getDrawable(R.drawable.ic_tab_history)*/)
                      .setContent(intent);
        tabHost.addTab(spec);
        tabHost.getTabWidget().getChildAt(2).getLayoutParams().height=50;
        
        intent = new Intent().setClass(this, HistoryActivity.class);
        spec = tabHost.newTabSpec("history").setIndicator("History",
                          null/*res.getDrawable(R.drawable.ic_tab_history)*/)
                      .setContent(intent);
        tabHost.addTab(spec);
        tabHost.getTabWidget().getChildAt(3).getLayoutParams().height=50;

        tabHost.setCurrentTab(0);
        
        
    }
    




}