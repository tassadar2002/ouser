package com.ouser.logic;

import com.ouser.event.EventArgs;
import com.ouser.event.EventCenter;
import com.ouser.event.EventId;
import com.ouser.event.EventListener;
import com.ouser.logic.event.StatusEventArgs;

import android.os.Handler;

public class BaseLogic {
	
	interface Factory {
		BaseLogic create();
	}
	
	protected Handler mHandler = new Handler();

	protected void fireStatusEvent(EventId eventId) {
		fireEvent(eventId, new StatusEventArgs(OperErrorCode.Success));
	}

	protected void fireStatusEvent(EventId eventId, OperErrorCode errCode) {
		fireEvent(eventId, new StatusEventArgs(errCode));
	}
	
	protected void fireEvent(final EventId eventId, final EventArgs args) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				EventCenter.self().fireEvent(eventId, args);
			}
		});
	}
	
	protected void fireStatusEvent(EventListener listener) {
		fireEvent(listener, new StatusEventArgs(OperErrorCode.Success));
	}

	protected void fireStatusEvent(EventListener listener, OperErrorCode errCode) {
		fireEvent(listener, new StatusEventArgs(errCode));
	}
	
	protected void fireEvent(final EventListener listener, final EventArgs args) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				listener.onEvent(EventId.eNone, args);
			}
		});
	}
}
