package com.nektos.smartphood.model;

import java.util.Date;
import java.util.Hashtable;
import java.util.Map;


public class Measurement {
    private Date dateTaken;
    private final Map<Metric,Object> metrics = new Hashtable<Metric,Object>();
    


	public void decrement(Metric metric) {
        Integer i = getInt(metric);
        if(i == null) {
            metrics.put(metric,0);
        } else if(i >= metric.getStep() ){
            setInt(metric,i-metric.getStep());
        } else {
            setInt(metric,0);
        }
	}

	public void increment(Metric metric) {
        Integer i = getInt(metric);
        if(i == null) {
            metrics.put(metric,0);
        } else {
            setInt(metric,i+metric.getStep());
        }
	}

	public Integer getInt(Metric metric) {
        Object o = metrics.get(metric);
        if(o==null) {
            metrics.put(metric, 0);
        	return 0;
        }
        else if (o instanceof Integer) {
        	return (Integer)o;
        } else {
        	try {return Integer.parseInt(getString(metric));} catch (Exception ex) { return null; }
        }
	}
	public String getString(Metric metric) {
        return String.valueOf(metrics.get(metric));
	}

	public Double getDouble(Metric metric) {
        Object o = metrics.get(metric);
        if(o==null)
        	return null;
        else if (o instanceof Double) {
        	return (Double)o;
        } else {
        	try {return Double.parseDouble(getString(metric));} catch (Exception ex) { return null; }
        }
	}

	public void setInt(Metric metric, int val) {
        metrics.put(metric, val);
	}
    
	public void setString(Metric metric, String val) {
        metrics.put(metric, val);
	}

	public void setDouble(Metric metric, double val) {
        metrics.put(metric, val);
	}

	public Date getDateTaken() {
		return dateTaken;
	}

	void setDateTaken(Date dateTaken) {
		this.dateTaken = dateTaken;
	}

}
