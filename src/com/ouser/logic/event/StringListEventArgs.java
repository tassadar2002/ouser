package com.ouser.logic.event;

import java.util.List;

import com.ouser.logic.OperErrorCode;

public class StringListEventArgs extends StatusEventArgs {

	private List<String> mStrings = null;
	
	public StringListEventArgs(List<String> value) {
		super(OperErrorCode.Success);
		mStrings = value;
	}
	public StringListEventArgs(OperErrorCode errCode) {
		super(errCode);
	}
	
	public List<String> getStrings() {
		return mStrings;
	}
}
