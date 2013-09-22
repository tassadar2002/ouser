package com.ouser.ui.chat.adapter;

import java.util.HashMap;
import java.util.Map;

import com.ouser.ui.base.BaseActivity;

class ItemViewHandlerFactory {

	public static Map<ViewType.Type, ItemViewHandler> createItems(BaseActivity activity, OnActionListener listener) {
		Map<ViewType.Type, ItemViewHandler> itemViews = new HashMap<ViewType.Type, ItemViewHandler>();
		itemViews.put(ViewType.Type.Time, new ItemTimeViewHandler());
		itemViews.put(ViewType.Type.Text, new ItemTextViewHandler());
		itemViews.put(ViewType.Type.Image, new ItemImageViewHandler());
		itemViews.put(ViewType.Type.Location, new ItemLocationViewHandler());
		itemViews.put(ViewType.Type.Audio, new ItemVoiceViewHandler());
		itemViews.put(ViewType.Type.Invite, new ItemInviteViewHandler());
		for(ItemViewHandler view : itemViews.values()) {
			view.setActivity(activity);
			view.setOnActionListener(listener);
		}
		return itemViews;
	}
}
