package com.ouser.cache;

import com.ouser.module.Location;
import com.ouser.module.Ouser;
import com.ouser.module.User;
import com.ouser.ui.topframework.TopFragmentType;

public class Cache {

	private static Cache ins = new Cache();

	public static Cache self() {
		return ins;
	}

	private UserCache mUserCache = new UserCache();
	private OuserCache mOuserCache = new OuserCache();
	private MemoryCache mMemoryCache = new MemoryCache();
	private TopFragmentDataCache mTopFragmentDataCache = new TopFragmentDataCache();

	// ouser
	public Ouser getOuser(String uid) {
		return mOuserCache.getItem(uid);
	}

	public boolean isOuserValid(String uid) {
		return mOuserCache.isValid(uid);
	}
	
	public void invalidOuser(String uid) {
		mOuserCache.invalid(uid);
	}

	public void setOuser(Ouser value) {
		mOuserCache.setItem(value);
	}

	public Ouser getMySelfOuser() {
		return mOuserCache.getMySelf();
	}

	// user
	public boolean hasCookie() {
		return mUserCache.hasCookie();
	}

	public String getMyUid() {
		return mUserCache.getUid();
	}

	public User getMySelfUser() {
		return mUserCache.getUser();
	}
	
	public boolean isMySelf(String id) {
		return id.equals(mUserCache.getUid());
	}

	public void setMySelf(User user) {
		mUserCache.setUserInfo(user);
	}

	public void clearMySelf() {
		mUserCache.clear();
		mOuserCache.clear();
		mMemoryCache.clear();
		mTopFragmentDataCache.clear();
	}

	public void setMyLocation(Location value) {
		mUserCache.setLocation(value);
	}
	
	// memory
	public void inviteUploadPhoto(String uid) {
		mMemoryCache.inviteUploadPhoto(uid);
	}
	public boolean isInviteUpdatePhoto(String uid) {
		return mMemoryCache.isInviteUpdatePhoto(uid);
	}
	
	public int getRecommendPlaceSerial() {
		return mMemoryCache.getRecommendPlaceSerial();
	}
	
	// message
	public void setMessageCount(int unReaded, int timeline) {
		mUserCache.setCount(unReaded, timeline);
	}
	
	public int getUnReadedCount() {
		return mUserCache.getUnReadedCount();
	}
	
	public int getTimelineCount() {
		return mUserCache.getTimelineCount();
	}
	
	public void clearTimelineCount() {
		mUserCache.clearTimelineCount();
	}
	
	// top fragment
	public void setTopFragmentData(TopFragmentType type, Object data) {
		mTopFragmentDataCache.setData(type, data);
	}
	
	public Object getTopFragmentData(TopFragmentType type) {
		return mTopFragmentDataCache.getData(type);
	}

	/**
	 * 启动处理
	 */
	public void onCreate() {
		mUserCache.onCreate();
	}
}
