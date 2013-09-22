package com.ouser.ui.map;

import android.os.Handler;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.ouser.R;
import com.ouser.event.EventArgs;
import com.ouser.event.EventId;
import com.ouser.event.EventListener;
import com.ouser.location.LocationManager;
import com.ouser.logic.LogicFactory;
import com.ouser.logic.OperErrorCode;
import com.ouser.logic.RadarLogic;
import com.ouser.logic.event.StatusEventArgs;
import com.ouser.module.Location;
import com.ouser.ui.chat.ChatBaseLayout;
import com.ouser.ui.helper.Alert;
import com.ouser.util.Const;

/**
 * 雷达界面
 * @author Administrator
 * @remark 暂时不考虑onPause和onResume，需要继续上传
 */
public class RadarInfo extends ChatBaseLayout {
	
	public interface OnActionListener {
		void onReady();
		void onError();
		void onDismiss();
	}

	private OnActionListener mActionListener = null;

	private AMap mMap = null;
	private SupportMapFragment mFragment = null;
	
	private float mZoom = Const.MapZoom;
	
	private Handler mHandler = new Handler();
	
	private RadarLogic.LocationChangedListener mListener = new RadarLogic.LocationChangedListener() {
		
		@Override
		public void onUpdate(Location targetLocation) {
			if(!LogicFactory.self().getRadar().isRunning()) {
				return;
			}
			if(mMap == null) {
				return;
			}
			mMap.clear();
			if(targetLocation != null) {
				mMap.addMarker(new MarkerOptions()
					.position(LocationManager.self().convertToModule(targetLocation))
					.icon(BitmapDescriptorFactory.defaultMarker()));
			}
			moveToMyLocation();
		}
	};
	
	
	@Override
	public void onDestroy() {
		LogicFactory.self().getRadar().stopRadar();
		super.onDestroy();
	}

	/** 是否正在运行 */
	public boolean isRunning() {
		return LogicFactory.self().getRadar().isRunning();
	}

	public void start(OnActionListener listener) {
		if(!getChatId().isSingle()) {
			if(listener != null) {
				listener.onError();
			}
			return;
		}

		mActionListener = listener;
		LogicFactory.self().getRadar().startRadar(getChatId().getSingleId(), mListener,  
				getActivity().createUIEventListener(new EventListener() {

			@Override
			public void onEvent(EventId id, EventArgs args) {
				getActivity().stopLoading();
				
				if(!LogicFactory.self().getRadar().isRunning()) {
					if(mActionListener != null) {
						mActionListener.onDismiss();
					}
					return;
				}
				
				StatusEventArgs statusArgs = (StatusEventArgs) args;
				if (statusArgs.getErrCode() == OperErrorCode.Success) {
					initMap();
					Alert.Toast("您的雷达定位已开启！对方开启后可以与Ta实时移动定位");
				} else {
					Alert.Toast("启动雷达失败");
					if(mActionListener != null) {
						mActionListener.onError();
					}
				}
			}
		}));
		getActivity().startLoading();
	}
	
	public void stop() {
		LogicFactory.self().getRadar().stopRadar();
		uninitMap();
	}

	private void initMap() {
		mFragment = SupportMapFragment.newInstance();
		getActivity().getSupportFragmentManager().beginTransaction()
			.replace(R.id.layout_radar, mFragment).commit();
		
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				if(!LogicFactory.self().getRadar().isRunning()) {
					if(mActionListener != null) {
						mActionListener.onDismiss();
					}
					return;
				}
				mMap = mFragment.getMap();
				if(mMap == null) {
					mHandler.post(this);
				} else {
					readyMap();
				}
			}
		};
		mHandler.post(runnable);
	}
	
	private void uninitMap() {
		getActivity().getSupportFragmentManager().beginTransaction().remove(mFragment).commit();
	}
	
	private void readyMap() {
//		mMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
//			
//			@Override
//			public void onCameraChange(CameraPosition arg0) {
//				logger.d("onCameraChange");
//				mZoom = mMap.getCameraPosition().zoom;
//			}
//		});
		mMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
			
			@Override
			public void onMapLoaded() {
				if(LogicFactory.self().getRadar().isRunning()) {
					moveToMyLocation();
				}
				
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						if(mActionListener != null) {
							mActionListener.onReady();
						}		
					}
				});
			}
		});
	}
	
	private void moveToMyLocation() {
		LatLng latlng = LocationManager.self().convertToModule(LocationManager.self().getCurrent());
		mMap.addMarker(new MarkerOptions()
			.position(latlng)
			.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_mark_big)));

		CameraPosition pos = new CameraPosition.Builder()
			.target(latlng).zoom(mZoom).bearing(0).tilt(0).build();
		mMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
	}
}
