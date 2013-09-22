package com.ouser.ui.topframework;

import java.util.HashMap;
import java.util.Map;

import com.ouser.ui.appoint.MyAppointFragment;
import com.ouser.ui.appoint.NearAppointFragment;
import com.ouser.ui.appoint.SearchAppointFragment;
import com.ouser.ui.other.MyMessageFragment;
import com.ouser.ui.other.TimelineFragment;
import com.ouser.ui.ouser.MyFriendFragment;
import com.ouser.ui.user.SettingFragment;

public class TopFragmentCreator {
	
	private static TopFragmentCreator ins = new TopFragmentCreator();
	public static TopFragmentCreator self() {
		return ins;
	}

	private Map<TopFragmentType, TopFragmentFactory> mItems = new HashMap<TopFragmentType, TopFragmentFactory>();
	
	private TopFragmentCreator() {
		mItems.put(TopFragmentType.NearAppoint, new NearAppointFragment.Factory());
		mItems.put(TopFragmentType.SearchAppoint, new SearchAppointFragment.Factory());
		mItems.put(TopFragmentType.MyFriend, new MyFriendFragment.Factory());
		mItems.put(TopFragmentType.MyMessage, new MyMessageFragment.Factory());
		mItems.put(TopFragmentType.Timeline, new TimelineFragment.Factory());
		mItems.put(TopFragmentType.MyAppoint, new MyAppointFragment.Factory());
		mItems.put(TopFragmentType.Setting, new SettingFragment.Factory());
	}
	
	public TopFragment getFragment(TopFragmentType type) {
		return mItems.get(type).create();
	}
}
