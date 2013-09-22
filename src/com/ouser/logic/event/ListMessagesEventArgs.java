package com.ouser.logic.event;

import java.util.List;

import com.ouser.logic.OperErrorCode;
import com.ouser.module.ListMessage;

public class ListMessagesEventArgs extends StatusEventArgs {

	List<ListMessage> mValue = null;
	
	public ListMessagesEventArgs(List<ListMessage> value) {
		super(OperErrorCode.Success);
		this.mValue = value;
	}
	public ListMessagesEventArgs(OperErrorCode errCode) {
		super(errCode);
	}
	
	public List<ListMessage> getListMessages() {
		return mValue;
	}
}
