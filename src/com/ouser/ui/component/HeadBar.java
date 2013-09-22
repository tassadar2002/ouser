package com.ouser.ui.component;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ouser.R;

/**
 * 界面的顶部工具栏
 * @author hanlixin
 *
 */
public class HeadBar {

	public static interface OnActionListener {
		void onClick();
	}
	public static interface OnHeadIconListener {
		void onClick();
	}

	private View mParentView = null;
	private OnHeadIconListener mHeadIconListener = null;

	public void onCreate(View parentView, final Activity activity) {
		mParentView = parentView;
		
		final ImageView imgNavigation = (ImageView)mParentView.findViewById(R.id.image_navigation);
		if(mHeadIconListener == null) {
			imgNavigation.setImageResource(R.drawable.title_return);
		}
		imgNavigation.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mHeadIconListener == null) {
					activity.finish();
				} else {
					mHeadIconListener.onClick();
				}
			}
		});
	}

	public void setTitle(String value) {
		if(mParentView == null) {
			return;
		}
		TextView txtTitle = (TextView)mParentView.findViewById(R.id.txt_title);
		txtTitle.setText(value);
	}

	public void setActionButton(String value, OnActionListener listener) {
		if(mParentView != null) {
			((TextView)mParentView.findViewById(R.id.btn_action)).setText(value);
			setActionButton(listener);
		}
	}
	
	public void setActionButton(int resId, OnActionListener listener) {
		if(mParentView != null) {
			mParentView.findViewById(R.id.btn_action).setBackgroundResource(resId);
			setActionButton(listener);
		}
	}

	public void setOnHeadIconListener(OnHeadIconListener listener) {
		mHeadIconListener = listener;
	}
	
	private void setActionButton(final OnActionListener listener) {
		View btnAction = mParentView.findViewById(R.id.btn_action);
		btnAction.setVisibility(View.VISIBLE);
		btnAction.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(listener != null) {
					listener.onClick();
				}
			}
		});
	}
}
