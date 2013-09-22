package com.ouser.cache;

import com.ouser.module.Location;
import com.ouser.module.User;
import com.ouser.persistor.PersistorManager;
import com.ouser.util.StringUtil;

class UserCache {
	
	private User mUser = null;
	
	private int mUnReadedMessageCount = 0;
	private int mTimelineCount = 0;
	
	public void onCreate() {
		mUser = PersistorManager.self().getLastestUser();
	}
	
	public boolean hasCookie() {
		return !StringUtil.isEmpty(mUser.getUid()) && !StringUtil.isEmpty(mUser.getPassword());
	}

	public String getUid() {
		return mUser.getUid();
	}
	
	public User getUser() {
		return mUser;
	}
	
	/**
	 * 设置用户信息，登录成功后调用
	 */
	public void setUserInfo(User user) {
		mUser = user;
		PersistorManager.self().setUser(mUser);
	}
	
	/**
	 * 退出登录
	 * 清空用户信息，登录失败后调用
	 * 会清空数据库中的最后成功登录用户标志位
	 */
	public void clear() {
		PersistorManager.self().clearLastest();
		mUser = PersistorManager.self().getLastestUser();
	}
	
	/**
	 * 变换物理位置
	 * @param value
	 */
	public void setLocation(Location value) {
		mUser.setLocation(value);
	}
	
	public void setCount(int unReaded, int timeline) {
		mUnReadedMessageCount = unReaded;
		mTimelineCount = timeline;
	}
	
	public void clearTimelineCount() {
		mTimelineCount = 0;
	}
	
	public int getUnReadedCount() {
		return mUnReadedMessageCount;
	}
	
	public int getTimelineCount() {
		return mTimelineCount;
	}
}
