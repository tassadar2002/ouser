package com.ouser.ui.helper;

import java.util.HashMap;
import java.util.Map;

import com.ouser.event.EventArgs;
import com.ouser.event.EventCenter;
import com.ouser.event.EventId;
import com.ouser.event.EventListener;
import com.ouser.ui.base.BaseActivity;
import com.ouser.ui.base.BaseFragment;

/**
 * 界面层的EventListener的封装类，用于判断回调时界面是否还存在，不存在就不通知了
 */
public class UIEventListener implements EventListener {
	
	private BaseActivity mActivity = null;
	private BaseFragment mFragment = null;
	private EventListener mListener = null;
	
	public UIEventListener(BaseActivity activity, EventListener listener) {
		mActivity = activity;
		mListener = listener;
	}
	
	public UIEventListener(BaseFragment fragment, EventListener listener) {
		mFragment = fragment;
		mListener = listener;
	}

	@Override
	public void onEvent(EventId id, EventArgs args) {
		if(mActivity != null) {
			if(!mActivity.isCreated()) {
				return;
			}
		}
		if(mFragment != null) {
			if(!mFragment.isActivityCreated()) {
				return;
			}
		}
		mListener.onEvent(id, args);
	}
	
	public static class Helper {
		private Map<EventListener, UIEventListener> mContainer = new HashMap<EventListener, UIEventListener>();
		
		private BaseActivity mActivity = null;
		public void setHost(BaseActivity value) {
			mActivity = value;
		}
		
		private BaseFragment mFragement = null;
		public void setHost(BaseFragment value) {
			mFragement = value;
		}
		
		public UIEventListener createUIEventListener(EventListener listener) {
			if(mActivity != null) {
				return new UIEventListener(mActivity, listener);
			}
			if(mFragement != null) {
				return new UIEventListener(mFragement, listener);
			}
			return null;
		}
		
		public void add(EventId id, EventListener listener) {
			UIEventListener uiListener = createUIEventListener(listener);
			EventCenter.self().addListener(id, uiListener);
			mContainer.put(listener, uiListener);
		}
		
		public void remove(EventListener listener) {
			if(mContainer.containsKey(listener)) {
				UIEventListener uiListener = mContainer.get(listener);
				EventCenter.self().removeListener(uiListener);
				mContainer.remove(listener);
			}
		}
		
		public void clear() {
			for(UIEventListener uiListener : mContainer.values()) {
				EventCenter.self().removeListener(uiListener);
			}
			mContainer.clear();
		}
	}
}
