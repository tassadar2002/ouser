package com.ouser.cache;

import java.util.ArrayList;
import java.util.List;

class MemoryCache {
	
	private int mRecommendPlaceSerial = 0;

	private List<String> mInviteUploadPhoto = new ArrayList<String>();
	
	public void inviteUploadPhoto(String uid) {
		mInviteUploadPhoto.add(uid);
	}
	
	public boolean isInviteUpdatePhoto(String uid) {
		return mInviteUploadPhoto.contains(uid);
	}
	
	public int getRecommendPlaceSerial() {
		return ++mRecommendPlaceSerial;
	}
	
	public void clear() {
		mInviteUploadPhoto.clear();
		mRecommendPlaceSerial = 0;
	}
}
