package com.ouser.module;

import android.os.Bundle;
import android.util.Pair;

public class Location {

	private Pair<Double, Double> mValue = new Pair<Double, Double>(-1d, -1d);
	
	public Location() {
		
	}
	
	public Location(Location value) {
		setValue(value.mValue.first, value.mValue.second);
	}
	
	public Location(double lat, double lng) {
		setValue(lat, lng);
	}

	public double getLat() {
		return mValue.first;
	}
	
	public double getLng() {
		return mValue.second;
	}
	
	public void setValue(double lat, double lng) {
		mValue = Pair.create(lat, lng);
	}

	public boolean isEmpty() {
		return mValue.first == -1 && mValue.second == -1;
	}
	public boolean isSame(Location value) {
		return mValue.first == value.mValue.first && mValue.second == value.mValue.second;
	}

	public Bundle toBundle() {
		Bundle bundle = new Bundle();
		bundle.putDouble("lat", getLat());
		bundle.putDouble("lng", getLng());
		return bundle;
	}
	public void fromBundle(Bundle bundle) {
		setValue(bundle.getDouble("lat"), bundle.getDouble("lng"));
	}
	
	public static Location valueOf(String value) {
		String[] values = value.split(",");
		if(values.length != 2) {
			return null;
		}
		return new Location(Double.valueOf(values[0]), Double.valueOf(values[1]));
	}

	@Override
	public String toString() {
		return mValue.first + "," + mValue.second;
	}
}
