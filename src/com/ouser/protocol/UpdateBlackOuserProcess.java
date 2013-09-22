package com.ouser.protocol;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdateBlackOuserProcess extends BaseProcess {

	private static final String URL = "http://app.zhengre.com/servlet/UpdateBlackListServlet_Android_V2_0";
	
	private String mMyUid = "";
	private String mTargetUid = "";
	private boolean mReport = false;
	private boolean mAdd = false;
	
	public void setMyUid(String value) {
		mMyUid = value;
	}
	public void setTargetUid(String targetUid) {
		mTargetUid = targetUid;
	}
	public void setReport(boolean value) {
		mReport = value;
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
			if(mAdd) {
				jsonOuser.put("sign", mReport ? "1" : "0");
			} else {
				jsonOuser.put("sign", "2");
			}
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
