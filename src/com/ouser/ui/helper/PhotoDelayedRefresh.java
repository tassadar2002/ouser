package com.ouser.ui.helper;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.widget.BaseAdapter;

/**
 * 延时刷新adapter view
 * 防止刷新次数过多导致界面卡住
 */
@SuppressLint("HandlerLeak")
public class PhotoDelayedRefresh {
	
	private static final int DelayedTick = 500;
	
	private BaseAdapter mAdapter = null;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			mAdapter.notifyDataSetChanged();
		}
	};
	
	public PhotoDelayedRefresh(BaseAdapter adapter) {
		mAdapter = adapter;
	}
	
	public void notifyDataSetChanged() {
		mHandler.removeMessages(0);
		mHandler.sendEmptyMessageDelayed(0, DelayedTick);
	}
}
