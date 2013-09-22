package com.ouser.protocol;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ouser.module.Location;
import com.ouser.module.Ouser;
import com.ouser.module.Ousers;
import com.ouser.util.UrlUtil;

public class GetRelationOusersProcess extends BaseProcess {
	
	public enum Type {
		// 关注
		eFollow,
		
		// 被关注（藕丝）
		eBeFollow,

		// 好友
		eFriend,
		
		eNone,
	}
	
	private static final String URL = "http://app.zhengre.com/servlet/GetPersonListServlet_Android_V2_0";
	
	private String mOwner = "";
	private Type mType = Type.eNone;
	private Location mLocation = null;
	private Util.PageNumber mPage = new Util.PageNumber();
	
	private Ousers mResult = new Ousers();
	
	public void setOwner(String value) {
		mOwner = value;
	}
	public void setType(Type value) {
		mType = value;
	}
	public void setLocation(Location value) {
		mLocation = value;
	}
	public void setPageNum(int value) {
		mPage.setValue(value);
	}
	
	public Ousers getResult() {
		return mResult;
	}

	@Override
	protected String getRequestUrl() {
		return URL;
	}

	@Override
	protected String getInfoParameter() {
		if(mType == Type.eNone) {
			return null;
		}
		try {
			JSONObject o = new JSONObject();
			o.put("email", mOwner);
			o.put("type", convertType());
			o.put("pgnum", mPage.getValue());
			o.put("lat", String.valueOf(mLocation.getLat()));
			o.put("lng", String.valueOf(mLocation.getLng()));
			return o.toString();
		} catch(JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onResult(String result) {
		try {
			Ousers ousers = new Ousers();
			JSONArray array = new JSONArray(result);
			for(int i = 0; i < array.length(); ++i) {
				JSONObject jsonOuser = array.optJSONObject(i);
				
				Ouser ouser = new Ouser();
				ouser.setUid(jsonOuser.optString("email"));
				ouser.setNickName(UrlUtil.decode(jsonOuser.optString("nickname")));			
				ouser.getPortrait().setUrl(jsonOuser.optString("head"));
				ouser.setGender(Util.convertToGender(jsonOuser.optString("gender")));
				ouser.setAge(jsonOuser.optInt("age"));
				ouser.setDistance(jsonOuser.optInt("distance"));
				ouser.setLastOnline(jsonOuser.optInt("interval"));
				ousers.add(ouser);
			}
			mResult = ousers;
		} catch(JSONException e) {
			e.printStackTrace();
			setStatus(ProcessStatus.ErrUnkown);
		}
	}

	@Override
	protected String getFakeResult() {
		return "";
	}

	private String convertType() {
		switch(mType) {
		case eFollow:
			return "1";
		case eBeFollow:
			return "2";
		case eFriend:
			return "3";
		default:
			return "0";
		}
	}
}
