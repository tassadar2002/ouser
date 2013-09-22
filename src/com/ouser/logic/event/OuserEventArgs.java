package com.ouser.logic.event;

import com.ouser.logic.OperErrorCode;
import com.ouser.module.Ouser;

public class OuserEventArgs extends StatusEventArgs {
	
	private Ouser mOuser = null;
	
	public OuserEventArgs(Ouser value) {
		super(OperErrorCode.Success);
		mOuser = value;
	}
	public OuserEventArgs(Ouser ouser, OperErrorCode errCode) {
		super(errCode);
		mOuser = ouser;
	}
	
	public Ouser getOuser() {
		return mOuser;
	}
}
