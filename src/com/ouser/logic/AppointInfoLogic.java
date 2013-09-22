package com.ouser.logic;

import com.ouser.cache.Cache;
import com.ouser.event.EventId;
import com.ouser.event.EventListener;
import com.ouser.logic.event.AppointEventArgs;
import com.ouser.module.Appoint;
import com.ouser.module.AppointId;
import com.ouser.protocol.GetAppointProcess;
import com.ouser.protocol.ResponseListener;

public class AppointInfoLogic extends BaseLogic {
	
	static class Factory implements BaseLogic.Factory {

		@Override
		public BaseLogic create() {
			return new AppointInfoLogic();
		}
	}
	
	AppointInfoLogic() {
	}

	/**
	 * 获得某条友约详细信息
	 * @param appoint
	 * @param listener
	 */
	public void get(Appoint appoint, final EventListener listener) {
		get(appoint.getAppointId(), listener);
	}
	
	public void get(final AppointId appointId, final EventListener listener) {
		final GetAppointProcess process = new GetAppointProcess();
		process.setMyUid(Cache.self().getMyUid());
		process.setAppointId(appointId);
		process.run(new ResponseListener() {

			@Override
			public void onResponse(String requestId) {
				OperErrorCode errCode = Util.convertFromStatus(process.getStatus());
				AppointEventArgs args = null;
				if (errCode == OperErrorCode.Success) {
					args = new AppointEventArgs(process.getResult());
				} else {
					Appoint appoint = new Appoint();
					appoint.setAppointId(appointId);
					args = new AppointEventArgs(appoint, errCode);
				}
				if(listener != null) {
					fireEvent(listener, args);
				}
				fireEvent(EventId.eGetAppointInfo, args);
			}
		});
	}

}
