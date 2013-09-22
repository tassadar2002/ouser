package com.ouser.module;

public class Timeline {
	
	public enum Type {
		eOuser,
		eAppoint,
		eNone,
	}

	private long id = 0;
	private String content = "";
	private Ouser ouser = new Ouser();
	private Type type = Type.eNone;
	
	// 发生时间
	// 单位：分钟
	private int time = 0;
	
	// 友约类型专用
	private AppointId appointId = new AppointId();
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public String getUid() {
		return this.ouser.getUid();
	}
	public void setUid(String uid) {
		this.ouser.setUid(uid);
	}
	public AppointId getAppointId() {
		return appointId;
	}
	public void setAppointId(AppointId appointId) {
		this.appointId = appointId;
	}
	public Photo getPortrait() {
		return this.ouser.getPortrait();
	}
	public void setPortrait(Photo portrait) {
		this.ouser.setPortrait(portrait);
	}
	public String getName() {
		return this.ouser.getNickName();
	}
	public void setName(String name) {
		this.ouser.setNickName(name);
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
}
