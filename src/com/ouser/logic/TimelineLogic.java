package com.ouser.logic;

import java.util.ArrayList;
import java.util.List;

import com.ouser.cache.Cache;
import com.ouser.event.EventListener;
import com.ouser.logic.event.TimelinesEventArgs;
import com.ouser.module.Timeline;
import com.ouser.persistor.PersistorManager;
import com.ouser.protocol.GetTimelineProcess;
import com.ouser.protocol.ResponseListener;

public class TimelineLogic extends BaseLogic {

	static class Factory implements BaseLogic.Factory {

		@Override
		public BaseLogic create() {
			return new TimelineLogic();
		}
	}
	
	TimelineLogic() {
	}
	
	public void getAll(final EventListener listener) {
		
		final GetTimelineProcess process = new GetTimelineProcess();
		process.setMyUid(Cache.self().getMyUid());
		process.run(new ResponseListener() {
			
			@Override
			public void onResponse(String requestId) {
				OperErrorCode errCode = Util.convertFromStatus(process.getStatus());
				TimelinesEventArgs args = null;
				if(errCode == OperErrorCode.Success) {
					
					// 清空未读数字
					// 只是这里清空timeline的，message的不清空
					Cache.self().clearTimelineCount();
					
					// 添加数据库
					List<Timeline> timelines = process.getResult();
					convertIntervalToTime(timelines);
					PersistorManager.self().addTimeline(Cache.self().getMyUid(), timelines);

					// 创建时间
					args = new TimelinesEventArgs(
							PersistorManager.self().getTimeline(Cache.self().getMyUid()));
				} else {
					args = new TimelinesEventArgs(new ArrayList<Timeline>(), errCode);
				}
				fireEvent(listener, args);
			}
		});
	}
	
	public void remove(Timeline value) {
		PersistorManager.self().removeTimeline(value);
	}
	
	// server返回的是interval，本地要转成时间，放入数据库
	private void convertIntervalToTime(List<Timeline> timelines) {
		for(Timeline timeline : timelines) {
			int currentMin = (int)(System.currentTimeMillis() / 60000);
			int time = currentMin - timeline.getTime();
			timeline.setTime(time);
		}
	}
}
