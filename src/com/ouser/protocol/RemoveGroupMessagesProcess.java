package com.ouser.protocol;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ouser.module.AppointId;

public class RemoveGroupMessagesProcess extends BaseProcess {

	private static final String URL = "http://app.zhengre.com/servlet/RemoveChatFromGroupServlet_Android_V2_0";
	
	private String mUid = "";
	private List<AppointId> mTargetAppointIds = new ArrayList<AppointId>();
	
	public void setMyUid(String value) {
		mUid = value;
	}
	public void setTargetAppointIds(List<AppointId> value) {
		mTargetAppointIds = value;
	}
	
	@Override
	protected String getRequestUrl() {
		return URL;
	}

	@Override
	protected String getInfoParameter() {
		try {
			JSONObject json = new JSONObject();
			json.put("email", mUid);
			
			JSONArray array = new JSONArray();
			for(AppointId appointId : mTargetAppointIds) {
				JSONObject o = new JSONObject();
				o.put("owner", appointId.getUid());
				o.put("desireid", appointId.getAid());
				array.put(o);
			}
			json.put("from", array);
			
			return json.toString();
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
