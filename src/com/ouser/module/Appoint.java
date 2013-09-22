package com.ouser.module;

import android.os.Bundle;
import android.util.Pair;

import com.ouser.util.Const;
import com.ouser.util.StringUtil;

/**
 * 友约
 * @author hanlixin
 * 
 */
public class Appoint {

	public enum Status {
		Ing(1), Done(2), Delete(3), None(0);

		private int value = 0;
		private Status(int value) {
			this.value = value;
		}
		private int getValue() {
			return this.value;
		}
		private static Status toEnum(int value) {
			for (Status status : Status.values()) {
				if (status.getValue() == value) {
					return status;
				}
			}
			return Status.None;
		}
	}
	
	/**
	 * 与当前用户的关系
	 * @author hanlixin
	 *
	 */
	public enum Relation {
		Publish(1), Join(2), None(0);
		
		private int value = 0;
		private Relation(int value) {
			this.value = value;
		}
		private int getValue() {
			return this.value;
		}
		private static Relation toEnum(int value) {
			for (Relation relation : Relation.values()) {
				if (relation.getValue() == value) {
					return relation;
				}
			}
			return Relation.None;
		}
	}

	////////////静态信息////////////
	private AppointId appointId = new AppointId();
	
	////////////获取为全部内容，发布为body部分////////////
	private String content = "";
	
	////////////详细信息专用////////////
	private String body = "";
	private String header = "";
	private String tail = "";
	
	////////////发布专用////////////
	private Pair<Integer, Integer> ageFilter = 
			new Pair<Integer, Integer>(Const.DefaultValue.Age, Const.DefaultValue.Age);
	private Gender genderFilter = Gender.None;
	private int costFilter = Enums.self().getDefaultValue(Enums.Type.Cost);
	private int periodFilter = Enums.self().getDefaultValue(Enums.Type.Period);

	// 友约时间，单位：分钟
	private int time = Const.DefaultValue.Time;
	private Location location = new Location();
	private String place = "";
	private Status status = Status.None;
	private Relation relation = Relation.None;
	
	// 单位：米
	private int distance = Const.DefaultValue.Distance;

	////////////动态信息////////////
	private int joinCount = 0;
	private int viewCount = 0;
	
	/////内容属性
	public String getBody() {
		return body;
	}
	public String getHeader() {
		return header;
	}
	public String getTail() {
		return tail;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String header, String body, String tail) {
		this.header = header;
		this.body = body;
		this.tail = tail;
		this.content = header + " " + body + " " + tail;
	}
	
	public AppointId getAppointId() {
		return appointId;
	}
	public void setAppointId(AppointId appointId) {
		this.appointId = appointId;
	}
	public Pair<Integer, Integer> getAgeFilter() {
		return ageFilter;
	}
	public void setAgeFilter(Pair<Integer, Integer> ageFilter) {
		this.ageFilter = ageFilter;
	}
	public Gender getGenderFilter() {
		return genderFilter;
	}
	public void setGenderFilter(Gender genderFilter) {
		this.genderFilter = genderFilter;
	}
	public int getCostFilter() {
		return costFilter;
	}
	public void setCostFilter(int costFilter) {
		this.costFilter = costFilter;
	}
	public int getPeriodFilter() {
		return periodFilter;
	}
	public void setPeriodFilter(int periodFilter) {
		this.periodFilter = periodFilter;
	}
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public Relation getRelation() {
		return relation;
	}
	public void setRelation(Relation relation) {
		this.relation = relation;
	}
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	public int getJoinCount() {
		return joinCount;
	}
	public void setJoinCount(int joinCount) {
		this.joinCount = joinCount;
	}
	public int getViewCount() {
		return viewCount;
	}
	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}
	/**
	 * 是否为同一个友约
	 * @param value
	 * @return
	 */
	public boolean isSame(Appoint value) {
		return appointId.isSame(value.appointId);
	}
	
	public boolean isEmpty() {
		return appointId.isEmpty();
	}
	
	public boolean isDeleted() {
		return StringUtil.isEmpty(content) && StringUtil.isEmpty(body);
	}
	
	/**
	 * 需要从服务器获取
	 * @return
	 */
	public boolean needFetch() {
		return StringUtil.isEmpty(content);
	}

	public Bundle toBundle() {
		Bundle bundle = new Bundle();
		bundle.putBundle("appointid", appointId.toBundle());
		
		bundle.putString("content", content);
		bundle.putString("body", body);
		
		bundle.putInt("minage", ageFilter.first);
		bundle.putInt("maxage", ageFilter.second);
		bundle.putInt("gender", genderFilter.getValue());
		bundle.putInt("cost", costFilter);
		
		bundle.putString("header", header);
		bundle.putString("tail", tail);
		
		bundle.putString("location", location.toString());
		bundle.putString("place", place);
		bundle.putInt("status", status.getValue());
		bundle.putInt("relation", relation.getValue());
		bundle.putInt("time", time);
		bundle.putInt("distance", distance);
		bundle.putInt("joincount", joinCount);
		bundle.putInt("viewcount", viewCount);
		return bundle;
	}

	public void fromBundle(Bundle bundle) {
		Bundle appointIdBundle = bundle.getBundle("appointid");
		this.appointId.fromBundle(appointIdBundle);
		
		content = bundle.getString("content");
		body = bundle.getString("body");
		
		ageFilter = Pair.create(bundle.getInt("minage"), bundle.getInt("maxage"));
		genderFilter = Gender.toEnum(bundle.getInt("gender"));
		costFilter = bundle.getInt("cost");
		
		header = bundle.getString("header");
		tail = bundle.getString("tail");
		
		location = Location.valueOf(bundle.getString("location"));
		place = bundle.getString("place");
		status = Status.toEnum(bundle.getInt("status"));
		relation = Relation.toEnum(bundle.getInt("relation"));
		time = bundle.getInt("time");
		distance = bundle.getInt("distance");
		joinCount = bundle.getInt("joincount");
		viewCount = bundle.getInt("viewcount");
	}
}
