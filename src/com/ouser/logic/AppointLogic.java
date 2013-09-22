package com.ouser.logic;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.ouser.cache.Cache;
import com.ouser.event.EventArgs;
import com.ouser.event.EventId;
import com.ouser.event.EventListener;
import com.ouser.logic.event.AppointEventArgs;
import com.ouser.logic.event.AppointInvolveOusersEventArgs;
import com.ouser.logic.event.AppointsEventArgs;
import com.ouser.logic.event.AppointsWithPublisherEventArgs;
import com.ouser.logic.event.FollowAppointsEventArgs;
import com.ouser.logic.event.IntegerEventArgs;
import com.ouser.logic.event.OuserEventArgs;
import com.ouser.logic.event.PlacesEventArgs;
import com.ouser.logic.event.StringEventArgs;
import com.ouser.logic.event.StringListEventArgs;
import com.ouser.module.Appoint;
import com.ouser.module.Appoints;
import com.ouser.module.AppointsWithPublisher;
import com.ouser.module.FollowAppointsWithOwner;
import com.ouser.module.Ouser;
import com.ouser.module.Ousers;
import com.ouser.persistor.PersistorManager;
import com.ouser.protocol.AppointInviteProcess;
import com.ouser.protocol.ExitAppointProcess;
import com.ouser.protocol.GetAppointCloudTagProcess;
import com.ouser.protocol.GetAppointInvolveOusersProcess;
import com.ouser.protocol.GetAppointRecommendPlaceProcess;
import com.ouser.protocol.GetFollowAppointProcess;
import com.ouser.protocol.GetJoinAppointProcess;
import com.ouser.protocol.GetPublishAppointProcess;
import com.ouser.protocol.JoinAppointProcess;
import com.ouser.protocol.PublishAppointProcess;
import com.ouser.protocol.RandomsAppointProcess;
import com.ouser.protocol.RemoveAppointProcess;
import com.ouser.protocol.ReportAppointProcess;
import com.ouser.protocol.ResponseListener;
import com.ouser.protocol.SearchAppointProcess;
import com.ouser.protocol.UpdateFollowAppointProcess;
import com.ouser.stat.Stat;
import com.ouser.stat.StatId;

public class AppointLogic extends BaseLogic {

	static class Factory implements BaseLogic.Factory {

		@Override
		public BaseLogic create() {
			return new AppointLogic();
		}
	}
	
	AppointLogic() {
	}
	
	/**
	 * 我发布友约
	 * @param appoint
	 * @param follow
	 */
	public void publish(Appoint appoint, boolean follow, boolean publicc, final EventListener listener) {
		Stat.onEvent(StatId.Publish);
		
		appoint.getAppointId().setUid(Cache.self().getMyUid());

		final PublishAppointProcess process = new PublishAppointProcess();
		process.setAppoint(appoint);
		process.setFollow(follow);
		process.setPublic(publicc);
		process.run(new ResponseListener() {

			@Override
			public void onResponse(String requestId) {
				OperErrorCode errCode = Util.convertFromStatus(process.getStatus());
				if(errCode == OperErrorCode.Success) {
					Cache.self().invalidOuser(Cache.self().getMyUid());
				}
				StringEventArgs stringArgs = new StringEventArgs(process.getAppointId(), errCode);
				fireEvent(listener, stringArgs);
			}
		});
	}

	/**
	 * 获得用户发布的友约
	 * @param uid
	 * @param onlyValid
	 */
	public void getPublish(final String uid, boolean onlyValid, final EventListener listener) {

		final GetPublishAppointProcess process = new GetPublishAppointProcess();
		process.setMyUid(Cache.self().getMyUid());
		process.setPublisherUid(uid);
		process.setOnlyValid(onlyValid);
		process.run(new ResponseListener() {

			@Override
			public void onResponse(String requestId) {
				OperErrorCode errCode = Util.convertFromStatus(process.getStatus());
				AppointsEventArgs appointsArgs = null;
				if (errCode == OperErrorCode.Success) {
					appointsArgs = new AppointsEventArgs(process.getResult());
				} else {
					appointsArgs = new AppointsEventArgs(new Appoints(), errCode);
				}
				fireEvent(listener, appointsArgs);
			}
		});
	}

