package com.ouser.logic.event;

import java.util.List;

import com.ouser.logic.OperErrorCode;
import com.ouser.module.NearOuser;

public class NearOusersEventArgs extends StatusEventArgs {

	private List<NearOuser> mValue = null;
	
	public NearOusersEventArgs(List<NearOuser> value) {
		super(OperErrorCode.Success);
		mValue = value;
	}
	public NearOusersEventArgs(OperErrorCode errCode) {
		super(errCode);
	}
	
	public List<NearOuser> getNearOusers() {
		return mValue;
	}
}
