package com.ouser.protocol;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ouser.module.Appoint;
import com.ouser.module.Appoints;
import com.ouser.module.Location;
import com.ouser.util.StringUtil;
import com.ouser.util.UrlUtil;

public class GetPublishAppointProcess extends BaseProcess {
	
	private static final String URL = "http://app.zhengre.com/servlet/GetUserNeedListServlet_Android_V2_0";
	
	private String mMyUid = "";
	private String mPublisherUid = "";
	
	/** 是否只获取有效友约，过期和删除的不能获取到 */
	private boolean mValid = false;
	
	private Appoints mResult = new Appoints();

	public void setMyUid(String value) {
		mMyUid = value;
	}
	public void setPublisherUid(String value) {
		mPublisherUid = value;
	}
	public void setOnlyValid(boolean value) {
		mValid = value;
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
			o.put("owner", mPublisherUid);
			o.put("status", mValid ? "1" : "0");
			return o.toString();
		} catch(JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onResult(String result) {
		try {
			Appoints appoints = new Appoints();
			JSONArray array = new JSONArray(result);
			for(int i = 0; i < array.length(); ++i) {
				JSONObject json = array.optJSONObject(i);
				Appoint appoint = new Appoint();
				// 忽略owner字段
				appoint.getAppointId().setAid(json.optString("desireid"));
				appoint.getAppointId().setUid(mPublisherUid);
				appoint.setContent(UrlUtil.decode(json.optString("headstr")), 
						UrlUtil.decode(json.optString("bodystr")), 
						UrlUtil.decode(json.optString("tailstr")));
				appoint.setStatus(Util.convertToStatus(json.optString("status")));
				appoint.setJoinCount(json.optInt(("joiner")));
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
				appoint.setRelation(Util.convertToRelation(json.optString("relation")));

				appoints.add(appoint);
			}
			mResult = appoints;
			
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
