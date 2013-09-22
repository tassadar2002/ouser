package com.ouser.ui.chat.adapter;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ouser.R;
import com.ouser.cache.Cache;
import com.ouser.event.EventArgs;
import com.ouser.event.EventId;
import com.ouser.event.EventListener;
import com.ouser.image.PhotoManager;
import com.ouser.module.Ouser;
import com.ouser.module.Photo;
import com.ouser.ui.base.BaseActivity;
import com.ouser.ui.helper.ActivitySwitch;

/**
 * 消息item处理类的基类
 * 主要处理群聊中的头像 和 所有的删除标志位
 * @author hanlixin
 *
 */
abstract class ItemMessageHandler implements ItemViewHandler {
	
	private EventListener mEventListener = new EventListener() {
		
		@Override
		public void onEvent(EventId id, EventArgs args) {
			mActionListener.notifyDataSetChanged();
		}
	};
	
	private BaseActivity mActivity = null;
	private OnActionListener mActionListener = null;
	
	private boolean mIsSingle = false;
	private boolean mIsInitedEvent = false;
	
	private boolean mEditing = false;
	
	abstract protected void createView(ChatItem item, ViewHolder holder, ViewGroup parent);

	@Override
	public void setOnActionListener(OnActionListener value) {
		mActionListener = value;
	}

	@Override
	public void setActivity(BaseActivity value) {
		mActivity = value;
	}

	@Override
	public void setEditing(boolean value) {
		mEditing = value;
	}

	@Override
	public void onClickItem(ChatItem item) {
		if(!mEditing) {
			return;
		}
		item.setSelected(!item.isSelected());
		mActionListener.notifyDataSetChanged();
	}

	@Override
	public void createView(ChatItem item, ViewHolder holder, View root) {

		initPortraitObserver(item);
		ImageView imagePortrait = (ImageView) root.findViewById(R.id.image_portrait);
		if(mIsSingle) {
			imagePortrait.setVisibility(View.GONE);
		} else {
			imagePortrait.setVisibility(View.VISIBLE);
			holder.imagePortrait = imagePortrait;
		}
		
		holder.chkSelect = (ImageView)root.findViewById(R.id.chk_select);
		
		// 向子类传递
		ViewGroup parent = (ViewGroup) root.findViewById(R.id.layout_content_container);
		createView(item, holder, parent);
	}

	@Override
	public void setContent(ChatItem item, ViewHolder holder) {
		final ChatItem fItem = item;
		holder.chkSelect.setVisibility(mEditing ? View.VISIBLE : View.GONE);
		if(mEditing) {
			holder.chkSelect.setImageResource(
					fItem.isSelected() ? R.drawable.checkbox_check : R.drawable.checkbox_uncheck);
		}
		
		if(holder.imagePortrait != null) {
			
			ImageView imagePortrait = holder.imagePortrait;
			
			Ouser ouser = null;
			if(item.getMessage().isSend()) {
				ouser = Cache.self().getMySelfOuser();
			} else {
				ouser = item.getMessage().getOuser();
			}
			
			fillPortrait(ouser, imagePortrait);
			imagePortrait.setOnClickListener(
					ActivitySwitch.getToProfileClickListener(getActivity(), ouser.getUid()));
		}
	}
	
	@Override
	public void onDestroy() {
	}
	
	public BaseActivity getActivity() {
		return mActivity;
	}

	public OnActionListener getOnActionListener() {
		return mActionListener;
	}

	public boolean isEditing() {
		return mEditing;
	}
	
	private void fillPortrait(Ouser ouser, ImageView imagePortrait) {
		Bitmap image = PhotoManager.self().getBitmap(ouser.getPortrait(), Photo.Size.Small);
		imagePortrait.setImageBitmap(image);
	}
	
	private void initPortraitObserver(ChatItem item) {
		if(mIsInitedEvent) {
			return;
		}
		mIsInitedEvent = true;
		
		if(item.getMessage().isSingle()) {
			mIsSingle = true;
		} else {
			getActivity().addUIEventListener(EventId.ePhotoDownloadComplete, mEventListener);
		}
	}
}
