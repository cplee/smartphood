package com.nektos.smartphood.model;

public enum Metric {
    Fruit("FRUIT",Integer.class,1),
    Vegetable("VEGETABLE",Integer.class,1),
    Meat("MEAT",Integer.class,1),
    Dairy("DAIRY",Integer.class,1),
    Nut("NUT",Integer.class,1),
    Grain("GRAIN",Integer.class,1),
    RGrain("RGRAIN",Integer.class,1),
    Fat("FAT",Integer.class,1),
    Salt("SALT",Integer.class,100),
    Water("WATER",Integer.class,1),
    BpSys("BPSYS",Integer.class,1),
    BpDia("BPDIA",Integer.class,1),
    Weight("WEIGHT",Integer.class,1);
    
    private final String columnName;
    private final Class<?> columnType;
    private final int step;
    private Metric(String columnName,Class<?> columnType,int step) {
        this.columnName = columnName;	
        this.columnType = columnType;
        this.step = step;
    }
    
    public String getColumnName() {
    	return columnName;
    }
    
    public Class<?> getColumnType() {
		return columnType;
	}
    
    public int getStep() {
		return step;
	}




}
