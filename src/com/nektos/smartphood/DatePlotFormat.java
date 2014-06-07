package com.nektos.smartphood;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatePlotFormat extends Format {
	 
	 
    // create a simple date format that draws on the year portion of our timestamp.
    // see http://download.oracle.com/javase/1.4.2/docs/api/java/text/SimpleDateFormat.html
    // for a full description of SimpleDateFormat.
    private final SimpleDateFormat dateFormat;

    public DatePlotFormat(String format) {
        dateFormat = new SimpleDateFormat(format);
    }

    @Override
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        long timestamp = ((Number) obj).longValue();
        Date date = new Date(timestamp);
        return dateFormat.format(date, toAppendTo, pos);
    }

    @Override
    public Object parseObject(String source, ParsePosition pos) {
        return null;

    }

}
