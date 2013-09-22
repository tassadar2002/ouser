package com.ouser.ui.component;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class ResizeLayout extends LinearLayout {
	
	public static interface Listener {
		
		/** 输入法可视变化 */
		void onInputMethodVisibleChanged(boolean show);
	}
	
	private Listener mListener = null;
	private int mInitHeight = 0;

	public ResizeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setListener(Listener value) {
		mListener = value;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
		if(oldh == 0) {
			mInitHeight = h;
		}
		if(mListener != null) {
			if(h == mInitHeight) {
				mListener.onInputMethodVisibleChanged(false);
			} else {
				mListener.onInputMethodVisibleChanged(true);
			}
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
