package com.ouser.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

public class ListUtil {

	public static<T> List<T> clone(List<T> value) {
		List<T> result = new ArrayList<T>();
		for(T t : value) {
			result.add(t);
		}
		return result;
	}
	
	public static<K, V> Map<K, V> clone(Map<K, V> value) {
		Map<K, V> result = new HashMap<K, V>();
		for(Map.Entry<K, V> entry : value.entrySet()) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
	
	public static<T> List<T> cloneSuper(List<? extends T> value) {
		List<T> result = new ArrayList<T>();
		for(T t: value) {
			result.add(t);
		}
		return result;
	}
	
	public static String[] toArray(List<String> value) {
		return value.toArray(new String[value.size()]);
	}
	
	public static List<String> fromArray(String[] value) {
		
		List<String> result = new ArrayList<String>();
		for(String s: value) {
			result.add(s);
		}
		return result;
	}
	
	public static String toJson(List<String> link) {
		
		JSONArray v = new JSONArray();
		for(String l: link) {
			v.put(l);
		}
		return v.toString();
	}
	
	public static List<String> fromJson(String link) {
		
		List<String> result = new ArrayList<String>();
		
		try {
			JSONArray v = new JSONArray(link);
			for(int i = 0; i < v.length(); ++i) {
				result.add(v.getString(i));
			}
		} catch(JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
}
