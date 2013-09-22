package com.ouser.ui.appoint;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ouser.R;
import com.ouser.cache.Cache;
import com.ouser.event.EventArgs;
import com.ouser.event.EventId;
import com.ouser.event.EventListener;
import com.ouser.logic.LogicFactory;
import com.ouser.logic.OperErrorCode;
import com.ouser.logic.event.StatusEventArgs;
import com.ouser.module.Appoint;
import com.ouser.module.AppointId;
import com.ouser.module.Appoints;
import com.ouser.module.Ouser;
import com.ouser.ui.base.BaseActivity;
import com.ouser.ui.base.BaseViewPager;
import com.ouser.ui.component.HeadBar;
import com.ouser.ui.component.MenuDialogBuilder;
import com.ouser.ui.helper.Alert;
import com.ouser.ui.helper.UIEventListener;
import com.ouser.ui.ouser.OuserInfoFragment;
import com.ouser.util.Const;

/**
 * 友约详情
 * 
 * @author hanlixin
 * 
 */
@SuppressLint("HandlerLeak")
public class AppointDetailActivity extends BaseActivity {

	private static final int MenuExit = 1;
	private static final int MenuRemove = 2;
	private static final int MenuReport = 3;
	private static final int MenuShareWeixin = 4;

	// ui
	private HeadBar mHeadBar = null;

	// data
	private Appoints mItems = new Appoints();
	private Ouser mPublisher = new Ouser();
	private AppointId mFocusAppointId = new AppointId();

