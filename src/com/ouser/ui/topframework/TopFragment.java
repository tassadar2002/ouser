package com.ouser.ui.topframework;

import android.os.Bundle;

import com.ouser.R;
import com.ouser.ui.base.BaseFragment;
import com.ouser.ui.component.HeadBar;

public class TopFragment extends BaseFragment {

	private HeadBar mHeadbar = new HeadBar();
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mHeadbar.onCreate(getView().findViewById(R.id.layout_head_bar), getActivity());
	}
	
	public HeadBar getHeadbar() {
		return mHeadbar;
	}
	
	public void onMenuOpen() {
		
	}
}
