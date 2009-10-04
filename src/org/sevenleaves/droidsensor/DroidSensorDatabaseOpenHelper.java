package org.sevenleaves.droidsensor;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DroidSensorDatabaseOpenHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "DEVICES";

	public static final int DATABASE_VERSION = 1;

	private static final String CREATE_TABLE_SQL;
	
	private static final String DROP_TABLE_SQL;
	
	static {
	
		StringBuilder b = new StringBuilder();
		b.append("create table DEVICES");
		b.append(" (");
		b.append("rowid integer primary key autoincrement,");
		b.append("ADDRESS text not null,");
		b.append("NAME text not null,");
		b.append("TWITTER_ID text");
		b.append(")");
		
		CREATE_TABLE_SQL = b.toString();
		
		b.setLength(0);
		b.append("drop table if existsDEVICES");
		
		DROP_TABLE_SQL = b.toString();
		
		
	}
	
	public DroidSensorDatabaseOpenHelper(Context context) {

		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL(CREATE_TABLE_SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		if(oldVersion == newVersion){
		
			return;
		}
		
		db.execSQL(DROP_TABLE_SQL);
		onCreate(db);
	}

}
