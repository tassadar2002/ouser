package com.ouser.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import com.ouser.util.UrlUtil;

public class EditAboutmeProcess extends BaseProcess {
	
	private static final String URL = "http://app.zhengre.com/servlet/UpdateUserAboutMeServlet_Android_V2_0";
	
	private String mMyUid = "";
	private String mAboutMe = "";
	
	public void setMyUid(String value) {
		mMyUid = value;
	}
	public void setAboutMe(String value) {
		mAboutMe = value;
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
			jsonOuser.put("aboutme", UrlUtil.encode(mAboutMe));
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
