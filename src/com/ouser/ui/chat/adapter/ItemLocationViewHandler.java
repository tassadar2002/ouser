package com.ouser.ui.chat.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ouser.R;
import com.ouser.ui.map.LocationViewActivity;
import com.ouser.util.Const;

class ItemLocationViewHandler extends ItemMessageHandler {

	private LayoutInflater mInflater = null;

	public ItemLocationViewHandler() {
		mInflater = LayoutInflater.from(Const.Application);
	}

	@Override
	public void createView(ChatItem item, ViewHolder holder, ViewGroup parent) {
		View v = mInflater.inflate(R.layout.layout_chat_item_location, parent);
		holder.txtContent = (TextView) v.findViewById(R.id.txt_content);
		holder.imageContent = (ImageView) v.findViewById(R.id.image_content);
	}

	@Override
	public int getItemViewType(ChatItem item) {
		return item.getMessage().isSend() ? ViewType.LocationSend : ViewType.LocationRecv;
	}

	@Override
	public void setContent(ChatItem item, ViewHolder holder) {
		super.setContent(item, holder);

		holder.txtContent.setText(item.getMessage().getLocation().getPlace());
		holder.imageContent.setOnClickListener(new OnClickListener(item));
	}

	private class OnClickListener implements View.OnClickListener {
		private ChatItem mItem = null;

		public OnClickListener(ChatItem item) {
			mItem = item;
		}

		@Override
		public void onClick(View v) {
			if (isEditing()) {
				onClickItem(mItem);
			} else {
				Intent intent = new Intent(getActivity(), LocationViewActivity.class);
				intent.putExtra("location", mItem.getMessage().getLocation().getLocation().toBundle());
				getActivity().startActivity(intent);
			}
		}
	}
}
