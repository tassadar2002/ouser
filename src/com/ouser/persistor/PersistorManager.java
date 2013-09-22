package com.ouser.persistor;

import java.util.List;

import com.ouser.module.Appoint;
import com.ouser.module.ChatId;
import com.ouser.module.ChatMessage;
import com.ouser.module.ListMessage;
import com.ouser.module.Timeline;
import com.ouser.module.User;

public class PersistorManager {

	private static PersistorManager ins = new PersistorManager();

	public static PersistorManager self() {
		return ins;
	}

	public User getLastestUser() {
		return new UserPreferences().getLastest();
	}

	public void setUser(User user) {
		new UserPreferences().set(user);
	}

	public void clearLastest() {
		new UserPreferences().clearLastestPass();
	}

	////////////////////////////列表消息////////////////////////////
	public List<ListMessage> getListMessages(String myUid) {
		return new MessageDb().getList(myUid);
	}
	
	public ListMessage getListMessage(String myUid, ChatId chatId) {
		return new MessageDb().get(myUid, chatId);
	}

	public void addListMessage(String myUid, List<ListMessage> messages) {
		new MessageDb().set(myUid, messages);
	}

	public void setListMessageLastMessage(String myUid, ListMessage message) {
		new MessageDb().setLastMessage(myUid, message);
	}

	public void setListMessageLastMessageAppointContent(String myUid, ChatId chatId, String name) {
		new MessageDb().setLastMessageAppointContent(myUid, chatId, name);
	}

	public void removeListMessage(String myUid, ChatId chatId) {
		new MessageDb().remove(myUid, chatId);
	}

	public void clearListMessageUnreadCount(String myUid, ChatId chatId) {
		new MessageDb().clearUnreadCount(myUid, chatId);
	}
	
	public void clearListMessagesUnreadCount(String myUid) {
		new MessageDb().clearUnreadCount(myUid);
	}

	////////////////////////////聊天消息////////////////////////////
	public List<ChatMessage> getChatMessages(String myUid, ChatId chatId) {
		return new ChatDb().getList(myUid, chatId);
	}

	public void appendMessage(String myUid, ChatMessage message) {
		new ChatDb().append(myUid, message);
	}

	public void appendMessages(String myUid, List<ChatMessage> messages) {
		new ChatDb().append(myUid, messages);
	}

	public void removeChatMessage(List<ChatMessage> messages) {
		new ChatDb().remove(messages);
	}
	
	public void clearChatMessage(String myUid, ChatId chatId) {
		new ChatDb().clear(myUid, chatId);
	}

	////////////////////////////动态////////////////////////////
	public List<Timeline> getTimeline(String myUid) {
		return new TimelineDb().getAll(myUid);
	}

	public void addTimeline(String myUid, List<Timeline> value) {
		new TimelineDb().addAll(myUid, value);
	}

	public void removeTimeline(Timeline value) {
		new TimelineDb().remove(value);
	}
	
	////////////////////////////友约邀请数////////////////////////////
	public int getAppointInviteCount(Appoint appoint, int time) {
		return new AppointInviteCountDb().getCount(appoint, time);
	}
	
	public void updateAppointInviteCount(Appoint appoint, int day, int incCount) {
		new AppointInviteCountDb().updateCount(appoint, day, incCount);
	}
}
