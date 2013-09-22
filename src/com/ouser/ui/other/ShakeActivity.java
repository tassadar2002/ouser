package com.ouser.ui.other;

import android.os.Bundle;

import com.ouser.R;
import com.ouser.event.EventArgs;
import com.ouser.event.EventId;
import com.ouser.event.EventListener;
import com.ouser.logic.LogicFactory;
import com.ouser.logic.OperErrorCode;
import com.ouser.logic.ShakeLogic;
import com.ouser.logic.event.AppointsEventArgs;
import com.ouser.ui.base.BaseActivity;
import com.ouser.ui.component.HeadBar;
import com.ouser.ui.helper.ActivitySwitch;
import com.ouser.ui.helper.Alert;

public class ShakeActivity extends BaseActivity {
	
	private boolean mGetting = false;
	
	private ShakeLogic.OnActionListener mListener = new ShakeLogic.OnActionListener() {
		
		@Override
		public void onShake() {
			if(!mGetting) {
				showRandomApponts();
			}
		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.actvy_shake);
		
		HeadBar headBar = new HeadBar();
		headBar.onCreate(findViewById(R.id.layout_head_bar), this);
		headBar.setTitle("摇一摇");
	}

	@Override
	public void onResume() {
		super.onResume();
		LogicFactory.self().getShake().start(mListener);
	}

	@Override
	public void onPause() {
		LogicFactory.self().getShake().stop();
		super.onPause();
	}
	
	private void showRandomApponts() {
		LogicFactory.self().getAppoint().getRandoms(createUIEventListener(new EventListener() {
			
			@Override
			public void onEvent(EventId id, EventArgs args) {
				stopLoading();
				AppointsEventArgs appointsArgs = (AppointsEventArgs)args;
				if(appointsArgs.getErrCode() == OperErrorCode.Success) {
					ActivitySwitch.toAppointDetail(getActivity(), appointsArgs.getAppoints());
				} else {
					Alert.handleErrCode(appointsArgs.getErrCode());
				}
				mGetting = false;
			}
		}));
		startLoading();
		mGetting = true;
	}
}
