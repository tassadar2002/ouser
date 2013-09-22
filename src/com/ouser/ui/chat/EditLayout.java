package com.ouser.ui.chat;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;

import com.ouser.R;
import com.ouser.module.ChatMessage;
import com.ouser.ui.helper.Alert;

class EditLayout extends ChatBaseLayout {
	
	private View mRoot = null;
	
	private boolean mEditing = false;

	@Override
	public void onCreate() {
		super.onCreate();
		mRoot = getActivity().findViewById(R.id.layout_chat_edit);
	}

	@Override
	public void onPause() {
		complete();
		super.onPause();
	}

	public void onEdit() {
		mEditing = !mEditing;
		show();
		if(mEditing) {
			View btnDelete = mRoot.findViewById(R.id.btn_delete);
			btnDelete.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					remove(false);
				}
			});
			View btnClear = mRoot.findViewById(R.id.btn_clear);
			btnClear.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					remove(true);
				}
			});
		}
		
		if(mEditing) {
			getChatHandler().stopChat();
		} else {
			getChatHandler().startChat();
		}
	}
	
	public boolean isEditing() {
		return mEditing;
	}

	private void complete() {
		mEditing = false;
		show();
		getLayoutFactory().getMessage().onDeleted();
		getActivity().completeEdit();
		getChatHandler().startChat();
	}
	
	private void show() {
		mRoot.setVisibility(mEditing ? View.VISIBLE : View.GONE);
		getLayoutFactory().getInput().show(!mEditing);
	}
	
	private void remove(final boolean clear) {
		if(getLayoutFactory().getMessage().getSelectedMessages().isEmpty()) {
			Alert.Toast("请选择要删除的记录");
			return;
		}
		new AlertDialog.Builder(getActivity())
			.setMessage("确认删除？")
			.setPositiveButton("确认", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(clear) {
						getChatHandler().clearMessage();
					} else {
						List<ChatMessage> messages = getLayoutFactory().getMessage().getSelectedMessages();
						getChatHandler().removeMessage(messages);
					}
					complete();					
				}
			})
			.setNegativeButton("取消", null)
			.create().show();
	}
}
