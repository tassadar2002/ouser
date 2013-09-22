package com.ouser.logic.event;

import java.util.List;

import com.ouser.logic.OperErrorCode;
import com.ouser.module.FollowAppoint;

public class FollowAppointsEventArgs extends StatusEventArgs {
	
	private List<FollowAppoint> mAppoints = null;
	
	public FollowAppointsEventArgs(List<FollowAppoint> value) {
		super(OperErrorCode.Success);
		mAppoints = value;
	}
	public FollowAppointsEventArgs(List<FollowAppoint> appoints, OperErrorCode errCode) {
		super(errCode);
		mAppoints = appoints;
	}
	
	public List<FollowAppoint> getAppoints() {
		return mAppoints;
	}
}
