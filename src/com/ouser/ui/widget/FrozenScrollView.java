package com.ouser.ui.widget;

import android.view.View;
import android.widget.ScrollView;

public class FrozenScrollView extends ScrollView {
	
	private View[] mFrozenViews = null;
	private View[] mOrginalViews = null;

	public FrozenScrollView(android.content.Context context,
			android.util.AttributeSet attrs) {
		super(context, attrs);
	}
	
    public void setFrozenView(View[] frozenViews, View[] orginalViews) {
    	mFrozenViews = frozenViews;
    	mOrginalViews = orginalViews;
    }
    
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {

		View showView = null;
		int y = getScrollY();
		int top = 0;
		for(int i = mOrginalViews.length - 1; i >= 0; --i) {
			
			View nowOv = mOrginalViews[i];
			View nowFv = mFrozenViews[i];
			View lastFv = (i != 0) ? mFrozenViews[i - 1] : null;
			
			if(y > nowOv.getTop()) {
				setViewVisible(nowFv, true);
				showView = nowFv;
				top = y;
				break;
			}
			if(lastFv == null) {
				break;
			}
			if(nowOv.getTop() - y < lastFv.getHeight()) {
				setViewVisible(lastFv, true);
				showView = lastFv;
				top = nowOv.getTop() - lastFv.getHeight();
				break;
			}
		}
		for(View v : mFrozenViews) {
			if(v != showView) {
				setViewVisible(v, false);
				v.setVisibility(View.INVISIBLE);
			}
		}
		if(showView != null) {
			showView.layout(0, top, showView.getWidth(), top + showView.getHeight());
		}
		super.onScrollChanged(l, t, oldl, oldt);
	}
	
	private void setViewVisible(View v, boolean visible) {
		if(visible) {
			if(v.getVisibility() != View.VISIBLE) {
				v.setVisibility(View.VISIBLE);
			}
		} else {
			if(v.getVisibility() == View.VISIBLE) {
				v.setVisibility(View.INVISIBLE);
			}
		}
	}
}
