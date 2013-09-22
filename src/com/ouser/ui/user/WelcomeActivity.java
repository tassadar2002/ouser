package com.ouser.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ouser.R;
import com.ouser.event.EventArgs;
import com.ouser.event.EventId;
import com.ouser.event.EventListener;
import com.ouser.logic.LogicFactory;
import com.ouser.logic.OperErrorCode;
import com.ouser.logic.UserLogic;
import com.ouser.logic.event.StatusEventArgs;
import com.ouser.ui.base.BaseActivity;
import com.ouser.ui.helper.Alert;
import com.ouser.ui.other.GuideActivity;
import com.ouser.ui.topframework.TopActivity;
import com.ouser.ui.topframework.TopFragmentType;
import com.ouser.util.Const;
import com.ouser.util.SharedPreferencesUtil;

/**
 * 欢迎activity
 * @author hanlixin
 *
 */
public class WelcomeActivity extends BaseActivity {
	
	/** 是否为进入软件时显示 */
	private boolean mShowEnter = true;
	
	/** 是否为第一次进入软件 */
	private boolean mShowFirst = false;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.actvy_welcome);
		
//		if(!LocationManager.self().isEnable()) {
//			Alert.Toast("本应用需要定位服务，请打开定位服务后重启本应用，谢谢");
//			finish();
//			return;
//		}
		
		LogicFactory.self().getUser().showWelcome();
		
		mShowFirst = SharedPreferencesUtil.getBoolean(
				Const.SharedPreferenceKey.DefaultName, 
				Const.SharedPreferenceKey.FirstStartup, true);
		if(mShowFirst) {
			findViewById(R.id.layout_user).setVisibility(View.GONE);
			startActivity(new Intent(this, GuideActivity.class));
			SharedPreferencesUtil.set(
					Const.SharedPreferenceKey.DefaultName,
					Const.SharedPreferenceKey.FirstStartup, false);
			mShowEnter = false;
		} else {
			UserLogic userLogic = LogicFactory.self().getUser();
			if(userLogic.canLoginWithLatest() && userLogic.isAutoLogin()) {
				login();
				findViewById(R.id.layout_user).setVisibility(View.GONE);
			}
		}
		
		findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), LoginActivity.class));
			}
		});
		findViewById(R.id.btn_register).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), RegisterActivity.class));
			}
		});
	}

	@Override
	protected void onResume() {
		if(!mShowEnter) {
			findViewById(R.id.layout_user).setVisibility(View.VISIBLE);
		}
		super.onResume();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		boolean exit = intent.getBooleanExtra("exit", true);
		if(exit) {
			finish();
		}
		super.onNewIntent(intent);
	}
	
	private void login() {
		LogicFactory.self().getUser().loginWithLatest(createUIEventListener(new EventListener() {
			
			@Override
			public void onEvent(EventId id, EventArgs args) {
				OperErrorCode errCode = ((StatusEventArgs)args).getErrCode();
				switch(errCode) {
				case Success:
					Intent intent = new Intent(getActivity(), TopActivity.class);
					int switchPage = TopFragmentType.NearAppoint.getValue();
					if(getIntent() != null) {
						switchPage = 
							getIntent().getIntExtra(Const.Intent.SwitchPage, TopFragmentType.NearAppoint.getValue());
					}
					intent.putExtra(Const.Intent.SwitchPage, switchPage);
					startActivity(intent);
					mShowEnter = false;
					break;
				case UidNoExist:
				case PasswordError:
					findViewById(R.id.layout_user).setVisibility(View.VISIBLE);
					startActivity(new Intent(Const.Application, LoginActivity.class));
					break;
				case LocationNotAviable:
					Alert.Toast("请先开启定位服务后重新启动程序");
					finish();
					break;
				case NetNotAviable:
					findViewById(R.id.layout_user).setVisibility(View.VISIBLE);
					Alert.showNetAvaiable();
					break;
				default:			
					Alert.Toast("登录失败");
					break;
				}
			}
		}));
	}
}
