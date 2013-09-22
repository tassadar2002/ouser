package com.ouser.persistor;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ouser.module.Timeline;

class TimelineDb extends BaseDb {

	private static final String T_Name = "timeline";
	private static final String F_Id = "_id";
	private static final String F_MyUid = "myuid";
	private static final String F_Uid = "uid";
	private static final String F_NickName = "nickname";
	private static final String F_Portrait = "portrait";
	private static final String F_Content = "content";
	private static final String F_Type = "type";
	private static final String F_Time = "time";
	private static final String F_AppointId = "appointid";
	private static final String F_Extra = "extra";
	
	static final String CreateTabelSql = String.format(
			"CREATE TABLE %s(" +
			"%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
			"%s TEXT, " +
			"%s TEXT, " +
			"%s TEXT, " +
			"%s TEXT, " +
			"%s TEXT, " +
			"%s INTEGER, " +
			"%s INTEGER, " +
			"%s TEXT, " + 
			"%s TEXT)",  
			T_Name, F_Id, F_MyUid, F_Uid, F_NickName, F_Portrait, F_Content, F_Type, F_Time, F_AppointId, F_Extra);
	
	public void addAll(String myUid, List<Timeline> timelines) {
		try {
			SQLiteDatabase db = openWritableDatabase();
			for(Timeline timeline : timelines) {
				ContentValues value = new ContentValues();
				value.put(F_MyUid, myUid);
				value.put(F_Uid, timeline.getUid());
				value.put(F_NickName, timeline.getName());
				value.put(F_Portrait, timeline.getPortrait().getUrl());
				value.put(F_Content, timeline.getContent());
				value.put(F_Type, Util.convertFromTimelineType(timeline.getType()));
				value.put(F_Time, timeline.getTime());
				value.put(F_AppointId, timeline.getAppointId().getAid());
				long id = db.insert(T_Name, null, value);
				if(id != -1) {
					timeline.setId(id);
				}
			}
		} finally {
			finalize();
		}
	}
	
	public void remove(Timeline value) {
		try {
			SQLiteDatabase db = openWritableDatabase();
			db.delete(T_Name, String.format("%s=?", F_Id), new String[]{String.valueOf(value.getId())});
		} finally {
			finalize();
		}
	}
	
	public List<Timeline> getAll(String myUid) {
		try {
			String where = String.format("%s=?", F_MyUid, F_Uid);
			Cursor c = query(T_Name, 
					new String[]{F_Id, F_Uid, F_NickName, F_Portrait, F_Content, F_Type, F_Time, F_AppointId}, 
					where,
					new String[]{myUid}, 
					String.format("%s DESC", F_Time));
			return convert(c);
		}
		finally {
			finalize();
		}
	}
	
	private List<Timeline> convert(Cursor c) {
		ArrayList<Timeline> timelines = new ArrayList<Timeline>();
		if(!c.moveToFirst()) {
			return timelines;
		}
		do {
			Timeline timeline = new Timeline();
			
			timeline.setId(c.getInt(c.getColumnIndex(F_Id)));
			timeline.setUid(c.getString(c.getColumnIndex(F_Uid)));
			timeline.setName(c.getString(c.getColumnIndex(F_NickName)));
			timeline.getPortrait().setUrl(c.getString(c.getColumnIndex(F_Portrait)));
			timeline.setContent(c.getString(c.getColumnIndex(F_Content)));
			timeline.setType(Util.convertToTimelineType(c.getInt(c.getColumnIndex(F_Type))));
			timeline.setTime(c.getInt(c.getColumnIndex(F_Time)));
			
			timeline.getAppointId().setUid(c.getString(c.getColumnIndex(F_Uid)));
			timeline.getAppointId().setAid(c.getString(c.getColumnIndex(F_AppointId)));
			timelines.add(timeline);
		} while(c.moveToNext());
		return timelines;
	}
}
