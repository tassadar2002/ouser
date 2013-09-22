package com.ouser.ui.chat.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ouser.R;
import com.ouser.ui.chat.ImageActivity;
import com.ouser.util.Const;
import com.ouser.util.ImageUtil;

class ItemImageViewHandler extends ItemMessageHandler {

	private LayoutInflater mInflater = null;

	public ItemImageViewHandler() {
		mInflater = LayoutInflater.from(Const.Application);
	}

	@Override
	public void createView(ChatItem item, ViewHolder holder, ViewGroup parent) {
		View v = mInflater.inflate(R.layout.layout_chat_item_image, parent);
		holder.imageContent = (ImageView) v.findViewById(R.id.image_content);
	}

	@Override
	public int getItemViewType(ChatItem item) {
		return item.getMessage().isSend() ? ViewType.ImageSend : ViewType.ImageRecv;
	}

	@Override
	public void setContent(ChatItem item, ViewHolder holder) {
		super.setContent(item, holder);

		Bitmap image = ImageUtil.fromBase64(item.getMessage().getContent());
		holder.imageContent.setImageBitmap(image);
		holder.imageContent.setOnClickListener(new OnClickListener(item, image));
	}

	private class OnClickListener implements View.OnClickListener {
		private ChatItem mItem = null;
		public Bitmap mBitmap = null;

		public OnClickListener(ChatItem item, Bitmap bitmap) {
			mItem = item;
			mBitmap = bitmap;
		}

		@Override
		public void onClick(View v) {
			if (isEditing()) {
				onClickItem(mItem);
			} else {
				Intent intent = new Intent(getActivity(), ImageActivity.class);
				intent.putExtra("data", mBitmap);
				getActivity().startActivity(intent);
			}
		}
	}
}
