package com.nektos.smartphood.model;

import static com.nektos.smartphood.model.Metric.BpDia;
import static com.nektos.smartphood.model.Metric.BpSys;
import static com.nektos.smartphood.model.Metric.Dairy;
import static com.nektos.smartphood.model.Metric.Fat;
import static com.nektos.smartphood.model.Metric.Fruit;
import static com.nektos.smartphood.model.Metric.Grain;
import static com.nektos.smartphood.model.Metric.Meat;
import static com.nektos.smartphood.model.Metric.Nut;
import static com.nektos.smartphood.model.Metric.RGrain;
import static com.nektos.smartphood.model.Metric.Salt;
import static com.nektos.smartphood.model.Metric.Vegetable;
import static com.nektos.smartphood.model.Metric.Water;
import static com.nektos.smartphood.model.Metric.Weight;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import com.androidplot.series.Series;


public class MeasurementDAO extends SQLiteOpenHelper{
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final String DATE_TAKEN = "DATE_TAKEN";
    private static final String DATE_TAKEN_TYPE = "STRING UNIQUE";
    
    private static final String DATABASE_NAME = "SmartPhood";
    private static final int    DATABASE_VERSION = 2;
    private static final String MEASUREMENT_TABLE_NAME = "measurement";


    
    
	private final SQLiteDatabase db;
    
	public MeasurementDAO(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.db = this.getWritableDatabase();
	}
    
	public Measurement getGoal() {
        Measurement m = new Measurement();
        loadGoal(m);
        return m;
	}
    
	public void destroy() {
	    this.db.close();	
	}
    
	public void loadGoal(Measurement goal) {
        try {
        	Cursor query = db.query(MEASUREMENT_TABLE_NAME, null, DATE_TAKEN+" is null", null, null, null, null);
            if(query.moveToFirst())
                mapToMeasurement(goal,query);
        } catch (Exception ex) {
        	setDefaultGoal(goal);
            storeGoal(goal);
        }
	}
    
	public Measurement getToday() {
        Measurement m = new Measurement();
        try {
            String todayDt = getTodayDate();
        	Cursor query = db.query(MEASUREMENT_TABLE_NAME, null, DATE_TAKEN+" = ?", new String[] {todayDt}, null, null, null);
            if(query.moveToFirst())
                mapToMeasurement(m,query);
        } catch (Exception ex) {
        }
        return m;
	}
    
	public void storeGoal(Measurement goal) {
        try {
            db.beginTransaction();  
            ContentValues v = mapToContentValues(goal);
    		int rows = db.update(MEASUREMENT_TABLE_NAME, v, DATE_TAKEN+" is null", null);
            if(rows == 0) {
                db.insert(MEASUREMENT_TABLE_NAME, null, v);
            }
            db.setTransactionSuccessful();
        } finally {
        	db.endTransaction();
        }
	}
	public void storeToday(Measurement today) {
        try {
            db.beginTransaction();  
            ContentValues v = mapToContentValues(today);
            String todayDt = getTodayDate();
            int rows = db.update(MEASUREMENT_TABLE_NAME, v, DATE_TAKEN+" = ?", new String[] {todayDt});
            if(rows == 0) {
                v.put(DATE_TAKEN, todayDt);
                db.insert(MEASUREMENT_TABLE_NAME, null, v);
            }
            db.setTransactionSuccessful();
        } finally {
        	db.endTransaction();
        }
	}
    
	public ArrayList<Measurement> getHistory(int limitDays) {
	    ArrayList<Measurement> history = new ArrayList<Measurement>();
        try {
            
        	Cursor query;
            if(limitDays > 0)
            	query = db.query(MEASUREMENT_TABLE_NAME, null, "julianday('now')-julianday("+DATE_TAKEN+") <= "+limitDays , null, null, null, DATE_TAKEN);
            else 
            	query = db.query(MEASUREMENT_TABLE_NAME, null, DATE_TAKEN+" is not null", null, null, null, DATE_TAKEN);
            while(query.moveToNext())
            {
                Measurement m = new Measurement();
        		mapToMeasurement(m,query);
                history.add(m);
            }
        } catch (Exception ex) {
        }
		return history;	
	}
    
	private String getTodayDate() {
        return DATE_FORMAT.format(new Date());
    }
    