	// view pager
	private BaseViewPager mViewPager = new BaseViewPager();

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			findViewById(R.id.layout_indicator).setVisibility(View.GONE);
		}
	};

	// listener
	private ActionClickListener mExitListener = new ActionClickListener("退出",
			ActionClickListener.Refresh);
	private ActionClickListener mRemoveListener = new ActionClickListener("删除",
			ActionClickListener.Remove);
	private ActionClickListener mReportListener = new ActionClickListener("举报",
			ActionClickListener.None);
	private ActionClickListener mShareListener = new ActionClickListener("分享",
			ActionClickListener.None);

	private DetailFragment.OnActionListener mFragmentListener = new DetailFragment.OnActionListener() {

		@Override
		public void onUpdate(Appoint appoint) {
			for (int i = 0; i < mItems.size(); ++i) {
				if (mItems.get(i).isSame(appoint)) {
					mItems.set(i, appoint);
					break;
				}
			}
		}
	};

	private BaseViewPager.OnActionListener mViewPagerListener = new BaseViewPager.OnActionListener() {

		@Override
		public void onPageChanged() {
			AppointDetailActivity.this.onPageChanged();
		}

		@Override
		public int getPageCount() {
			return mItems.size();
		}

		@Override
		public Fragment createFragment(int index) {
			DetailFragment fragment = new DetailFragment(mItems.get(index),
					mFragmentListener);
			return fragment;
		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.actvy_appoint);

		mHeadBar = new HeadBar();
		mHeadBar.onCreate(findViewById(R.id.layout_head_bar), this);

		parseArgument();
		initViewPager();

		if (mPublisher.getUid().equals(Cache.self().getMyUid())) {
			// 本人发布，不显示发布者信息
			findViewById(R.id.layout_publisher).setVisibility(View.GONE);
		} else {
			OuserInfoFragment fragment = new OuserInfoFragment(mPublisher);
			fragment.setActionListener(new OuserInfoFragment.OnActionListener() {

				@Override
				public void onInitActionButton(TextView button) {
					button.setVisibility(View.GONE);
				}
			});
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.layout_publisher, fragment).commit();
			fragment.asyncInitData();
		}

		setupUI(getWindow().getDecorView());

		findViewById(R.id.btn_last).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						mViewPager.toLast();
					}
				});
		findViewById(R.id.btn_next).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						mViewPager.toNext();
					}
				});

		mHeadBar.setActionButton(R.drawable.title_more,
				new HeadBar.OnActionListener() {

					@Override
					public void onClick() {
						showMenu();
					}
				});
	}

	private void showMenu() {
		List<Pair<Integer, String>> items = new ArrayList<Pair<Integer, String>>();

		final Appoint appoint = mItems.get(mViewPager.getCurrentIndex());
		switch (appoint.getRelation()) {
		case Publish:
			items.add(new Pair<Integer, String>(MenuShareWeixin, "分享到微信"));
			items.add(new Pair<Integer, String>(MenuRemove, "删除"));
			break;
		case Join:
			items.add(new Pair<Integer, String>(MenuShareWeixin, "分享到微信"));
			items.add(new Pair<Integer, String>(MenuExit, "退出"));
			items.add(new Pair<Integer, String>(MenuReport, "举报"));
			break;
		case None:
			items.add(new Pair<Integer, String>(MenuReport, "举报"));
			break;
		default:
			break;
		}

		MenuDialogBuilder.Callback callback = new MenuDialogBuilder.Callback() {

			@Override
			public void onClick(int key) {
				switch (key) {
				case MenuShareWeixin:
					LogicFactory
							.self()
							.getShare()
							.shareToWeixin(
									appoint,
									new UIEventListener(getActivity(),
											mShareListener));
					break;
				case MenuExit:
					LogicFactory
							.self()
							.getAppoint()
							.exit(appoint,
									new UIEventListener(getActivity(),
											mExitListener));
					startLoading();
					break;
				case MenuRemove:
					LogicFactory
							.self()
							.getAppoint()
							.remove(appoint,
									new UIEventListener(getActivity(),
											mRemoveListener));
					startLoading();
					break;
				case MenuReport:
					LogicFactory
							.self()
							.getAppoint()
							.report(appoint,
									new UIEventListener(getActivity(),
											mReportListener));
					startLoading();
					break;
				}
			}
		};

		new MenuDialogBuilder(this).setMenuItem(items, callback)
				.setTop(findViewById(R.id.layout_head_bar).getHeight())
				.create().show();
	}

	private void parseArgument() {
		Bundle extra = getIntent().getExtras();

		String publisherUId = extra.getString(Const.Intent.PublisherUid);
		mPublisher.setUid(publisherUId);

		mFocusAppointId.fromBundle(extra.getBundle("focus"));
		mItems.fromBundle(extra.getBundle("appoints"));
	}

	private void initViewPager() {
		for (int i = 0; i < mItems.size(); ++i) {
			if (mItems.get(i).getAppointId().isSame(mFocusAppointId)) {
				mViewPager.setCurrentIndex(i);
				break;
			}
		}
		mViewPager.init(this, (ViewPager) findViewById(R.id.viewpager_appoint),
				mViewPagerListener);
	}

	private void onPageChanged() {

		// 设置标题
		mHeadBar.setTitle(String.format("%d/%d",
				mViewPager.getCurrentIndex() + 1, mItems.size()));

		// 刷新数据
		DetailFragment fragment = (DetailFragment) mViewPager
				.getFragment(mViewPager.getCurrentIndex());
		fragment.asyncInitData();

		// indicator
		setIndicator();
	}

	private void setIndicator() {
		if (mItems.size() == 1) {
			findViewById(R.id.layout_indicator).setVisibility(View.GONE);
		} else {
			findViewById(R.id.layout_indicator).setVisibility(View.VISIBLE);

			ImageView btnLast = (ImageView) findViewById(R.id.btn_last);
			if (mViewPager.getCurrentIndex() == 0) {
				btnLast.setEnabled(false);
				btnLast.setImageResource(R.drawable.last_disable);
			} else {
				btnLast.setEnabled(true);
				btnLast.setImageResource(R.drawable.last_enable);
			}

			ImageView btnNext = (ImageView) findViewById(R.id.btn_next);
			if (mViewPager.getCurrentIndex() == mItems.size() - 1) {
				btnNext.setEnabled(false);
				btnNext.setImageResource(R.drawable.next_disable);
			} else {
				btnNext.setEnabled(true);
				btnNext.setImageResource(R.drawable.next_enable);
			}
			mHandler.removeMessages(0);
			mHandler.sendEmptyMessageDelayed(0, 2000);

			setupUI(findViewById(R.id.viewpager_appoint));
		}
	}

	private void setupUI(View view) {
		view.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				setIndicator();
				return false;
			}
		});

		if (view instanceof ViewGroup) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				View innerView = ((ViewGroup) view).getChildAt(i);
				setupUI(innerView);
			}
		}
	}

	private class ActionClickListener implements EventListener {

		private static final int None = 0;
		private static final int Remove = 1;
		private static final int Refresh = 2;

		private String mText = "";
		private int mAction = 0;

		public ActionClickListener(String text, int action) {
			mText = text;
			mAction = action;
		}

		@Override
		public void onEvent(EventId id, EventArgs args) {
			stopLoading();
			StatusEventArgs statusArgs = (StatusEventArgs) args;
			if (statusArgs.getErrCode() == OperErrorCode.Success) {
				Alert.Toast(mText + "成功");

				if (mAction == Remove) {
					mItems.remove(mViewPager.getCurrentIndex());
					mViewPager.removeCurrent();

					// 没有友约了，返回
					if (mItems.isEmpty()) {
						finish();
					}
				}
				if (mAction == Refresh) {
					DetailFragment fragment = (DetailFragment) mViewPager
							.getFragment(mViewPager.getCurrentIndex());
					fragment.refresh();
				}
			} else {
				Alert.handleErrCode(statusArgs.getErrCode());
			}
		}
	}
}
