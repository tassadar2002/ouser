package com.ouser.ui.topframework;

import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.ouser.R;
import com.ouser.cache.Cache;
import com.ouser.event.EventArgs;
import com.ouser.event.EventId;
import com.ouser.event.EventListener;
import com.ouser.logic.LogicFactory;
import com.ouser.logic.OperErrorCode;
import com.ouser.logic.event.StringListEventArgs;
import com.ouser.ui.base.BaseActivity;
import com.ouser.ui.base.BaseFragment;
import com.ouser.ui.component.HeadBar;
import com.ouser.ui.helper.ActivitySwitch;
import com.ouser.ui.helper.Alert;
import com.ouser.ui.user.UpgradeDialogBuilder;
import com.ouser.ui.user.WelcomeActivity;
import com.ouser.util.Const;
import com.slidingmenu.lib.SlidingMenu;

/**
 * 顶级activity
 * 
 * @author hanlixin
 * 
 */
public class TopActivity extends BaseActivity implements TopFragmentHandler {

	private long mLastClickBack = Const.DefaultValue.Time;
	private SlidingMenu mMenu = null;

	/**
	 * 菜单点击监听器
	 */
	private OnMenuSelectedListener mMenuListener = new OnMenuSelectedListener() {

		@Override
		public void onHeaderClick() {
			ActivitySwitch.toProfile(getActivity(), Cache.self().getMyUid());
		}

		@Override
		public void onSelected(TopFragment fragment) {
			if (getContent() != fragment) {
				replaceContent(fragment, null);
			}
			mMenu.toggle();
		}
	};

	/**
	 * top fragment head bar点击监听器
	 */
	private HeadBar.OnHeadIconListener mHeadIconListener = new HeadBar.OnHeadIconListener() {

		@Override
		public void onClick() {
			mMenu.toggle();
		}
	};

	@Override
	public void change(TopFragmentType type, Bundle bundle) {
		TopFragment fragment = TopFragmentCreator.self().getFragment(type);
		replaceContent(fragment, bundle);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		mMenu = new SlidingMenu(this);
		mMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		mMenu.setBehindScrollScale(1f);
		mMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		mMenu.setFadeEnabled(true);
		mMenu.setFadeDegree(0.35f);
		mMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
		mMenu.setMenu(R.layout.sliding_menu_frame);
		mMenu.setOnOpenedListener(new SlidingMenu.OnOpenedListener() {
			
			@Override
			public void onOpened() {
				TopFragment fragment = (TopFragment) getSupportFragmentManager().findFragmentById(R.id.top_content);
				if(fragment != null) {
					fragment.onMenuOpen();
				}
			}
		});

		MenuFragment menuFragment = new MenuFragment();
		menuFragment.setListener(mMenuListener);
		getSupportFragmentManager().beginTransaction().replace(R.id.sliding_menu, menuFragment)
				.commit();

		mMenu.setContent(R.layout.actvy_top);
		
		if(getIntent() != null) {
			int page = getIntent().getIntExtra(Const.Intent.SwitchPage, TopFragmentType.NearAppoint.getValue());
			change(TopFragmentType.find(page), getIntent().getExtras());
		} else {
			change(TopFragmentType.NearAppoint, null);
		}

		upgrade();
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		BaseFragment fragment = (BaseFragment) getSupportFragmentManager().findFragmentById(
				R.id.top_content);
		if (fragment != null && fragment.onFragmentActivityResult(arg0, arg1, arg2)) {
			return;
		} else {
			super.onActivityResult(arg0, arg1, arg2);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		mMenu.showContent();
		int page = intent.getIntExtra(Const.Intent.SwitchPage, TopFragmentType.NearAppoint.getValue());
		change(TopFragmentType.find(page), intent.getExtras());
	}

	@Override
	public void onBackPressed() {
		long nowTime = Calendar.getInstance().getTimeInMillis();
		if(nowTime - mLastClickBack < Const.ExitAppClickBackMinInterval) {
			Intent intent = new Intent(this, WelcomeActivity.class);
			intent.putExtra("exit", true);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		} else {
			Alert.Toast("要退出应用，请再次点击返回键", false, false);
			mLastClickBack = nowTime;
		}
	}
	
	public HeadBar.OnHeadIconListener getHeadIconClickListener() {
		 return mHeadIconListener;
	}

	private void replaceContent(TopFragment newFragment, Bundle bundle) {
		FragmentManager mgr = getSupportFragmentManager();
		TopFragment oldFragment = (TopFragment) mgr.findFragmentById(R.id.top_content);
		if (oldFragment != null) {
			oldFragment.onSaveState();
		}

		mgr.beginTransaction().replace(R.id.top_content, newFragment).commit();
		newFragment.getHeadbar().setOnHeadIconListener(mHeadIconListener);
		newFragment.onRestoreState();
		newFragment.asyncInitData(bundle);
	}

	private Fragment getContent() {
		return getSupportFragmentManager().findFragmentById(R.id.top_content);
	}

	private void upgrade() {
		LogicFactory.self().getUpgrade().check(new EventListener() {

			@Override
			public void onEvent(EventId id, EventArgs args) {
				StringListEventArgs stringsArgs = (StringListEventArgs) args;
				if (stringsArgs.getErrCode() == OperErrorCode.Success) {
					List<String> strs = stringsArgs.getStrings();
					Activity currentActivity = Const.Application.getCurrentActivity();
					final String url = strs.get(2);
					new UpgradeDialogBuilder(currentActivity)
						.setVersion(strs.get(0), strs.get(1))
						.setCallback(new UpgradeDialogBuilder.Callback() {

							@Override
							public void onOK() {
								LogicFactory.self().getUpgrade().downloadPackage(url);
							}
						}).create().show();
				}
			}
		});
	}
}
