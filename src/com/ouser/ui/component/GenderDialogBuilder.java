package com.ouser.ui.component;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ouser.R;
import com.ouser.module.Gender;
import com.ouser.util.Const;

public class GenderDialogBuilder extends ContentDialogBuilder {

	public interface Callback {
		void onSelect(Gender gender);
	}
	
	private Gender mGender = Gender.None;
	
	private View[] mRegionGender = new View[2];
	private TextView[] mTxtGender = new TextView[2];
	private ImageView[] mImageGender = new ImageView[2];
	
	public GenderDialogBuilder(Activity activiy) {
		super(activiy);
		setResId(R.layout.layout_gender_dialog);
	}
	
	public GenderDialogBuilder setSelect(Gender gender) {
		mGender = gender;
		return this;
	}
	
	public GenderDialogBuilder setCallback(final Callback value) {
		setCallback(new ContentDialogBuilder.Callback() {
			
			@Override
			public void onPrepare(View view) {
				
				mTxtGender[0] = (TextView)view.findViewById(R.id.txt_male); 
				mTxtGender[1] = (TextView)view.findViewById(R.id.txt_female);
				
				mImageGender[0] = (ImageView)view.findViewById(R.id.image_male); 
				mImageGender[1] = (ImageView)view.findViewById(R.id.image_female);
				
				mRegionGender[0] = view.findViewById(R.id.rbtn_male); 
				mRegionGender[1] = view.findViewById(R.id.rbtn_female);
				
				mRegionGender[0].setTag(Gender.Male);
				mRegionGender[1].setTag(Gender.Female);
				
				if(mGender == null) {
					mGender = Gender.Female;
				}
				
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
			}

			@Override
			public boolean onOK(View view) {
				value.onSelect(mGender);
				return true;
			}
		});
		return this;
	}
	
	private void onGenderSelect() {
		for(int i = 0; i < 2; ++i) {
			if((Gender)mRegionGender[i].getTag() == mGender) {
				mRegionGender[i].setBackgroundColor(getColor(R.color.light_gray));
				mTxtGender[i].setTextColor(getColor(R.color.main));
			} else {
				mRegionGender[i].setBackgroundColor(Const.Application.getResources().getColor(R.color.white));
				mTxtGender[i].setTextColor(getColor(R.color.black_text));
			}
		}
	}
	
	private int getColor(int resId) {
		return Const.Application.getResources().getColor(resId);
	}
}
