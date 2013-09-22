package com.ouser.module;

import java.util.Comparator;

public class ChatMessage {

	public enum Type {
		Text,
		Image,
		Location,
		Audio,
		Invite,
	}

	// 数据库中的id
	private long id = 0;
	
	private ChatId chatId = null;

	// 私聊,群聊时都表示说话人
	private Ouser ouser = new Ouser();
	
	// 常规数据
	private boolean isSend = false;
	private Type type = Type.Text;
	
	// 单位：毫秒
	private long time = 0;
	private String session = "";

	// 内容，文本，图片，音频专用
	private String content = "";
	
	// 内容，定位专用
	private LocationSection location = new LocationSection();
	
	// 内容，友约广播专用
	private InviteSection invite = new InviteSection();
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public ChatId getChatId() {
		return chatId;
	}
	public void setChatId(ChatId chatId) {
		this.chatId = chatId;
	}
	public Ouser getOuser() {
		return ouser;
	}
	public void setOuser(Ouser ouser) {
		this.ouser = ouser;
	}
	public boolean isSend() {
		return isSend;
	}
	public void setSend(boolean isSend) {
		this.isSend = isSend;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getSession() {
		return session;
	}
	public void setSession(String session) {
		this.session = session;
	}
	
	public LocationSection getLocation() {
		return location;
	}
	public void setLocation(LocationSection location) {
		this.location = location;
	}
	public InviteSection getInvite() {
		return invite;
	}
	public void setInvite(InviteSection invite) {
		this.invite = invite;
	}
	
	/**
	 * 是否为单聊
	 * @return
	 */
	public boolean isSingle() {
		return chatId == null ? true : chatId.isSingle();
	}

	public static class ComparatorByTime implements Comparator<ChatMessage> {

		 public int compare(ChatMessage arg0, ChatMessage arg1) {
			 if(arg0.getTime() < arg1.getTime()) {
				 return -1;
			 }
			 if(arg0.getTime() > arg1.getTime()) {
				 return 1;
			 }
			 return 0;
		 }
	}
	
	public static class ComparatorByTimeDesc implements Comparator<ChatMessage> {

		 public int compare(ChatMessage arg0, ChatMessage arg1) {
			 if(arg0.getTime() > arg1.getTime()) {
				 return -1;
			 }
			 if(arg0.getTime() < arg1.getTime()) {
				 return 1;
			 }
			 return 0;
		 }
	}
	
	public static class LocationSection {
		private String place = "";
		private Location location = new Location();
		public String getPlace() {
			return place;
		}
		public void setPlace(String place) {
			this.place = place;
		}
		public Location getLocation() {
			return location;
		}
		public void setLocation(Location location) {
			this.location = location;
		}
	}
	
	public static class InviteSection {
		private String content = "";
		private AppointId appointId = new AppointId();
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public AppointId getAppointId() {
			return appointId;
		}
		public void setAppointId(AppointId appointId) {
			this.appointId = appointId;
		}
	}
}
