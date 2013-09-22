package com.ouser.protocol;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdateFollowOuserProcess extends BaseProcess {

	private static final String URL = "http://app.zhengre.com/servlet/UserFocuseOtherServlet_Android_V2_0";
	
	private String mMyUid = "";
	private String mTargetUid = "";
	private boolean mAdd = false;
	
	public void setMyUid(String value) {
		mMyUid = value;
	}
	public void setTargetUid(String targetUid) {
		mTargetUid = targetUid;
	}
	public void setAddOrRemove(boolean value) {
		mAdd = value;
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
			jsonOuser.put("aimemail", mTargetUid);
			jsonOuser.put("sign", mAdd ? "1" : "0");
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
