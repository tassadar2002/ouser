package com.ouser.logic.event;

import java.util.List;

import com.ouser.logic.OperErrorCode;
import com.ouser.module.Timeline;

public class TimelinesEventArgs extends StatusEventArgs {

	private List<Timeline> mTimelines = null;
	
	public TimelinesEventArgs(List<Timeline> value) {
		super(OperErrorCode.Success);
		mTimelines = value;
	}
	public TimelinesEventArgs(List<Timeline> value, OperErrorCode errCode) {
		super(errCode);
		mTimelines = value;
	}
	
	public List<Timeline> getTimelines() {
		return mTimelines;
	}
}
