package com.ouser.persistor;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

class DatabaseHelper extends SQLiteOpenHelper {
	
	private static final int Version = 1;

	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}
	
	public DatabaseHelper(Context context, String name, int version){
		this(context, name, null, version);
	}
	
	public DatabaseHelper(Context context) {
		this(context, "ouser", Version);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(ChatDb.CreateTabelSql);
		db.execSQL(MessageDb.CreateTabelSql);
		db.execSQL(TimelineDb.CreateTabelSql);
		db.execSQL(AppointInviteCountDb.CreateTabelSql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
