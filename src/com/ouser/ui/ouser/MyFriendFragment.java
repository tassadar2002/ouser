package com.ouser.ui.ouser;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ouser.R;
import com.ouser.cache.Cache;
import com.ouser.logic.LogicFactory;
import com.ouser.module.Ouser;
import com.ouser.ui.component.HeadBar;
import com.ouser.ui.topframework.TopFragment;
import com.ouser.ui.topframework.TopFragmentFactory;
import com.ouser.ui.topframework.TopFragmentType;

/**
 * 我的好友列表
 * @author Administrator
 *
 */
public class MyFriendFragment extends TopFragment {
	
	public static class Factory implements TopFragmentFactory {

		@Override
		public TopFragment create() {
			return new MyFriendFragment();
		}
	}

	private List<Ouser> mItems = new ArrayList<Ouser>();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.actvy_frgmt_my_friend, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		getHeadbar().setTitle("我的好友");
		getHeadbar().setActionButton(R.drawable.title_add_friend, new HeadBar.OnActionListener() {
			
			@Override
			public void onClick() {
				startActivity(new Intent(getActivity(), SearchOuserActivity.class));
			}
		});
	}

	@Override
	public void onSaveState() {
		super.onSaveState();
		Cache.self().setTopFragmentData(TopFragmentType.MyFriend, mItems);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onRestoreState() {
		super.onRestoreState();
		List<Ouser> value = (List<Ouser>)Cache.self().getTopFragmentData(TopFragmentType.MyFriend);
		if(value != null) {
			mItems = value;
		}
	}

	@Override
	public void syncInitData(Bundle bundle) {
		super.syncInitData(bundle);
		
		MyFriendListFragment fragment = new MyFriendListFragment();
		fragment.setData(mItems);
		replaceFragment(R.id.layout_my_friend_list, fragment);
		fragment.asyncInitData();
	}

	@SuppressLint("ValidFragment")
	private class MyFriendListFragment extends RelationListFragment {

		public MyFriendListFragment() {
			super(Cache.self().getMyUid());
			observePhotoDownloadEvent(true);
		}

		@Override
		protected void refreshData(boolean append) {
			LogicFactory.self().getOuser().getFriends(mUid, append ? getCurrentPageIndex() + 1 : 0, getMainEventListener());
		}
	}
}
