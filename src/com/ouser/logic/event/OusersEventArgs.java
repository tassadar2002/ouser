package com.ouser.logic.event;

import com.ouser.logic.OperErrorCode;
import com.ouser.module.Ousers;

public class OusersEventArgs extends StatusEventArgs {

	private Ousers mOusers = null;
	
	public OusersEventArgs(Ousers value) {
		super(OperErrorCode.Success);
		mOusers = value;
	}
	public OusersEventArgs(Ousers ousers, OperErrorCode errCode) {
		super(errCode);
		mOusers = ousers;
	}
	
	public Ousers getOusers() {
		return mOusers;
	}
}
