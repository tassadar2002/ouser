package com.ouser.ui.appoint;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ouser.R;
import com.ouser.module.Gender;
import com.ouser.ui.component.ContentDialogBuilder;
import com.ouser.util.Const;
import com.sc.android.view.RangeSeekBar;

class PersonFilterDialogBuilder extends ContentDialogBuilder {

	public interface Callback {
		void onSelect(Gender gender, int minAge, int maxAge);
	}
	
	private Gender mGender = Gender.None;
	private int mMinAge = -1;
	private int mMaxAge = -1;
	
	private View[] mRegionGender = new View[3];
	private TextView[] mTxtGender = new TextView[3];
	private ImageView[] mImageGender = new ImageView[3];
	
	private TextView[] mTxtAge = new TextView[13];
	
	public PersonFilterDialogBuilder(Activity activiy) {
		super(activiy);
		setResId(R.layout.layout_person_filter_dialog);
	}
	
	public PersonFilterDialogBuilder setSelect(Gender gender, int minAge, int maxAge) {
		mGender = gender;
		mMinAge = minAge;
		mMaxAge = maxAge;
		return this;
	}
	
	public PersonFilterDialogBuilder setCallback(final Callback value) {
		setCallback(new ContentDialogBuilder.Callback() {
			
			@Override
			public void onPrepare(View view) {
				if(mMinAge == -1) {
					mMinAge = 20;
				}
				if(mMaxAge == -1) {
					mMaxAge = 40;
				}
				
				mTxtGender[0] = (TextView)view.findViewById(R.id.txt_whole);
				mTxtGender[1] = (TextView)view.findViewById(R.id.txt_male); 
				mTxtGender[2] = (TextView)view.findViewById(R.id.txt_female);
				
				mImageGender[0] = (ImageView)view.findViewById(R.id.image_whole);
				mImageGender[1] = (ImageView)view.findViewById(R.id.image_male); 
				mImageGender[2] = (ImageView)view.findViewById(R.id.image_female);
				
				mRegionGender[0] = view.findViewById(R.id.rbtn_whole);
				mRegionGender[1] = view.findViewById(R.id.rbtn_male); 
				mRegionGender[2] = view.findViewById(R.id.rbtn_female);
				
				mRegionGender[0].setTag(Gender.None);
				mRegionGender[1].setTag(Gender.Male);
				mRegionGender[2].setTag(Gender.Female);
				
				for(View txt : mRegionGender) {
					txt.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							mGender = (Gender)v.getTag();
							onGenderSelect();
						}
					});
				}
				onGenderSelect();
				
				ViewGroup vg = (ViewGroup)view.findViewById(R.id.layout_age);
				for(int i = 0; i < 13; ++i) {
					TextView txt = new TextView(mActivity);
					txt.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1));
					txt.setGravity(Gravity.CENTER_HORIZONTAL);
					txt.setTextSize(14);
					txt.setText(String.valueOf(5 * i));
					txt.setTag(5 * i);
					vg.addView(txt);
					mTxtAge[i] = txt;
				}
				onAgeSelect();
				
				final RangeSeekBar seekbar = (RangeSeekBar)view.findViewById(R.id.rangeSeekBar);
				seekbar.setListener(new RangeSeekBar.RangeSeekBarListener() {
					
					@Override
					public void onSeek(int index, float value) {
						if(index == 0) {
							mMinAge = (int)value;
						}
						if(index == 1) {
							mMaxAge = (int)value;
						}
						onAgeSelect();
					}
					
					@Override
					public void onCreate(int index, float value) {
						seekbar.setThumbValue(0, mMinAge);
						seekbar.setThumbValue(1, mMaxAge);
					}
				});
			}

			@Override
			public boolean onOK(View view) {
				value.onSelect(mGender, mMinAge, mMaxAge);
				return true;
			}
		});
		return this;
	}
	
	private void onGenderSelect() {
		for(int i = 0; i < 3; ++i) {
			if((Gender)mRegionGender[i].getTag() == mGender) {
				mRegionGender[i].setBackgroundColor(getColor(R.color.light_gray));
				mTxtGender[i].setTextColor(getColor(R.color.main));
			} else {
				mRegionGender[i].setBackgroundColor(Const.Application.getResources().getColor(R.color.white));
				mTxtGender[i].setTextColor(getColor(R.color.black_text));
			}
		}
	}
	
	private void onAgeSelect() {
		for(TextView txt : mTxtAge) {
			txt.setTextColor(getColor(R.color.black_text));
		}
		for(TextView txt : mTxtAge) {
			if((Integer)txt.getTag() == mMaxAge || (Integer)txt.getTag() == mMinAge) {
				txt.setTextColor(getColor(R.color.main));
			}
		}
	}
	
	private int getColor(int resId) {
		return Const.Application.getResources().getColor(resId);
	}
}
