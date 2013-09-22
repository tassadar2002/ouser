package com.ouser.protocol;

import org.json.JSONException;
import org.json.JSONObject;

public class GetRadaServerProcess extends BaseProcess {

	private static final String URL = "http://app.zhengre.com/servlet/GetRadaRoomServlet_V3_1";
	
	private String mMyUid = "";
	private String mTargetUid = "";
	private String mIp = "";
	private int mPort = 0;
	
	public void setMyUid(String value) {
		mMyUid = value;
	}
	public void setTargetUid(String value) {
		mTargetUid = value;
	}
	
	public String getIp() {
		return mIp;
	}
	public int getPort() {
		return mPort;
	}
	
	@Override
	protected String getRequestUrl() {
		return URL;
	}

	@Override
	protected String getInfoParameter() {
		try {
			JSONObject jsonOuser = new JSONObject();
			jsonOuser.put("email", mMyUid);
			jsonOuser.put("dest", mTargetUid);
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
			mIp = o.optString("ip");
			mPort = o.optInt("port");
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
