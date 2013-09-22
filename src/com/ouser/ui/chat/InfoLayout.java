package com.ouser.ui.chat;

import android.view.View;
import android.widget.TextView;

import com.ouser.R;
import com.ouser.module.Ouser;
import com.ouser.ui.appoint.AppointInfoFragment;
import com.ouser.ui.base.BaseFragment;
import com.ouser.ui.map.RadarInfo;
import com.ouser.ui.ouser.OuserInfoFragment;

class InfoLayout extends ChatBaseLayout {

	@Override
	public void onCreate() {
		super.onCreate();
		
		BaseFragment fragment = null;
		if(getChatId().isSingle()) {
			fragment = initOuserInfoLayout();
		} else {
			fragment = new AppointInfoFragment(getChatId().getGroupId());
		}
		getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.layout_info, fragment).commit();
		fragment.asyncInitData();
	}
	
	private BaseFragment initOuserInfoLayout() {
		Ouser ouser = new Ouser();
		ouser.setUid(getChatId().getSingleId());
		OuserInfoFragment fragment = new OuserInfoFragment(ouser);
		fragment.setActionListener(new OuserInfoFragment.OnActionListener() {
			
			@Override
			public void onInitActionButton(TextView button) {
				if(!getChatId().isSingle()) {
					button.setVisibility(View.GONE);
					return;
				}
				final TextView fButton = button;
				fButton.setBackgroundResource(R.drawable.radar_close);
				fButton.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						RadarInfo radar = getLayoutFactory().getRadar();
						if(radar.isRunning()) {
							radar.stop();
							fButton.setBackgroundResource(R.drawable.radar_close);
						} else {
							radar.start(new RadarInfo.OnActionListener() {

								@Override
								public void onReady() {
									fButton.setEnabled(true);
									fButton.setBackgroundResource(R.drawable.radar_open);
								}

								@Override
								public void onError() {
									fButton.setEnabled(true);
									fButton.setBackgroundResource(R.drawable.radar_open);
								}

								@Override
								public void onDismiss() {
								}
							});
							fButton.setEnabled(false);
						}
					}
				});
			}
		});
		return fragment;
	}
}
