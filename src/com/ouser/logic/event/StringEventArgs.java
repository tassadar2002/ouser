package com.ouser.logic.event;

import com.ouser.logic.OperErrorCode;

public class StringEventArgs extends StatusEventArgs {

	private String mString = null;
	
	public StringEventArgs(String value) {
		super(OperErrorCode.Success);
		mString = value;
	}
	public StringEventArgs(String value, OperErrorCode errCode) {
		super(errCode);
		mString = value;
	}
	
	public String getString() {
		return mString;
	}
}
