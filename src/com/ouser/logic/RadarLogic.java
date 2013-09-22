package com.ouser.logic;

import android.os.AsyncTask;

import com.ouser.cache.Cache;
import com.ouser.event.EventListener;
import com.ouser.location.LocationManager;
import com.ouser.location.RadarClient;
import com.ouser.module.Location;
import com.ouser.protocol.GetRadaServerProcess;
import com.ouser.protocol.ResponseListener;

public class RadarLogic extends BaseLogic {

	static class Factory implements BaseLogic.Factory {

		@Override
		public BaseLogic create() {
			return new RadarLogic();
		}
	}
	
	RadarLogic() {
	}
	
	public interface LocationChangedListener {
		void onUpdate(Location targetLocation);		
	}

	private String mUid = "";
	private String mServer = "";
	private int mPort = 0;
	private boolean mRunning = false;
	private LocationChangedListener mListener = null;
	private LocationFetcher mTask = null;
	
	public boolean isRunning() {
		return mRunning;
	}
	
	public void startRadar(final String uid, final LocationChangedListener locationListener, 
			final EventListener listener) {
		if(mRunning) {
			return;
		}
		mRunning = true;
		if("".equals(mServer) || mPort == 0) {
			final GetRadaServerProcess process = new GetRadaServerProcess();
			process.setMyUid(Cache.self().getMyUid());
			process.setTargetUid(uid);
			process.run(new ResponseListener() {
				
				@Override
				public void onResponse(String requestId) {
					OperErrorCode errCode = Util.convertFromStatus(process.getStatus());
					if(errCode == OperErrorCode.Success) {
						mServer = process.getIp();
						mPort = process.getPort();
						mUid = uid;
						if(!mRunning) {
							fireStatusEvent(listener, OperErrorCode.Unknown);
							return;
						}
						if(start(locationListener)) {
							fireStatusEvent(listener, OperErrorCode.Success);
							return;
						}
					}
					fireStatusEvent(listener, OperErrorCode.Unknown);
				}
			});
		} else {
			if(start(locationListener)) {
				fireStatusEvent(listener, OperErrorCode.Success);
			} else {
				fireStatusEvent(listener, OperErrorCode.Unknown);
			}
		}
	}
	
	public void stopRadar() {
		if(!mRunning) {
			return;
		}
		mRunning = false;
		mTask.cancel(false);
		mTask = null;
		LocationManager.self().stopRadar();
	}
	
	private boolean start(LocationChangedListener locationListener) {
		if(LocationManager.self().startRadar()) {
			mListener = locationListener;
			mTask = new LocationFetcher();
			mTask.execute();
			return true;
		}
		return false;
	}
	
	private class LocationFetcher extends AsyncTask<Void, Location, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			while (true) {
				if (isCancelled()) {
					break;
				}
				try {
					int errorCount = 0;					
					RadarClient rc = new RadarClient(Cache.self().getMyUid(), mUid, mServer, mPort);
					if (!rc.annouce()) {
						Thread.sleep(5000);
						continue;
					}
					while (true) {
						if (isCancelled()) {
							rc.disconnect();
							break;
						}
						if (errorCount > 10) {
							break;
						}

						Location location = LocationManager.self().getCurrent();
						if (!rc.reportLocation((float)location.getLng(), (float)location.getLat())) {
							++errorCount;
						}

						location = rc.requestLocation();
						if (location == null) {
							++errorCount;
						}
						publishProgress(location);
						Thread.sleep(5000);
					}
					rc.disconnect();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Location... values) {
			if(mListener != null) {
				mListener.onUpdate(values[0]);
			}
		}
	}
}
