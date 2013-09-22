 package com.ouser.protocol;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class RemoveSingleMessagesProcess extends BaseProcess {
	
	private static final String URL = "http://app.zhengre.com/servlet/RemoveChatServlet_Android_V2_0";
	
	private String mUid = "";
	private List<String> mTargetUids = new ArrayList<String>();
	
	public void setMyUid(String value) {
		mUid = value;
	}
	public void setTargetUids(List<String> value) {
		mTargetUids = value;
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
			
			StringBuilder sb = new StringBuilder();
			for(String targetUid : mTargetUids) {
				sb.append(targetUid).append(",");
			}
			String uids = sb.toString();
			if(!uids.equals("")) {
				uids = uids.substring(0, uids.length() - 1);
			}
			json.put("from", uids);
			
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
