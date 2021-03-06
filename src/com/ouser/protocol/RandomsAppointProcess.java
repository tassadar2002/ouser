package com.ouser.protocol;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ouser.module.Appoint;
import com.ouser.module.Appoints;
import com.ouser.module.Location;
import com.ouser.util.StringUtil;
import com.ouser.util.UrlUtil;

public class RandomsAppointProcess extends BaseProcess {

	private static final String URL = "http://app.zhengre.com/servlet/ShakeServlet_Android_V2_0";

	private String mMyUid = "";
	private Appoints mResult = null;

	public void setMyUid(String value) {
		mMyUid = value;
	}

	public Appoints getResult() {
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
			JSONArray array = new JSONArray(result);
			
			Appoints appoints = new Appoints();
			for(int i = 0; i < array.length(); ++i) {
				JSONObject json = array.getJSONObject(i);

				Appoint appoint = new Appoint();
				// 忽略timeinterval字段
				appoint.getAppointId().setAid(json.optString("desireid"));
				appoint.getAppointId().setUid(json.optString("owner"));
				appoint.setContent(UrlUtil.decode(json.optString("headstr")),
						UrlUtil.decode(json.optString("bodystr")),
						UrlUtil.decode(json.optString("tailstr")));
				appoint.setRelation(Util.convertToRelation(json.optString("relation")));
				appoint.setStatus(Util.convertToStatus(json.optString("status")));
				appoint.setJoinCount(json.optInt("joiner"));
				appoint.setViewCount(json.optInt("viewer"));
				appoint.setPlace(UrlUtil.decode(json.optString("place")));
				
				double lat = json.optDouble("lat");
				double lng = json.optDouble("lng");
				if(lat != 0 || lng != 0) {
					appoint.setLocation(new Location(lat, lng));
				}
				String time = json.optString("time");
				if(!StringUtil.isEmpty(time)) {
					appoint.setTime(Integer.parseInt(time));
				}

				appoints.add(appoint);
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
