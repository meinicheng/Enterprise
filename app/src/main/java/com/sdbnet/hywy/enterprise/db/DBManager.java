package com.sdbnet.hywy.enterprise.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {
	private static DBManager mDBManagerInstance = null;
	private Context mContext = null;
	private DBHelper dbHelper = null;
	private SQLiteDatabase mSQLiteDB = null;

	public DBManager(Context ctx) {
		mContext = ctx;
		if (dbHelper == null) {
			dbHelper = DBHelper.getInstance(ctx);
			mSQLiteDB = dbHelper.getWritableDatabase();
		}
	}

	public void closeDatabase() {
		dbHelper.close();
	}

	// public static DBManager getDBManager(Context ctx){
	// if(mDBManagerInstance == null ){
	// mDBManagerInstance = new DBManager(ctx);
	// }
	// return mDBManagerInstance;
	// }

}