	private ContentValues mapToContentValues(Measurement m) {
        ContentValues values = new ContentValues();
        for(Metric metric: Metric.values()) {
            Class<?> colType = metric.getColumnType();
            try {
                if(colType == Integer.class) {
                   values.put(metric.getColumnName(), m.getInt(metric));
                } else {
                   values.put(metric.getColumnName(), m.getString(metric));
                }
            } catch (Exception ex) {
            }
        }
        return values;
	}

	private void mapToMeasurement(Measurement m, Cursor query) {
        for(Metric metric: Metric.values()) {
            int colIdx = query.getColumnIndex(metric.getColumnName());
            Class<?> colType = metric.getColumnType();
            try {
                if(colType == Integer.class) {
                    m.setInt(metric, query.getInt(colIdx));
                } else {
                    m.setString(metric, query.getString(colIdx));
                }
            }  catch (Exception ex) {
            }
        }
        
		try {
            String dt = query.getString(query.getColumnIndex(DATE_TAKEN));
            if(dt != null)
    			m.setDateTaken(DATE_FORMAT.parse(dt));
            else
            	m.setDateTaken(null);
		} catch (ParseException e) {
		}
	}

    
	private void setDefaultGoal(Measurement goal) 
	{
        // TODO: I don't like this...
        goal.setInt(Fruit,5);
        goal.setInt(Vegetable,5);
        goal.setInt(Grain,3);
        goal.setInt(RGrain,3);
        goal.setInt(Nut,1);
        goal.setInt(Meat,2);
        goal.setInt(Dairy,3);
        goal.setInt(Fat,3);
        goal.setInt(Salt,1500);
        goal.setInt(Water,16);
	}


    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder create = new StringBuilder();
        create.append("CREATE TABLE ").append(MEASUREMENT_TABLE_NAME).append(" (");
        create.append(DATE_TAKEN).append(" ").append(DATE_TAKEN_TYPE);
        
        for(Metric metric : Metric.values()) {
            String type;
            Class<?> colType = metric.getColumnType();
            if(colType == Integer.class) {
                type = "INTEGER";
            } else {
                type = "STRING";
            }
            
            create.append(", ").append(metric.getColumnName()).append(" ").append(type);
        }
        
        create.append(");");
        
        db.execSQL(create.toString());
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            backupDB(db);
        } catch (Exception ex) {
        }
        
        if(oldVersion == 1 && newVersion == 2) {
            try {
                // add some columns
                db.execSQL("ALTER TABLE "+MEASUREMENT_TABLE_NAME+" ADD COLUMN "+RGrain.getColumnName()+" INTEGER");
                db.execSQL("ALTER TABLE "+MEASUREMENT_TABLE_NAME+" ADD COLUMN "+Water.getColumnName()+" INTEGER");
                db.execSQL("ALTER TABLE "+MEASUREMENT_TABLE_NAME+" ADD COLUMN "+Weight.getColumnName()+" INTEGER");
                db.execSQL("ALTER TABLE "+MEASUREMENT_TABLE_NAME+" ADD COLUMN "+BpSys.getColumnName()+" INTEGER");
                db.execSQL("ALTER TABLE "+MEASUREMENT_TABLE_NAME+" ADD COLUMN "+BpDia.getColumnName()+" INTEGER");
            } catch (Exception ex) {}
        }
	}

    private void backupDB(SQLiteDatabase db) throws IOException {
        File sd = new File(Environment.getExternalStorageDirectory(),"com.nektos.smartphood");
        sd.mkdirs();

        if (sd.canWrite()) {
            String currentDBPath = db.getPath();
            String backupDBPath = DATABASE_NAME+"-"+System.currentTimeMillis();
            File currentDB = new File(currentDBPath);
            File backupDB = new File(sd, backupDBPath);

            if (currentDB.exists()) {
                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
            }
        }
    }

	public int getRecentNonZero(Metric m) {
        try {
        	Cursor query = db.query(MEASUREMENT_TABLE_NAME, new String[] {m.getColumnName()}, DATE_TAKEN+" is not null and "+m.getColumnName()+" > 0", null, null, null, DATE_TAKEN+" desc", "1");
            if(query.moveToFirst())
                return query.getInt(0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
	}
}
