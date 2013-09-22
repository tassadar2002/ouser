package com.ouser.pusher;

import java.util.ArrayList;
import java.util.List;

import com.umeng.common.Log;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

@SuppressLint("HandlerLeak")
public class PushService extends Service {

	private int mNowMessage = 0;
	private List<PusherElement> mItems = new ArrayList<PusherElement>();
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			handle(msg.what);
		}
	};

	public class PushServiceBinder extends Binder {
		
		public PushService getService() {
			return PushService.this;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return new PushServiceBinder();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("push service", "on create");
	}

	/**
	 * 
	 * @param pusher
	 * @param interval 单位 秒
	 * @param imme
	 */
	synchronized public void startListen(Pusher pusher, String id, int interval, boolean imme) {
		for(PusherElement element : mItems) {
			if(element.mId.equals(id)) {
				return;
			}
		}
		PusherElement element = new PusherElement();
		element.mId = id;
		element.mPusher = pusher;
		element.mInterval = interval * 1000;
		element.mMessageId = ++mNowMessage;
		mItems.add(element);
		
		startRun(element, imme);
	}
	
	synchronized public void stopListen(String id) {
		for(PusherElement element : mItems) {
			if(element.mId.equals(id)) {
				mHandler.removeMessages(element.mMessageId);
				mItems.remove(element);
				break;
			}
		}
	}
	
	synchronized private void handle(int messageId) {
		for(PusherElement element : mItems) {
			if(element.mMessageId == messageId) {
				element.mPusher.onRequest();
				mHandler.sendEmptyMessageDelayed(messageId, element.mInterval);
				break;
			}
		}
	}
	
	private void startRun(PusherElement element, boolean imme) {
		if(mHandler.hasMessages(element.mMessageId)) {
			return;
		}
		if(imme) {
			mHandler.sendEmptyMessage(element.mMessageId);
		} else {
			mHandler.sendEmptyMessageDelayed(element.mMessageId, element.mInterval);
		}
	}
	
	private class PusherElement {
		private String mId = "";
		private Pusher mPusher = null;
		private long mInterval = 0;
		private int mMessageId = 0;
	}
}
