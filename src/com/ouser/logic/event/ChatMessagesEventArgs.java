package com.ouser.logic.event;

import java.util.List;

import com.ouser.logic.OperErrorCode;
import com.ouser.module.ChatMessage;

public class ChatMessagesEventArgs extends StatusEventArgs {

	List<ChatMessage> mValue = null;
	
	public ChatMessagesEventArgs(List<ChatMessage> value) {
		super(OperErrorCode.Success);
		this.mValue = value;
	}
	public ChatMessagesEventArgs(OperErrorCode errCode) {
		super(errCode);
	}
	
	public List<ChatMessage> getChatMessages() {
		return mValue;
	}
}
