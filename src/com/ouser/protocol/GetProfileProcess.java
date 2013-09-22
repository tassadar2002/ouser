package com.ouser.protocol;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ouser.module.Location;
import com.ouser.module.Ouser;
import com.ouser.module.Photo;
import com.ouser.util.UrlUtil;

public class GetProfileProcess extends BaseProcess {

	private static final String URL = "http://app.zhengre.com/servlet/GetUserProfileServlet_Android_V2_0";

	private String mMyUid = "";
	private Ouser mTarget = null;

	public void setMyUid(String value) {
		mMyUid = value;
	}

	public void setTarget(Ouser value) {
		mTarget = value;
	}

	public Ouser getTarget() {
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
			o.put("email", mMyUid);
			o.put("aimemail", mTarget.getUid());

			// 强制给-1
			o.put("version", "-1");
			return o.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onResult(String result) {
		try {
			JSONObject jsonOuser = new JSONObject(result);
			
			int status = jsonOuser.optInt("status");
			if(status != 0) {
				setStatus(ProcessStatus.ErrUnkown);
				return;
			}

			String uid = mTarget.getUid();
			mTarget = parseJson(jsonOuser);
			if(mTarget != null) {
				mTarget.setUid(uid);
			}

		} catch (JSONException e) {
			e.printStackTrace();
			setStatus(ProcessStatus.ErrUnkown);
		}
	}

	@Override
	protected String getFakeResult() {
		return "";
	}

	static Ouser parseJson(JSONObject o) {
		try {
			Ouser ouser = new Ouser();
			ouser.setNickName(UrlUtil.decode(o.optString("nickname")));
			ouser.getPortrait().setUrl(o.optString("headphoto"));
			ouser.setGender(Util.convertToGender(o.optString("gender")));
			ouser.setBirthday(o.optString("birthday"));
			ouser.setAboutme(UrlUtil.decode(o.optString("aboutme")));
			ouser.setHeight(o.optInt("height"));
			ouser.setBody(o.optInt("bodytype"));
			ouser.setMerry(o.optInt("merrystatus"));
			ouser.setEducation(o.optInt("education"));
			ouser.setSchool(UrlUtil.decode(o.optString("school")));
			ouser.setCompany(UrlUtil.decode(o.optString("company")));
			ouser.setJobTitle(UrlUtil.decode(o.optString("vocation")));
			ouser.setHobby(UrlUtil.decode(o.optString("interest")));
			ouser.setLevel(o.optInt("level"));
			ouser.setInviteCount(o.optInt("invest"));
			ouser.setRelation(o.optInt("relation"));
			ouser.setLastLocation(new Location(o.optDouble("lastlat"), o.optDouble("lastlng")));
			ouser.setPublishAppointCount(o.optInt("desirecount"));
			ouser.setFollowerCount(o.optInt("focuse"));
			ouser.setBeFollowerCount(o.optInt("fans"));
			ouser.setFollowAppointCount(o.optInt("tagsum"));
			JSONArray array = o.optJSONArray("photo");
			for (int i = 0; i < array.length(); ++i) {
				Photo photo = new Photo();
				photo.setUrl(array.optString(i));
				ouser.getPhotos().add(photo);
			}
			return ouser;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
