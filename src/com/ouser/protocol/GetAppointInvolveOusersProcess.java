package com.ouser.protocol;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ouser.module.AppointId;
import com.ouser.module.Ouser;
import com.ouser.module.Ousers;

public class GetAppointInvolveOusersProcess extends BaseProcess {
	
	private static final String URL = "http://app.zhengre.com/servlet/GetNeedVisitorListServlet_V2_0";

	private String mMyUid = "";
	private AppointId mAppointId = null;
	private Ousers mJoinList = new Ousers();
	private Ousers mViewList = new Ousers();
	
	public void setMyUid(String value) {
		mMyUid = value;
	}
	public void setAppointId(AppointId value) {
		mAppointId = value;
	}
	
	public Ousers getJoinList() {
		return mJoinList;
	}
	public Ousers getViewList() {
		return mViewList;
	}

	@Override
	protected String getRequestUrl() {
		return URL;
	}

	@Override
	protected String getInfoParameter() {
		try {
			JSONObject o = new JSONObject();
			o.put("srcemail", mAppointId.getUid());
			o.put("desireid", mAppointId.getAid());
			o.put("visitemail", mMyUid);
			return o.toString();
		} catch(JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onResult(String result) {
		try {
			JSONObject o = new JSONObject(result);
			parseOuserList(o.optJSONArray("joiner"), mJoinList);
			parseOuserList(o.optJSONArray("viewer"), mViewList);
		} catch(JSONException e) {
			e.printStackTrace();
			setStatus(ProcessStatus.ErrUnkown);
		}
	}

	@Override
	protected String getFakeResult() {
		return "";
	}
	
	private void parseOuserList(JSONArray jsonOusers, Ousers ousers) throws JSONException {
		if(jsonOusers == null) {
			return;
		}
		for(int i = 0; i < jsonOusers.length(); ++i) {
			JSONObject jsonOuser = jsonOusers.optJSONObject(i);
			Ouser ouser = new Ouser();
			ouser.setUid(jsonOuser.optString("email"));
			ouser.getPortrait().setUrl(jsonOuser.optString("headphoto"));
			ousers.add(ouser);
		}
	}
}
