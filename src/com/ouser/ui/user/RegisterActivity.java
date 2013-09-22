package com.ouser.ui.user;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ouser.R;
import com.ouser.event.EventArgs;
import com.ouser.event.EventId;
import com.ouser.event.EventListener;
import com.ouser.logic.LogicFactory;
import com.ouser.logic.event.StatusEventArgs;
import com.ouser.module.Gender;
import com.ouser.module.Ouser;
import com.ouser.module.User;
import com.ouser.ui.base.BaseActivity;
import com.ouser.ui.component.DateDialogBuilder;
import com.ouser.ui.component.GenderDialogBuilder;
import com.ouser.ui.component.HeadBar;
import com.ouser.ui.component.MenuDialogBuilder;
import com.ouser.ui.helper.Alert;
import com.ouser.ui.helper.Formatter;
import com.ouser.ui.helper.HideKeyboard;
import com.ouser.ui.helper.PhotoFetcher;
import com.ouser.ui.topframework.TopActivity;
import com.ouser.util.ImageUtil;
import com.ouser.util.StringUtil;

public class RegisterActivity extends BaseActivity {
	
	private static final int MenuCamera = 1;
	private static final int MenuAlbum = 2;
	
	private PhotoFetcher mFetcher = new PhotoFetcher();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.actvy_register);
		
		HeadBar headBar = new HeadBar();
		headBar.onCreate(findViewById(R.id.layout_head_bar), this);
		headBar.setTitle("注册藕丝儿");
		
		final ImageView imagePortrait = (ImageView)findViewById(R.id.image_portrait);
		imagePortrait.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				List<Pair<Integer, String>> items = new ArrayList<Pair<Integer, String>>();
				items.add(new Pair<Integer, String>(MenuCamera, "拍照"));
				items.add(new Pair<Integer, String>(MenuAlbum, "相册"));
				
				new MenuDialogBuilder(getActivity())
					.setMenuItem(items, new MenuDialogBuilder.Callback() {
						
						@Override
						public void onClick(int key) {
							switch(key) {
							case MenuCamera:
								mFetcher.getFromCamera();
								break;
							case MenuAlbum:
								mFetcher.getFromAlbum();
								break;
							}
						}
					})
					.setTop(getActivity().findViewById(R.id.layout_head_bar).getHeight())
					.create().show();
			}
		});
		
		mFetcher.setActivity(this);
		mFetcher.setOnActionListener(new PhotoFetcher.OnActionListener() {
			
			@Override
			public void onComplete(Parcelable data) {
				imagePortrait.setImageBitmap((Bitmap)data);
				imagePortrait.setTag(true);
			}
			
			@Override
			public void onCancel() {
			}
		});
		
		final TextView txtGender = (TextView)findViewById(R.id.txt_gender);
		txtGender.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new GenderDialogBuilder(getActivity())
				.setSelect((Gender)txtGender.getTag())
				.setCallback(new GenderDialogBuilder.Callback() {
					
					@Override
					public void onSelect(Gender gender) {
						txtGender.setTag(gender);
						txtGender.setText(Formatter.getGenderText(gender));
					}
				})
				.setTop(findViewById(R.id.layout_head_bar).getHeight())
				.create().show();
			}
		});
		
		final TextView txtBirthday = (TextView)findViewById(R.id.txt_birthday);
		txtBirthday.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new DateDialogBuilder(getActivity())
				.setSelect((Calendar)txtBirthday.getTag())
				.setCallback(new DateDialogBuilder.Callback() {

					@Override
					public void onSelect(Calendar value) {
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
		
		final EditText editNickName = (EditText)findViewById(R.id.edit_nickname);
		final EditText editEmail = (EditText)findViewById(R.id.edit_email);
		final EditText editPassword = (EditText)findViewById(R.id.edit_password);
		
		HideKeyboard.setupUI(this);
		
		headBar.setActionButton(R.drawable.title_save, new HeadBar.OnActionListener() {
			
			@Override
			public void onClick() {
				String email = editEmail.getText().toString();
				String password = editPassword.getText().toString();
				String nickName = editNickName.getText().toString();
				String birthday = txtBirthday.getText().toString();
				Gender gender = (Gender)txtGender.getTag();
				Drawable drawable = imagePortrait.getDrawable();
				
				if(StringUtil.isEmpty(email)) {
					Alert.Toast("用户名不能为空");
					return;
				}
				if(StringUtil.isEmpty(password)) {
					Alert.Toast("密码不能为空");
					return;
				}
				if(password.trim().length() < 6) {
					Alert.Toast("密码至少6位");
					return;
				}
				if(StringUtil.isEmpty(nickName)) {
					Alert.Toast("昵称不能为空");
					return;
				}
				if(imagePortrait.getTag() == null) {
					Alert.Toast("请选择头像");
					return;
				}
				if(gender == null) {
					Alert.Toast("性别不能为空");
					return;
				}
				if(StringUtil.isEmpty(birthday)) {
					Alert.Toast("生日不能为空");
					return;
				}
				Ouser ouser = new Ouser();
				ouser.setUid(email);
				ouser.setNickName(nickName);
				ouser.setGender(gender);
				ouser.setBirthday(birthday);
				
				User user = new User();
				user.setUid(email);
				user.setPassword(password);
				
				LogicFactory.self().getUser().register(user, ouser, ImageUtil.drawableToBitmap(drawable), 
						createUIEventListener(new EventListener() {
					
					@Override
					public void onEvent(EventId id, EventArgs args) {
						stopLoading();
						StatusEventArgs statusArgs = (StatusEventArgs)args;
						switch(statusArgs.getErrCode()) {
						case Success:
							Alert.Toast("注册成功");
							startActivity(new Intent(getActivity(), TopActivity.class));
							finish();
							break;
						case UidExist:
							Alert.Toast("邮箱已经注册");
							break;
						case UidInvalid:
							Alert.Toast("邮箱无效");
							break;
						case LocationNotAviable:
							Alert.Toast("请先开启定位服务后重新启动程序");
							break;
						default:
							Alert.handleErrCode(statusArgs.getErrCode());
							break;
						}
					}
				}));
				startLoading();	
			}
		});
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		mFetcher.handleActivityResult(arg0, arg1, arg2);
	}

}
