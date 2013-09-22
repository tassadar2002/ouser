package com.ouser.protocol;

import org.json.JSONException;
import org.json.JSONObject;

public class SetPortraitUrlProcess extends BaseProcess {

	private static final String URL = "http://app.zhengre.com/servlet/UpdateHeadImageServlet_Android_2_V2_0";
	
	private String mUid = "";
	private String mPortraitUrl = "";

	public void setMyUid(String value) {
		this.mUid = value;
	}
	public void setPortraitUrl(String value) {
		this.mPortraitUrl = value;
	}
	
	// 目前未使用
	public String getResult() {
		return mPortraitUrl;
	}

	@Override
	protected String getRequestUrl() {
		return URL;
	}

	@Override
	protected String getInfoParameter() {
		try {
			JSONObject jsonOuser = new JSONObject();
			jsonOuser.put("email", mUid);
			jsonOuser.put("head", mPortraitUrl);
			return jsonOuser.toString();
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
				mPortraitUrl = o.optString("head");
				break;
			case -10:
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
