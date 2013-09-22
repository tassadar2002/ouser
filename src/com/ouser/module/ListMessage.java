package com.ouser.module;


/**
 * 消息总揽，每个藕丝一条
 * 我的消息界面的数据结构
 * @author Administrator
 *
 */
public class ListMessage {
	
	// 消息的标识
	private ChatId chatId = null;

	// 单聊，群聊都为最后说话人
	// 有可能是自己
	private Ouser ouser = new Ouser();
	
	// 群聊信息
	private Appoint appoint = new Appoint();
	
	private String content = "";
	
	// 单位：秒
	private int time = 0;
	private int count = 0;

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
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public Appoint getAppoint() {
		return appoint;
	}
	public void setAppoint(Appoint appoint) {
		this.appoint = appoint;
	}
	
	/**
	 * 是否为单聊
	 * @return
	 */
	public boolean isSingle() {
		return chatId == null ? true : chatId.isSingle();
	}
}
