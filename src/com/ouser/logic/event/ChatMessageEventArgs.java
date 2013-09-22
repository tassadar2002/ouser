package com.ouser.logic.event;

import com.ouser.logic.OperErrorCode;
import com.ouser.module.ChatMessage;

public class ChatMessageEventArgs extends StatusEventArgs {

	private ChatMessage mMessage = null;
	
	public ChatMessageEventArgs(ChatMessage message) {
		super(OperErrorCode.Success);
		mMessage = message;
	}
	public ChatMessageEventArgs(OperErrorCode errCode) {
		super(errCode);
	}
	
	public ChatMessage getMessage() {
		return mMessage;
	}
}
