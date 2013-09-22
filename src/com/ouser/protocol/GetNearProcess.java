package com.ouser.protocol;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ouser.module.Appoint;
import com.ouser.module.Location;
import com.ouser.module.NearOuser;
import com.ouser.module.Ouser;
import com.ouser.util.UrlUtil;

public class GetNearProcess extends BaseProcess {
	
	private static final String URL = "http://app.zhengre.com/servlet/SpaceSearchServlet_Android_V2_0";
	
	private String mMyUid = "";
	private Location mLocation = null;
	private Util.PageNumber mPage = new Util.PageNumber();
	
	private List<NearOuser> mResult = new ArrayList<NearOuser>();
	
	public void setMyUid(String value) {
		mMyUid = value;
	}
	public void setLocation(Location value) {
		mLocation = value;
	}
	public void setPageNumber(int value) {
		mPage.setValue(value);
	}
	
	public List<NearOuser> getResult() {
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
			o.put("lat", String.valueOf(mLocation.getLat()));
			o.put("lng", String.valueOf(mLocation.getLng()));
			o.put("startage", "-1");
			o.put("stopage", "-1");
			o.put("gender", "");
			o.put("pgnum", mPage.getValue());
			
			// 如果为游客，uid为空
			o.put("email", mMyUid);
			return o.toString();
		} catch(JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onResult(String result) {
		mResult.clear();
		try {
			JSONArray array = new JSONArray(result);
			for(int i = 0; i < array.length(); ++i) {
				JSONObject json = array.optJSONObject(i);
				
				Ouser ouser = new Ouser();
				ouser.setUid(json.optString("email"));
				ouser.getPortrait().setUrl(json.optString("headphoto"));
				ouser.setGender(Util.convertToGender(json.optString("gender")));
				ouser.setAge(json.optInt("age"));
				ouser.setDistance(json.optInt("distance"));
				ouser.setLastOnline(json.optInt("timeinterval"));
				ouser.setNickName(UrlUtil.decode(json.optString("nickname")));
				ouser.setPublishAppointCount(json.optInt("needcount"));
				
				Appoint appoint = new Appoint();
				appoint.setContent(UrlUtil.decode(json.optString("need")));
				appoint.getAppointId().setAid(json.optString("needid"));
				appoint.getAppointId().setUid(ouser.getUid());
				
				NearOuser nearOuser = new NearOuser();
				nearOuser.setOuser(ouser);
				nearOuser.setLastAppoint(appoint);
				
				mResult.add(nearOuser);
			}
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
