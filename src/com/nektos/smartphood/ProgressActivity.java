package com.nektos.smartphood;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.nektos.smartphood.model.Measurement;
import com.nektos.smartphood.model.MeasurementDAO;
import com.nektos.smartphood.model.Metric;

public class ProgressActivity extends Activity {
    private Measurement today = null;
    protected MeasurementDAO dao;
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress);
        
        dao = new MeasurementDAO(getApplicationContext());
        today = dao.getToday(); 
        
        applyDefault(Metric.Weight,150);
        applyDefault(Metric.BpSys, 100);
        applyDefault(Metric.BpDia, 70);
        dao.storeToday(today);
        
        registerMeasurement(R.id.DecWeight, R.id.IncWeight, R.id.Weight, Metric.Weight);
        registerMeasurement(R.id.DecSys, R.id.IncSys, R.id.Sys, Metric.BpSys);
        registerMeasurement(R.id.DecDia, R.id.IncDia, R.id.Dia, Metric.BpDia);
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
        
    	dao.destroy();
    }
    
    
    private void applyDefault(Metric metric, int defaultVal) {
        if(today.getInt(metric) == 0) {
            int val = dao.getRecentNonZero(metric);
        	today.setInt(metric, val==0?defaultVal:val);
        }
    }
    
    protected void registerMeasurement(int decId, int incId, int statusId, final Metric metric) {
        
        final EditText status = (EditText)findViewById(statusId);
        status.setText(Integer.toString(today.getInt(metric)));
        
        status.addTextChangedListener(new TextWatcher() {
			
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			public void afterTextChanged(Editable s) {
                today.setInt(metric, Integer.parseInt(s.toString()));
                dao.storeToday(today);
			}
		});
        
        // decrement
        ((ImageView)findViewById(decId)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
                today.decrement(metric);
                status.setText(Integer.toString(today.getInt(metric)));
                
                dao.storeToday(today);
			}
		});
        
        // increment
        ((ImageView)findViewById(incId)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
                today.increment(metric);
                status.setText(Integer.toString(today.getInt(metric)));
                
                dao.storeToday(today);
			}
		});
		
	}

}