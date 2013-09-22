package com.ouser.event;

public interface EventListener {
	void onEvent(EventId id, EventArgs args);
}
