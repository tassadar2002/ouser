package com.ouser.module;

import java.util.ArrayList;

import android.util.Pair;

@SuppressWarnings("serial")
public class AppointsWithPublisher extends ArrayList<Pair<Appoint, Ouser>> {

	public void add(Appoint appoint, Ouser ouser) {
		add(Pair.create(appoint, ouser));
	}
	public Appoint getAppoint(int index) {
		return get(index).first;
	}
	public Ouser getOuser(int index) {
		return get(index).second;
	}
}
