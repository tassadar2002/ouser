package com.ouser.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import com.ouser.module.AppointId;

/**
 * 退出友约
 * @author hanlixin
 *
 */
public class ExitAppointProcess extends BaseProcess {

	private static final String URL = "http://app.zhengre.com/servlet/RemoveOneJoinNeedServlet_Android_V2_0";

	private String mMyUid = "";
	private AppointId mAppointId = null;

	public void setMyUid(String value) {
		mMyUid = value;
	}
	public void setAppointId(AppointId value) {
		mAppointId = value;
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
			o.put("desireid", mAppointId.getAid());
			o.put("owner", mAppointId.getUid());
			return o.toString();

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onResult(String result) {
		try {
			JSONObject o = new JSONObject(result);
			int value = o.optInt("status");
			switch (value) {
			case 0:
				setStatus(ProcessStatus.Success);
				break;
			case -10:
				setStatus(ProcessStatus.ErrUnkown);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			setStatus(ProcessStatus.ErrUnkown);
		}
	}

	@Override
	protected String getFakeResult() {
		return "{status:0}";
	}
}
