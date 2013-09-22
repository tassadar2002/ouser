package com.ouser.ui.chat.adapter;

import java.util.Map;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ouser.R;
import com.ouser.ui.base.BaseActivity;
import com.ouser.util.Const;

@SuppressLint("HandlerLeak")
public class MessageAdapter extends BaseAdapter {

	/** 容器 */
	private BaseActivity mActivity = null;
	private LayoutInflater mInflater = null;
	
	/** 数据 */
	private ChatItems mItems = new ChatItems();
	
	private boolean mIsEditing = false;
	
	/** 视图处理器 */
	private Map<ViewType.Type, ItemViewHandler> mItemViewHandler = null;
	
	private OnActionListener mActionListener = new OnActionListener() {
		
		@Override
		public void notifyDataSetChanged() {
			MessageAdapter.this.notifyDataSetChanged();
		}

		@Override
		public ChatItems getItems() {
			return mItems;
		}
	};

	public MessageAdapter(BaseActivity activity) {
		mActivity = activity;
		mInflater = LayoutInflater.from(Const.Application);
		
		// factory
		mItemViewHandler = ItemViewHandlerFactory.createItems(mActivity, mActionListener);
	}
	
	public void onDestroy() {
		for(ItemViewHandler handler : mItemViewHandler.values()) {
			handler.onDestroy();
		}
	}
	
	public void setEditing(boolean value) {
		mIsEditing = value;
		if(!value) {
			for(ChatItem item : mItems.getItems()) {
				item.setSelected(false);
			}
		}
		for(ItemViewHandler handler : mItemViewHandler.values()) {
			handler.setEditing(value);
		}
	}
	
	public void onClickItem(int index) {
		ChatItem item = mItems.get(index);
		for(ItemViewHandler handler : mItemViewHandler.values()) {
			handler.onClickItem(item);
		}
	}
	
	public ChatItems getItems() {
		return mItems;
	}

	@Override
	public boolean isEnabled(int position) {
		return mIsEditing;
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public int getItemViewType(int position) {
		ChatItem item = mItems.get(position);
		return getItemViewHandler(item).getItemViewType(item);
	}

	@Override
	public int getViewTypeCount() {
		return ViewType.Count;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ChatItem item = mItems.get(position);
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(getLayoutId(item), null);
			convertView.setTag(holder);
			createContentLayout(item, holder, convertView);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		getItemViewHandler(item).setContent(item, holder);
		return convertView;
	}

	private void createContentLayout(ChatItem item, ViewHolder holder, View parentView) {
		getItemViewHandler(item).createView(item, holder, parentView);
	}
	
	private ItemViewHandler getItemViewHandler(ChatItem item) {
		return mItemViewHandler.get(ViewType.convert(item));
	}

	private int getLayoutId(ChatItem item) {
		if (item.getType() == ChatItem.Type.Time) {
			return R.layout.lvitem_chat_time;
		} else {
			if (item.getMessage().isSend()) {
				return R.layout.lvitem_chat_send;
			} else {
				return R.layout.lvitem_chat_recv;
			}
		}
	}
}
