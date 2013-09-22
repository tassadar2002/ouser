package com.ouser.ui.user;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.ouser.R;
import com.ouser.event.EventArgs;
import com.ouser.event.EventId;
import com.ouser.event.EventListener;
import com.ouser.logic.LogicFactory;
import com.ouser.logic.OperErrorCode;
import com.ouser.logic.event.StringListEventArgs;
import com.ouser.ui.component.Loading;
import com.ouser.ui.helper.Alert;
import com.ouser.ui.ouser.BlackActivity;
import com.ouser.ui.topframework.TopFragment;
import com.ouser.ui.topframework.TopFragmentFactory;
import com.ouser.util.Const;
import com.umeng.fb.UMFeedbackService;

/**
 * 设置activity
 * @author hanlixin
 *
 */
public class SettingFragment extends TopFragment {

	public static class Factory implements TopFragmentFactory {

		@Override
		public TopFragment create() {
			return new SettingFragment();
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.actvy_frgmt_setting, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		getHeadbar().setTitle("系统设置");		
		getView().findViewById(R.id.layout_black).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), BlackActivity.class));
			}
		});
		getView().findViewById(R.id.layout_password).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), ModifyPasswordActivity.class));
			};
		});
		getView().findViewById(R.id.layout_feedback).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				UMFeedbackService.setGoBackButtonVisible();
				UMFeedbackService.openUmengFeedbackSDK(getActivity());
			}
		});
		getView().findViewById(R.id.layout_upgrade).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final Loading loading = new Loading();
				LogicFactory.self().getUpgrade().check(createUIEventListener(new EventListener() {

					@Override
					public void onEvent(EventId id, EventArgs args) {
						loading.stop();
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
								})
								.setTop(getView().findViewById(R.id.layout_head_bar).getHeight())
								.create().show();
						} else if(stringsArgs.getErrCode() == OperErrorCode.NoNeedUpgrade) {
							Alert.Toast("当前版本已是最新版本");
						} else {
							Alert.handleErrCode(stringsArgs.getErrCode());
						}
					}
				}));
				loading.start(getActivity());
			}
		});
		
		final CheckBox chkAutoLogin = (CheckBox)getView().findViewById(R.id.chk_autologin);
		chkAutoLogin.setChecked(LogicFactory.self().getUser().isAutoLogin());
		chkAutoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				LogicFactory.self().getUser().setAutoLogin(chkAutoLogin.isChecked());
			}
		});
		
		final CheckBox chkShareToWeixin = (CheckBox)getView().findViewById(R.id.chk_shareto_weixin);
		chkShareToWeixin.setChecked(LogicFactory.self().getShare().isShareToWeixin());
		chkShareToWeixin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				LogicFactory.self().getShare().setShareToWeixin(chkShareToWeixin.isChecked());
			}
		});
		
		View btnLogout = getView().findViewById(R.id.btn_logout);
		btnLogout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				LogicFactory.self().getUser().logout();
				getActivity().finish();
				startActivity(new Intent(getActivity(), LoginActivity.class));
			}
		});
	}
	
	@Override
	public void syncInitData(Bundle bundle) {
		super.syncInitData(bundle);
	}
}
