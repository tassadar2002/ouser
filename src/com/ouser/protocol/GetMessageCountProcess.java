package com.ouser.protocol;

import org.json.JSONException;
import org.json.JSONObject;

public class GetMessageCountProcess extends BaseProcess {

	private static final String URL = "http://app.zhengre.com/servlet/GetUserUnreadCountServlet_V2_0";
	
	private String mMyUid = "";
	private int mUnReadedMessageCount = 0;
	private int mTimelineCount = 0;
	
	public void setMyUid(String value) {
		mMyUid = value;
	}
	public int getUnReadedMessageCount() {
		return mUnReadedMessageCount;
	}
	public int getTimelineCount() {
		return mTimelineCount;
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
		} catch(JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onResult(String result) {
		try {
			JSONObject o = new JSONObject(result);
			mUnReadedMessageCount = o.optInt("totalchat");
			mTimelineCount = o.optInt("totalmsg");
		} catch(Exception e) {
			e.printStackTrace();
			setStatus(ProcessStatus.ErrUnkown);
		}
	}
	
	@Override
	protected String getFakeResult() {
		try {
			JSONObject jsonStatus = new JSONObject();
			jsonStatus.put("totalchat", "10");
			jsonStatus.put("totalmsg", "5");
			return jsonStatus.toString();
			
		} catch(JSONException e) {
			e.printStackTrace();
		}
		return "";
	}
}
