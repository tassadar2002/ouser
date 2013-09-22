package com.ouser.pusher;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;

import com.ouser.util.Const;

/**
 * 是否常驻？
 * @author hanlixin
 *
 */
@SuppressLint("HandlerLeak")
public class PusherManager {
	
	private static final PusherManager ins = new PusherManager();
	public static PusherManager self() {
		return ins;
	}
	
	private PushService mService = null;
	
	private ServiceConnection mConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = ((PushService.PushServiceBinder)service).getService();
		}
	};
	
	private PusherManager() {
		Intent intent = new Intent(Const.Application, PushService.class);
		Const.Application.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}
	
	public void onDestroy() {
		Const.Application.unbindService(mConnection);
	}
	
	public void startListen(Pusher pusher, String id, int interval, boolean imme) {
		if(mService == null) {
			final Pusher fPusher = pusher;
			final String fId = id;
			final int fInterval = interval;
			final boolean fImme = imme;
			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					if(mService != null) {
						mService.startListen(fPusher, fId, fInterval, fImme);
					} else {
						handler.postDelayed(this, 500);
					}
				}
				
			}, 500);
		} else {
			mService.startListen(pusher, id, interval, imme);
		}
	}
	
	public void stopListen(String id) {
		if(mService != null) {
			mService.stopListen(id);
		}
	}
}
