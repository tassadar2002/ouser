package com.ouser.protocol;

import org.json.JSONException;
import org.json.JSONObject;

public class RemovePhotoProcess extends BaseProcess {
	
	private static final String URL = "http://app.zhengre.com/servlet/RemoveImageServlet_Android_V2_0";
	
	private String mUid = "";
	private String mUrl = "";
	
	public void setMyUid(String value) {
		this.mUid = value;
	}
	public void setUrl(String value) {
		this.mUrl = value;
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
			jsonOuser.put("imgurl", mUrl);
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
			case -4:
				setStatus(ProcessStatus.ErrPhotoIsPortrait);
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
