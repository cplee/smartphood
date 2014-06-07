package com.nektos.smartphood;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.nektos.smartphood.model.Measurement;
import com.nektos.smartphood.model.Metric;

public class TodayActivity extends GoalActivity {
    private Measurement today = null;
    

    @Override
    protected void repaintMeasurement(int statusId,  Metric metric) {
        if(today == null)
            today = dao.getToday();
        
        final TextView status = (TextView)findViewById(statusId);
        status.setText(today.getInt(metric) + " / "+goal.getInt(metric));
    }
    
    @Override
    protected void registerMeasurement(int decId, int incId, int statusId, final Metric metric) {
        if(today == null)
            today = dao.getToday();
        
        final TextView status = (TextView)findViewById(statusId);
         
        // decrement
        ((ImageView)findViewById(decId)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
                today.decrement(metric);
                status.setText(today.getInt(metric) + " / "+goal.getInt(metric));
                
                dao.storeToday(today);
			}
		});
		
        // increment
        ((ImageView)findViewById(incId)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
                today.increment(metric);
                status.setText(today.getInt(metric) + " / "+goal.getInt(metric));
                
                dao.storeToday(today);
			}
		});
		
	}



}