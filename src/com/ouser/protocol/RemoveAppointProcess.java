package com.ouser.protocol;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ouser.module.AppointId;

/**
 * 删除发布的友约
 * @author hanlixin
 *
 */
public class RemoveAppointProcess extends BaseProcess {

	private static final String URL = "http://app.zhengre.com/servlet/AlterUserNeedStatServlet_Android_V2_0";
	
	private AppointId mAppointId = null;

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
			JSONObject jsonAppoint = new JSONObject();
			jsonAppoint.put("email", mAppointId.getUid());
			JSONArray array = new JSONArray();
			array.put(mAppointId.getAid());
			jsonAppoint.put("desireid", array);
			return jsonAppoint.toString();
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
