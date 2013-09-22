package com.ouser.ui.chat;

import android.content.Intent;
import android.view.MotionEvent;

import com.ouser.logic.ChatLogic;
import com.ouser.module.ChatId;

public class ChatBaseLayout {

	/** 逻辑层处理器 */
	private ChatLogic.ChatHandler mChatHandler = null;
	
	/** activity */
	private ChatActivity mActivity = null;
	
	/** 聊天Id */
	private ChatId mChatId = null;
	
	private LayoutFactory mLayoutFactory = null;

	public void setActivity(ChatActivity value) {
		mActivity = value;
	}
	public ChatActivity getActivity() {
		return mActivity;
	}
	
	public void setChatId(ChatId value) {
		mChatId = value;
	}
	public ChatId getChatId() {
		return mChatId;
	}
	
	public void setChatHandler(ChatLogic.ChatHandler value) {
		mChatHandler = value;
	}
	public ChatLogic.ChatHandler getChatHandler() {
		return mChatHandler;
	}

	public LayoutFactory getLayoutFactory() {
		return mLayoutFactory;
	}
	public void setLayoutFactory(LayoutFactory layoutFactory) {
		this.mLayoutFactory = layoutFactory;
	}
	
	public void onCreate() {
	}
	
	public void onPause() {
	}
	
	public void onResume() {
	}
	
	public void onDestroy() {
	}
	
	public void onActivityResult(int type, int result, Intent intent) {
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		return false;
	}
}
