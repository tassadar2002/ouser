package com.ouser.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import com.ouser.module.AppointId;

public class ReportAppointProcess extends BaseProcess {

	private static final String URL = "http://app.zhengre.com/servlet/ReportUserNeedServlet_Android_V2_0";
	
	private String mMyUid = "";
	private AppointId mAppointId = null;
	
	public void setMyUid(String value) {
		this.mMyUid = value;
	}
	public void setAppointId(AppointId value) {
		this.mAppointId = value;
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
			jsonOuser.put("owner", mAppointId.getUid());
			jsonOuser.put("desireid", mAppointId.getAid());
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
