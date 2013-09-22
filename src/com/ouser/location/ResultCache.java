package com.ouser.location;

import com.amap.api.search.poisearch.PoiPagedResult;

public class ResultCache {

	private PoiPagedResult mResult = null;
	public ResultCache(PoiPagedResult value) {
		mResult = value;
	}
	PoiPagedResult getResult() {
		return mResult;
	}
}
