package com.ouser.util;

import java.util.Random;

public class NumberUtil {
	
	private static Random mRandom = new Random();

	public static int random(int start, int end) {
		return Math.abs(mRandom.nextInt()) % (end - start) + start;
	}
	
	public static int random(int end) {
		return random(0, end);
	}
	
	public static int random() {
		return Math.abs(mRandom.nextInt());
	}
}
