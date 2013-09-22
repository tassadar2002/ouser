package com.ouser.protocol;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ouser.module.FollowAppoint;
import com.ouser.module.FollowAppointsWithOwner;
import com.ouser.util.NumberUtil;
import com.ouser.util.UrlUtil;

public class GetFollowAppointProcess extends BaseProcess {
	
	private static final String URL = "http://app.zhengre.com/servlet/GetUserTabListServlet_Android_V2_0";
	
	private FollowAppointsWithOwner mTarget = new FollowAppointsWithOwner();
	private Util.PageNumber mPage = new Util.PageNumber();

	public void setTargetUid(String value) {
		mTarget.setId(value);
	}
	public void setPage(int value) {
		mPage.setValue(value);
	}
	
	public FollowAppointsWithOwner getResult() {
		return mTarget;
	}
	
	@Override
	protected String getRequestUrl() {
		return URL;
	}

	@Override
	protected String getInfoParameter() {
		try {
			JSONObject o = new JSONObject();
			o.put("email", mTarget.getId());
			o.put("pgnum", mPage.getValue());
			return o.toString();
		} catch(JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onResult(String result) {
		try {
			List<FollowAppoint> appoints = new ArrayList<FollowAppoint>();
			JSONArray array = new JSONArray(result);
			for(int i = 0; i < array.length(); ++i) {
				JSONObject jsonAppoint = array.optJSONObject(i);
				FollowAppoint appoint = new FollowAppoint();
				appoint.setTag(UrlUtil.decode(jsonAppoint.optString("tagname")));
				appoint.setCount(jsonAppoint.optInt("tagnum"));
				appoint.setHot(jsonAppoint.optInt("hotnum"));
				appoints.add(appoint);
			}
			mTarget.set(appoints);
		} catch(JSONException e) {
			e.printStackTrace();
			setStatus(ProcessStatus.ErrUnkown);
		}
	}

	@Override
	protected String getFakeResult() {
		
		try {
			JSONArray array = new JSONArray();
			for(int i = 0; i < NumberUtil.random(50); ++i) {
				JSONObject jsonFollow = new JSONObject();
				jsonFollow.put("tagname", UrlUtil.encode("喝酒"));
				jsonFollow.put("tagnum", 100);
				jsonFollow.put("hotnum", 200);
				array.put(jsonFollow);
			}
			return array.toString();
		} catch(JSONException e) {
			e.printStackTrace();
		}
		return "";
	}

}
