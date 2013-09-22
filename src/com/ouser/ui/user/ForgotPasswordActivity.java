package com.ouser.ui.user;

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
import com.ouser.util.StringUtil;

public class ForgotPasswordActivity extends BaseActivity {


	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.actvy_forgot_password);
		
		HeadBar headBar = new HeadBar();
		headBar.onCreate(findViewById(R.id.layout_head_bar), this);
		headBar.setTitle("找回密码");

		findViewById(R.id.btn_forgotpass).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText editName = (EditText)findViewById(R.id.edit_name);				
				String name = editName.getText().toString();
				if("".equals(name)) {
					Alert.Toast("请输入用户名");
					return;
				}
				
				LogicFactory.self().getUser().forgotPassword(name, createUIEventListener(new EventListener() {
					
					@Override
					public void onEvent(EventId id, EventArgs args) {
						StatusEventArgs statusArgs = (StatusEventArgs)args;
						if(statusArgs.getErrCode() == OperErrorCode.Success) {
							Alert.Toast("密码重置邮件已经发送到您的邮箱，请登录您的邮箱重置密码");
						} else {
							Alert.handleErrCode(statusArgs.getErrCode());
						}
					}
				}));
			}
		});
		
		String uid = Cache.self().getMySelfUser().getUid();
		if(!StringUtil.isEmpty(uid)) {
			((EditText)findViewById(R.id.edit_name)).setText(uid);
		}
		
		HideKeyboard.setupUI(this);
	}
}