	/**
	 * 获得我参加的友约
	 */
	public void getJoin(final EventListener listener) {
		final GetJoinAppointProcess process = new GetJoinAppointProcess();
		process.setMyUid(Cache.self().getMyUid());
		process.run(new ResponseListener() {

			@Override
			public void onResponse(String requestId) {
				OperErrorCode errCode = Util.convertFromStatus(process.getStatus());
				AppointsWithPublisherEventArgs appointsArgs = null;
				if (errCode == OperErrorCode.Success) {
					appointsArgs = new AppointsWithPublisherEventArgs(process.getResult());
				} else {
					appointsArgs = new AppointsWithPublisherEventArgs(new AppointsWithPublisher(), errCode);
				}
				fireEvent(listener, appointsArgs);
			}
		});
	}

	/**
	 * 获得友约的参加和查看用户
	 * @param uid
	 * @param appointId
	 * @param listener
	 */
	public void getInvolveOusers(Appoint appoint, final EventListener listener) {

		final GetAppointInvolveOusersProcess process = new GetAppointInvolveOusersProcess();
		process.setMyUid(Cache.self().getMyUid());
		process.setAppointId(appoint.getAppointId());
		process.run(new ResponseListener() {

			@Override
			public void onResponse(String requestId) {
				OperErrorCode errCode = Util.convertFromStatus(process.getStatus());
				AppointInvolveOusersEventArgs args = null;
				if (errCode == OperErrorCode.Success) {
					args = new AppointInvolveOusersEventArgs(process.getJoinList(), process
							.getViewList());
				} else {
					args = new AppointInvolveOusersEventArgs(new Ousers(), new Ousers(), errCode);
				}
				fireEvent(listener, args);
			}
		});
	}

	/**
	 * 获得关注标签的友约
	 * @param uid
	 * @param pageNum
	 */
	public void getFollow(final String uid, int pageNum, final EventListener listener) {
		final GetFollowAppointProcess process = new GetFollowAppointProcess();
		process.setTargetUid(uid);
		process.setPage(pageNum);
		process.run(new ResponseListener() {

			@Override
			public void onResponse(String requestId) {
				OperErrorCode errCode = Util.convertFromStatus(process.getStatus());
				FollowAppointsEventArgs appointsArgs = null;
				if (errCode == OperErrorCode.Success) {
					appointsArgs = new FollowAppointsEventArgs(process.getResult());
				} else {
					FollowAppointsWithOwner appointsWithOwner = new FollowAppointsWithOwner();
					appointsWithOwner.setId(uid);
					appointsArgs = new FollowAppointsEventArgs(appointsWithOwner, errCode);
				}
				fireEvent(listener, appointsArgs);
			}
		});
	}

	/**
	 * 获得友约热词
	 * @param listener
	 */
	public void getCloudTag(final EventListener listener) {
		final GetAppointCloudTagProcess process = new GetAppointCloudTagProcess();
		process.run(new ResponseListener() {

			@Override
			public void onResponse(String requestId) {
				OperErrorCode errCode = Util.convertFromStatus(process.getStatus());
				StringListEventArgs args = null;
				if (errCode == OperErrorCode.Success) {
					args = new StringListEventArgs(process.getResult());
				} else {
					args = new StringListEventArgs(errCode);
				}
				fireEvent(listener, args);
			}
		});
	}

