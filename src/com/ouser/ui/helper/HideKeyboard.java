package com.ouser.ui.helper;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.ouser.util.SystemUtil;

public class HideKeyboard {

	public static void setupUI(Activity activity) {
		setupUI(activity, activity.getWindow().getDecorView());
	}
	
	public static void setupUI(final Activity activity, View view) {
		
		//Set up touch listener for non-text box views to hide keyboard.
	    if(!(view instanceof EditText)) {
	        view.setOnTouchListener(new View.OnTouchListener() {

	            public boolean onTouch(View v, MotionEvent event) {
	                SystemUtil.hideKeyboard(activity.getCurrentFocus());
	                return false;
	            }
	        });
	    }

	    //If a layout container, iterate over children and seed recursion.
	    if (view instanceof ViewGroup) {
	        for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
	            View innerView = ((ViewGroup) view).getChildAt(i);
	            setupUI(activity, innerView);
	        }
	    }
	}
}
