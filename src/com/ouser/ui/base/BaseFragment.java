package com.ouser.ui.base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;

import com.ouser.event.EventId;
import com.ouser.event.EventListener;
import com.ouser.ui.component.Loading;
import com.ouser.ui.helper.UIEventListener;

@SuppressLint("HandlerLeak")
public class BaseFragment extends Fragment {
	
	//private final String clazz = getClass().toString();
	
	private boolean mActivityCreated = false;
	public boolean isActivityCreated() {
		return mActivityCreated;
	}
	
	public BaseFragment getFragment() {
		return this;
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
	// 不做类型检查，子类做
	public void startLoading() {
		((BaseActivity)getActivity()).startLoading();
	}
	
	public void stopLoading() {
		((BaseActivity)getActivity()).stopLoading();
	}
	
	public Loading getLoading() {
		return ((BaseActivity)getActivity()).getLoading();
	}
	
	////////////////////数据加载处理///////////////////////////
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 0) {
				if(mActivityCreated) {
					syncInitData((Bundle)msg.obj);
				} else {
					sendMessage(mHandler.obtainMessage(0, msg.obj));
				}
			}
		}
	};

	/**
	 * 异步初始化数据
	 * @remark 可以在replace之后马上调用，执行一定在onactivitycreated之后
	 */
	public void asyncInitData() {
		asyncInitData(null);
	}
	public void asyncInitData(Bundle bundle) {
		mHandler.sendMessage(mHandler.obtainMessage(0, bundle));
	}
	
	/** 同步初始化数据 */
	protected void syncInitData(Bundle bundle) {
	}
	
	///////////////////状态保存处理///////////////////////////
	/** fragment被销毁时缓存数据 */
	public void onSaveState() {
	}
	
	/** fragment恢复时初始化缓存数据 */
	public void onRestoreState() {
	}
	
	////////////////////与activity交互处理///////////////////////////
	/** activity的onActivityResult的间接调用 */
	public boolean onFragmentActivityResult(int requestCode, int resultCode, Intent intent) {
		return false;
	}
	
	////////////////////fragment帮助方法///////////////////////////
	/**
	 * 替换为新fragment
	 * @param id 父控件id
	 * @param fragment
	 */
	protected void replaceFragment(int id, Fragment fragment) {
		getChildFragmentManager().beginTransaction().replace(id, fragment).commit();
	}
	
	/**
	 * 移除fragment
	 * @param id 父控件id
	 */
	protected void removeFragment(int id) {
		// 正在销毁，不允许remove fragment
		// 否则异常java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
		if(((BaseActivity)getActivity()).isDestroyed()) {
			return;
		}
		Fragment fragment = findFragment(id);
		if(fragment != null) {
			getChildFragmentManager().beginTransaction().remove(fragment).commit();
		}
	}
	
	/**
	 * 获得fragment
	 * @param id 父控件id
	 * @return
	 */
	protected Fragment findFragment(int id) {
		return getChildFragmentManager().findFragmentById(id);
	}
	
	////////////////////fragment原生方法///////////////////////////
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
//		DebugLogger.write("onActivityCreated " + clazz);
		mActivityCreated = true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mEventListenerHelper.setHost(this);
//		DebugLogger.write("onCreate " + clazz);
	}

	@Override
	public void onDestroy() {
//		DebugLogger.write("onDestroy " + clazz);
		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
//		DebugLogger.write("onDestroyView " + clazz);
		mActivityCreated = false;
		mEventListenerHelper.clear();
		super.onDestroyView();
	}
}
