package com.ouser.protocol;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ouser.module.Timeline;
import com.ouser.util.UrlUtil;

public class GetTimelineProcess extends BaseProcess {

	private static final String URL = "http://app.zhengre.com/servlet/GetUserMsgListServlet_Android_V2_0";
	
	private String mMyUid = "";	
	private List<Timeline> mTimelines = null;
	
	public void setMyUid(String value) {
		mMyUid = value;
	}
	
	public List<Timeline> getResult() {
		return mTimelines;
	}

	@Override
	protected String getRequestUrl() {
		return URL;
	}

	@Override
	protected String getInfoParameter() {
		try {
			JSONObject o = new JSONObject();
			o.put("email", mMyUid);
			return o.toString();
		} catch(JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onResult(String result) {
		mTimelines = new ArrayList<Timeline>();
		try {
			JSONArray array = new JSONArray(result);
			for(int i = 0; i < array.length(); ++i) {
				JSONObject json = array.optJSONObject(i);
				
				Timeline timeline = new Timeline();
				timeline.setType(convertType(json.optString("msgtype")));
				
				timeline.setUid(json.optString("email"));
				timeline.setName(UrlUtil.decode(json.optString("nick")));
				timeline.getPortrait().setUrl(json.optString("head"));
				timeline.setContent(UrlUtil.decode(json.optString("content")));
				timeline.setTime(json.optInt("interval"));
				
				timeline.getAppointId().setAid(json.optString("desireid"));
				timeline.getAppointId().setUid(json.optString("email"));
				
				mTimelines.add(timeline);
			}
		} catch(JSONException e) {
			e.printStackTrace();
			setStatus(ProcessStatus.ErrUnkown);
		}
	}

	@Override
	protected String getFakeResult() {
		return "";
	}
	
	private Timeline.Type convertType(String value) {
		if("1".equals(value)) {
			return Timeline.Type.eOuser;
		}
		if("2".equals(value)) {
			return Timeline.Type.eAppoint;
		}
		return Timeline.Type.eNone;
	}
}
