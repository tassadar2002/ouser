package com.ouser.location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.model.LatLng;
import com.amap.api.search.core.AMapException;
import com.amap.api.search.core.LatLonPoint;
import com.amap.api.search.geocoder.Geocoder;
import com.amap.api.search.poisearch.PoiItem;
import com.amap.api.search.poisearch.PoiPagedResult;
import com.amap.api.search.poisearch.PoiSearch;
import com.amap.api.search.poisearch.PoiTypeDef;
import com.ouser.event.EventCenter;
import com.ouser.event.EventId;
import com.ouser.logger.Logger;
import com.ouser.logic.OperErrorCode;
import com.ouser.module.Place;
import com.ouser.util.Const;

/**
 * 获取定位信息。
 */
public class LocationManager implements AMapLocationListener {

	private static LocationManager ins = new LocationManager();

	public static LocationManager self() {
		return ins;
	}

	private static final Logger logger = new Logger("location");

	private LocationManagerProxy locationManager = null;
	private Geocoder mCoder = null;

	private boolean mRadaring = false;
	private com.ouser.module.Location mMyLocation = new com.ouser.module.Location();

	private Handler mHandler = new Handler();

	private LocationManager() {
		mCoder = new Geocoder(Const.Application);
	}

	public boolean isEnable() {
		return getManager().isProviderEnabled(LocationProviderProxy.AMapNetwork);
	}

	public void fetchCurrent() {

		// new Handler().post(new Runnable() {
		//
		// @Override
		// public void run() {
		// mMyLocation = new com.ouser.module.Location();
		// mMyLocation.setLat(39);
		// mMyLocation.setLng(117);
		// EventCenter.self().fireEvent(EventId.eLocationChanged,
		// new LocationEventArgs(mMyLocation));
		// }
		// });

		enableMyLocation();
	}

	public boolean startRadar() {
		if (enableMyLocation()) {
			mRadaring = true;
		}
		return mRadaring;
	}

	public void stopRadar() {
		disableMyLocation();
		mRadaring = false;
	}

	/** 防止在外部修改，使用了副本 */
	public com.ouser.module.Location getCurrent() {
		return new com.ouser.module.Location(mMyLocation);
	}

	public void clear() {
		mMyLocation = new com.ouser.module.Location();
	}

	public void getNameFromLocation(final com.ouser.module.Location location) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					List<Address> addresses = mCoder.getFromLocation(location.getLat(),
							location.getLng(), 1);
					if (addresses != null && addresses.size() > 0) {
						Address address = addresses.get(0);
						final String addressName = address.getFeatureName();
						mHandler.post(new Runnable() {

							@Override
							public void run() {
								EventCenter.self().fireEvent(EventId.eGetAddress,
										new LocationEventArgs(location, addressName));
							}
						});
					}
				} catch (AMapException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void getNearLocationByName(final String keyword, final com.ouser.module.Location location) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					PoiSearch poiSearch = new PoiSearch(Const.Application, new PoiSearch.Query(
							keyword, PoiTypeDef.All));
					if (location != null) {
						poiSearch.setBound(new PoiSearch.SearchBound(convertToPoint(location),
								Const.PoiSearchRaduis));
					}
					poiSearch.setPageSize(Const.PoiResultCount);
					handlePoiResult(poiSearch.searchPOI(), 1);
				} catch (AMapException e) {
					e.printStackTrace();
					handleErrorPoiEvent();
				}
			}
		}).start();
	}

	public void getLocationByName(final String keyword) {
		getNearLocationByName(keyword, null);
	}

	public void getLocationByName(final ResultCache cache, final int page) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				handlePoiResult(cache.getResult(), page + 1);
			}
		}).start();
	}

	public void onDestroy() {
		disableMyLocation();

		if (locationManager != null) {
			locationManager.removeUpdates(this);
			locationManager.destory();
		}
		locationManager = null;
	}

	private boolean enableMyLocation() {

		if (getManager().isProviderEnabled(LocationProviderProxy.AMapNetwork)) {
			getManager().requestLocationUpdates(LocationProviderProxy.AMapNetwork, 2000, 10, this);
			return true;
		}
		return false;

		// Criteria cri = new Criteria();
		// cri.setAccuracy(Criteria.ACCURACY_COARSE);
		// cri.setAltitudeRequired(false);
		// cri.setBearingRequired(false);
		// cri.setCostAllowed(false);
		// logger.d("enableMyLocation. getting best provider");
		// String bestProvider = getManager().getBestProvider(cri, true);
		// logger.d("enableMyLocation. best provider is " + bestProvider);
		// getManager().requestLocationUpdates(bestProvider, (long)2000,
		// (float)10, this);
		// logger.d("enableMyLocation. requestLocationUpdates");
		// return true;
	}

	private void disableMyLocation() {
		getManager().removeUpdates(this);
	}

	private LocationManagerProxy getManager() {
		if (locationManager == null) {
			logger.d("creating location manager proxy");
			locationManager = LocationManagerProxy.getInstance(Const.Application);
			logger.d("created location manager proxy success");
		}
		return locationManager;
	}

	private void handlePoiResult(final PoiPagedResult result, int page) {
		try {
			final ArrayList<Place> addresses = new ArrayList<Place>();
			if (result != null && page <= result.getPageCount()) {
				List<PoiItem> poiItems = result.getPage(page);
				if (poiItems != null && poiItems.size() > 0) {
					for (PoiItem item : poiItems) {
						Place address = new Place();
						address.setPlace(item.getTitle());
						address.setCode(item.getAdCode());
						address.setDistance(item.getDistance());
						address.setLocation(convertFromPoint(item.getPoint()));
						addresses.add(address);
						// 可能会有-1的情况
						if (address.getDistance() < 0) {
							address.setDistance(0);
						}
					}
					Collections.sort(addresses, new Place.ComparatorByCode());
					Collections.sort(addresses, new Place.ComparatorByDistance());
				}
			}
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					EventCenter.self().fireEvent(EventId.eGetPoiAddress,
							new PoiAddressesEventArgs(addresses, result));
				}
			});
		} catch (AMapException e) {
			e.printStackTrace();
			handleErrorPoiEvent();
		}
	}

	private void handleErrorPoiEvent() {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				EventCenter.self().fireEvent(EventId.eGetPoiAddress,
						new PoiAddressesEventArgs(OperErrorCode.Unknown));
			}
		});
	}

	@Override
	public void onLocationChanged(AMapLocation arg0) {

		if (arg0 != null) {

			double geoLat = arg0.getLatitude();
			double geoLng = arg0.getLongitude();

			mMyLocation.setValue(geoLat, geoLng);

			logger.d("location changed " + mMyLocation.toString());

			if (!mRadaring) {
				// 仅一次消息
				disableMyLocation();
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						EventCenter.self().fireEvent(EventId.eLocationChanged,
								new LocationEventArgs(mMyLocation));
					}
				});
				// Toast.makeText(Const.Application, "定位成功:(" + geoLng + "," +
				// geoLat + ")",
				// Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	public LatLonPoint convertToPoint(com.ouser.module.Location location) {
		return new LatLonPoint(location.getLat(), location.getLng());
	}

	public com.ouser.module.Location convertFromPoint(LatLonPoint point) {
		return new com.ouser.module.Location(point.getLatitude(), point.getLongitude());
	}

	public LatLng convertToModule(com.ouser.module.Location location) {
		return new LatLng(location.getLat(), location.getLng());
	}

	public com.ouser.module.Location convertFromModule(LatLng module) {
		return new com.ouser.module.Location(module.latitude, module.longitude);
	}
}
