package com.ouser.util;

public class MutablePair<K, V> {

	public K first;
	public V second;
	
	public static<K, V> MutablePair<K, V> create(K first, V second) {
		MutablePair<K, V> value = new MutablePair<K, V>();
		value.first = first;
		value.second = second;
		return value;
	}
}
