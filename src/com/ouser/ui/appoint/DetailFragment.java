package com.ouser.ui.appoint;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ouser.R;
import com.ouser.event.EventArgs;
import com.ouser.event.EventId;
import com.ouser.event.EventListener;
import com.ouser.logic.LogicFactory;
import com.ouser.logic.OperErrorCode;
import com.ouser.logic.event.AppointEventArgs;
import com.ouser.logic.event.AppointInvolveOusersEventArgs;
import com.ouser.logic.event.StatusEventArgs;
import com.ouser.module.Appoint;
import com.ouser.module.Appoint.Status;
import com.ouser.module.ChatId;
import com.ouser.module.ChatIdFactory;
import com.ouser.module.Ouser;
import com.ouser.ui.base.BaseFragment;
import com.ouser.ui.helper.ActivitySwitch;
import com.ouser.ui.helper.Alert;
import com.ouser.ui.helper.Formatter;
import com.ouser.ui.map.LocationViewActivity;
import com.ouser.ui.topframework.TopActivity;
import com.ouser.ui.topframework.TopFragmentType;
import com.ouser.ui.widget.ClickableTextView;
import com.ouser.util.Const;
import com.ouser.util.StringUtil;

/**
 * 友约详情
 * 
 * @author hanlixin
 * 
 */
@SuppressLint("ValidFragment")
class DetailFragment extends BaseFragment {

	public interface OnActionListener {
		/** 友约信息发生变化 */
		void onUpdate(Appoint appoint);
	}

	private Appoint mAppoint = null;

	private PhotosLayout mLayoutJoin = null;
	private PhotosLayout mLayoutView = null;

	private OnActionListener mActionListener = null;

