package com.ouser.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import com.ouser.module.Ouser;
import com.ouser.util.UrlUtil;

public class EditProfileProcess extends BaseProcess {
	
	private static final String URL = "http://app.zhengre.com/servlet/UpdateUserProfileServlet_Android_V2_0";
	
	private Ouser mMySelf = null;
	
	public void setOuser(Ouser value) {
		mMySelf = value;
	}

	@Override
	protected String getRequestUrl() {
		return URL;
	}

	@Override
	protected String getInfoParameter() {
		try {
			JSONObject jsonOuser = new JSONObject();
			jsonOuser.put("email", mMySelf.getUid());
			jsonOuser.put("nickname", UrlUtil.encode(mMySelf.getNickName()));
			jsonOuser.put("gender", Util.convertFromGender(mMySelf.getGender()));
			jsonOuser.put("birthday", mMySelf.getBirthday());
			jsonOuser.put("height", String.valueOf(mMySelf.getHeight()));
			jsonOuser.put("bodytype", String.valueOf(mMySelf.getBody()));
			jsonOuser.put("marry", String.valueOf(mMySelf.getMerry()));
			jsonOuser.put("education", String.valueOf(mMySelf.getEducation()));
			jsonOuser.put("school", UrlUtil.encode(mMySelf.getSchool()));
			jsonOuser.put("company", UrlUtil.encode(mMySelf.getCompany()));
			jsonOuser.put("vocation", UrlUtil.encode(mMySelf.getJobTitle()));
			jsonOuser.put("insterest", UrlUtil.encode(mMySelf.getHobby()));
			return jsonOuser.toString();
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
