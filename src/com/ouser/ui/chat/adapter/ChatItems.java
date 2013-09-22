package com.ouser.ui.chat.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.ouser.module.ChatMessage;
import com.ouser.util.Const;

public class ChatItems {

	private List<ChatItem> mItems = new ArrayList<ChatItem>();

	private SimpleDateFormat mDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
			Locale.CHINA);

	public void add(ChatMessage message) {
		ChatItem item = new ChatItem();
		item.setMessage(message);
		item.setType(ChatItem.Type.Message);
		mItems.add(item);
		adjust();
	}

	public void addRange(List<ChatMessage> messages) {
		for (ChatMessage message : messages) {
			ChatItem item = new ChatItem();
			item.setMessage(message);
			item.setType(ChatItem.Type.Message);
			mItems.add(item);
		}
		adjust();
	}

	public int size() {
		return mItems.size();
	}

	public ChatItem get(int pos) {
		return mItems.get(pos);
	}
	
	public void clear() {
		mItems.clear();
	}
	
	// TODO 改为iterator
	public List<ChatItem> getItems() {
		return mItems;
	}

	private void adjust() {
		filterTime();
		sort();
		addTime();
	}

	private void filterTime() {
		List<ChatItem> timeItems = new ArrayList<ChatItem>();
		for (ChatItem item : mItems) {
			if (item.getType() == ChatItem.Type.Time) {
				timeItems.add(item);
			}
		}
		mItems.removeAll(timeItems);
	}

	private void sort() {
		Collections.sort(mItems, new ComparatorByTime());
	}

	private void addTime() {

		if (mItems.size() == 0) {
			return;
		}

		// 上一条日志
		ChatMessage lastMessage = new ChatMessage();
		lastMessage.setTime((-1) * Const.SessionInterval);

		int index = 0;
		while (index < mItems.size()) {

			ChatMessage message = mItems.get(index).getMessage();
			if (message.getTime() - lastMessage.getTime() > Const.SessionInterval) {

				// 添加时间间隔
				Date date = new Date();
				date.setTime(message.getTime());

				ChatItem item = new ChatItem();
				item.setType(ChatItem.Type.Time);
				item.setContent(mDateTimeFormat.format(date));

				mItems.add(index, item);
				++index;
			}
			lastMessage = message;
			++index;
		}
	}

	public static class ComparatorByTime implements Comparator<ChatItem> {

		public int compare(ChatItem arg0, ChatItem arg1) {
			if (arg0.getMessage().getTime() < arg1.getMessage().getTime()) {
				return -1;
			}
			if (arg0.getMessage().getTime() > arg1.getMessage().getTime()) {
				return 1;
			}
			return 0;
		}
	}
}
