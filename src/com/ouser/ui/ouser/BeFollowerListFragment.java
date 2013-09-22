package com.ouser.ui.ouser;

import android.annotation.SuppressLint;

import com.ouser.logic.LogicFactory;

/**
 * 藕丝列表
 * @author Administrator
 *
 */
@SuppressLint("ValidFragment")
public class BeFollowerListFragment extends RelationListFragment {

	public BeFollowerListFragment(String uid) {
		super(uid);
	}

	@Override
	public void refreshData(boolean append) {
		LogicFactory.self().getOuser().getBeFollowers(mUid, append ? getCurrentPageIndex() + 1 : 0, getMainEventListener());
	}
}
