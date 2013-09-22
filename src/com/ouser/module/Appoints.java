package com.ouser.module;

import java.util.ArrayList;

import android.os.Bundle;

@SuppressWarnings("serial")
public class Appoints extends ArrayList<Appoint> {

	public Bundle toBundle() {
		Bundle bundle = new Bundle();
		bundle.putInt("count", size());
		for(int i = 0; i < size(); ++i) {
			bundle.putBundle("appoint" + i, get(i).toBundle());
		}
		return bundle;
	}
	
	public void fromBundle(Bundle bundle) {
		int count = bundle.getInt("count");
		for(int i = 0; i < count; ++i) {
			Appoint appoint = new Appoint();
			appoint.fromBundle(bundle.getBundle("appoint" + i));
			add(appoint);
		}
	}
}
