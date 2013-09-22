package com.ouser.ui.chat.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ouser.R;
import com.ouser.ui.widget.EmotionTextView;
import com.ouser.util.Const;

class ItemTextViewHandler extends ItemMessageHandler {
	
	private LayoutInflater mInflater = null;
	
	public ItemTextViewHandler() {
		mInflater = LayoutInflater.from(Const.Application);
	}

	@Override
	public int getItemViewType(ChatItem item) {
		return item.getMessage().isSend() ? 
				ViewType.TextSend : ViewType.TextRecv;
	}

	@Override
	public void createView(ChatItem item, ViewHolder holder, ViewGroup parent) {
		View v = mInflater.inflate(R.layout.layout_chat_item_text, parent);
		holder.txtContent = (TextView) v.findViewById(R.id.txt_content);
	}

	@Override
	public void setContent(ChatItem item, ViewHolder holder) {
		super.setContent(item, holder);
		
		((EmotionTextView)holder.txtContent).setText(item.getMessage().getContent());
	}
}
