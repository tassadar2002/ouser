package com.ouser.cache;

import java.util.HashMap;
import java.util.Map;

import com.ouser.ui.topframework.TopFragmentType;

class TopFragmentDataCache {

	private Map<TopFragmentType, Object> mItems = new HashMap<TopFragmentType, Object>();
	
	public void setData(TopFragmentType type, Object data) {
		mItems.put(type, data);
	}
	
	public Object getData(TopFragmentType type) {
		if(mItems.containsKey(type)) {
			return mItems.get(type);
		}
		return null;
	}
	
	public void clear() {
		mItems.clear();
	}
}
