package com.ouser.ui.component;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ouser.R;

public class ContentDialogBuilder extends DialogBuilder {
	
	public interface Callback {
		void onPrepare(View view);
		boolean onOK(View view);
	}

	protected int mContentRes = 0;
	protected Callback mCallback = null;
	
	public ContentDialogBuilder(Activity activity) {
		super(activity);
	}
	
	public ContentDialogBuilder setResId(int resId) {
		mContentRes = resId;
		return this;
	}
		
	public ContentDialogBuilder setCallback(Callback callback) {
		mCallback = callback;
		return this;
	}
	
	public Dialog create() {
		final Dialog dialog = super.create();
		
		View view = LayoutInflater.from(mActivity).inflate(R.layout.layout_content_dialog, null);
		dialog.setContentView(view);
		
		ViewGroup vg = (ViewGroup)view.findViewById(R.id.layout_content);
		final View root = LayoutInflater.from(mActivity).inflate(mContentRes, vg);
		view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mCallback != null) {
					if(!mCallback.onOK(root)) {
						return;
					}
				}
				dialog.dismiss();
			}
		});
		view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		if(mCallback != null) {
			mCallback.onPrepare(root);
		}
		
		return dialog;
	}
}
