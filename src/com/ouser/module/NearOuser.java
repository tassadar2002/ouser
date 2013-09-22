package com.ouser.module;

import java.util.Comparator;

public class NearOuser {

	private Ouser ouser = new Ouser();
	private Appoint lastAppoint = new Appoint();
	
	public Ouser getOuser() {
		return ouser;
	}
	public void setOuser(Ouser ouser) {
		this.ouser = ouser;
	}
	public Appoint getLastAppoint() {
		return lastAppoint;
	}
	public void setLastAppoint(Appoint lastAppoint) {
		this.lastAppoint = lastAppoint;
	}

	public boolean hasLastAppoint() {
		return lastAppoint != null && !"".equals(lastAppoint.getAppointId());
	}
	
	public static class ComparatorByDistance implements Comparator<NearOuser> {

		private static Ouser.ComparatorByDistance comparer = new Ouser.ComparatorByDistance();
		
		 public int compare(NearOuser arg0, NearOuser arg1) {
			 return comparer.compare(arg0.getOuser(), arg1.getOuser());
		 }
	}
}
