package com.ouser.logic.event;

import com.ouser.logic.OperErrorCode;
import com.ouser.module.Appoints;

public class AppointsEventArgs extends StatusEventArgs {

	private Appoints mAppoints = null;
	
	public AppointsEventArgs(Appoints value) {
		super(OperErrorCode.Success);
		mAppoints = value;
	}
	public AppointsEventArgs(Appoints appoints, OperErrorCode errCode) {
		super(errCode);
		mAppoints = appoints;
	}
	
	public Appoints getAppoints() {
		return mAppoints;
	}
}
