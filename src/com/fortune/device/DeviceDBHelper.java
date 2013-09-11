package com.fortune.device;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DeviceDBHelper extends SQLiteOpenHelper {
	public static final int DBVersion = 1;
	public static final String DBName = "FortuneDevice.db";
	public static final String TableName = "DeviceTable";
	
	public static final String ColumnSSID = "SSID";
	public static final String ColumnBSSID = "BSSID";
	public static final String ColumnPassword = "Password";
	public static final String ColumnStatus = "Status";

	public DeviceDBHelper(Context context) {
		super(context, DBName, null, DBVersion);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		final String initialTable = "CREATE TABLE IF NOT EXISTS " + TableName + "( " +
				"_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				ColumnSSID + " VARCHAR(50), " +
				ColumnBSSID + " VARCHAR(50), " +
				ColumnPassword + " VARCHAR(50), " +
				ColumnStatus + " VARCHAR(50) " +");";
		
		db.execSQL(initialTable);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		final String dropTable = "DROP TABLE IF EXISTS " + TableName;
        db.execSQL(dropTable);
        onCreate(db);
	}

}
