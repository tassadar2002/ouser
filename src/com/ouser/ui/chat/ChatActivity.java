package com.ouser.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

import com.ouser.R;
import com.ouser.logic.ChatLogic;
import com.ouser.logic.LogicFactory;
import com.ouser.module.ChatId;
import com.ouser.module.ChatIdFactory;
import com.ouser.ui.base.BaseActivity;
import com.ouser.ui.component.HeadBar;
import com.ouser.util.Const;

public class ChatActivity extends BaseActivity {
	
	private HeadBar mHeadBar = null;

	/** 逻辑层对象 */
	private ChatLogic.ChatHandler mChatHandler = null;

	/** 界面元素的容器 */
	private LayoutFactory mLayoutFactory = new LayoutFactory();
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.actvy_chat);
		
		Bundle bundle = getIntent().getBundleExtra(Const.Intent.ChatId);
		ChatId chatId = ChatIdFactory.fromBundle(bundle);

		mChatHandler = LogicFactory.self().getChat().startChat(chatId);
		
		if(!chatId.isSingle()) {
			mChatHandler.setCacheAppointContent(
					getIntent().getStringExtra(Const.Intent.AppointContent));
		}

		mHeadBar = new HeadBar();
		mHeadBar.onCreate(findViewById(R.id.layout_head_bar), this);
		mHeadBar.setTitle(chatId.isSingle() ? "Ta的消息" : "聊天室");
		mHeadBar.setActionButton(R.drawable.title_edit, new HeadBar.OnActionListener() {
			
			@Override
			public void onClick() {
				mLayoutFactory.getEdit().onEdit();
				mLayoutFactory.getMessage().onEdit();
				setActionButtonImage();
			}
		});

		for(ChatBaseLayout layout : mLayoutFactory.getLayouts()) {
			layout.setActivity(this);
			layout.setChatId(chatId);
			layout.setChatHandler(mChatHandler);
			layout.setLayoutFactory(mLayoutFactory);
			layout.onCreate();
		}
	}

	@Override
	protected void onPause() {
		for(ChatBaseLayout layout : mLayoutFactory.getLayouts()) {
			layout.onPause();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		for(ChatBaseLayout layout : mLayoutFactory.getLayouts()) {
			layout.onResume();
		}
	}

	@Override
	protected void onDestroy() {
		for(ChatBaseLayout layout : mLayoutFactory.getLayouts()) {
			layout.onDestroy();
		}
		mChatHandler.stopChat();
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		for(ChatBaseLayout layout : mLayoutFactory.getLayouts()) {
			layout.onActivityResult(arg0, arg1, arg2);
		}
		super.onActivityResult(arg0, arg1, arg2);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		for(ChatBaseLayout layout : mLayoutFactory.getLayouts()) {
			if(layout.onTouchEvent(event)) {
				return true;
			}
		}
		return super.onTouchEvent(event);
	}
	
	public void completeEdit() {
		setActionButtonImage();
	}
	
	private void setActionButtonImage() {
		findViewById(R.id.btn_action).setBackgroundResource(mLayoutFactory.getEdit().isEditing() ? 
				R.drawable.title_cancel : R.drawable.title_edit);
	}
}
