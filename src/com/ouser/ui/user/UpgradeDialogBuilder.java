package com.ouser.ui.user;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.ouser.R;
import com.ouser.ui.component.ContentDialogBuilder;

public class UpgradeDialogBuilder extends ContentDialogBuilder {

	public interface Callback {
		void onOK();
	}
	
	private String mVersion = "";
	private String mDesc = "";
	
	public UpgradeDialogBuilder(Activity activiy) {
		super(activiy);
		setResId(R.layout.layout_upgrade_dialog);
	}

	public UpgradeDialogBuilder setVersion(String version, String desc) {
		mVersion = version;
		mDesc = desc;
		return this;
	}
	
	public UpgradeDialogBuilder setCallback(final Callback value) {
		setCallback(new ContentDialogBuilder.Callback() {
			
			@Override
			public void onPrepare(View view) {
				((TextView)view.findViewById(R.id.txt_version)).setText("发现新版本" + mVersion + "，是否升级");
				((TextView)view.findViewById(R.id.txt_desc)).setText(mDesc);
			}

			@Override
			public boolean onOK(View view) {
				value.onOK();
				return true;
			}
		});
		return this;
	}
}
