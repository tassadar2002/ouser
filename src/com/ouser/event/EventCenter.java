package com.ouser.event;

import java.util.ArrayList;
import java.util.List;

import com.ouser.util.ListUtil;

public class EventCenter {
	
	private static EventCenter ins = new EventCenter();
	public static EventCenter self() {
		return ins;
	}

	private List<EventListenerPackage> mListeners = new ArrayList<EventListenerPackage>();

	public void addListener(EventListener value)	{
		synchronized (this.mListeners) {
			this.mListeners.add(new EventListenerPackage(EventId.eAll, value));
		}
	}
	
	/**
	 * 添加监听器
	 * @param id
	 * @param listener
	 * @remark 不会重复添加
	 */
	public void addListener(EventId id, EventListener listener)	{
		synchronized (this.mListeners) {
			if(!hasListener(id, listener)) {
				this.mListeners.add(new EventListenerPackage(id, listener));
			}
		}
	}

	public void removeListener(EventListener value)	{
		
		synchronized (mListeners) {
			List<EventListenerPackage> listeners = null;
			listeners = ListUtil.clone(mListeners);
			for(EventListenerPackage pack: listeners) {
				if(pack.listener == value) {
					mListeners.remove(pack);
				}
			}
		}
	}

	public void fireEvent(EventId id, EventArgs args)	{
		
		List<EventListenerPackage> listeners = null;
		synchronized (mListeners) {
			listeners = ListUtil.clone(mListeners);
		}
		for(EventListenerPackage pack: listeners) {
			if(pack.id == EventId.eAll) {
				pack.listener.onEvent(id, args);
			}
			if(pack.id == id) {
				pack.listener.onEvent(id, args);
			}
		}
	}
	
	private boolean hasListener(EventId id, EventListener listener) {
		for(EventListenerPackage pack: mListeners) {
			if(pack.id == id && pack.listener == listener) {
				return true;
			}
		}
		return false;
	}
	
	private static class EventListenerPackage
	{
		public EventListenerPackage(EventId id, EventListener listener)	{
			this.id = id;
			this.listener = listener;
		}
		private EventId id;
		private EventListener listener;
	}
}




