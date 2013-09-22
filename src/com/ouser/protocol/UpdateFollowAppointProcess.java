package com.ouser.protocol;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 关注或取消关注友约标签
 * @author hanlixin
 *
 */
public class UpdateFollowAppointProcess extends BaseProcess {

	private static final String URL = "http://app.zhengre.com/servlet/FocuseTabServlet_Android_V2_0";
	
	private String mMyUid = "";
	private String mTags = "";
	private boolean mIsFollow = false;
	
	public void setMyUid(String value) {
		mMyUid = value;
	}
	public void setTags(String value) {
		mTags = value;
	}
	public void setIsFollow(boolean value) {
		mIsFollow = value;
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
			o.put("tabs", mTags);
			o.put("sign", mIsFollow ? "0" : "1");
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
