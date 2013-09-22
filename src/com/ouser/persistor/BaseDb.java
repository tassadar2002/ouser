package com.ouser.persistor;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ouser.util.Const;

class BaseDb {

	private DatabaseHelper mHelper = null;
	private SQLiteDatabase mDatabase = null;
	private Cursor mCursor = null;
	
	protected SQLiteDatabase openReadableDatabase() {
		if(mHelper == null) {
			mHelper = new DatabaseHelper(Const.Application);
		}
		mDatabase = mHelper.getReadableDatabase();
		return mDatabase;
	}
	
	protected SQLiteDatabase openWritableDatabase() {
		if(mHelper == null) {
			mHelper = new DatabaseHelper(Const.Application);
		}
		mDatabase = mHelper.getWritableDatabase();
		return mDatabase;
	}
	
	@Override
	protected void finalize() {
		finalizeCursor();
		finalizeDb();
	}
	
	protected void finalizeCursor() {
		if(mCursor != null) {
			mCursor.close();
			mCursor = null;
		}
	}

	protected void finalizeDb() {
		if(mDatabase != null) {
			mDatabase.close();
			mDatabase = null;
		}
	}
	
	protected Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String orderBy, String limit) {
		if(mDatabase == null) {
			openReadableDatabase();
		}
		mCursor = mDatabase.query(table, columns, selection, selectionArgs, null, null, orderBy, limit);
		return mCursor;
	}
	
	protected Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String orderBy) {
		return query(table, columns, selection, selectionArgs, orderBy, null);
	}
	
	protected Cursor query(String table, String[] columns, String selection, String[] selectionArgs) {
		return query(table, columns, selection, selectionArgs, null, null);
	}
}
