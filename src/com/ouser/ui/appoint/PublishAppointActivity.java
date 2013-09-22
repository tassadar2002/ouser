package com.ouser.ui.appoint;

import java.util.Calendar;
import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.js.cloudtags.KeywordsFlow;
import com.ouser.R;
import com.ouser.cache.Cache;
import com.ouser.event.EventArgs;
import com.ouser.event.EventId;
import com.ouser.event.EventListener;
import com.ouser.logic.LogicFactory;
import com.ouser.logic.OperErrorCode;
import com.ouser.logic.event.AppointEventArgs;
import com.ouser.logic.event.StringEventArgs;
import com.ouser.logic.event.StringListEventArgs;
import com.ouser.module.Appoint;
import com.ouser.module.AppointId;
import com.ouser.module.Enums;
import com.ouser.module.Gender;
import com.ouser.module.Location;
import com.ouser.ui.base.BaseActivity;
import com.ouser.ui.component.DateTimeDialogBuilder;
import com.ouser.ui.component.EnumDialogBuilder;
import com.ouser.ui.component.HeadBar;
import com.ouser.ui.helper.Alert;
import com.ouser.ui.helper.HideKeyboard;
import com.ouser.ui.map.LocationSelectActivity;
import com.ouser.ui.widget.AppointEditText;
import com.ouser.util.Const;
import com.ouser.util.SystemUtil;

/**
 * 发布友约
 * @author hanlixin
 *
 */
public class PublishAppointActivity extends BaseActivity {

	private static final int StartMapEnter = 1;
	
