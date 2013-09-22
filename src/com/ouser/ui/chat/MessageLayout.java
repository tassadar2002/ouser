package com.ouser.ui.chat;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ouser.R;
import com.ouser.event.EventArgs;
import com.ouser.event.EventId;
import com.ouser.event.EventListener;
import com.ouser.logic.OperErrorCode;
import com.ouser.logic.event.ChatMessagesEventArgs;
import com.ouser.module.ChatMessage;
import com.ouser.ui.chat.adapter.ChatItem;
import com.ouser.ui.chat.adapter.MessageAdapter;
import com.ouser.ui.helper.Alert;

class MessageLayout extends ChatBaseLayout {

	/** 控件 */
	private ListView mListView = null;

	/** 数据 */
	private MessageAdapter mAdapter = null;

	private EventListener mListener = new EventListener() {

		@Override
		public void onEvent(EventId id, EventArgs args) {
			if (id == EventId.ePushMessage) {
				ChatMessagesEventArgs listArgs = (ChatMessagesEventArgs) args;
				OperErrorCode errCode = listArgs.getErrCode();
				if (errCode != OperErrorCode.Success) {
					return;
				}
				mAdapter.getItems().addRange(listArgs.getChatMessages());
				mAdapter.notifyDataSetChanged();
				selectLastItem();
			} else if (id == EventId.eSendMessage) {
				// TODO
				// 去掉发送的菊花
				mAdapter.notifyDataSetChanged();
			}
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		
		initAdapter();
		initListView();
		initEvent();
	}

	@Override
	public void onDestroy() {
		mAdapter.onDestroy();
		super.onDestroy();
	}

	public void onEdit() {
		mAdapter.setEditing(getLayoutFactory().getEdit().isEditing());
		mAdapter.notifyDataSetChanged();
	}

	public void onSend(ChatMessage message) {
		mAdapter.getItems().add(message);
		mAdapter.notifyDataSetChanged();
		selectLastItem();
	}
	
	public void onDeleted() {
		// TODO 可以优化，只删除选中的部分
		initAdapter();
		mListView.setAdapter(mAdapter);
	}
	
	public List<ChatMessage> getSelectedMessages() {
		List<ChatMessage> messages = new ArrayList<ChatMessage>();
		for(ChatItem item : mAdapter.getItems().getItems()) {
			if(item.isSelected()) {
				messages.add(item.getMessage());
			}
		}
		return messages;
	}
	
	private void selectLastItem() {
		new Handler().post(new Runnable() {

			@Override
			public void run() {
				mListView.setSelection(mAdapter.getCount() - 1);
			}
		});
	}
	
	private void initAdapter() {
		mAdapter = new MessageAdapter(getActivity());
		getChatHandler().getAllMessage(getActivity().createUIEventListener(new EventListener() {
			
			@Override
			public void onEvent(EventId id, EventArgs args) {
				getActivity().stopLoading();
				ChatMessagesEventArgs messageArgs = (ChatMessagesEventArgs)args;
				if(messageArgs.getErrCode() == OperErrorCode.Success) {
					mAdapter.getItems().clear();
					mAdapter.getItems().addRange(messageArgs.getChatMessages());
					mAdapter.notifyDataSetChanged();
					selectLastItem();
				} else {
					Alert.handleErrCode(messageArgs.getErrCode());
				}
			}
		}));
		getActivity().startLoading();
	}
	
	private void initListView() {
		mListView = (ListView) getActivity().findViewById(R.id.list_chat);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				mAdapter.onClickItem(arg2);
			}
		});
	}
	
	private void initEvent() {
		getActivity().addUIEventListener(EventId.eSendMessage, mListener);
		getActivity().addUIEventListener(EventId.ePushMessage, mListener);
	}
}
