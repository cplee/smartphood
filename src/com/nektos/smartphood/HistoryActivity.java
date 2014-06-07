package com.nektos.smartphood;

import java.util.Set;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ZoomControls;

import com.androidplot.series.XYSeries;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;
import com.nektos.smartphood.model.MeasurementDAO;
import com.nektos.smartphood.model.Metric;
import com.nektos.smartphood.model.MetricSeriesList;

public class HistoryActivity extends Activity implements OnTouchListener{

    
    protected static final float SWIPE_MIN_DISTANCE = 0;
	protected static final float SWIPE_THRESHOLD_VELOCITY = 0;
	private XYPlot plot;
	private MetricSeriesList msl;
    private Mode mode;
	private Metric metric;
	private GestureDetector gestureDetector;
    private double upperBoundry;
    
	enum Mode {
		Week(7,"EEE",7),
		Month(30,"MMM dd",5),
		Quarter(90,"MMM",4),
		Year(365,"MMM",6);
        
        final int days;
        final String format;
        final int domainStep;
		private Mode(int days,String format,int domainStep) {
			this.days = days;
            this.format = format;
            this.domainStep = domainStep;
		}
	}
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
        
    	msl.destroy();
    }
	

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        
        ZoomControls zc = (ZoomControls) findViewById(R.id.zoomControls);
        
        // open DAO
        msl = new MetricSeriesList(new MeasurementDAO(getApplicationContext()));

        // Initialize our XYPlot reference:
        plot = (XYPlot) findViewById(R.id.MeasuermentOverTime);
 
        
//        plot.getRangeLabelWidget().setVisible(false);
        
//        plot.getDomainLabelWidget().setVisible(false);

        plot.getLegendWidget().setVisible(false);

 
        // By default, AndroidPlot displays developer guides to aid in laying out your plot.
        // To get rid of them call disableAllMarkup():
        plot.disableAllMarkup();
        
        this.metric = Metric.Fruit;
        this.mode = Mode.Quarter;
//        redraw();
        
        zc.setOnZoomInClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    mode = Mode.values()[mode.ordinal()-1];
                    redraw();
                } catch (Exception ex){}
            }
		});
        zc.setOnZoomOutClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    mode = Mode.values()[mode.ordinal()+1];
                    redraw();
                } catch (Exception ex){}
            }
		});
        
        this.gestureDetector = new GestureDetector(getApplicationContext(),
                new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDown(MotionEvent e) {
                    return true;	
                }
                @Override
                	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                        int direction = 0;
                        // right to left swipe
                        if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                            direction = 1;
                        }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                            direction = -1;
                        }
                        try {
                            if(direction != 0) {
                            	metric = Metric.values()[metric.ordinal()+direction];
                                redraw();
                            }
                        } catch (Exception ex) {
                        }
                		return super.onFling(e1, e2, velocityX, velocityY);
                	}	
                
                }
        
        );
        
        this.plot.setOnTouchListener(this);
        
    }

	public boolean onTouch(View v, MotionEvent event) {
            if(gestureDetector.onTouchEvent(event))
                return true;
            else
				return false;
	}
    
    
	private int getColor(Metric m) {
        int white = (255<<16) | (255<<8) | 255;
        int color = (white / Metric.values().length) * (m.ordinal()+1);
        return (255<<24) | color;
	}
    
    private void redraw() {
        Set<XYSeries> seriesSet = plot.getSeriesSet();
        for(XYSeries s: seriesSet) {
        	plot.removeSeries(s);
        }
        
        msl.reset();
        
//        plot.clear();
        
        Paint lineFill = new Paint();
        lineFill.setAlpha(200);
        lineFill.setShader(new LinearGradient(0, 0, 0, 250, Color.WHITE, getColor(metric), Shader.TileMode.MIRROR));
 
        LineAndPointFormatter format  = new LineAndPointFormatter(Color.rgb(0, 0,0), Color.BLUE, getColor(metric));
        format.setFillPaint(lineFill);
        
        XYSeries series = msl.createSeries(this.metric,mode.days);
        plot.addSeries(series, format);
        
        double newUpperBoundry = 0;
        for(int i=0; i<series.size(); i++) {
            try {
                newUpperBoundry = Math.max(newUpperBoundry, series.getY(i).doubleValue());
            } catch (Exception ex) {}
        }
        newUpperBoundry *= 1.5;
        
        BoundaryMode newMode = newUpperBoundry > upperBoundry?BoundaryMode.GROW:BoundaryMode.SHRINNK;
        upperBoundry = newUpperBoundry;
        
        
        int step = Math.max(metric.getStep(),(int) (newUpperBoundry/10d));
        plot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, step);
        plot.setRangeUpperBoundary(upperBoundry, newMode);
        plot.setRangeLabel(metric.name());
        
        
        plot.setDomainStep(XYStepMode.SUBDIVIDE, mode.domainStep);
        plot.setDomainValueFormat(new DatePlotFormat(mode.format));
        plot.setDomainLabel(mode.name());
        
        plot.redraw();
	}
    
    
    @Override
    protected void onResume() {
        super.onResume();
        
        redraw();
//        msl.reset();
//		plot.redraw();
    }



}