package com.ouser.logic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.ouser.event.EventListener;
import com.ouser.logic.event.StringListEventArgs;
import com.ouser.net.HttpComm;
import com.ouser.net.HttpDownloader;
import com.ouser.net.HttpResultCallback;
import com.ouser.util.Const;

public class UpgradeLogic extends BaseLogic {

	static class Factory implements BaseLogic.Factory {

		@Override
		public BaseLogic create() {
			return new UpgradeLogic();
		}
	}
	
	UpgradeLogic() {
	}
	
	public void check(final EventListener listener) {
		
//		if(true) {
//			List<String> strs = new ArrayList<String>();
//			strs.add("1.0.0.1");
//			strs.add("12345566666\r\n31332中文");
//			strs.add("http://unknown");
//			StringListEventArgs args = new StringListEventArgs(strs);
//			fireEvent(listener, args);
//			return;
//		}
		
		new HttpComm().get(Const.UpgradeServer, new HttpResultCallback() {

			@Override
			public void onResponse(HttpDownloaderResult result, String url, String message) {
				StringListEventArgs args = null;
				if (result == HttpDownloaderResult.eSuccessful) {
					Data data = parseMenifest(message);
					if (data == null) {
						args = new StringListEventArgs(OperErrorCode.Unknown);
					} else {
						if (checkVesion(data.code)) {
							List<String> strs = new ArrayList<String>();
							strs.add(data.version);
							strs.add(data.desc);
							strs.add(data.url);
							args = new StringListEventArgs(strs);
						} else {
							args = new StringListEventArgs(OperErrorCode.NoNeedUpgrade);
						}
					}
				} else {
					args = new StringListEventArgs(OperErrorCode.NetNotAviable);
				}
				fireEvent(listener, args);
			}

			@Override
			public void onProgress(String url, float rate) {
			}
		});
	}

	public void downloadPackage(String url) {
		final String fileName = Const.WorkDir + "upgrade/ouser.apk";
		new File(fileName).deleteOnExit();
		new File(Const.WorkDir + "upgrade").mkdirs();

		new HttpDownloader(true).download(url, fileName, new HttpResultCallback() {

			@Override
			public void onResponse(HttpDownloaderResult result, String url, String message) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setDataAndType(Uri.fromFile(new File(fileName)),
						"application/vnd.android.package-archive");
				Const.Application.startActivity(intent);
			}

			@Override
			public void onProgress(String url, float rate) {
			}
		});
	}

	private Data parseMenifest(String content) {
		try {
			Data data = new Data();
			JSONObject json = new JSONObject(content);
			data.version = json.optString("version");
			data.code = json.optInt("code");
			data.desc = json.optString("desc");
			data.url = json.optString("url");
			return data;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	private boolean checkVesion(int newVersion) {
		int currentVersion = 0;
		try {
			currentVersion = Const.Application.getPackageManager().getPackageInfo("com.ouser", 0).versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return newVersion > currentVersion;
	}

	private static class Data {
		public String version;
		public int code;
		public String desc;
		public String url;
	}
}
