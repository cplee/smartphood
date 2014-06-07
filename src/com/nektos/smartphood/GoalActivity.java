package com.nektos.smartphood;

import static com.nektos.smartphood.model.Metric.*;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.nektos.smartphood.model.Measurement;
import com.nektos.smartphood.model.MeasurementDAO;
import com.nektos.smartphood.model.Metric;

public class GoalActivity extends Activity {
    protected Measurement goal = null;
    protected MeasurementDAO dao;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.measurement);
        
        // open DAO
        dao = new MeasurementDAO(getApplicationContext());
        
        // get the current goal and measurements
        goal = dao.getGoal();
        
        // register click handlers
        registerMeasurement(R.id.DecFruit, R.id.IncFruit, R.id.StatusFruit, Fruit);
        registerMeasurement(R.id.DecVegetable, R.id.IncVegetable, R.id.StatusVegetable, Vegetable);
        registerMeasurement(R.id.DecMeat, R.id.IncMeat, R.id.StatusMeat, Meat);
        registerMeasurement(R.id.DecDairy, R.id.IncDairy, R.id.StatusDairy, Dairy);
        registerMeasurement(R.id.DecGrain, R.id.IncGrain, R.id.StatusGrain, Grain);
        registerMeasurement(R.id.DecRGrain, R.id.IncRGrain, R.id.StatusRGrain, RGrain);
        registerMeasurement(R.id.DecNut, R.id.IncNut, R.id.StatusNut, Nut);
        registerMeasurement(R.id.DecFat, R.id.IncFat, R.id.StatusFat, Fat);
        registerMeasurement(R.id.DecWater, R.id.IncWater, R.id.StatusWater, Water);
        registerMeasurement(R.id.DecSalt, R.id.IncSalt, R.id.StatusSalt, Salt);
        
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
        
    	dao.destroy();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        dao.loadGoal(goal);
        
        repaintMeasurement(R.id.StatusFruit,  Fruit);
        repaintMeasurement(R.id.StatusVegetable, Vegetable);
        repaintMeasurement(R.id.StatusMeat,  Meat);
        repaintMeasurement(R.id.StatusDairy, Dairy);
        repaintMeasurement(R.id.StatusGrain, Grain);
        repaintMeasurement(R.id.StatusRGrain, RGrain);
        repaintMeasurement(R.id.StatusNut,  Nut);
        repaintMeasurement(R.id.StatusFat,  Fat);
        repaintMeasurement(R.id.StatusSalt,  Salt);
        repaintMeasurement(R.id.StatusWater,  Water);
    }
    
    
    protected void repaintMeasurement(int statusId,  Metric metric) {
        final TextView status = (TextView)findViewById(statusId);
        status.setText(Integer.toString(goal.getInt(metric)));
    }
    
    
    protected void registerMeasurement(int decId, int incId, int statusId, final Metric metric) {
        
        final TextView status = (TextView)findViewById(statusId);
         
        // decrement
        ((ImageView)findViewById(decId)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
                goal.decrement(metric);
                status.setText(Integer.toString(goal.getInt(metric)));
                
                dao.storeGoal(goal);
			}
		});
		
        // increment
        ((ImageView)findViewById(incId)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
                goal.increment(metric);
                status.setText(Integer.toString(goal.getInt(metric)));
                
                dao.storeGoal(goal);
			}
		});
		
	}


}