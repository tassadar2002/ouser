package com.ouser.ui.profile;

import java.util.Calendar;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ouser.R;
import com.ouser.event.EventArgs;
import com.ouser.event.EventId;
import com.ouser.event.EventListener;
import com.ouser.logic.LogicFactory;
import com.ouser.logic.OperErrorCode;
import com.ouser.logic.event.StatusEventArgs;
import com.ouser.module.Enums;
import com.ouser.module.Gender;
import com.ouser.module.Ouser;
import com.ouser.ui.base.BaseActivity;
import com.ouser.ui.component.DateDialogBuilder;
import com.ouser.ui.component.EnumDialogBuilder;
import com.ouser.ui.component.GenderDialogBuilder;
import com.ouser.ui.component.HeadBar;
import com.ouser.ui.helper.Alert;
import com.ouser.ui.helper.Formatter;
import com.ouser.ui.helper.HideKeyboard;
import com.ouser.util.Constellation;
import com.ouser.util.StringUtil;

/**
 * 编辑我的资料
 * @author Administrator
 *
 */
public class EditProfileActivity extends BaseActivity {
	
	private String mUid = "";
	private boolean mDirty = false;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.actvy_edit_profile);
		
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
		headBar.setTitle("编辑资料");
		
		Bundle bundle = getIntent().getExtras().getBundle("data");
		Ouser mySelf = new Ouser();
		mySelf.fromBundle(bundle);
		
		mUid = mySelf.getUid();

		final EditText editNickName = setEditTextValue(R.id.edit_nickname, mySelf.getNickName());
		final EditText editHobby = setEditTextValue(R.id.edit_hobby, mySelf.getHobby());
		final EditText editSchool = setEditTextValue(R.id.edit_school, mySelf.getSchool());
		final EditText editCompany = setEditTextValue(R.id.edit_company, mySelf.getCompany());
		final EditText editJobTitle = setEditTextValue(R.id.edit_jobtitle, mySelf.getJobTitle());
		
		final View viewMerry = setEnumValue(R.id.layout_merry, R.id.txt_merry, Enums.Type.Merry, mySelf.getMerry());
		final View viewHeight = setEnumValue(R.id.layout_height, R.id.txt_height, Enums.Type.Height, mySelf.getHeight());
		final View viewBody = setEnumValue(R.id.layout_body, R.id.txt_body, Enums.Type.Body, mySelf.getBody());
		final View viewEducation = setEnumValue(R.id.layout_education, R.id.txt_education, Enums.Type.Education, mySelf.getEducation());
		
		final TextView txtBirthday = (TextView)findViewById(R.id.txt_birthday);
		txtBirthday.setTag(Constellation.convertBirthday(mySelf.getBirthday()));
		txtBirthday.setText(mySelf.getBirthday());
		findViewById(R.id.layout_birthday).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new DateDialogBuilder(getActivity())
					.setSelect((Calendar)txtBirthday.getTag())
					.setCallback(new DateDialogBuilder.Callback() {

						@Override
						public void onSelect(Calendar value) {
							if(!txtBirthday.getTag().equals(value)) {
								mDirty = true;
							}
							String format = String.format(Locale.CHINA, "%04d-%02d-%02d", 
									value.get(Calendar.YEAR), 
									value.get(Calendar.MONTH) + 1, 
									value.get(Calendar.DAY_OF_MONTH));
							txtBirthday.setText(format);
							txtBirthday.setTag(value);
						}
					})
					.setTop(findViewById(R.id.layout_head_bar).getHeight())
					.create().show();	
			}
		});
		
		final TextView txtGender = (TextView)findViewById(R.id.txt_gender);
		txtGender.setTag(mySelf.getGender());
		txtGender.setText(Formatter.getGenderText(mySelf.getGender()));
		findViewById(R.id.layout_gender).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final Gender value = (Gender)txtGender.getTag();
				new GenderDialogBuilder(getActivity())
					.setSelect(value)
					.setCallback(new GenderDialogBuilder.Callback() {
						
						@Override
						public void onSelect(Gender gender) {
							if(!value.equals(gender)) {
								mDirty = true;
							}
							txtGender.setTag(gender);
							txtGender.setText(Formatter.getGenderText(gender));
						}
					})
					.setTop(findViewById(R.id.layout_head_bar).getHeight())
					.create().show();
			}
		});
		
		HideKeyboard.setupUI(this);
		
		headBar.setActionButton(R.drawable.title_save, new HeadBar.OnActionListener() {
			
			@Override
			public void onClick() {

				String text = editNickName.getText().toString();
				if(StringUtil.isEmpty(text.trim())) {
					Alert.Toast("昵称不能为空");
					editNickName.requestFocus();
					return;
				}
				if(text.length() > 15) {
					Alert.Toast("昵称最多填写15个字符");
					editNickName.requestFocus();
					return;
				}
				text = editHobby.getText().toString();
				if(text.length() > 20) {
					Alert.Toast("爱好最多填写20个字符");
					editHobby.requestFocus();
					return;
				}
				text = editSchool.getText().toString();
				if(text.length() > 20) {
					Alert.Toast("学校最多填写20个字符");
					editSchool.requestFocus();
					return;
				}
				text = editCompany.getText().toString();
				if(text.length() > 15) {
					Alert.Toast("公司最多填写15个字符");
					editCompany.requestFocus();
					return;
				}
				text = editJobTitle.getText().toString();
				if(text.length() > 15) {
					Alert.Toast("职位最多填写15个字符");
					editJobTitle.requestFocus();
					return;
				}
				
				Ouser ouser = new Ouser();
				ouser.setUid(mUid);
				ouser.setNickName(editNickName.getText().toString());
				if(txtGender.getTag() != null) {
					ouser.setGender((Gender)txtGender.getTag());
				}
				if(viewMerry.getTag() != null) {
					ouser.setMerry((Integer)viewMerry.getTag());
				}
				if(viewHeight.getTag() != null) {
					ouser.setHeight((Integer)viewHeight.getTag());
				}
				if(viewBody.getTag() != null) {
					ouser.setBody((Integer)viewBody.getTag());
				}
				if(viewEducation.getTag() != null) {
					ouser.setEducation((Integer)viewEducation.getTag());
				}
				ouser.setHobby(editHobby.getText().toString());
				ouser.setSchool(editSchool.getText().toString());
				ouser.setCompany(editCompany.getText().toString());
				ouser.setJobTitle(editJobTitle.getText().toString());
				ouser.setBirthday(txtBirthday.getText().toString());

				LogicFactory.self().getProfile().edit(ouser, 
						createUIEventListener(new EventListener() {
							
							@Override
							public void onEvent(EventId id, EventArgs args) {
								StatusEventArgs statusArgs = (StatusEventArgs)args;
								if(statusArgs.getErrCode() == OperErrorCode.Success) {
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
	public void onBackPressed() {
		if(mDirty) {
			promptExit();
		} else {
			super.onBackPressed();
		}
	}

	private View setEnumValue(int layoutId, int txtId, final Enums.Type type, int selectedValue) {
		final TextView txt = (TextView)findViewById(txtId);
		if(selectedValue != -1) {
			txt.setTag(selectedValue);
			txt.setText(Enums.self().getText(type, selectedValue));
		}
		findViewById(layoutId).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final Integer value = (Integer)txt.getTag();
				
				new EnumDialogBuilder(getActivity())
				.setEnum(	Enums.self().getTitle(type), 
							Enums.self().getList(type), 
							value == null ? 0 : value, 
							new EnumDialogBuilder.Callback() {
					
					@Override
					public void onSelect(int key) {
						if(value != key) {
							mDirty = true;
						}
						txt.setTag(key);
						txt.setText(Enums.self().getText(type, key));
					}
				})
				.setTop(findViewById(R.id.layout_head_bar).getHeight())
				.create().show();
			}
		});
		return txt;
	}
	
	private EditText setEditTextValue(int resId, String value) {
		final EditText edit = (EditText)findViewById(resId);
		edit.setText(value);
		edit.addTextChangedListener(new TextWatcher() {
			
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
		return edit;
	}
	
	private void promptExit() {
		new AlertDialog.Builder(this)
		.setTitle("保存提示")
		.setMessage("您修改的资料还未保存，是否退出？")
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
