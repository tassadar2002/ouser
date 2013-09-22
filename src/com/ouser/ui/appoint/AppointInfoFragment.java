package com.ouser.ui.appoint;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ouser.R;
import com.ouser.event.EventArgs;
import com.ouser.event.EventId;
import com.ouser.event.EventListener;
import com.ouser.image.PhotoManager;
import com.ouser.logic.LogicFactory;
import com.ouser.logic.OperErrorCode;
import com.ouser.logic.event.AppointEventArgs;
import com.ouser.logic.event.OuserEventArgs;
import com.ouser.module.Appoint;
import com.ouser.module.AppointId;
import com.ouser.module.Photo;
import com.ouser.ui.base.BaseFragment;
import com.ouser.ui.helper.ActivitySwitch;
import com.ouser.ui.helper.Alert;
import com.ouser.ui.helper.Formatter;

@SuppressLint("ValidFragment")
public class AppointInfoFragment extends BaseFragment {

	private Appoint mAppoint = null;

	public AppointInfoFragment(Appoint appoint) {
		mAppoint = appoint;
	}
	
	public AppointInfoFragment(AppointId appointId) {
		mAppoint = new Appoint();
		mAppoint.setAppointId(appointId);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.frgmt_appoint_info, null);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getView().setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				LogicFactory.self().getAppointInfo().get(mAppoint.getAppointId(), 
						createUIEventListener(new EventListener() {
					
					@Override
					public void onEvent(EventId id, EventArgs args) {
						stopLoading();
						AppointEventArgs appointArgs = (AppointEventArgs)args;
						if(appointArgs.getErrCode() == OperErrorCode.Success) {
							ActivitySwitch.toAppointDetailForResult(
									getActivity(), appointArgs.getAppoint());
						} else {
							Alert.handleErrCode(appointArgs.getErrCode());
						}
					}
				}));
				startLoading();
			}
		});
	}

	@Override
	public void syncInitData(Bundle bundle) {
		LogicFactory.self().getAppointInfo().get(mAppoint.getAppointId(), 
				createUIEventListener(new EventListener() {
			
			@Override
			public void onEvent(EventId id, EventArgs args) {
				stopMyselfLoading();
				
				AppointEventArgs appointArgs = (AppointEventArgs)args;
				if(appointArgs.getErrCode() == OperErrorCode.Success) {
					mAppoint = appointArgs.getAppoint();
				}
				// 填充友约信息
				fillAppoint();
				
				// 填充发布人头像
				getPublisherPortrait();
			}
		}));
		startMyselfLoading();
	}
	
	private void getPublisherPortrait() {
		String uid = mAppoint.getAppointId().getUid();
		LogicFactory.self().getProfile().get(uid, createUIEventListener(new EventListener() {
			
			@Override
			public void onEvent(EventId id, EventArgs args) {
				OuserEventArgs ouserArgs = (OuserEventArgs)args;
				if(ouserArgs.getErrCode() == OperErrorCode.Success) {
					((ImageView)getView().findViewById(R.id.image_portrait)).setImageBitmap(
							PhotoManager.self().getBitmap(ouserArgs.getOuser().getPortrait(), Photo.Size.Small));
				}
			}
		}));
	}

	private void startMyselfLoading() {
		getView().findViewById(R.id.progress_appoint_loading).setVisibility(View.VISIBLE);
		getView().findViewById(R.id.layout_appoint_info).setVisibility(View.GONE);
	}
	
	private void stopMyselfLoading() {
		getView().findViewById(R.id.progress_appoint_loading).setVisibility(View.GONE);
		getView().findViewById(R.id.layout_appoint_info).setVisibility(View.VISIBLE);
	}

	private void fillAppoint() {
		((TextView)getView().findViewById(R.id.txt_content)).setText(Formatter.getAppointContent(mAppoint));
		((TextView)getView().findViewById(R.id.txt_involve_count)).setText(
				mAppoint.getJoinCount() + "人参加      " + mAppoint.getViewCount() + "人浏览");
	}
}
