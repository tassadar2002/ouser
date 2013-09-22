package com.ouser.module;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class PhotosWithOwner extends ArrayList<Photo> {

	private String uid = "";

	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	
	public void set(List<Photo> value) {
		clear();
		addAll(value);
	}
}
