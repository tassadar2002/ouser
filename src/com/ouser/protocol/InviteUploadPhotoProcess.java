package com.ouser.protocol;

import org.json.JSONException;
import org.json.JSONObject;

public class InviteUploadPhotoProcess extends BaseProcess {

	private static final String URL = "http://app.zhengre.com/servlet/InviteUserUploadImageServlet_V1_1";
	
	private String mMyUid = "";
	private String mTargetUid = "";
	
	public void setMyUid(String value) {
		mMyUid = value;
	}
	public void setTargetUid(String value) {
		mTargetUid = value;
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
			int value = o.optInt("status");
			switch(value) {
			case 0:
				setStatus(ProcessStatus.Success);
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
