package com.ouser.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Pair;

import com.ouser.module.Appoint;
import com.ouser.util.Const;
import com.ouser.util.UrlUtil;

public class PublishAppointProcess extends BaseProcess {

	private static final String URL = "http://app.zhengre.com/servlet/DistributeNeedServlet_Android_V2_0";
	
	private Appoint mAppoint = null;
	private boolean mFollow = false;
	private boolean mPublic = false;
	private String mAppointId = "";
	
	public void setAppoint(Appoint value) {
		mAppoint = value;
	}
	public void setFollow(boolean value) {
		mFollow = value;
	}
	public void setPublic(boolean value) {
		mPublic = value;
	}
	
	public String getAppointId() {
		return mAppointId;
	}

	@Override
	protected String getRequestUrl() {
		return URL;
	}

	@Override
	protected String getInfoParameter() {
		try {
			JSONObject json = new JSONObject();
			json.put("email", mAppoint.getAppointId().getUid());
			json.put("desire", UrlUtil.encode(mAppoint.getBody()));
			json.put("gender", Util.convertFromGender(mAppoint.getGenderFilter()));
			Pair<Integer, Integer> ageFilter = mAppoint.getAgeFilter();
			if(ageFilter.first == Const.DefaultValue.Age || 
					ageFilter.second == Const.DefaultValue.Age) {
				json.put("minage", "-1");
				json.put("maxage", "-1");
			} else {
				json.put("minage", String.valueOf(ageFilter.first));
				json.put("maxage", String.valueOf(ageFilter.second));
			}
			if(mAppoint.getLocation().isEmpty()) {
				json.put("lat", "");
				json.put("lng", "");
			} else {
				json.put("lat", String.valueOf(mAppoint.getLocation().getLat()));
				json.put("lng", String.valueOf(mAppoint.getLocation().getLng()));
			}
			json.put("place", UrlUtil.encode(mAppoint.getPlace()));
			json.put("cost", String.valueOf(mAppoint.getCostFilter()));
			json.put("period", String.valueOf(mAppoint.getPeriodFilter()));
			if(mAppoint.getTime() == Const.DefaultValue.Time) {
				json.put("time", "");
			} else {
				json.put("time", String.valueOf(mAppoint.getTime()));
			}
			json.put("sign", mFollow ? "1" : "0");
			json.put("personal", mPublic ? "0" : "1");
			
			json.put("number", "-1");

			return json.toString();
		} catch(JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onResult(String result) {
		try {
			JSONObject o = new JSONObject(result);
			int value = o.optInt("status");
			switch(value) {
			case 0:
				setStatus(ProcessStatus.Success);
				mAppointId = o.optString("desireid");
				break;
			case -10:
				setStatus(ProcessStatus.ErrUnkown);
				break;
			default:
				setStatus(ProcessStatus.ErrUnkown);
				break;
			}
		} catch(Exception e) {
			e.printStackTrace();
			setStatus(ProcessStatus.ErrUnkown);
		}
	}
	
	@Override
	protected String getFakeResult() {
		return "{status:0}";
	}
}
