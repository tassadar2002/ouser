package com.ouser.protocol;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.ouser.module.Appoint;
import com.ouser.module.Ouser;
import com.ouser.util.UrlUtil;

public class AppointInviteProcess extends BaseProcess {

	private static final String URL = "http://app.zhengre.com/servlet/SendInvestToOtherServlet_Android_V2_0";
	
	private Appoint mAppoint = null;
	private List<Ouser> mTarget = new ArrayList<Ouser>();
	private String mContent = "";
	private String mDate = "";
	
	public void setAppoint(Appoint value) {
		mAppoint = value;
	}
	public void setContent(String value) {
		mContent = value;
	}
	public void setTarget(List<Ouser> value) {
		mTarget = value;
	}
	public void setDate(String value) {
		mDate = value;
	}
	
	@Override
	protected String getRequestUrl() {
		return URL;
	}

	@Override
	protected String getInfoParameter() {
		try {
			JSONObject json = new JSONObject();
			json.put("email", mAppoint.getAppointId().getUid());
			json.put("dest", genTargetString());
			json.put("date", mDate);
			JSONObject jsonAppointId = new JSONObject();
			jsonAppointId.put("desireid", mAppoint.getAppointId().getAid());
			jsonAppointId.put("owner", mAppoint.getAppointId().getUid());
			jsonAppointId.put("textContent", mContent);
			JSONObject jsonChat = new JSONObject();
			jsonChat.put("content", jsonAppointId.toString());
			jsonChat.put("msgtype", "5");
			json.put("chat", UrlUtil.encode(jsonChat.toString()));
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
	
	private String genTargetString() {
		StringBuilder sb = new StringBuilder();
		for(Ouser ouser : mTarget) {
			sb.append(ouser.getUid() + ",");
		}
		if(sb.length() != 0) {
			return sb.toString().substring(0, sb.length() - 1);
		} else {
			return "";
		}
	}
}
