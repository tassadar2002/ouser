package com.ouser.ui.map;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.ouser.R;
import com.ouser.event.EventArgs;
import com.ouser.event.EventId;
import com.ouser.event.EventListener;
import com.ouser.location.LocationEventArgs;
import com.ouser.location.LocationManager;
import com.ouser.location.PoiAddressesEventArgs;
import com.ouser.logic.LogicFactory;
import com.ouser.logic.OperErrorCode;
import com.ouser.logic.event.PlacesEventArgs;
import com.ouser.module.Location;
import com.ouser.module.Place;
import com.ouser.ui.base.BaseActivity;
import com.ouser.ui.helper.Formatter;
import com.ouser.util.Const;

@SuppressLint("HandlerLeak")
public class LocationSelectActivity extends BaseActivity {
	
	public static final String IntentWithSearch = "withsearch";

	// control
	private AMap mMap;
	private TextView mTxtAddress = null;

	// current
	private Location mLocation = new Location();
	private String mAddressName = "";
	
	private boolean mShowPlace = true;
	
	private List<Item> mItems = new ArrayList<Item>();
	private AddressAdapter mAdapter = new AddressAdapter();
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			mLocation = LocationManager.self().convertFromModule(((CameraPosition)msg.obj).target);
			if(!mShowPlace) {
				formatAddressName();
			}
			mShowPlace = false;
			setAddressName();
			LocationManager.self().getNameFromLocation(mLocation);
		}
	};

	private EventListener mLocationListener = new EventListener() {

		@Override
		public void onEvent(EventId id, EventArgs args) {
			if (id == EventId.eGetAddress) {
				LocationEventArgs locationArgs = (LocationEventArgs) args;
				getNearPlace(locationArgs.getAddressName());
			} else if(id == EventId.eGetPoiAddress) {
				PoiAddressesEventArgs placesArgs = (PoiAddressesEventArgs)args;
				refillItems(placesArgs.getPlaces(), true);
				mAdapter.notifyDataSetChanged();
			}
		}
	};
	
	private EventListener mEventListener = new EventListener() {
		
		@Override
		public void onEvent(EventId id, EventArgs args) {
			PlacesEventArgs placesArgs = (PlacesEventArgs)args;
			if(placesArgs.getErrCode() == OperErrorCode.Success) {
				refillItems(placesArgs.getPlaces(), false);
				mAdapter.notifyDataSetChanged();
			}
		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.actvy_location_select);
		
		Bundle locationBundle = getIntent().getBundleExtra("location");
		if (locationBundle != null && !locationBundle.isEmpty()) {
			mLocation.fromBundle(locationBundle);
			mAddressName = getIntent().getStringExtra("place");
		} else {
			mLocation = LocationManager.self().getCurrent();
			formatAddressName();
		}
		final boolean withSearch = getIntent().getBooleanExtra(IntentWithSearch, false);
		
		findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		if(withSearch) {
			findViewById(R.id.btn_search).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), LocationSearchActivity.class);
					startActivityForResult(intent, 0);
				}
			});
		} else {
			findViewById(R.id.btn_search).setVisibility(View.GONE);
		}
		
		findViewById(R.id.btn_current).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mLocation = LocationManager.self().getCurrent();
				formatAddressName();
				moveTo();
			}
		});
		
		ListView listview = (ListView)findViewById(R.id.list);
		listview.setAdapter(mAdapter);
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Place item = mItems.get(arg2).place;
				complete(item.getLocation(), item.getPlace());
			}
		});
		
		mTxtAddress = (TextView)findViewById(R.id.txt_pin);
		mTxtAddress.getBackground().setAlpha(150);
		mTxtAddress.setVisibility(View.GONE);
		mTxtAddress.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String address = mTxtAddress.getText().toString();
				complete(mLocation, address);
			}
		});
		
		View imagePin = findViewById(R.id.image_pin);
		imagePin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String address = mTxtAddress.getText().toString();
				complete(mLocation, address);
			}
		});

		mMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		mMap.getUiSettings().setZoomControlsEnabled(true);// 设置系统默认缩放按钮可见
		mMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {

			@Override
			public void onCameraChange(CameraPosition arg0) {
				
				// 延时执行
				mHandler.removeMessages(0);
				mHandler.sendMessageDelayed(mHandler.obtainMessage(0, arg0), 300);
			}
		});
		moveTo();

		addUIEventListener(EventId.eGetAddress, mLocationListener);
		addUIEventListener(EventId.eGetPoiAddress, mLocationListener);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(resultCode == RESULT_OK) {
			mLocation.fromBundle(data.getBundleExtra("location"));
			mAddressName = data.getStringExtra("name");
			moveTo();
			mShowPlace = true;
		}
	}
	
	private void getNearPlace(String name) {
		LogicFactory.self().getAppoint().getAppointRecommendPlace(createUIEventListener(mEventListener));
		LocationManager.self().getNearLocationByName(name, mLocation);
	}
	
	private void refillItems(List<Place> places, boolean isFromLocation) {
		List<Item> removed = new ArrayList<Item>();
		for(Item item : mItems) {
			if(item.isFromLocation == isFromLocation) {
				removed.add(item);
			}
		}
		mItems.removeAll(removed);

		List<Item> added = new ArrayList<Item>();
		for(Place place : places) {
			added.add(new Item(place, isFromLocation));
		}
		if(isFromLocation) {
			mItems.addAll(added);
		} else {
			mItems.addAll(0, added);
		}
	}

	private void complete(Location location, String addressName) {
		Intent intent = new Intent();
		intent.putExtra("location", location.toBundle());
		intent.putExtra("place", addressName);
		setResult(RESULT_OK, intent);
		finish();
	}
	
	private void setAddressName() {
		if(mLocation == null) {
			mTxtAddress.setVisibility(View.GONE);
		} else {
			mTxtAddress.setText(mAddressName);
			mTxtAddress.setVisibility(View.VISIBLE);
		}
	}
	
	private void formatAddressName() {
		mAddressName = String.format("东经:%1$.4f 北纬:%2$.4f", mLocation.getLng(), mLocation.getLat());
	}
	
	private void moveTo() {
		LatLng latlng = LocationManager.self().convertToModule(mLocation);
		CameraPosition pos = new CameraPosition.Builder()
			.target(latlng).zoom(Const.MapZoom).bearing(0).tilt(0).build();
		mMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
	}
	
	/** 界面保存的类型 */
	private static class Item {
		
		private Place place = null;
		private boolean isFromLocation = false;
		public Item(Place place, boolean isFromLocatiom) {
			this.place = place;
			this.isFromLocation = isFromLocatiom;
		}
	}

	private class AddressAdapter extends BaseAdapter {

		private class ViewHolder {
			public ImageView imageLocation = null;
			public TextView txtName = null;
			public TextView txtDistance = null;
		}
		
		@Override
		public int getCount() {
			return mItems.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if(convertView == null) {
				convertView = LayoutInflater.from(
						Const.Application).inflate(R.layout.lvitem_near_location, null);
				holder = new ViewHolder();
				holder.imageLocation = (ImageView)convertView.findViewById(R.id.image_location);
				holder.txtDistance = (TextView)convertView.findViewById(R.id.txt_distance);
				holder.txtName = (TextView)convertView.findViewById(R.id.txt_name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder)convertView.getTag();
			}
			Item item = mItems.get(position);
			holder.txtName.setText(item.place.getPlace());
			holder.txtDistance.setText(Formatter.formatDistance(item.place.getDistance()));
			if(item.isFromLocation) {
				holder.imageLocation.setVisibility(View.INVISIBLE);
			} else {
				holder.imageLocation.setVisibility(View.VISIBLE);
			}
			return convertView;
		}
	}
}
