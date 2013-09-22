package com.ouser.ui.other;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;

import com.ouser.R;
import com.ouser.ui.base.BaseActivity;
import com.ouser.ui.user.WelcomeActivity;

public class GuideActivity extends BaseActivity {
	
	private int[] mImageResourceId = {
			R.drawable.guide_001,
			R.drawable.guide_002, 
			R.drawable.guide_003, 
			R.drawable.guide_004, 
			R.drawable.guide_005, 
	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.actvy_guide);
		
		final List<View> views = new ArrayList<View>();
		for(int i = 0; i < mImageResourceId.length; ++i) {
			View v = LayoutInflater.from(this).inflate(R.layout.layout_guide, null);
			((ImageView)v.findViewById(R.id.image_guide)).setImageResource(mImageResourceId[i]);
			views.add(v);
		}
		
		View lastView = views.get(mImageResourceId.length - 1);
		final View btnStart = lastView.findViewById(R.id.btn_start);
		btnStart.setVisibility(View.VISIBLE);
		btnStart.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), WelcomeActivity.class);
				intent.putExtra("exit", false);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			}
		});
		int height = getWindow().getWindowManager().getDefaultDisplay().getHeight();
		int width = getWindow().getWindowManager().getDefaultDisplay().getWidth();
		int top = height * 700 / 1280;
		int bottom = height * 100 / 1280;
		
		AbsoluteLayout.LayoutParams params = (AbsoluteLayout.LayoutParams)btnStart.getLayoutParams();
		params.x = 0;
		params.y = top;
		params.width = width;
		params.height = bottom;
		btnStart.setLayoutParams(params);		
		
		ViewPager vp = (ViewPager)findViewById(R.id.viewpager);
		vp.setAdapter(new PagerAdapter() {
			
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}
			
			@Override
			public int getCount() {
				return mImageResourceId.length;
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				((ViewPager) container).addView(views.get(position), 0);
				return views.get(position);
			}

			@Override
			public void destroyItem(ViewGroup container, int position, Object object) {
				((ViewPager)container).removeView(views.get(position));
			}
		});
	}

	
}
