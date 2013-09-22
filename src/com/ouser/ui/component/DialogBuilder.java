package com.ouser.ui.component;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;

import com.ouser.R;

public class DialogBuilder {
	
	protected Activity mActivity = null;
	protected OuserDialog mDialog = null;
	
	/** 距顶部偏移 */
	private int mTop = 0;
	
	public class OuserDialog extends Dialog {
		public OuserDialog(Context context) {
			super(context, R.style.Dialog);
		}		
	}
	
	public DialogBuilder(Activity activity) {
		mActivity = activity;
	}
	
	public DialogBuilder setTop(int top) {
		mTop = top;
		return this;
	}

	public Dialog create() {
		mDialog = new OuserDialog(mActivity);
		
		Display display = mActivity.getWindowManager().getDefaultDisplay();	
		WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
		layoutParams.width = display.getWidth();
		layoutParams.gravity = Gravity.TOP;
		layoutParams.y = mTop;
		mDialog.getWindow().setAttributes(layoutParams);
		
		return mDialog;
	}
}
