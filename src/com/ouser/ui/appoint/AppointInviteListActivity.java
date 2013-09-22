package com.ouser.ui.appoint;

import java.util.List;

import android.os.Bundle;

import com.ouser.R;
import com.ouser.cache.Cache;
import com.ouser.event.EventArgs;
import com.ouser.event.EventId;
import com.ouser.event.EventListener;
import com.ouser.logic.LogicFactory;
import com.ouser.logic.OperErrorCode;
import com.ouser.logic.event.StatusEventArgs;
import com.ouser.module.Appoint;
import com.ouser.module.Ouser;
import com.ouser.ui.base.BaseActivity;
import com.ouser.ui.component.HeadBar;
import com.ouser.ui.helper.Alert;
import com.ouser.ui.ouser.BeFollowerListFragment;
import com.ouser.ui.ouser.FollowerListFragment;
import com.ouser.ui.ouser.RelationListFragment;

public class AppointInviteListActivity extends BaseActivity {

	public static final String IntentType = "type";
	public static final String IntentAppoint = "appoint";
	public static final String IntentRemain = "remain";

	public static final int BeFollower = 3;
	public static final int Follower = 4;

	private int mRemain = 0;
	
	private int mType = 0;
	private Appoint mAppoint = new Appoint();

	private RelationListFragment mFragment = null;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.actvy_list);

		mType = getIntent().getExtras().getInt(IntentType);
		mAppoint.fromBundle(getIntent().getExtras().getBundle(IntentAppoint));
		mRemain = getIntent().getIntExtra(IntentRemain, 0);

		HeadBar headBar = new HeadBar();
		headBar.onCreate(findViewById(R.id.layout_head_bar), this);
		headBar.setActionButton(R.drawable.title_save, new HeadBar.OnActionListener() {
			
			@Override
			public void onClick() {
				action();
			}
		});
		
		String uid = Cache.self().getMyUid();
		switch (mType) {
		case BeFollower:
			mFragment = new BeFollowerListFragment(uid);
			headBar.setTitle("我的藕丝儿");
			break;
		case Follower:
			mFragment = new FollowerListFragment(uid);
			headBar.setTitle("我的关注");
			break;
		default:
			break;
		}

		if (mFragment == null) {
			return;
		}

		mFragment.setSelectOrToInfo(true);
		getSupportFragmentManager().beginTransaction().replace(R.id.layout_list, mFragment)
				.commit();
		mFragment.asyncInitData();
	}

	private void action() {
		List<Ouser> ousers = mFragment.getSelected();
		if(ousers.size() > mRemain) {
			Alert.Toast("该友约今日最多还可邀请" + mRemain + "人，请重新选择后再次邀请");
			return;
		}
		LogicFactory.self().getAppoint().invite(mAppoint, mFragment.getSelected(), 
				createUIEventListener(new EventListener() {
			
			@Override
			public void onEvent(EventId id, EventArgs args) {
				stopLoading();
				StatusEventArgs statusArgs = (StatusEventArgs)args;
				if(statusArgs.getErrCode() == OperErrorCode.Success) {
					Alert.Toast("邀请成功");
					setResult(RESULT_OK);
				} else {
					Alert.handleErrCode(statusArgs.getErrCode());
				}
				finish();
			}
		}));
		startLoading();
	}
}
