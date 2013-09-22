package com.ouser.ui.user;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.ouser.R;
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

public class ModifyPasswordActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.actvy_modify_password);
		
		HeadBar headBar = new HeadBar();
		headBar.onCreate(findViewById(R.id.layout_head_bar), this);
		headBar.setTitle("密码管理");
		
		final EditText editOldPassword = (EditText)findViewById(R.id.edit_old_password);
		final EditText editNewPassword = (EditText)findViewById(R.id.edit_new_password);
		final EditText editConfirmPassword = (EditText)findViewById(R.id.edit_confirm_password);
		
		HideKeyboard.setupUI(this);
		
		View btnConfirm = findViewById(R.id.btn_confirm);
		btnConfirm.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String oldPass = editOldPassword.getText().toString();
				String newPass = editNewPassword.getText().toString();
				String confirmPass = editConfirmPassword.getText().toString();
				
				if(oldPass.length() < 6) {
					Alert.Toast("旧密码长度至少6位");
					return;
				}
				if(newPass.length() < 6) {
					Alert.Toast("新密码长度至少6位");
					return;
				}
				if(!newPass.equals(confirmPass)) {
					Alert.Toast("新密码与确认密码不一致");
					return;
				}
				LogicFactory.self().getUser().modifyPasswrod(oldPass, newPass, 
						createUIEventListener(new EventListener() {
					
					@Override
					public void onEvent(EventId id, EventArgs args) {
						StatusEventArgs statusArgs = (StatusEventArgs)args;
						if(statusArgs.getErrCode() == OperErrorCode.Success) {
							Alert.Toast("操作成功");
							finish();
						} else if(statusArgs.getErrCode() == OperErrorCode.PasswordError){
							Alert.Toast("旧密码不正确");
							editOldPassword.requestFocus();
						} else {
							Alert.handleErrCode(statusArgs.getErrCode());
						}
					}
				}));
			}
		});
	}
}
