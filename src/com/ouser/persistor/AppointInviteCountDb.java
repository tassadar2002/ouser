package com.ouser.persistor;

import android.content.ContentValues;
import android.database.Cursor;

import com.ouser.module.Appoint;

class AppointInviteCountDb extends BaseDb {

	private static final String T_Name = "appointinvitecount";
	private static final String F_Id = "_id";
	private static final String F_Aid = "aid";
	private static final String F_Uid = "uid";
	private static final String F_Count = "count";
	private static final String F_DAY = "day";
	private static final String F_Extra = "extra";
	
	static final String CreateTabelSql = String.format(
			"CREATE TABLE %s(" +
			"%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
			"%s TEXT, " +
			"%s TEXT, " +
			"%s INTEGER, " +
			"%s INTEGER, " +
			"%s TEXT)",  
			T_Name, F_Id, F_Aid, F_Uid, F_Count, F_DAY, F_Extra);
	
	public void updateCount(Appoint appoint, int day, int incCount) {
		int count = getCount(appoint, day);
		if(count == 0) {
			insert(appoint, day, incCount);
		} else {
			update(appoint, day, count + incCount);
		}
	}
	
	public int getCount(Appoint appoint, int day) {
		try {
			String whereStr = String.format(
					"%s=? and %s=? and %s=?", 
					F_Aid, F_Uid, F_DAY);
			String[] whereArgs = new String[]{ 
					appoint.getAppointId().getAid(),
					appoint.getAppointId().getUid(), 
					day + ""};
			Cursor c = query(T_Name, 
					new String[]{F_Count}, 
					whereStr,
					whereArgs);

			if(!c.moveToFirst()) {
				return 0;
			}
			return c.getInt(0);
		}
		finally {
			finalize();
		}
	}
	
	private void insert(Appoint appoint, int day, int count) {
		try {
			ContentValues values = new ContentValues();
			values.put(F_Aid, appoint.getAppointId().getAid());
			values.put(F_Uid, appoint.getAppointId().getUid());
			values.put(F_DAY, day);
			values.put(F_Count, count);
			openWritableDatabase().insert(T_Name, null, values);
		} finally {
			finalize();
		}
	}
	
	private void update(Appoint appoint, int day, int count) {
		try {
			ContentValues value = new ContentValues();
			value.put(F_Count, count);

			String whereStr = String.format(
					"%s=? and %s=? and %s=?", 
					F_Aid, F_Uid, F_DAY);
			String[] whereArgs = new String[]{ 
					appoint.getAppointId().getAid(),
					appoint.getAppointId().getUid(), 
					day + ""};
			openWritableDatabase().update(T_Name, value, whereStr, whereArgs);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			finalize();
		}
	}
}
