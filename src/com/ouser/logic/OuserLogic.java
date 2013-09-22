package com.ouser.logic;

import com.ouser.cache.Cache;
import com.ouser.event.EventListener;
import com.ouser.logic.event.NearOusersEventArgs;
import com.ouser.logic.event.OuserEventArgs;
import com.ouser.logic.event.OusersEventArgs;
import com.ouser.module.Ouser;
import com.ouser.module.Ousers;
import com.ouser.protocol.GetBlackOusersProcess;
import com.ouser.protocol.GetNearProcess;
import com.ouser.protocol.GetRelationOusersProcess;
import com.ouser.protocol.InviteUploadPhotoProcess;
import com.ouser.protocol.ResponseListener;
import com.ouser.protocol.SearchOuserProcess;
import com.ouser.protocol.UpdateBlackOuserProcess;
import com.ouser.protocol.UpdateFollowOuserProcess;
import com.ouser.stat.Stat;
import com.ouser.stat.StatId;

public class OuserLogic extends BaseLogic {

	static class Factory implements BaseLogic.Factory {

		@Override
		public BaseLogic create() {
			return new OuserLogic();
		}
	}
	
	OuserLogic() {
	}
	
	/**
	 * 获取附近藕丝
	 * @param pageIndex 取第几页，从0开始
	 * @param ageFilter 年龄筛选
	 * @param genderFilter 性别筛选
	 */
	/**
	 * @param ageFilter
	 * @param genderFilter
	 * @param pageIndex
	 */
	public void getNear(int pageIndex, final EventListener listener) {

		final GetNearProcess process = new GetNearProcess();
		process.setPageNumber(pageIndex);
		process.setMyUid(Cache.self().getMyUid());
		process.setLocation(Cache.self().getMySelfUser().getLocation());
		process.run(new ResponseListener() {

			@Override
			public void onResponse(String requestId) {
				OperErrorCode errCode = Util.convertFromStatus(process.getStatus());
				NearOusersEventArgs args = null;
				if (errCode == OperErrorCode.Success) {
					args = new NearOusersEventArgs(process.getResult());
				} else {
					args = new NearOusersEventArgs(errCode);
				}
				fireEvent(listener, args);
			}
		});
	}
	
	/**
	 * 根据昵称查找用户
	 * @param keyword
	 * @param pageNum
	 */
	public void search(String keyword, int pageNum, final EventListener listener) {
		final SearchOuserProcess process = new SearchOuserProcess();
		process.setMyUid(Cache.self().getMyUid());
		process.setLocation(Cache.self().getMySelfUser().getLocation());
		process.setKeyword(keyword);
		process.setPageNum(pageNum);
		process.run(new ResponseListener() {

			@Override
			public void onResponse(String requestId) {
				OperErrorCode errCode = Util.convertFromStatus(process.getStatus());
				OusersEventArgs args = null;
				if (errCode == OperErrorCode.Success) {
					args = new OusersEventArgs(process.getResult());
				} else {
					args = new OusersEventArgs(new Ousers(), errCode);
				}
				fireEvent(listener, args);
			}
		});
	}

	/**
	 * 获得好友
	 * @param uid
	 * @param pageNum
	 */
	public void getFriends(String uid, int pageNum, EventListener listener) {
		getRelations(uid, pageNum, GetRelationOusersProcess.Type.eFriend, listener);
	}

	/**
	 * 获得关注用户（关注）
	 * @param uid
	 * @param pageNum
	 */
	public void getFollowers(String uid, int pageNum, EventListener listener) {
		getRelations(uid, pageNum, GetRelationOusersProcess.Type.eFollow, listener);
	}

	/**
	 * 获得藕丝用户（被关注）
	 * @param uid
	 * @param pageNum
	 */
	public void getBeFollowers(String uid, int pageNum, EventListener listener) {
		getRelations(uid, pageNum, GetRelationOusersProcess.Type.eBeFollow, listener);
	}

	/**
	 * 获得我的黑名单用户
	 * @param pageNum
	 */
	public void getBlacks(int pageNum, final EventListener listener) {
		final GetBlackOusersProcess process = new GetBlackOusersProcess();
		process.setMyUid(Cache.self().getMyUid());
		process.setPageNum(pageNum);
		process.run(new ResponseListener() {

			@Override
			public void onResponse(String requestId) {
				OperErrorCode errCode = Util.convertFromStatus(process.getStatus());
				OusersEventArgs args = null;
				if (errCode == OperErrorCode.Success) {
					args = new OusersEventArgs(process.getResult());
				} else {
					args = new OusersEventArgs(new Ousers(), errCode);
				}
				fireEvent(listener, args);
			}
		});
	}

	private void getRelations(final String uid, int pageNum, GetRelationOusersProcess.Type type,
			final EventListener listener) {
		final GetRelationOusersProcess process = new GetRelationOusersProcess();
		process.setOwner(uid);
		process.setType(type);
		process.setPageNum(pageNum);
		process.setLocation(Cache.self().getMySelfUser().getLocation());
		process.run(new ResponseListener() {

			@Override
			public void onResponse(String requestId) {
				OperErrorCode errCode = Util.convertFromStatus(process.getStatus());
				OusersEventArgs args = null;
				if (errCode == OperErrorCode.Success) {
					args = new OusersEventArgs(process.getResult());
				} else {
					args = new OusersEventArgs(new Ousers(), errCode);
				}
				fireEvent(listener, args);
			}
		});
	}

