package com.ouser.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.ouser.R;
import com.ouser.cache.Cache;
import com.ouser.event.EventArgs;
import com.ouser.event.EventId;
import com.ouser.event.EventListener;
import com.ouser.logic.LogicFactory;
import com.ouser.logic.OperErrorCode;
import com.ouser.logic.event.StatusEventArgs;
import com.ouser.ui.base.BaseActivity;
import com.ouser.ui.component.HeadBar;
import com.ouser.ui.helper.Alert;
import com.ouser.ui.helper.HideKeyboard;
import com.ouser.ui.topframework.TopActivity;
import com.ouser.util.StringUtil;

/**
 * 登录activty
 * @author hanlixin
 *
 */
public class LoginActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.actvy_login);
		
		HeadBar headBar = new HeadBar();
		headBar.onCreate(findViewById(R.id.layout_head_bar), this);
		headBar.setTitle("登录");

		findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				login();				
			}
		});
		findViewById(R.id.btn_forgotpass).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), ForgotPasswordActivity.class));
			}
		});
		
		String uid = Cache.self().getMySelfUser().getUid();
		if(!StringUtil.isEmpty(uid)) {
			((EditText)findViewById(R.id.edit_name)).setText(uid);
		}
		
		HideKeyboard.setupUI(this);
	}
	
	private void login() {
		final EditText editName = (EditText)findViewById(R.id.edit_name);
		final EditText editPass = (EditText)findViewById(R.id.edit_password);

		String name = editName.getText().toString();
		String password = editPass.getText().toString();
		if("".equals(name)) {
			Alert.Toast("请输入用户名");
			return;
		}
		if("".equals(password)) {
			Alert.Toast("请输入密码");
			return;
		}
		
		LogicFactory.self().getUser().login(name, password, createUIEventListener(new EventListener() {
			
			@Override
			public void onEvent(EventId id, EventArgs args) {
				stopLoading();
				OperErrorCode errCode = ((StatusEventArgs)args).getErrCode();
				
				switch(errCode) {
				case Success:
					startActivity(new Intent(getActivity(), TopActivity.class));
					finish();
					break;
				case UidNoExist:
					Alert.Toast("用户名不存在");
					break;
				case PasswordError:
					Alert.Toast("密码错误");
					break;
				case LocationNotAviable:
					Alert.Toast("请先开启定位服务后重新启动程序");
					break;
				case NetNotAviable:
					Alert.showNetAvaiable();
					break;
				default:
					Alert.Toast("登录失败");
					break;
				}
			}
		}));
		startLoading();
	}
}

