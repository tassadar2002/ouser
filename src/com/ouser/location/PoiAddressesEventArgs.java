package com.ouser.location;

import java.util.ArrayList;
import java.util.List;

import com.amap.api.search.poisearch.PoiPagedResult;
import com.ouser.logic.OperErrorCode;
import com.ouser.logic.event.StatusEventArgs;
import com.ouser.module.Place;

public class PoiAddressesEventArgs extends StatusEventArgs {
	
	private ResultCache mCache = null;
	private List<Place> mPlaces = null;
	
	public PoiAddressesEventArgs(List<Place> places, PoiPagedResult result) {
		super(OperErrorCode.Success);
		mPlaces = places;
		mCache = new ResultCache(result);
	}
	
	public PoiAddressesEventArgs(OperErrorCode errCode) {
		super(errCode);
		mPlaces = new ArrayList<Place>();
	}
	
	public ResultCache getCache() {
		return mCache;
	}
	
	public List<Place> getPlaces() {
		return mPlaces;
	}
}
