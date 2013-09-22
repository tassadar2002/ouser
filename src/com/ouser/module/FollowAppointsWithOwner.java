package com.ouser.module;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class FollowAppointsWithOwner extends ArrayList<FollowAppoint> {
	
	/** 
	 * 列表所属
	 * 没有所属为空
	 */
	private String id = "";

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public void set(List<FollowAppoint> value) {
		clear();
		addAll(value);
	}
}
