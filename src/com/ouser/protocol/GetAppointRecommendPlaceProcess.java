package com.ouser.protocol;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ouser.module.Location;
import com.ouser.module.Place;
import com.ouser.util.UrlUtil;

public class GetAppointRecommendPlaceProcess extends BaseProcess {

	private static final String URL = "http://app.zhengre.com/servlet/SuggestMeetPlaceServlet_Android_V3_2";
	
	private Location mLocation = null;
	private int mSerial = 0;
	private List<Place> mResult = new ArrayList<Place>();
	
	public void setLocation(Location value) {
		mLocation = value;
	}
	public void setSerial(int value) {
		mSerial = value;
	}
	
	public List<Place> getResult() {
		return mResult;
	}

	@Override
	protected String getRequestUrl() {
		return URL;
	}

	@Override
	protected String getInfoParameter() {
		try {
			JSONObject o = new JSONObject();
			o.put("lng", String.valueOf(mLocation.getLng()));
			o.put("lat", String.valueOf(mLocation.getLat()));
			o.put("serial", String.valueOf(mSerial));
			return o.toString();
		} catch(JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onResult(String result) {
		try {
			List<Place> places = new ArrayList<Place>();
			JSONArray array = new JSONArray(result);
			for(int i = 0; i < array.length(); ++i) {
				JSONObject json = array.optJSONObject(i);
				
				Place place = new Place();
				place.setDistance(json.optInt("distance"));
				place.setPlace(UrlUtil.decode(json.optString("place")));
				place.getLocation().setValue(json.optDouble("lat"), json.optDouble("lng"));
				places.add(place);
			}
			mResult = places;
		} catch(JSONException e) {
			e.printStackTrace();
			setStatus(ProcessStatus.ErrUnkown);
		}
	}

	@Override
	protected String getFakeResult() {
		return "";
	}
}
