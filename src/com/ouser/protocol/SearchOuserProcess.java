package com.ouser.protocol;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ouser.module.Location;
import com.ouser.module.Ouser;
import com.ouser.module.Ousers;
import com.ouser.util.UrlUtil;

public class SearchOuserProcess extends BaseProcess {

	private static final String URL = "http://app.zhengre.com/servlet/NickSearchServlet_Android_V2_0";
	
	private String mMyUid = "";
	private String mKeyowrd = "";
	private Location mLocation = null;
	private Util.PageNumber mPage = new Util.PageNumber();
	private Ousers mResult = new Ousers();

	public void setMyUid(String value) {
		mMyUid = value;
	}
	public void setLocation(Location value) {
		mLocation = value;
	}
	public void setKeyword(String value) {
		mKeyowrd = value;
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
		try {
			JSONObject o = new JSONObject();
			o.put("email", mMyUid);
			o.put("nick", UrlUtil.encode(mKeyowrd));
			o.put("lat", String.valueOf(mLocation.getLat()));
			o.put("lng", String.valueOf(mLocation.getLng()));
			o.put("pgnum", mPage.getValue());
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
				ouser.getPortrait().setUrl(jsonOuser.optString("head"));
				ouser.setGender(Util.convertToGender(jsonOuser.optString("gender")));
				ouser.setAge(jsonOuser.optInt("age"));
				ouser.setDistance(jsonOuser.optInt("distance"));
				ouser.setLastOnline(jsonOuser.optInt("interval"));
				ouser.setNickName(UrlUtil.decode(jsonOuser.optString("nick")));				
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
}
