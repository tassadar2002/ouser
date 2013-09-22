package com.ouser.ui.profile;

import android.os.Bundle;

import com.ouser.R;
import com.ouser.cache.Cache;
import com.ouser.ui.appoint.FollowAppointListFragment;
import com.ouser.ui.appoint.PublishedAppointListFragment;
import com.ouser.ui.base.BaseActivity;
import com.ouser.ui.base.BaseListFragment;
import com.ouser.ui.component.HeadBar;
import com.ouser.ui.ouser.BeFollowerListFragment;
import com.ouser.ui.ouser.FollowerListFragment;
import com.ouser.util.Const;

/**
 * profile界面的相关列表
 * 
 * @author Administrator
 * 
 */
public class ProfileListActivity extends BaseActivity {

	public static final int PublishAppoint = 1;
	public static final int FollowAppoint = 2;
	public static final int BeFollower = 3;
	public static final int Follower = 4;

	private int mType = 0;

	private BaseListFragment mFragment = null;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.actvy_list);

		mType = getIntent().getIntExtra(Const.Intent.Type, PublishAppoint);
		String uid = getIntent().getStringExtra(Const.Intent.Uid);
		String name = getIntent().getStringExtra(Const.Intent.NickName);
		
		String format = "";
		switch (mType) {
		case PublishAppoint:
			PublishedAppointListFragment publishList = new PublishedAppointListFragment(
					uid);
			if(Cache.self().getMyUid().equals(uid)) {
				publishList.setFetchOnlyValid(false);
			} else {
				publishList.setFetchOnlyValid(true);
			}
			mFragment = publishList;
			format = "%s的友约";
			break;
		case FollowAppoint:
			mFragment = new FollowAppointListFragment(uid);
			format = "%s关注的#";
			break;
		case BeFollower:
			mFragment = new BeFollowerListFragment(uid);
			format = "%s的藕丝";
			break;
		case Follower:
			mFragment = new FollowerListFragment(uid);
			format = "%s的关注";
			break;
		default:
			break;
		}

		if (mFragment == null) {
			return;
		}
		
		String title = "";
		if(uid.equals(Cache.self().getMyUid())) {
			title = String.format(format, "我");
		} else {
			title = String.format(format, name);
		}

		HeadBar headBar = new HeadBar();
		headBar.onCreate(findViewById(R.id.layout_head_bar), this);
		headBar.setTitle(title);

		getSupportFragmentManager().beginTransaction().replace(R.id.layout_list, mFragment)
				.commit();
		mFragment.asyncInitData();
	}
}
