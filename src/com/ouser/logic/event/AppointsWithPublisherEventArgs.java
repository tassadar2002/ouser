package com.ouser.logic.event;

import com.ouser.logic.OperErrorCode;
import com.ouser.module.AppointsWithPublisher;

public class AppointsWithPublisherEventArgs extends StatusEventArgs {

	private AppointsWithPublisher mAppoints = null;
	
	public AppointsWithPublisherEventArgs(AppointsWithPublisher value) {
		super(OperErrorCode.Success);
		mAppoints = value;
	}
	public AppointsWithPublisherEventArgs(AppointsWithPublisher appoints, OperErrorCode errCode) {
		super(errCode);
		mAppoints = appoints;
	}
	
	public AppointsWithPublisher getAppoints() {
		return mAppoints;
	}
}
