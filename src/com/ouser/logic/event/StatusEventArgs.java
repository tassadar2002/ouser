package com.ouser.logic.event;

import com.ouser.event.EventArgs;
import com.ouser.logic.OperErrorCode;

public class StatusEventArgs extends EventArgs {
	
	private OperErrorCode mErrCode = OperErrorCode.None;
	
	public StatusEventArgs(OperErrorCode errCode) {
		mErrCode = errCode;
	}
	public OperErrorCode getErrCode() {
		return mErrCode;
	}
}