	private View.OnClickListener mInviteClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (mAppoint.getStatus() == Status.Done) {
				Alert.Toast("该友约已过期");
			} else {
				Intent intent = new Intent(getActivity(), AppointInviteActivity.class);
				intent.putExtra(AppointInviteActivity.IntentAppoint, mAppoint.toBundle());
				startActivity(intent);
			}
		}
	};

	private View.OnClickListener mJoinClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			LogicFactory.self().getAppoint().join(mAppoint, createUIEventListener(new EventListener() {

				@Override
				public void onEvent(EventId id, EventArgs args) {
					stopLoading();
					StatusEventArgs statusArgs = (StatusEventArgs) args;
					if (statusArgs.getErrCode() == OperErrorCode.Success) {
						refreshAppoint(true);
						refreshInvolve();
					} else {
						Alert.handleErrCode(statusArgs.getErrCode());
					}
				}
			}));
			startLoading();
		}
	};

	private View.OnClickListener mSingleChatClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			ChatId chatId = ChatIdFactory.create(mAppoint.getAppointId().getUid());
			ActivitySwitch.toChatForResult(getActivity(), chatId);
		}
	};

	private View.OnClickListener mGroupChatClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			AppointValidChecker.check(mAppoint, getFragment(), new AppointValidChecker.OnValidListener() {
				
				@Override
				public void onValid(Appoint appoint) {
					ChatId chatId = ChatIdFactory.create(mAppoint.getAppointId());
					ActivitySwitch.toGroupChatForResult(getActivity(), chatId, mAppoint.getContent());
				}
			});
		}
	};

	public DetailFragment(Appoint appoint, OnActionListener listener) {
		mAppoint = appoint;
		mActionListener = listener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.frgmt_appoint_detail, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		fillAppoint();
		initInvolve();
	}

	@Override
	public void syncInitData(Bundle bundle) {
		// 只加载参加和查看藕丝，不刷新友约内容
		refreshInvolve();
	}

	public void refresh() {
		refreshAppoint(false);
		refreshInvolve();
	}

	private void fillAppoint() {
		AppointBodyClickListener listener = new AppointBodyClickListener();
		ClickableTextView txtContent = (ClickableTextView) getView().findViewById(R.id.txt_content);
		txtContent.clearText();
		txtContent.appendText(mAppoint.getHeader());
		for (String tag : mAppoint.getBody().split("#")) {
			if (!"".equals(tag)) {
				txtContent.appendText("#" + tag, listener);
			}
		}
		txtContent.appendText(mAppoint.getTail());
		txtContent.complete();

		if (mAppoint.getTime() == Const.DefaultValue.Time) {
			getView().findViewById(R.id.layout_time).setVisibility(View.GONE);
		} else {
			TextView txtTime = (TextView) getView().findViewById(R.id.txt_time);
			txtTime.setText(Formatter.formatDateTime(mAppoint.getTime()));
		}

		if (mAppoint.getLocation().isEmpty()) {
			getView().findViewById(R.id.layout_location).setVisibility(View.GONE);
		} else {
			TextView txtLocation = (TextView) getView().findViewById(R.id.txt_location);
			if (StringUtil.isEmpty(mAppoint.getPlace())) {
				txtLocation.setText("点击查看友约地点");
			} else {
				txtLocation.setText(mAppoint.getPlace());
			}

			getView().findViewById(R.id.layout_location).setOnClickListener(
					new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							if (mAppoint.getLocation().isEmpty()) {
								return;
							}
							Intent intent = new Intent(getActivity(), LocationViewActivity.class);
							intent.putExtra("location", mAppoint.getLocation().toBundle());
							intent.putExtra("place", mAppoint.getPlace());
							startActivity(intent);
						}
					});
		}
		refreshActionButton();
	}

	private void refreshAppoint(final boolean join) {
		LogicFactory.self().getAppointInfo().get(mAppoint, createUIEventListener(new EventListener() {

			@Override
			public void onEvent(EventId id, EventArgs args) {
				stopLoading();
				AppointEventArgs appointArgs = (AppointEventArgs) args;
				if (appointArgs.getErrCode() == OperErrorCode.Success) {
					mAppoint = appointArgs.getAppoint();
					fillAppoint();
					if (mActionListener != null) {
						mActionListener.onUpdate(mAppoint);
					}
					if(join) {
						share();
					}
				} else {
					Alert.handleErrCode(appointArgs.getErrCode());
				}
			}
		}));
		startLoading();
	}
	
	private void share() {
		if(!LogicFactory.self().getShare().isShareToWeixin()) {
			return;
		}
		new AlertDialog.Builder(getActivity())
			.setMessage("是否分享到微信？")
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					LogicFactory.self().getShare().shareToWeixin(mAppoint, createUIEventListener(new EventListener() {
						
						@Override
						public void onEvent(EventId id, EventArgs args) {
							stopLoading();
							StatusEventArgs statusArgs = (StatusEventArgs)args;
							if(statusArgs.getErrCode() == OperErrorCode.Success) {
								Alert.Toast("分享成功");
							} else {
								Alert.handleErrCode(statusArgs.getErrCode());
							}
						}
					}));
					startLoading();
				}
			})
			.setNegativeButton("取消", null)
			.create().show();
	}

	private void refreshActionButton() {
		ImageView btnLeft = (ImageView) getView().findViewById(R.id.btn_appoint_action_left);
		ImageView btnRight = (ImageView) getView().findViewById(R.id.btn_appoint_action_right);

		switch (mAppoint.getRelation()) {
		case Publish:
			btnLeft.setImageResource(R.drawable.appoint_invent);
			btnLeft.setOnClickListener(mInviteClickListener);

			if (mAppoint.getJoinCount() == 0) {
				btnRight.setImageResource(R.drawable.appoint_group_chat_disable);
			} else {
				btnRight.setImageResource(R.drawable.appoint_group_chat);
				btnRight.setOnClickListener(mGroupChatClickListener);
			}
			break;
		case Join:
			btnLeft.setImageResource(R.drawable.appoint_single_chat);
			btnLeft.setOnClickListener(mSingleChatClickListener);

			btnRight.setImageResource(R.drawable.appoint_group_chat);
			btnRight.setOnClickListener(mGroupChatClickListener);
			break;
		case None:
			btnLeft.setImageResource(R.drawable.appoint_join);
			btnLeft.setOnClickListener(mJoinClickListener);

			btnRight.setImageResource(R.drawable.appoint_single_chat);
			btnRight.setOnClickListener(mSingleChatClickListener);
		}
	}

	private void initInvolve() {
		mLayoutJoin = new PhotosLayout.JoinPhotosLayout(this);
		mLayoutView = new PhotosLayout.ViewPhotosLayout(this);

		mLayoutJoin.onCreate(mAppoint.getJoinCount());
		mLayoutView.onCreate(mAppoint.getViewCount());

		// FrozenScrollView sv = (FrozenScrollView)
		// getView().findViewById(R.id.layout_detail);
		// View[] frozenView = { mLayoutJoin.getFrozenView(),
		// mLayoutView.getFrozenView() };
		// View[] orginalView = { mLayoutJoin.getOrginalLayout(),
		// mLayoutView.getOrginalLayout() };
		// sv.setFrozenView(frozenView, orginalView);
	}

	private void refreshInvolve() {
		getView().findViewById(R.id.progress_join).setVisibility(View.VISIBLE);

		LogicFactory.self().getAppoint().getInvolveOusers(mAppoint, createUIEventListener(new EventListener() {

			@Override
			public void onEvent(EventId id, EventArgs args) {
				AppointInvolveOusersEventArgs ousersArgs = (AppointInvolveOusersEventArgs) args;
				if (ousersArgs.getErrCode() == OperErrorCode.Success) {
					fillInvolve(ousersArgs.getJoinOusers(), ousersArgs.getViewOusers());
				} else {
					Alert.handleErrCode(ousersArgs.getErrCode());
				}
			}
		}));
	}

	private void fillInvolve(List<Ouser> joinOusers, List<Ouser> viewOusers) {
		getView().findViewById(R.id.progress_join).setVisibility(View.GONE);

		mLayoutJoin.refresh(joinOusers);
		mLayoutView.refresh(viewOusers);

		getView().findViewById(R.id.layout_join_head).setVisibility(
				joinOusers.isEmpty() ? View.GONE : View.VISIBLE);
		getView().findViewById(R.id.layout_view_head).setVisibility(
				viewOusers.isEmpty() ? View.GONE : View.VISIBLE);
	}

	private class AppointBodyClickListener implements ClickableTextView.OnClickListener {

		@Override
		public void onClick(String text) {
			Intent intent = new Intent(getActivity(), TopActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.putExtra(Const.Intent.SwitchPage, TopFragmentType.SearchAppoint.getValue());
			intent.putExtra(SearchAppointFragment.IntentKeyword, text);
			startActivity(intent);
		}
	}
}
