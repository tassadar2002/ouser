package com.ouser.logic.event;

import com.ouser.event.EventArgs;

public class MessageAndTimelineCountEventArgs extends EventArgs {

	private int unReadedMessageCount = 0;
	private int timelineCount = 0;
	
	public MessageAndTimelineCountEventArgs(int unReadedMessageCount, int timelineCount) {
		this.unReadedMessageCount = unReadedMessageCount;
		this.timelineCount = timelineCount;
	}

	public int getUnReadedMessageCount() {
		return unReadedMessageCount;
	}
	public int getTimelineCount() {
		return timelineCount;
	}
}
