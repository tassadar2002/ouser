package com.ouser.location;

import com.ouser.logic.OperErrorCode;
import com.ouser.logic.event.StatusEventArgs;
import com.ouser.module.Location;

public class LocationEventArgs extends StatusEventArgs {

	private Location mLocation = null;
	private String mAddressName = "";
	
	public LocationEventArgs(Location location) {
		super(OperErrorCode.Success);
		mLocation = location;
	}
	public LocationEventArgs(Location location, String addressName) {
		super(OperErrorCode.Success);
		mLocation = location;
		mAddressName = addressName;
	}
	public LocationEventArgs(OperErrorCode errCode) {
		super(errCode);
		mLocation = new Location();
	}
	
	public Location getLocation() {
		return mLocation;
	}
	public String getAddressName() {
		return mAddressName;
	}
}