	/**
	 * 查找友约
	 * @param keyword
	 * @param start
	 */
	public void search(String keyword, int pageNum, final EventListener listener) {
		Stat.onEvent(StatId.SearchTag);
		
		final SearchAppointProcess process = new SearchAppointProcess();
		process.setMyUid(Cache.self().getMyUid());
		process.setKeyword(keyword);
		process.setPageNum(pageNum);
		process.run(new ResponseListener() {

			@Override
			public void onResponse(String requestId) {
				OperErrorCode errCode = Util.convertFromStatus(process.getStatus());
				AppointsWithPublisherEventArgs appointsArgs = null;
				if (errCode == OperErrorCode.Success) {
					appointsArgs = new AppointsWithPublisherEventArgs(process.getResult());
				} else {
					appointsArgs = new AppointsWithPublisherEventArgs(new AppointsWithPublisher(), errCode);
				}
				fireEvent(listener, appointsArgs);
			}
		});
	}
	
	public void getRandoms(final EventListener listener) {
		final RandomsAppointProcess process = new RandomsAppointProcess();
		process.setMyUid(Cache.self().getMyUid());
		process.run(new ResponseListener() {
			
			@Override
			public void onResponse(String requestId) {
				OperErrorCode errCode = Util.convertFromStatus(process.getStatus());
				AppointsEventArgs args = null;
				if(errCode == OperErrorCode.Success) {
					args = new AppointsEventArgs(process.getResult());
				} else {
					args = new AppointsEventArgs(new Appoints(), errCode);
				}
				fireEvent(listener, args);
			}
		});
	}
	
	/**
	 * 我参加友约
	 * @param appoint
	 * @param listener
	 */
	public void join(final Appoint appoint, final EventListener listener) {
		Stat.onEvent(StatId.Join);
		
		final JoinAppointProcess process = new JoinAppointProcess();
		process.setMyUid(Cache.self().getMyUid());
		process.setAppointId(appoint.getAppointId());
		process.run(new ResponseListener() {

			@Override
			public void onResponse(String requestId) {
				OperErrorCode errCode = Util.convertFromStatus(process.getStatus());
				fireEvent(listener, new AppointEventArgs(appoint, errCode));
			}
		});
	}

	/**
	 * 我举报友约
	 * @param appoint
	 * @param listener
	 */
	public void report(final Appoint appoint, final EventListener listener) {
		final ReportAppointProcess process = new ReportAppointProcess();
		process.setMyUid(Cache.self().getMyUid());
		process.setAppointId(appoint.getAppointId());
		process.run(new ResponseListener() {

			@Override
			public void onResponse(String requestId) {
				OperErrorCode errCode = Util.convertFromStatus(process.getStatus());
				fireEvent(listener, new AppointEventArgs(appoint, errCode));
			}
		});
	}

	/**
	 * 我退出友约
	 * @param appoint
	 * @param listener
	 */
	public void exit(final Appoint appoint, final EventListener listener) {
		final ExitAppointProcess process = new ExitAppointProcess();
		process.setMyUid(Cache.self().getMyUid());
		process.setAppointId(appoint.getAppointId());
		process.run(new ResponseListener() {

			@Override
			public void onResponse(String requestId) {
				OperErrorCode errCode = Util.convertFromStatus(process.getStatus());
				fireEvent(listener, new AppointEventArgs(appoint, errCode));
			}
		});
	}

	/**
	 * 我删除友约
	 * @param appoint
	 * @param listener
	 */
	public void remove(final Appoint appoint, final EventListener listener) {
		final RemoveAppointProcess process = new RemoveAppointProcess();
		process.setAppointId(appoint.getAppointId());
		process.run(new ResponseListener() {

			@Override
			public void onResponse(String requestId) {
				OperErrorCode errCode = Util.convertFromStatus(process.getStatus());
				if(errCode == OperErrorCode.Success) {
					Cache.self().invalidOuser(Cache.self().getMyUid());
				}
				fireEvent(listener, new AppointEventArgs(appoint, errCode));
			}
		});
	}

