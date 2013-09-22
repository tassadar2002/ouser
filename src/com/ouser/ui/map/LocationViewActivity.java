package com.ouser.ui.map;

import android.os.Bundle;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.ouser.R;
import com.ouser.location.LocationManager;
import com.ouser.module.Location;
import com.ouser.ui.base.BaseActivity;
import com.ouser.ui.component.HeadBar;
import com.ouser.util.Const;

public class LocationViewActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.actvy_location_view);

		HeadBar headBar = new HeadBar();
		headBar.onCreate(findViewById(R.id.layout_head_bar), this);
		headBar.setTitle("位置查看");

		Location location = null;
		Bundle locationBundle = getIntent().getBundleExtra("location");
		if (locationBundle != null) {
			location = new Location();
			location.fromBundle(locationBundle);
		} else {
			location = LocationManager.self().getCurrent();
		}
		
		AMap aMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		aMap.getUiSettings().setZoomControlsEnabled(true);// 设置系统默认缩放按钮可见
		
		LatLng latlng = LocationManager.self().convertToModule(location);
		CameraPosition pos = new CameraPosition.Builder()
			.target(latlng).zoom(Const.MapZoom).bearing(0).tilt(0).build();
		
		aMap.addMarker(new MarkerOptions()
			.position(latlng)
			.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_mark_big)));
		aMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
		
//		aMap.setOnMarkerClickListener(this);// 对marker添加点击监听器
//		aMap.setOnCameraChangeListener(new OnCameraChangeListener() { // 设置可视区域改变监听器
//
//			@Override
//			public void onCameraChange(CameraPosition cameraPosion) {
//				VisibleRegion visibleRegion = aMap.getProjection()
//						.getVisibleRegion(); // 获取可视区域
//				LatLngBounds latLngBounds = aMap.getProjection()
//						.getVisibleRegion().latLngBounds;// 获取可视区域的Bounds
//				boolean isContain = latLngBounds.contains(Constants.SHANGHAI);// 判断上海经纬度是否包括在北京区域
//				Log.d("BasicMapActivity",
//						"visible region: " + visibleRegion.toString()
//								+ "LatLngBounds：" + latLngBounds.toString()
//								+ "上海经纬度是否在北京范围内" + isContain);
//			}
//
//		});

//		MapView mapView = (MapView) findViewById(R.id.mapView);
//		mapView.setBuiltInZoomControls(true);
//		MapController mapController = mapView.getController();
		
//		GeoPoint point = LocationManager.self().convertToPoint(location);
//		mapController.setCenter(point);
//		mapController.setZoom(Const.MapZoom);
//		
//		MapView.LayoutParams geoLP;  
//		geoLP = new MapView.LayoutParams(MapView.LayoutParams.WRAP_CONTENT,  
//		                                 MapView.LayoutParams.WRAP_CONTENT,  
//		                                 point,  
//		                                 MapView.LayoutParams.TOP_LEFT);  
//		 
//		ImageView image = new ImageView(getApplicationContext());
//		image.setImageDrawable(getResources().getDrawable(R.drawable.location));
//
//		mapView.addView(image, geoLP); 
    }
}
