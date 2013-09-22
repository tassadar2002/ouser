package com.ouser.logic.event;

import com.ouser.logic.OperErrorCode;
import com.ouser.module.Ousers;

public class AppointInvolveOusersEventArgs extends StatusEventArgs {
	
	private Ousers mJoinOusers = null;
	private Ousers mViewOusers = null;
	
	public AppointInvolveOusersEventArgs(Ousers join, Ousers view) {
		super(OperErrorCode.Success);
		mJoinOusers = join;
		mViewOusers = view;
	}
	
	public AppointInvolveOusersEventArgs(Ousers join, Ousers view, OperErrorCode errCode) {
		super(errCode);
		mJoinOusers = join;
		mViewOusers = view;
	}
	
	public Ousers getJoinOusers() {
		return mJoinOusers;
	}
	public Ousers getViewOusers() {
		return mViewOusers;
	}
}