	/**
	 * 我添加友约标签关注
	 * @param tags
	 * @param listener
	 */
	public void addFolow(final String tags, final EventListener listener) {
		Stat.onEvent(StatId.FollowTag);
		
		final UpdateFollowAppointProcess process = new UpdateFollowAppointProcess();
		process.setMyUid(Cache.self().getMyUid());
		process.setTags(tags);
		process.setIsFollow(true);
		process.run(new ResponseListener() {

			@Override
			public void onResponse(String requestId) {
				OperErrorCode errCode = Util.convertFromStatus(process.getStatus());
				if(errCode == OperErrorCode.Success) {
					Cache.self().invalidOuser(Cache.self().getMyUid());
				}
				fireEvent(listener, new StringEventArgs(tags, errCode));
			}
		});
	}

	/**
	 * 我删除友约标签关注
	 * @param tags
	 * @param listener
	 */
	public void removeFollow(final String tags, final EventListener listener) {
		final UpdateFollowAppointProcess process = new UpdateFollowAppointProcess();
		process.setMyUid(Cache.self().getMyUid());
		process.setTags(tags);
		process.setIsFollow(false);
		process.run(new ResponseListener() {

			@Override
			public void onResponse(String requestId) {
				OperErrorCode errCode = Util.convertFromStatus(process.getStatus());
				if(errCode == OperErrorCode.Success) {
					Cache.self().invalidOuser(Cache.self().getMyUid());
				}
				fireEvent(listener, new StringEventArgs(tags, errCode));
			}
		});
	}
	
	public void getAppointRecommendPlace(final EventListener listener) {
		final GetAppointRecommendPlaceProcess process = new GetAppointRecommendPlaceProcess();
		process.setLocation(Cache.self().getMySelfUser().getLocation());
		process.setSerial(Cache.self().getRecommendPlaceSerial());
		process.run(new ResponseListener() {
			
			@Override
			public void onResponse(String requestId) {
				OperErrorCode errCode = Util.convertFromStatus(process.getStatus());
				fireEvent(listener, new PlacesEventArgs(process.getResult(), errCode));
			}
		});
	}
	
	/**
	 * 我获得某友约邀请剩余条数
	 * @param appoint
	 * @return
	 */
	public void getInviteRemainCount(final Appoint appoint, final EventListener listener) {		
		LogicFactory.self().getProfile().get(Cache.self().getMyUid(), new EventListener() {
			
			@Override
			public void onEvent(EventId id, EventArgs args) {
				OuserEventArgs ouserArgs = (OuserEventArgs)args;
				if(ouserArgs.getErrCode() == OperErrorCode.Success) {
					int remain = ouserArgs.getOuser().getInviteCount() - 
							PersistorManager.self().getAppointInviteCount(appoint, getToday());
					fireEvent(listener, new IntegerEventArgs(remain));
				} else {
					fireEvent(listener, new IntegerEventArgs(0, ouserArgs.getErrCode()));
				}
			}
		});
	}
	
	/**
	 * 我发送友约邀请
	 * @param appoint
	 * @param ousers
	 * @param listener
	 */
	public void invite(final Appoint appoint, final List<Ouser> ousers, final EventListener listener) {
		Stat.onEvent(StatId.Invest);
		
		final AppointInviteProcess process = new AppointInviteProcess();
		process.setTarget(ousers);
		process.setContent(appoint.getContent() + "，快来参加吧");
		process.setAppoint(appoint);
		process.setDate(new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date()));
		process.run(new ResponseListener() {
			
			@Override
			public void onResponse(String requestId) {
				OperErrorCode errCode = Util.convertFromStatus(process.getStatus());
				if(errCode == OperErrorCode.Success) {
					PersistorManager.self().updateAppointInviteCount(appoint, getToday(), ousers.size());
				}
				fireStatusEvent(listener, errCode);
			}
		});
	}
	
	private int getToday() {
		return (int)(new Date().getTime() / (1000*60*60*24));
	}
}
