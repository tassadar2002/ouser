package com.ouser.ui.appoint;

import com.ouser.event.EventArgs;
import com.ouser.event.EventId;
import com.ouser.event.EventListener;
import com.ouser.logic.LogicFactory;
import com.ouser.logic.OperErrorCode;
import com.ouser.logic.event.AppointEventArgs;
import com.ouser.module.Appoint;
import com.ouser.module.AppointId;
import com.ouser.ui.base.BaseActivity;
import com.ouser.ui.base.BaseFragment;
import com.ouser.ui.helper.Alert;

public class AppointValidChecker {

	public interface OnValidListener {
		void onValid(Appoint appoint);
	}
	
	public static void check(final Appoint appoint, 
			final BaseFragment fragment, final OnValidListener listener) {
		check(appoint.getAppointId(), fragment, listener);
	}
	
	public static void check(final Appoint appoint, 
			final BaseActivity activity, final OnValidListener listener) {
		check(appoint.getAppointId(), activity, listener);
	}
	
	public static void check(final AppointId appointId, 
			final BaseFragment fragment, final OnValidListener listener) {
		check(appointId, (BaseActivity)fragment.getActivity(), listener);
	}
	
	public static void check(final AppointId appointId, 
			final BaseActivity activity, final OnValidListener listener) {
		LogicFactory.self().getAppointInfo().get(appointId, 
				activity.createUIEventListener(new EventListener() {

			@Override
			public void onEvent(EventId id, EventArgs args) {
				activity.stopLoading();
				AppointEventArgs appointArgs = (AppointEventArgs) args;
				if (appointArgs.getErrCode() == OperErrorCode.Success) {
					if (appointArgs.getAppoint().isDeleted()) {
						Alert.Toast("该友约已被删除");
					} else {
						if(listener != null) {
							listener.onValid(appointArgs.getAppoint());
						}
					}
				} else {
					Alert.handleErrCode(appointArgs.getErrCode());
				}
			}
		}));
		activity.startLoading();
	}
}
