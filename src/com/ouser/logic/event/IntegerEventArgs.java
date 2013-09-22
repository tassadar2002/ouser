package com.ouser.logic.event;

import com.ouser.logic.OperErrorCode;

public class IntegerEventArgs extends StatusEventArgs {

	private int mInt = 0;
	
	public IntegerEventArgs(int value) {
		super(OperErrorCode.Success);
		mInt = value;
	}
	public IntegerEventArgs(int value, OperErrorCode errCode) {
		super(errCode);
		mInt = value;
	}
	
	public int getInteger() {
		return mInt;
	}
}
