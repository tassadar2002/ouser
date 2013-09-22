package com.ouser.cache;

import java.util.HashMap;
import java.util.Map;

import com.ouser.module.Ouser;
import com.ouser.util.Const;

class OuserCache {
	
	private Map<String, OuserElement> mItems = new HashMap<String, OuserElement>();

	synchronized public Ouser getItem(String uid) {
		if(mItems.containsKey(uid)) {
			return mItems.get(uid).ouser;
		}
		return null;
	}
	
	synchronized public boolean isValid(String uid) {
		if(mItems.containsKey(uid)) {
			return System.currentTimeMillis() - mItems.get(uid).updateTime < Const.OuserInvalidTimeout;
		}
		return false;
	}
	
	synchronized public void invalid(String uid) {
		if(mItems.containsKey(uid)) {
			mItems.get(uid).updateTime = 0;
		}
	}
	
	synchronized public void setItem(Ouser value) {
		if(value == null) {
			return;
		}
		OuserElement element = new OuserElement();
		element.ouser = value;
		element.updateTime = System.currentTimeMillis();
		mItems.put(value.getUid(), element);
	}

	synchronized public Ouser getMySelf() {
		return getItem(Cache.self().getMyUid());
	}
	
	synchronized public void clear() {
		mItems.clear();
	}

	public static class OuserElement {
		private Ouser ouser = null;
		private long updateTime = 0;
		
		public Ouser getOuser() {
			return ouser;
		}
	}
}
