package com.ouser.logger;

import android.util.Log;

public class Logger {
	
	private static final boolean ENABLE_LOG = true;

	private String mTag = "";
	public Logger(String tag) {
		mTag = tag;
	}
	
	public void v(String msg) {
		if(ENABLE_LOG) {
			Log.v(mTag, msg);
		}
	}
	public void d(String msg) {
		if(ENABLE_LOG) {
			Log.d(mTag, msg);
		}
	}
	public void i(String msg) {
		if(ENABLE_LOG) {
			Log.i(mTag, msg);
		}
	}
	public void w(String msg) {
		if(ENABLE_LOG) {
			Log.w(mTag, msg);
		}
	}
	public void e(String msg) {
		if(ENABLE_LOG) {
			Log.e(mTag, msg);
		}
	}
}
