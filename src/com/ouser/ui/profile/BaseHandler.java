package com.ouser.ui.profile;

import android.content.Intent;

import com.ouser.module.Ouser;

class BaseHandler {
	
	/** 请求的发出者 */
	public static class RequestFrom {
		
		/** 变更头像 */
		public static final int ChangePortait = 0;
		
		/** 添加照片 */
		public static final int AddPhoto = 1;
	}
	
	// property
	private ProfileActivity mActivity = null;
	public ProfileActivity getActivity() {
		return mActivity;
	}
	public void setActivity(ProfileActivity value) {
		mActivity = value;
	}
	
	// helper
	public Ouser getOuser() {
		return getActivity().getOuser();
	}
	public void setOuser(Ouser ouser) {
		getActivity().setOuser(ouser);
	}
	public boolean isMySelf() {
		return getActivity().isMySelf();
	}
	public HandlerFactory getHandlerFactory() {
		return getActivity().getHandlerFactory();
	}
	public void startLoading() {
		getActivity().startLoading();
	}
	public void stopLoading() {
		getActivity().stopLoading();
	}

	// interface
	public void onCreate() {
	}
	
	public void onPause() {
	}
	
	public void onDestroy() {
	}

	public void onActivityResult(int type, int result, Intent intent) {
		
	}
}

