package com.ouser.module;

import java.util.Comparator;

/**
 * 友约地点
 * @author hanlixin
 *
 */
public class Place {

	private Location location = new Location();
	private String place = "";
	private String code = "";
	
	// 单位：米
	private int distance = 0;

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
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	
	public static class ComparatorByDistance implements Comparator<Place> {

		 public int compare(Place arg0, Place arg1) {
			 if(arg0.getDistance() < arg1.getDistance()) {
				 return -1;
			 }
			 if(arg0.getDistance() > arg1.getDistance()) {
				 return 1;
			 }
			 return 0;
		 }
	}
	public static class ComparatorByCode implements Comparator<Place> {

		 public int compare(Place arg0, Place arg1) {
			 return arg0.getCode().compareTo(arg1.getCode());
		 }
	}
}
