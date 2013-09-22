package com.ouser.ui.chat.adapter;

import android.view.View;
import android.widget.TextView;

import com.ouser.R;
import com.ouser.ui.base.BaseActivity;

class ItemTimeViewHandler implements ItemViewHandler {

	public ItemTimeViewHandler() {
	}
	
	@Override
	public void onDestroy() {
	}

	@Override
	public void setActivity(BaseActivity value) {
	}

	@Override
	public void setOnActionListener(OnActionListener value) {
	}

	@Override
	public void setEditing(boolean value) {
	}

	@Override
	public void onClickItem(ChatItem item) {
	}

	@Override
	public int getItemViewType(ChatItem item) {
		return ViewType.Time;
	}

	@Override
	public void createView(ChatItem item, ViewHolder holder, View root) {
		holder.txtContent = (TextView) root.findViewById(R.id.txt_content);
	}

	@Override
	public void setContent(ChatItem item, ViewHolder holder) {
		holder.txtContent.setText(item.getContent());
	}
}
