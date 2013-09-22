package com.ouser.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import com.ouser.module.Ouser;
import com.ouser.module.User;

public class LoginProcess extends BaseProcess {
	
	private static final String URL = "http://app.zhengre.com/servlet/UserLoginServlet_Android_V2_0";
	
	private User mUser = null;
	private String mPhoneUuid = "";
	private Ouser mMyself = new Ouser();
	private int mUnReadedMessageCount = 0;
	private int mTimelineCount = 0;
	
	public void setUser(User value) {
		mUser = value;
	}
	public void setPhoneUuid(String value) {
		mPhoneUuid = value;
	}
	public Ouser getMyself() {
		return mMyself;
	}
	public int getUnReadedMessageCount() {
		return mUnReadedMessageCount;
	}
	public int getTimelineCount() {
		return mTimelineCount;
	}
	@Override
	protected String getRequestUrl() {
		return URL;
	}

	@Override
	protected String getInfoParameter() {
		try {
			JSONObject o = new JSONObject();
			o.put("email", mUser.getUid());
			o.put("pwd", mUser.getPassword());
			o.put("lng", String.valueOf(mUser.getLocation().getLng()));
			o.put("lat", String.valueOf(mUser.getLocation().getLat()));
			o.put("uuid", mPhoneUuid);
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
				mMyself = GetProfileProcess.parseJson(o);
				mMyself.setUid(mUser.getUid());
				
				mUnReadedMessageCount = o.optInt("totalchat");
				mTimelineCount = o.optInt("totalmsg");
				break;
			case -1:
				setStatus(ProcessStatus.ErrPass);
				break;
			case -2:
				setStatus(ProcessStatus.ErrUid);
				break;
			case -10:
				setStatus(ProcessStatus.ErrException);
				break;
			}
		} catch(Exception e) {
			e.printStackTrace();
			setStatus(ProcessStatus.ErrUnkown);
		}
	}
	
	@Override
	protected String getFakeResult() {
		return "";
	}
}
