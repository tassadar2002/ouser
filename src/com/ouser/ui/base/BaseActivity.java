package com.ouser.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.ouser.event.EventId;
import com.ouser.event.EventListener;
import com.ouser.ui.component.Loading;
import com.ouser.ui.helper.UIEventListener;
import com.ouser.util.Const;
import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends FragmentActivity {
	
	// helper
	public BaseActivity getActivity() {
		return this;
	}
	
	// flag
	/**
	 * activity是否正在销毁
	 * 主要用以判断逻辑层回调后是否继续执行
	 */
	private boolean mCreated = false;
	public boolean isCreated() {
		return mCreated;
	}

	// event
	private UIEventListener.Helper mEventListenerHelper = new UIEventListener.Helper();
	
	public UIEventListener createUIEventListener(EventListener listener) {
		return mEventListenerHelper.createUIEventListener(listener);
	}
	
	public void addUIEventListener(EventId id, EventListener listener) {
		mEventListenerHelper.add(id, listener);
	}
	
	public void removeUIEventListener(EventListener listener) {
		mEventListenerHelper.remove(listener);
	}
	
	// loading
	private Loading mLoading = null;
	public void startLoading() {
		if(!getLoading().isShow()) {
			getLoading().start(this);
		}
	}
	
	public void stopLoading() {
		getLoading().stop();
	}
	
	public Loading getLoading() {
		if(mLoading == null) {
			mLoading = new Loading();
		}
		return mLoading;
	}

	@Override
	public void onAttachFragment(Fragment fragment) {
		super.onAttachFragment(fragment);
//		DebugLogger.write("onAttachFragment " + this);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		mCreated = true;
		mEventListenerHelper.setHost(this);
//		DebugLogger.write("onCreate " + this);
	}

	@Override
	protected void onDestroy() {
		mCreated = false;
		mEventListenerHelper.clear();
//		DebugLogger.write("onDestroy " + this);
		super.onDestroy();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
//		DebugLogger.write("onNewIntent " + this);
	}

	@Override
	protected void onPause() {
//		DebugLogger.write("onPause " + this);
		if(mLoading != null) {
			mLoading.forceStop();
		}
		MobclickAgent.onPause(this);
		Const.Application.setActivityOnStop(this);
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Const.Application.setCurrentActivity(this);
		Const.Application.setActivityOnStart(this);
		MobclickAgent.onResume(this);
//		DebugLogger.write("onResume " + this);
	}

	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();
//		DebugLogger.write("onResumeFragments " + this);
	}

	@Override
	protected void onStart() {
		super.onStart();
//		DebugLogger.write("onStart " + this);
	}

	@Override
	protected void onStop() {
//		DebugLogger.write("onStop " + this);
		super.onStop();
	}

}
