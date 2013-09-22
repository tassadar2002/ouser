package com.ouser.ui.profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.ouser.R;
import com.ouser.event.EventArgs;
import com.ouser.event.EventId;
import com.ouser.event.EventListener;
import com.ouser.logic.LogicFactory;
import com.ouser.logic.OperErrorCode;
import com.ouser.logic.event.StatusEventArgs;
import com.ouser.module.Ouser;
import com.ouser.ui.base.BaseActivity;
import com.ouser.ui.component.HeadBar;
import com.ouser.ui.helper.Alert;
import com.ouser.ui.helper.HideKeyboard;

/**
 * 编辑我的态度，提交
 * 
 * @author Administrator
 * 
 */
public class EditAboutmeActivity extends BaseActivity {

	private boolean mDirty = false;
	private String mUid = "";

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.actvy_edit_aboutme);

		HeadBar headBar = new HeadBar();
		headBar.onCreate(findViewById(R.id.layout_head_bar), this);
		headBar.setOnHeadIconListener(new HeadBar.OnHeadIconListener() {
			
			@Override
			public void onClick() {
				if(mDirty) {
					promptExit();
				} else {
					finish();
				}	
			}
		});
		headBar.setTitle("修改我的态度");

		Bundle bundle = getIntent().getExtras().getBundle("data");
		Ouser mySelf = new Ouser();
		mySelf.fromBundle(bundle);

		mUid = mySelf.getUid();

		final EditText editValue = (EditText) findViewById(R.id.edit_value);
		editValue.setText(mySelf.getAboutme());
		editValue.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mDirty = true;
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		
		HideKeyboard.setupUI(this);
		
		headBar.setActionButton(R.drawable.title_save, new HeadBar.OnActionListener() {
			
			@Override
			public void onClick() {
				String text = editValue.getText().toString();
				if(text.length() > 80) {
					Alert.Toast("我的态度最多填写80个字符");
					editValue.requestFocus();
					return;
				}

				Ouser ouser = new Ouser();
				ouser.setUid(mUid);
				ouser.setAboutme(text);
				LogicFactory.self().getProfile().editAboutme(ouser, 
						createUIEventListener(new EventListener() {

							@Override
							public void onEvent(EventId id, EventArgs args) {
								StatusEventArgs statusArgs = (StatusEventArgs) args;
								if (statusArgs.getErrCode() == OperErrorCode.Success) {
									stopLoading();
									Alert.Toast("修改成功");
									setResult(RESULT_OK);
									finish();
								} else {
									Alert.handleErrCode(statusArgs.getErrCode());
								}
							}
						}));
				startLoading();
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		if(mDirty) {
			promptExit();
		} else {
			super.onBackPressed();
		}
	}

	private void promptExit() {
		new AlertDialog.Builder(this)
		.setTitle("保存提示")
		.setMessage("您修改的内容还未保存，是否退出？")
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		})
		.setNegativeButton("取消", null)
		.create().show();
	}
}
