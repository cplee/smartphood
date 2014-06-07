package com.nektos.smartphood.model;

import java.util.ArrayList;

import com.androidplot.series.XYSeries;

public class MetricSeriesList {
    private final MeasurementDAO dao;
    private ArrayList<Measurement> measurements;
    
    public MetricSeriesList(MeasurementDAO dao) {
        this.dao = dao;
	}
    
    public void destroy() {
    	this.dao.destroy();
    }

	public void reset() {
        measurements = null;
	}
    
    public XYSeries createSeries(Metric metric,int days) {
        return new MetricSeries(metric,days);	
    }
    
	public int size() {
        if(measurements==null) {
            return 0;
        }
        return measurements.size();
	}
    
    private ArrayList<Measurement> getMeasurements(int days) {
        if(measurements==null) {
        	measurements = dao.getHistory(days);
        }
    	return measurements;
    }
    
    private class MetricSeries implements XYSeries {

		private final Metric metric;
		private final int days;

		public MetricSeries(Metric metric,int days) {
            this.metric = metric;
            this.days = days;
		}

		public String getTitle() {
			return metric.toString();
		}

		public int size() {
			return getMeasurements(days).size();
		}

		public Number getX(int i) {
            return getMeasurements(days).get(i).getDateTaken().getTime();
		}

		public Number getY(int i) {
            return getMeasurements(days).get(i).getInt(metric);
		}
    	
    }



}
