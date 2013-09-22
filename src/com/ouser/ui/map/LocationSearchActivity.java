package com.ouser.ui.map;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ouser.R;
import com.ouser.event.EventId;
import com.ouser.location.LocationManager;
import com.ouser.location.PoiAddressesEventArgs;
import com.ouser.location.ResultCache;
import com.ouser.logic.event.StatusEventArgs;
import com.ouser.module.Place;
import com.ouser.ui.base.BaseActivity;
import com.ouser.ui.base.BaseListFragment;
import com.ouser.ui.component.HeadBar;
import com.ouser.ui.helper.HideKeyboard;
import com.ouser.util.AddressCode;
import com.ouser.util.Const;

public class LocationSearchActivity extends BaseActivity {
	
	private List<Place> mItems = new ArrayList<Place>();
	private ResultCache mCache = null;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.actvy_location_search);
		
		HeadBar headBar = new HeadBar();
		headBar.onCreate(findViewById(R.id.layout_head_bar), this);
		headBar.setTitle("位置搜索");
		
		findViewById(R.id.btn_search).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				fillListView();
			}
		});
		
		HideKeyboard.setupUI(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void fillListView() {
		EditText edit = (EditText)findViewById(R.id.edit_keyword);
		
		SearchFragment fragment = 
			(SearchFragment)getSupportFragmentManager().findFragmentById(R.id.layout_list);
		if(fragment == null) {
			fragment = new SearchFragment();
			getSupportFragmentManager().beginTransaction().replace(R.id.layout_list, fragment).commit();
		}
		fragment.setKeyword(edit.getText().toString());
		fragment.asyncInitData();
	}
	
	@SuppressLint("ValidFragment")
	private class SearchFragment extends BaseListFragment {
		
		private String mKeyword = "";

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			enableRefresh(false);
			// TODO 这里实际是两层的ui listener
			addUIEventListener(EventId.eGetPoiAddress, getMainEventListener());
		}

		public void setKeyword(String value) {
			mKeyword = value;
		}

		@Override
		protected void refreshData(boolean append) {
			if(append) {
				LocationManager.self().getLocationByName(mCache, getCurrentPageIndex() + 1);
			} else {
				LocationManager.self().getLocationByName(mKeyword);
			}
		}

		@Override
		protected int getItemCount() {
			return mItems.size();
		}

		@Override
		protected boolean handleMainEvent(StatusEventArgs statusArgs, boolean append) {
			PoiAddressesEventArgs poiArgs = (PoiAddressesEventArgs)statusArgs;
			mCache = poiArgs.getCache();
			if(append) {
				mItems.addAll(poiArgs.getPlaces());
			} else {
				mItems = poiArgs.getPlaces();
			}
			return poiArgs.getPlaces().isEmpty();
		}

		@Override
		protected void onClickItem(int index) {
			Place address = mItems.get(index);
			Intent intent = new Intent();
			intent.putExtra("name", address.getPlace());
			intent.putExtra("location", address.getLocation().toBundle());
			setResult(RESULT_OK, intent);
			finish();
		}

		@Override
		protected View getItemView(int position, View convertView) {
			ViewHolder holder = null;
			if(convertView == null) {
				convertView = LayoutInflater.from(
						Const.Application).inflate(R.layout.lvitem_search_address, null);
				holder = new ViewHolder();
				holder.txtName = (TextView)convertView.findViewById(R.id.txt_name);
				holder.txtAddress = (TextView)convertView.findViewById(R.id.txt_address);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder)convertView.getTag();
			}
			Place item = mItems.get(position);
			holder.txtName.setText(item.getPlace());
			holder.txtAddress.setText(AddressCode.getDesc(item.getCode()));
			
			HideKeyboard.setupUI(getActivity(), convertView);
			return convertView;
		}
		
		private class ViewHolder {
			public TextView txtName = null;
			public TextView txtAddress = null;
		}
	}
}
