package com.ouser.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import com.ouser.module.Appoint;
import com.ouser.module.AppointId;
import com.ouser.module.Location;
import com.ouser.util.StringUtil;
import com.ouser.util.UrlUtil;

public class GetAppointProcess extends BaseProcess {

	private static final String URL = "http://app.zhengre.com/servlet/GetUserOneNeedServlet_Android_V2_0";

	private String mMyUid = "";
	private AppointId mAppointId = null;
	private Appoint mResult = null;

	public void setMyUid(String value) {
		mMyUid = value;
	}

	public void setAppointId(AppointId value) {
		mAppointId = value;
	}

	public Appoint getResult() {
		return mResult;
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
			o.put("owner", mAppointId.getUid());
			o.put("desireid", mAppointId.getAid());
			return o.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onResult(String result) {
		if (result.trim().equals("{}")) {
			mResult = new Appoint();
			mResult.setAppointId(mAppointId);
			mResult.setStatus(Appoint.Status.Delete);
			return;
		}
		try {
			JSONObject json = new JSONObject(result);

			Appoint appoint = new Appoint();
			// 忽略 desireid 和 owner
			appoint.setContent(UrlUtil.decode(json.optString("headstr")),
					UrlUtil.decode(json.optString("bodystr")),
					UrlUtil.decode(json.optString("tailstr")));
			appoint.setRelation(Util.convertToRelation(json.optString("relation")));
			appoint.setStatus(Util.convertToStatus(json.optString("status")));
			appoint.setJoinCount(json.optInt("joiner"));
			appoint.setViewCount(json.optInt("viewer"));
			appoint.setPlace(UrlUtil.decode(json.optString("place")));
			
			double lat = json.optDouble("lat");
			double lng = json.optDouble("lng");
			if(lat != 0 || lng != 0) {
				appoint.setLocation(new Location(lat, lng));
			}
			String time = json.optString("time");
			if(!StringUtil.isEmpty(time)) {
				appoint.setTime(Integer.parseInt(time));
			}
			mResult = appoint;
			mResult.setAppointId(mAppointId);

		} catch (JSONException e) {
			e.printStackTrace();
			setStatus(ProcessStatus.ErrUnkown);
		}
	}

	@Override
	protected String getFakeResult() {
		return "";
	}
}
