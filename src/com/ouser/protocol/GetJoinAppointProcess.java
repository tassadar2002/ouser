package com.ouser.protocol;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ouser.module.Appoint;
import com.ouser.module.AppointsWithPublisher;
import com.ouser.module.Location;
import com.ouser.module.Ouser;
import com.ouser.util.StringUtil;
import com.ouser.util.UrlUtil;

public class GetJoinAppointProcess extends BaseProcess {

	private static final String URL = "http://app.zhengre.com/servlet/GetUserJoinNeedListServlet_Android_V2_0";

	private String mMyUid = null;
	private AppointsWithPublisher mResult = new AppointsWithPublisher();

	public void setMyUid(String value) {
		mMyUid = value;
	}
	
	public AppointsWithPublisher getResult() {
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
			o.put("email", mMyUid);
			return o.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onResult(String result) {
		try {
			AppointsWithPublisher appoints = new AppointsWithPublisher();
			JSONArray array = new JSONArray(result);
			for (int i = 0; i < array.length(); ++i) {
				JSONObject json = array.optJSONObject(i);
				Appoint appoint = new Appoint();
				appoint.getAppointId().setAid(json.optString("desireid"));
				appoint.getAppointId().setUid(json.optString("owner"));
				appoint.setContent(UrlUtil.decode(json.optString("headstr")),
						UrlUtil.decode(json.optString("bodystr")), 
						UrlUtil.decode(json.optString("tailstr")));
				appoint.setStatus(Util.convertToStatus(json.optString("status")));
				appoint.setJoinCount(json.optInt("joiner"));
				appoint.setViewCount(json.optInt("viewer"));
				appoint.setPlace(UrlUtil.decode(json.optString("place")));
				
				String distance = json.optString("distance");
				if(!StringUtil.isEmpty(distance)) {
					double d = Double.parseDouble(distance);
					appoint.setDistance((int)d);
				}
				
				double lat = json.optDouble("lat");
				double lng = json.optDouble("lng");
				if(lat != 0 || lng != 0) {
					appoint.setLocation(new Location(lat, lng));
				}
				appoint.setRelation(Util.convertToRelation(json.optString("relation")));
				
				Ouser ouser = new Ouser();
				ouser.setUid(json.optString("owner"));
				ouser.setNickName(UrlUtil.decode(json.optString("nick")));
				ouser.getPortrait().setUrl(json.optString("head"));
				ouser.setAge(json.optInt("age"));
				ouser.setGender(Util.convertToGender(json.optString("gender")));
				
				appoints.add(appoint, ouser);
			}
			mResult = appoints;

		} catch (JSONException e) {
			e.printStackTrace();
			setStatus(ProcessStatus.ErrUnkown);
		}
	}

	@Override
	protected String getFakeResult() {
		return "";
	}

}
