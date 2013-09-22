package com.ouser.ui.ouser;

import android.os.Bundle;

import com.ouser.R;
import com.ouser.ui.base.BaseActivity;
import com.ouser.ui.component.HeadBar;

public class BlackActivity extends BaseActivity {

	private BlackFragment mFragment = null;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.actvy_black);

		HeadBar headBar = new HeadBar();
		headBar.onCreate(findViewById(R.id.layout_head_bar), this);
		headBar.setTitle("黑名单");

		mFragment = new BlackFragment();
		getSupportFragmentManager().beginTransaction().replace(R.id.layout_list, mFragment)
				.commit();
		mFragment.asyncInitData();
	}
}