	/**
	 * 我是否可以关注对方
	 * @param ouser
	 * @return
	 */
	public boolean canAddFollow(Ouser ouser) {
		if (ouser.getRelation() == -1) {
			return true;
		}
		return !ouser.isReleation(Ouser.Relation.Follow)
				&& !ouser.getUid().equals(Cache.self().getMySelfUser());
	}

	/**
	 * 我是否可以把对方加入到黑名单
	 * @param ouser
	 * @return
	 */
	public boolean canAddBlack(Ouser ouser) {
		if (ouser.getRelation() == -1) {
			return true;
		}
		return !ouser.isReleation(Ouser.Relation.Black)
				&& !ouser.getUid().equals(Cache.self().getMySelfUser());
	}

	/**
	 * 我对对方添加关注
	 * @param ouser
	 * @param listener
	 */
	public void addFollow(final Ouser ouser, final EventListener listener) {
		Stat.onEvent(StatId.Follow);
		
		boolean inblack = ouser.isReleation(Ouser.Relation.Black);
		if (inblack) {
			OuserEventArgs args = new OuserEventArgs(ouser, OperErrorCode.InBlack);
			fireEvent(listener, args);
			return;
		}

		final UpdateFollowOuserProcess process = new UpdateFollowOuserProcess();
		process.setMyUid(Cache.self().getMyUid());
		process.setTargetUid(ouser.getUid());
		process.setAddOrRemove(true);
		process.run(new ResponseListener() {

			@Override
			public void onResponse(String requestId) {
				OperErrorCode errCode = Util.convertFromStatus(process.getStatus());
				if(errCode == OperErrorCode.Success) {
					Cache.self().invalidOuser(Cache.self().getMyUid());
					Cache.self().invalidOuser(ouser.getUid());
				}
				fireEvent(listener, new OuserEventArgs(ouser, errCode));
			}
		});
	}
	
	/**
	 * 我对对方取消关注
	 * @param ouser
	 * @param listener
	 */
	public void removeFollow(final Ouser ouser, final EventListener listener) {
		final UpdateFollowOuserProcess process = new UpdateFollowOuserProcess();
		process.setMyUid(Cache.self().getMyUid());
		process.setTargetUid(ouser.getUid());
		process.setAddOrRemove(false);
		process.run(new ResponseListener() {

			@Override
			public void onResponse(String requestId) {
				OperErrorCode errCode = Util.convertFromStatus(process.getStatus());
				if(errCode == OperErrorCode.Success) {
					Cache.self().invalidOuser(Cache.self().getMyUid());
					Cache.self().invalidOuser(ouser.getUid());
				}
				fireEvent(listener, new OuserEventArgs(ouser, errCode));
			}
		});
	}

	/**
	 * 添加对方到我的黑名单
	 * @param ouser
	 * @param report 是否同时举报
	 * @param listener
	 */
	public void addBlack(final Ouser ouser, boolean report, final EventListener listener) {
		final UpdateBlackOuserProcess process = new UpdateBlackOuserProcess();
		process.setMyUid(Cache.self().getMyUid());
		process.setTargetUid(ouser.getUid());
		process.setAddOrRemove(true);
		process.setReport(report);
		process.run(new ResponseListener() {

			@Override
			public void onResponse(String requestId) {
				OperErrorCode errCode = Util.convertFromStatus(process.getStatus());
				if(errCode == OperErrorCode.Success) {
					Cache.self().invalidOuser(ouser.getUid());
				}
				fireEvent(listener, new OuserEventArgs(ouser, errCode));
			}
		});
	}
	
	/**
	 * 从我的黑名单中删除对方
	 * @param ouser
	 * @param listener
	 */
	public void removeBlack(final Ouser ouser, final EventListener listener) {
		final UpdateBlackOuserProcess process = new UpdateBlackOuserProcess();
		process.setMyUid(Cache.self().getMyUid());
		process.setTargetUid(ouser.getUid());
		process.setAddOrRemove(false);
		process.run(new ResponseListener() {

			@Override
			public void onResponse(String requestId) {
				OperErrorCode errCode = Util.convertFromStatus(process.getStatus());
				if(errCode == OperErrorCode.Success) {
					Cache.self().invalidOuser(ouser.getUid());
				}
				fireEvent(listener, new OuserEventArgs(ouser, errCode));
			}
		});
	}
	
	/**
	 * 邀请对方上传照片，不关注返回
	 * @param ouser
	 */
	public void inviteUploadPhoto(Ouser ouser) {
		final InviteUploadPhotoProcess process = new InviteUploadPhotoProcess();
		process.setMyUid(Cache.self().getMyUid());
		process.setTargetUid(ouser.getUid());
		process.run();
		Cache.self().inviteUploadPhoto(ouser.getUid());
	}
}
