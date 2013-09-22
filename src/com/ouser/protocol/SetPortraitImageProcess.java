package com.ouser.protocol;

import org.json.JSONException;
import org.json.JSONObject;

public class SetPortraitImageProcess extends BaseProcess {

	private static final String URL = "http://app.zhengre.com/servlet/UpdateHeadImageServlet_Android_V2_0";
	
	private String mUid = "";
	private String mSuffix = "";
	private String mData = "";
	private String mUrl = "";
	
	public void setMyUid(String value) {
		this.mUid = value;
	}
	public void setSuffix(String value) {
		this.mSuffix = value;
	}
	public void setData(String value) {
		this.mData = value;
	}
	
	// 目前未使用
	public String getResult() {
		return mUrl;
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
			jsonOuser.put("suffix", mSuffix);
			jsonOuser.put("image", mData);
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
				mUrl = o.optString("imgurl");
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
