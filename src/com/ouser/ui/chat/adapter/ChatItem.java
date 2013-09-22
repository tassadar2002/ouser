package com.ouser.ui.chat.adapter;

import com.ouser.module.ChatMessage;

/**
 * 消息列表中的项
 */
public class ChatItem {
	
	public enum Type {
		Message, /** 消息 */
		Time,
		/** session时间显示 */
	}

	private Type mType = Type.Message;
	
	// for type message
	private ChatMessage mMessage = null;
	
	// for type time
	private String mContent = "";
	
	// for audio
	private boolean mIsPlaying = false;
	private int mAudioLength = 0;
	private int mAudioCurrentPos = 0;
	
	// for edit
	private boolean mSelected = false;
	
	public Type getType() {
		return mType;
	}
	public void setType(Type value) {
		this.mType = value;
	}
	public ChatMessage getMessage() {
		return mMessage;
	}
	public void setMessage(ChatMessage value) {
		this.mMessage = value;
	}
	public String getContent() {
		return mContent;
	}
	public void setContent(String content) {
		this.mContent = content;
	}
	public boolean isPlaying() {
		return mIsPlaying;
	}
	public void setPlaying(boolean isPlaying) {
		this.mIsPlaying = isPlaying;
	}
	public int getAudioLength() {
		return mAudioLength;
	}
	public void setAudioLength(int value) {
		this.mAudioLength = value;
	}
	public int getAudioCurrentPos() {
		return mAudioCurrentPos;
	}
	public void setAudioCurrentPos(int audioCurrentPos) {
		this.mAudioCurrentPos = audioCurrentPos;
	}
	public boolean isSelected() {
		return mSelected;
	}
	public void setSelected(boolean selected) {
		this.mSelected = selected;
	}
}
