package com.jmcdale.wifinder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.net.wifi.ScanResult;

public class WiFiDataSQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_TRAINING_DATA = "training";
	public static final String COL_ID = "_id";
	public static final String COL_BSSID = "bssid";
	public static final String COL_LOCATION = "location";
	public static final String COL_LEVEL = "level";
	public static final String COL_CREATED = "created";
	
//	public static final String COL_SSID = "ssid";
//	public static final String COL_CAPABILITIES = "capabilities";
//	public static final String COL_FREQUENCY = "freq";

	private static final String DATABASE_NAME = "wifitracking.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "CREATE TABLE "+ TABLE_TRAINING_DATA + "( " 
			+ COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
			+ COL_BSSID + " TEXT NOT NULL, "
			+ COL_LOCATION + " TEXT NOT NULL, "
			+ COL_LEVEL + " INTEGER NOT NULL, "
			+ COL_CREATED + " INTEGER NOT NULL "
			+" );";

	public WiFiDataSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(WiFiDataSQLiteHelper.class.getName(),
	        "Upgrading database from version " + oldVersion + " to "
	            + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAINING_DATA);
		onCreate(db);
	}
	
	/////////////////////////////////////////////////////////////////
	
	public boolean insertTrainingSet(String pLocation, List<ScanResult> pTrainingData){
		SQLiteDatabase db = this.getWritableDatabase();
		boolean success = true;
		long createDate = Calendar.getInstance().getTimeInMillis();
		db.beginTransaction();
		try{
			long id;
			for (ScanResult scanResult : pTrainingData) {
				ContentValues cv = new ContentValues();
				cv.put(COL_BSSID, scanResult.BSSID);
				cv.put(COL_LOCATION, pLocation);
				cv.put(COL_LEVEL, scanResult.level);
				cv.put(COL_CREATED, createDate);
				id = db.insertOrThrow(TABLE_TRAINING_DATA, null, cv);
				if(success && id < 0) success = false;
			}
			if(success){
				db.setTransactionSuccessful();
			}
		}catch(Exception e){
			Log.e(WiFiDataSQLiteHelper.class.getName(), "Error during INSERT: " + e.getMessage());
		}
		
		db.endTransaction();
		
		db.close();
		Log.i("WiFiDataSQLiteHelper.class.getName()", "INSERTION SUCCESS?  " + success);
		return success;
	}
	
	public static final String SELECT_DISTINCT_LOCATIONS = "SELECT DISTINCT " + COL_LOCATION + " FROM " + TABLE_TRAINING_DATA;
	
	public ArrayList<String> getLocations(){
		SQLiteDatabase db = this.getWritableDatabase();
		ArrayList<String> locations = new ArrayList<String>();
		
		Cursor c = db.rawQuery(SELECT_DISTINCT_LOCATIONS, null);
		int col = c.getColumnIndex(COL_LOCATION);
		c.moveToFirst();
		while(!c.isAfterLast()){
			locations.add(c.getString(col));
			Log.i("WiFiDataSQLiteHelper.class.getName()", "LOCATION: "+ c.getString(col));
			c.moveToNext();
		}
		db.close();
		return locations;
	}

}