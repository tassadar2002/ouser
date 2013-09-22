package com.ouser.protocol;

import org.json.JSONException;
import org.json.JSONObject;

public class ModifyPasswordProcess extends BaseProcess {

	private static final String URL = "http://app.zhengre.com/servlet/ChangeUserPasswordServlet_Android_V2_0";
	
	private String mMyUid = "";
	private String mOldPassword = "";
	private String mNewPassword = "";
	
	public void setMyUid(String value) {
		mMyUid = value;
	}
	public void setOldPassword(String value) {
		mOldPassword = value;
	}
	public void setNewPassword(String value) {
		mNewPassword = value;
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
			jsonOuser.put("pass", mNewPassword);
			jsonOuser.put("oldpass", mOldPassword);
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
			case -5:
				setStatus(ProcessStatus.ErrPass);
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
