package com.ouser.module;

import java.util.HashMap;
import java.util.Map;

import com.ouser.util.StringUtil;

import android.os.Bundle;

/**
 * 照片
 * @author hanlixin
 *
 */
public class Photo {
	
	public enum Size {
		Small(80, 0),	/** 小列表，菜单 */
		Normal(100, 1),	/** profile */
		Large(134, 2),	/** 大列表 */
		XLarge(640, 3);	/** 大图 */
		
		private int size = 0;
		private int key = 0;
		Size(int size, int key) {
			this.size = size;
			this.key = key;
		}
		public int getSize() {
			return size;
		}
		int getKey() {
			return key;
		}
		
		static Size fromKey(int key) {
			for(Size s : Size.values()) {
				if(s.getKey() == key) {
					return s;
				}
			}
			return null;
		}
	}

	private String url = "";
	private int resId = 0;
	private Map<Size, String> paths = new HashMap<Size, String>();
	private Map<Size, Integer> tryTimes = new HashMap<Size, Integer>();

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
		this.tryTimes.clear();
	}
	public int getResId() {
		return resId;
	}
	public void setResId(int res) {
		this.resId = res;
	}
	public void setPath(String path, Size size) {
		paths.put(size, path);
	}
	public String getPath(Size size) {
		if(paths.containsKey(size)) {
			return paths.get(size);
		}
		return "";
	}
	public void setTryTime(int value, Size size) {
		tryTimes.put(size, value);
	}
	public int getTryTime(Size size) {
		if(tryTimes.containsKey(size)) {
			return tryTimes.get(size);
		}
		return 0;
	}

	public boolean isEmpty() {
		return StringUtil.isEmpty(url) && resId == 0;
	}
	public boolean isSame(Photo p) {
		return p.url.equals(this.url) || (
				this.resId != 0 && p.resId == this.resId);
	}
	public Bundle toBundle() {
		
		StringBuilder sbPath = new StringBuilder();
		for(Map.Entry<Size, String> entry : paths.entrySet()) {
			sbPath.append(entry.getKey().getKey()).append(":").append(entry.getValue()).append(";");
		}
		StringBuilder sbTryTime = new StringBuilder();
		for(Map.Entry<Size, Integer> entry : tryTimes.entrySet()) {
			sbPath.append(entry.getKey().getKey()).append(":").append(entry.getValue()).append(";");
		}
		
		Bundle bundle = new Bundle();
		bundle.putString("url", url);
		bundle.putInt("resid", resId);
		bundle.putString("path", sbPath.toString());
		bundle.putString("trytime", sbTryTime.toString());
		return bundle;
	}
	public void fromBundle(Bundle bundle) {
		url = bundle.getString("url");
		resId = bundle.getInt("resid");
		String strPath = bundle.getString("path");
		String strTryTime = bundle.getString("trytime");
		
		this.paths.clear();
		for(String str : strPath.split(";")) {
			if("".equals(str)) {
				continue;
			}
			String[] values = str.split(":");
			this.paths.put(Size.fromKey(Integer.parseInt(values[0])), values[1]);
		}
		this.tryTimes.clear();
		for(String str : strTryTime.split(";")) {
			if("".equals(str)) {
				continue;
			}
			String[] values = str.split(":");
			this.tryTimes.put(Size.fromKey(Integer.parseInt(values[0])), Integer.parseInt(values[1]));
		}
	}
}