	private View mLayoutAppointInfo = null;
	private KeywordsFlow mLayoutCloudTag = null;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.actvy_publish_appoint);
		
		HeadBar headBar = new HeadBar();
		headBar.onCreate(findViewById(R.id.layout_head_bar), this);
		headBar.setTitle("发布友约");
		
		mLayoutAppointInfo = findViewById(R.id.layout_appoint_info);
		mLayoutCloudTag = (KeywordsFlow)findViewById(R.id.keywordsflow);

		final AppointEditText editBody = (AppointEditText)findViewById(R.id.edit_tag);
		findViewById(R.id.btn_tag).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mLayoutAppointInfo.getVisibility() == View.VISIBLE) {
			        LogicFactory.self().getAppoint().getCloudTag(
			        		createUIEventListener(new EventListener() {
						
						@Override
						public void onEvent(EventId id, EventArgs args) {
							stopLoading();
							mLayoutCloudTag.rubKeywords();
							StringListEventArgs stringsArgs = (StringListEventArgs)args;
							if(stringsArgs.getErrCode() == OperErrorCode.Success) {
								for(String string : stringsArgs.getStrings()) {
									mLayoutCloudTag.feedKeyword(string);
								}
								mLayoutCloudTag.go2Show(KeywordsFlow.ANIMATION_IN);
							} else {
								Alert.handleErrCode(stringsArgs.getErrCode());
							}
						}
					}));
			        startLoading();
				}
				switchVisible();
				editBody.clearFocus();
			}
		});
		editBody.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {
					if(mLayoutAppointInfo.getVisibility() == View.GONE) {
						switchVisible();
					}
				}
			}
		});
		
		findViewById(R.id.btn_clear_location).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setLocationValue(null, "不限");
			}
		});
		findViewById(R.id.edit_location).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TextView txtLocation = (TextView)findViewById(R.id.txt_location);
				Intent intent = new Intent(getActivity(), LocationSelectActivity.class);
				Location location = (Location)txtLocation.getTag();
				if(location != null) {
					intent.putExtra("location", location.toBundle());
					intent.putExtra("place", txtLocation.getText().toString());
				}
				intent.putExtra(LocationSelectActivity.IntentWithSearch, true);				
				startActivityForResult(intent, StartMapEnter);
			}
		});
		
		findViewById(R.id.btn_clear_time).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setTimeValue(null, "不限");
			}
		});
		findViewById(R.id.edit_time).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TextView txtTime = (TextView)findViewById(R.id.txt_time);
				Calendar select = 
					(txtTime.getTag() == null) ? Calendar.getInstance() : (Calendar)txtTime.getTag();
				
				final DateTimeDialogBuilder builder = new DateTimeDialogBuilder(getActivity());
				builder.setSelect(select);
				builder.setTop(findViewById(R.id.layout_head_bar).getHeight());
				builder.setCallback(new DateTimeDialogBuilder.Callback() {
					
					@Override
					public void onSelect(Calendar value) {
						if(value.before(Calendar.getInstance())) {
							value = Calendar.getInstance();
						}
						String format = String.format(Locale.CHINA, "%04d-%02d-%02d %02d:%02d", 
								value.get(Calendar.YEAR), 
								value.get(Calendar.MONTH) + 1, 
								value.get(Calendar.DAY_OF_MONTH), 
								value.get(Calendar.HOUR_OF_DAY), 
								value.get(Calendar.MINUTE));
						setTimeValue(value, format);
					}
				});
				builder.create().show();
			}
		});
		
		TextView txtPerson = (TextView)findViewById(R.id.txt_person);
		txtPerson.setText("不限");
		txtPerson.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final TextView txtPerson = (TextView)findViewById(R.id.txt_person);
				PersonFilterTag tag = (PersonFilterTag)txtPerson.getTag();
				if(tag == null) {
					tag = new PersonFilterTag();
				}
				new PersonFilterDialogBuilder(getActivity())
					.setSelect(tag.gender, tag.minAge, tag.maxAge)
					.setCallback(new PersonFilterDialogBuilder.Callback() {

						@Override
						public void onSelect(Gender gender, int minAge, int maxAge) {
							PersonFilterTag tag = new PersonFilterTag();
							tag.gender = gender;
							tag.minAge = minAge;
							tag.maxAge = maxAge;
							txtPerson.setTag(tag);
							String text = tag.minAge + "-" + tag.maxAge + "岁";
							if(tag.gender != Gender.None) {
								if(tag.gender == Gender.Female) {
									text += "的美女";
								} else {
									text += "的帅哥";
								}
							}
							txtPerson.setText(text);
						}
					})
					.setTop(findViewById(R.id.layout_head_bar).getHeight())
					.create().show();
			}
		});
		
		TextView txtCost = (TextView)findViewById(R.id.txt_cost);
		int defaultCost = Enums.self().getDefaultValue(Enums.Type.Cost);
		txtCost.setTag(defaultCost);
		txtCost.setText(Enums.self().getText(Enums.Type.Cost, defaultCost));
		txtCost.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showEnumDialog(R.id.txt_cost, Enums.Type.Cost);
			}
		});
		
		TextView txtPeriod = (TextView)findViewById(R.id.txt_period);
		int defaultPeriod = Enums.self().getDefaultValue(Enums.Type.Period);
		txtPeriod.setTag(defaultPeriod);
		txtPeriod.setText(Enums.self().getText(Enums.Type.Period, defaultPeriod));
		txtPeriod.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showEnumDialog(R.id.txt_period, Enums.Type.Period);
			};
		});
		
		final View viewFollow = findViewById(R.id.edit_follow);
		viewFollow.setTag(true);
		findViewById(R.id.edit_follow).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SystemUtil.hideKeyboard(editBody);
				ImageView imageCheck = (ImageView)findViewById(R.id.image_follow);
				if((Boolean)viewFollow.getTag()) {
					imageCheck.setImageResource(R.drawable.checkbox_uncheck);
					viewFollow.setTag(false);
				} else {
					imageCheck.setImageResource(R.drawable.checkbox_check);
					viewFollow.setTag(true);
				}
			}
		});
		
		final View viewPublic = findViewById(R.id.edit_public);
		viewPublic.setTag(true);
		findViewById(R.id.edit_public).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ImageView imageCheck = (ImageView)findViewById(R.id.image_public);
				if((Boolean)viewPublic.getTag()) {
					imageCheck.setImageResource(R.drawable.checkbox_uncheck);
					viewPublic.setTag(false);
				} else {
					imageCheck.setImageResource(R.drawable.checkbox_check);
					viewPublic.setTag(true);
				}
			}
		});
		
		final ImageView imageOption = (ImageView)findViewById(R.id.image_option);
		imageOption.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				View layoutOption = findViewById(R.id.layout_option);
				if(layoutOption.getVisibility() == View.VISIBLE) {
					layoutOption.setVisibility(View.GONE);
					imageOption.setImageResource(R.drawable.appoint_option_show);
				} else {
					layoutOption.setVisibility(View.VISIBLE);
					imageOption.setImageResource(R.drawable.appoint_option_hide);
				}
			}
		});
		
        mLayoutCloudTag.setDuration(800l);
        mLayoutCloudTag.setOnItemClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				editBody.append(((TextView)v).getText().toString());
			}
		});
        
        HideKeyboard.setupUI(this);
		
		findViewById(R.id.btn_publish).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AppointEditText editBody = (AppointEditText)findViewById(R.id.edit_tag);
				if(editBody.isEmptyText()) {
					Alert.Toast("无效的友约内容");
					return;
				}
				if(editBody.getText().toString().length() > 60) {
					Alert.Toast("友约内容最多填写60个字符");
					editBody.requestFocus();
					return;
				}
				
				Appoint appoint = new Appoint();
				appoint.setContent("", editBody.getText().toString(), "");
				PersonFilterTag person = (PersonFilterTag)findViewById(R.id.txt_person).getTag();
				if(person != null) {
					appoint.setAgeFilter(new Pair<Integer, Integer>(person.minAge, person.maxAge));
					appoint.setGenderFilter(person.gender);
				}
				Integer cost = (Integer)findViewById(R.id.txt_cost).getTag();
				if(cost != null) {
					appoint.setCostFilter(cost);
				}
				Integer period = (Integer)findViewById(R.id.txt_period).getTag();
				if(period != null) {
					appoint.setPeriodFilter(period);
				}
				TextView txtLocation = (TextView)findViewById(R.id.txt_location);
				Location location = (Location)txtLocation.getTag();
				if(location != null) {
					appoint.setPlace(txtLocation.getText().toString());
					appoint.setLocation(location);
				}
				Calendar time = (Calendar)findViewById(R.id.txt_time).getTag();
				if(time != null) {
					appoint.setTime((int)(time.getTimeInMillis() / 1000 / 60));
				}
				
				Boolean follow = (Boolean)findViewById(R.id.edit_follow).getTag();
				Boolean publicc = (Boolean)findViewById(R.id.edit_public).getTag();
				
				LogicFactory.self().getAppoint().publish(appoint, 
						follow == null ? true : follow, 
						publicc == null ? true : publicc, 
						createUIEventListener(new EventListener() {
					
					@Override
					public void onEvent(EventId id, EventArgs args) {
						stopLoading();
						StringEventArgs stringArgs = (StringEventArgs)args;
						if(stringArgs.getErrCode() == OperErrorCode.Success) {
							publishSuccess(stringArgs.getString());
						} else {
							Alert.handleErrCode(stringArgs.getErrCode());
						}
					}
				}));
				startLoading();
			}
		});
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		switch(arg0) {
		case StartMapEnter:
			if(arg1 == RESULT_OK) {
				Bundle bundle = arg2.getExtras().getBundle("location");
				Location location = new Location();
				location.fromBundle(bundle);
				String place = arg2.getExtras().getString("place");
				setLocationValue(location, place);
			}
			break;
		default:
			super.onActivityResult(arg0, arg1, arg2);
			break;
		}
	}
	
	private void setLocationValue(Location location, String text) {
		TextView txt = (TextView)findViewById(R.id.txt_location);
		txt.setText(text);
		txt.setTag(location);
		findViewById(R.id.btn_clear_location).setVisibility(location == null ? View.GONE : View.VISIBLE);
	}
	
	private void setTimeValue(Calendar calendar, String text) {
		TextView txt = (TextView)findViewById(R.id.txt_time);
		txt.setText(text);
		txt.setTag(calendar);
		findViewById(R.id.btn_clear_time).setVisibility(calendar == null ? View.GONE : View.VISIBLE);
	}
	
	/**
	 * 发布友约成功
	 */
	private void publishSuccess(String appointId) {
		Alert.Toast("发布成功");
		
		LogicFactory.self().getAppointInfo().get(new AppointId(appointId, Cache.self().getMyUid()), 
				createUIEventListener(new EventListener() {
			
			@Override
			public void onEvent(EventId id, EventArgs args) {
				stopLoading();
				AppointEventArgs appointArgs = (AppointEventArgs)args;
				if(appointArgs.getErrCode() == OperErrorCode.Success) {
					Intent intent = new Intent(getActivity(), AppointInviteActivity.class);
					intent.putExtra(AppointInviteActivity.IntentAppoint, appointArgs.getAppoint().toBundle());
					startActivity(intent);
				}
				setResult(RESULT_OK);
				finish();
			}
		}));
		startLoading();
	}
	
	private void showEnumDialog(int resId, final Enums.Type type) {
		final TextView txt = (TextView)findViewById(resId);
		Integer value = (Integer)txt.getTag();
		new EnumDialogBuilder(getActivity())
			.setEnum(	Enums.self().getTitle(type), 
						Enums.self().getList(type), 
						value == null ? 0 : value, 
						new EnumDialogBuilder.Callback() {
				
				@Override
				public void onSelect(int key) {
					txt.setTag(key);
					txt.setText(Enums.self().getText(type, key));
				}
			})
			.setTop(findViewById(R.id.layout_head_bar).getHeight())
			.create().show();
	}

	private void switchVisible() {
		if(mLayoutAppointInfo.getVisibility() == View.VISIBLE) {
			mLayoutCloudTag.setVisibility(View.VISIBLE);
			mLayoutAppointInfo.setVisibility(View.GONE);
		} else {
			mLayoutAppointInfo.setVisibility(View.VISIBLE);
			mLayoutCloudTag.setVisibility(View.GONE);
		}
	}
	
	private static class PersonFilterTag {
		public Gender gender = Gender.None;
		public int minAge = Const.DefaultValue.Age;
		public int maxAge = Const.DefaultValue.Age;
	}
}
