package com.ouser.logic.event;

import com.ouser.logic.OperErrorCode;
import com.ouser.module.Appoint;

public class AppointEventArgs extends StatusEventArgs {
	
	private Appoint mAppoint = null;
	
	public AppointEventArgs(Appoint value) {
		super(OperErrorCode.Success);
		mAppoint = value;
	}
	public AppointEventArgs(Appoint value, OperErrorCode errCode) {
		super(errCode);
	}
	
	public Appoint getAppoint() {
		return mAppoint;
	}
}
