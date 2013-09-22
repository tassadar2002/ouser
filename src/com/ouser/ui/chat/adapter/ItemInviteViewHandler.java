package com.ouser.ui.chat.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ouser.R;
import com.ouser.module.Appoint;
import com.ouser.ui.appoint.AppointValidChecker;
import com.ouser.ui.helper.ActivitySwitch;
import com.ouser.util.Const;

class ItemInviteViewHandler extends ItemMessageHandler {

	private LayoutInflater mInflater = null;

	public ItemInviteViewHandler() {
		mInflater = LayoutInflater.from(Const.Application);
	}

	@Override
	public int getItemViewType(ChatItem item) {
		return item.getMessage().isSend() ? ViewType.InviteSend : ViewType.InviteRecv;
	}

	@Override
	public void createView(ChatItem item, ViewHolder holder, ViewGroup parent) {
		View v = mInflater.inflate(R.layout.layout_chat_item_invite, parent);
		holder.txtContent = (TextView) v.findViewById(R.id.txt_content);
	}

	@Override
	public void setContent(ChatItem item, ViewHolder holder) {
		super.setContent(item, holder);

		holder.txtContent.setText(item.getMessage().getInvite().getContent());
		holder.txtContent.setOnClickListener(new OnClickListener(item));
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
				AppointValidChecker.check(mItem.getMessage().getInvite().getAppointId(), 
						getActivity(), new AppointValidChecker.OnValidListener() {
					
					@Override
					public void onValid(Appoint appoint) {
						ActivitySwitch.toAppointDetailForResult(getActivity(), appoint);
					}
				});
			}
		}
	}
}
