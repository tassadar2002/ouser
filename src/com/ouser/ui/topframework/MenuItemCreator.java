package com.ouser.ui.topframework;

import com.ouser.R;

public class MenuItemCreator {
	
	private static MenuItemCreator ins = new MenuItemCreator();
	public static MenuItemCreator self() {
		return ins;
	}
	
	private MenuItem[] mItems = {
			new MenuItem(TopFragmentType.NearAppoint, R.id.layout_near),
			new MenuItem(TopFragmentType.MyAppoint, R.id.layout_my_appoint),
			new MenuItem(TopFragmentType.SearchAppoint, R.id.layout_search_appoint),
			new MenuItem(TopFragmentType.MyFriend, R.id.layout_my_friend), 
			new MenuItem(TopFragmentType.MyMessage, R.id.layout_my_message), 
			new MenuItem(TopFragmentType.Timeline, R.id.layout_timeline),  
			new MenuItem(TopFragmentType.Setting, R.id.layout_setting),
	};
	
	private MenuItemCreator() {
	}
	
	public MenuItem[] getItems() {
		return mItems;
	}
	
	public MenuItem getItem(int layoutId) {
		for(MenuItem item : mItems) {
			if(item.getLayoutId() == layoutId) {
				return item;
			}
		}
		return null;
	}
	
	public static class MenuItem {
		private TopFragmentType mType = TopFragmentType.Setting;
		private int mLayoutId = 0;
		private int mTipCount = 0;
		public MenuItem(TopFragmentType type, int layoutId) {
			mType = type;
			mLayoutId = layoutId;
		}
		public TopFragmentType getType() {
			return mType;
		}
		public int getLayoutId() {
			return mLayoutId;
		}
		public int getTipCount() {
			return mTipCount;
		}
		public void setTipCount(int tipCount) {
			this.mTipCount = tipCount;
		}
	}
}
