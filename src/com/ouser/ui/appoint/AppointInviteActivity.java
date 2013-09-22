package com.ouser.ui.appoint;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ouser.R;
import com.ouser.event.EventArgs;
import com.ouser.event.EventId;
import com.ouser.event.EventListener;
import com.ouser.logic.LogicFactory;
import com.ouser.logic.OperErrorCode;
import com.ouser.logic.event.IntegerEventArgs;
import com.ouser.logic.event.StatusEventArgs;
import com.ouser.module.Appoint;
import com.ouser.ui.base.BaseActivity;
import com.ouser.ui.component.HeadBar;
import com.ouser.ui.helper.Alert;

public class AppointInviteActivity extends BaseActivity {

	public static final String IntentAppoint = "appoint";

	private Appoint mAppoint = new Appoint();

	private int mRemain = 0;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.actvy_appoint_invite);

		Bundle bundle = getIntent().getExtras().getBundle(IntentAppoint);
		if (bundle == null) {
			return;
		}
		mAppoint.fromBundle(bundle);

		HeadBar headBar = new HeadBar();
		headBar.onCreate(findViewById(R.id.layout_head_bar), this);
		headBar.setTitle("友约邀请");

		setRemainInviteCountText("该友约今日还可发送0条邀请");
		setRemainInviteCount();

		((TextView) findViewById(R.id.txt_content)).setText(mAppoint
				.getContent());

		findViewById(R.id.btn_follow).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						if (mRemain > 0) {
							Intent intent = new Intent(getActivity(),
									AppointInviteListActivity.class);
							intent.putExtra(
									AppointInviteListActivity.IntentType,
									AppointInviteListActivity.Follower);
							intent.putExtra(
									AppointInviteListActivity.IntentAppoint,
									mAppoint.toBundle());
							intent.putExtra(
									AppointInviteListActivity.IntentRemain,
									mRemain);
							startActivityForResult(intent, 0);
						} else {
							Alert.Toast("该友约今日已不能发送邀请");
						}
					}
				});
		findViewById(R.id.btn_befollow).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						if (mRemain > 0) {
							Intent intent = new Intent(getActivity(),
									AppointInviteListActivity.class);
							intent.putExtra(
									AppointInviteListActivity.IntentType,
									AppointInviteListActivity.BeFollower);
							intent.putExtra(
									AppointInviteListActivity.IntentAppoint,
									mAppoint.toBundle());
							intent.putExtra(
									AppointInviteListActivity.IntentRemain,
									mRemain);
							startActivityForResult(intent, 0);
						} else {
							Alert.Toast("该友约今日已不能发送邀请");
						}
					}
				});
		findViewById(R.id.btn_phone_contact).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						shareToSms();
					}
				});
		findViewById(R.id.btn_share_weixin).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						shareToWeixin();
					}
				});
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if (arg0 == 0 && arg1 == RESULT_OK) {
			setRemainInviteCount();
		}
	}

	private void shareToWeixin() {
		LogicFactory
				.self()
				.getShare()
				.shareToWeixin(mAppoint,
						createUIEventListener(new EventListener() {

							@Override
							public void onEvent(EventId id, EventArgs args) {
								StatusEventArgs statusArgs = (StatusEventArgs) args;
								if (statusArgs.getErrCode() == OperErrorCode.Success) {
									Alert.Toast("分享成功");
								} else {
									Alert.handleErrCode(statusArgs.getErrCode());
								}
							}
						}));
	}

	private void shareToSms() {
		LogicFactory
				.self()
				.getShare()
				.shareToSms(mAppoint,
						createUIEventListener(new EventListener() {

							@Override
							public void onEvent(EventId id, EventArgs args) {
								StatusEventArgs statusArgs = (StatusEventArgs) args;
								if (statusArgs.getErrCode() != OperErrorCode.Success) {
									Alert.handleErrCode(statusArgs.getErrCode());
								}
							}
						}));
	}

	private void setRemainInviteCount() {
		LogicFactory
				.self()
				.getAppoint()
				.getInviteRemainCount(mAppoint,
						createUIEventListener(new EventListener() {

							@Override
							public void onEvent(EventId id, EventArgs args) {
								stopLoading();
								IntegerEventArgs intArgs = (IntegerEventArgs) args;
								if (intArgs.getErrCode() == OperErrorCode.Success) {
									String text = String.format(
											"该友约今日还可发送%d条邀请",
											intArgs.getInteger());
									setRemainInviteCountText(text);
									mRemain = intArgs.getInteger();
								} else {
									Alert.handleErrCode(intArgs.getErrCode());
								}
							}
						}));
		startLoading();
	}

	private void setRemainInviteCountText(String text) {
		((TextView) findViewById(R.id.txt_remain_count)).setText(text);
	}
}
