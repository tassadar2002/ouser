package com.ouser.logic.event;

import java.util.List;

import com.ouser.logic.OperErrorCode;
import com.ouser.module.Place;

public class PlacesEventArgs extends StatusEventArgs {

	private List<Place> mPlaces = null;
	public PlacesEventArgs(List<Place> places) {
		super(OperErrorCode.Success);
		mPlaces = places;
	}
	
	public PlacesEventArgs(List<Place> places, OperErrorCode errCode) {
		super(errCode);
		mPlaces = places;
	}
	
	public List<Place> getPlaces() {
		return mPlaces;
	}
}
