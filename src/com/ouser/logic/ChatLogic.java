package com.ouser.logic;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.ouser.cache.Cache;
import com.ouser.event.EventArgs;
import com.ouser.event.EventCenter;
import com.ouser.event.EventId;
import com.ouser.event.EventListener;
import com.ouser.logic.event.AppointEventArgs;
import com.ouser.logic.event.ChatMessageEventArgs;
import com.ouser.logic.event.ChatMessagesEventArgs;
import com.ouser.logic.event.ListMessagesEventArgs;
import com.ouser.logic.event.OusersEventArgs;
import com.ouser.module.AppointId;
import com.ouser.module.ChatId;
import com.ouser.module.ChatMessage;
import com.ouser.module.ListMessage;
import com.ouser.module.Location;
import com.ouser.module.Ouser;
import com.ouser.module.Ousers;
import com.ouser.persistor.PersistorManager;
import com.ouser.protocol.GetGroupMessageProcess;
import com.ouser.protocol.GetListMessageProcess;
import com.ouser.protocol.GetMessageBaseProcess;
import com.ouser.protocol.GetSingleMessageProcess;
import com.ouser.protocol.RemoveGroupMessagesProcess;
import com.ouser.protocol.RemoveSingleMessagesProcess;
import com.ouser.protocol.ResponseListener;
import com.ouser.protocol.SendGroupMessageProcess;
import com.ouser.protocol.SendMessageBaseProcess;
import com.ouser.protocol.SendSingleMessageProcess;
import com.ouser.pusher.Pusher;
import com.ouser.pusher.PusherManager;
import com.ouser.stat.Stat;
import com.ouser.stat.StatId;
import com.ouser.util.Const;
import com.ouser.util.FileUtil;
import com.ouser.util.ImageUtil;
import com.ouser.util.MutablePair;
import com.ouser.util.StringUtil;

public class ChatLogic extends BaseLogic {

	static class Factory implements BaseLogic.Factory {

		@Override
		public BaseLogic create() {
			return new ChatLogic();
		}
	}
	
	ChatLogic() {
	}
	
	/**
	 * 获得我的消息列表中所有消息，会从服务器拉取
	 */
	public void getMyMessageFromRemote(final EventListener listener) {

		final GetListMessageProcess process = new GetListMessageProcess();
		process.setMyUid(Cache.self().getMyUid());
		process.run(new ResponseListener() {

			@Override
			public void onResponse(String requestId) {
				OperErrorCode errCode = Util.convertFromStatus(process.getStatus());
				if (errCode == OperErrorCode.Success) {
					saveMyMessage(process.getResult(), listener);
				} else {
					fireEvent(listener, new ListMessagesEventArgs(errCode));
				}
			}
		});
	}

	/**
	 * 获得我的消息列表中所有消息，仅从本地获得
	 * 
	 * @return
	 */
	public void getMyMessageFromLocal(final EventListener listener) {
		List<ListMessage> messages = PersistorManager.self().getListMessages(
				Cache.self().getMyUid());
		continueGetMyMessage(messages, listener);
	}

	/**
	 * 保存我的消息
	 * @param messages
	 * @param listener
	 */
	private void saveMyMessage(final List<ListMessage> messages, final EventListener listener) {
		new AsyncTask<Void, Void, Void>() {

			private List<ListMessage> mMessages = null;

			@Override
			protected Void doInBackground(Void... params) {
				PersistorManager.self().addListMessage(Cache.self().getMyUid(), messages);
				mMessages = PersistorManager.self().getListMessages(Cache.self().getMyUid());
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				continueGetMyMessage(mMessages, listener);
			}

		}.execute();
	}

	/**
	 * 继续获取每条消息中的藕丝信息
	 * @param messages
	 * @param listener
	 */
	private void continueGetMyMessage(final List<ListMessage> messages, final EventListener listener) {
		List<String> uids = new ArrayList<String>();
		for (ListMessage message : messages) {
			String uid = message.getOuser().getUid();
			if(!uids.contains(uid)) {
				uids.add(uid);
			}
		}
		LogicFactory.self().getProfile().getSimples(uids, new EventListener() {

			@Override
			public void onEvent(EventId id, EventArgs args) {
				OusersEventArgs ousersArgs = (OusersEventArgs) args;
				OperErrorCode errCode = ousersArgs.getErrCode();
				Ousers ousers = ousersArgs.getOusers();

				ListMessagesEventArgs messageArgs = null;
				if (errCode == OperErrorCode.Success) {
					for (ListMessage message : messages) {
						for (Ouser ouser : ousers) {
							if (message.getOuser().isSame(ouser)) {
								message.setOuser(ouser);
								break;
							}
						}
					}
					messageArgs = new ListMessagesEventArgs(messages);
				} else {
					messageArgs = new ListMessagesEventArgs(errCode);
				}
				fireEvent(listener, messageArgs);
			}
		});
	}

	/**
	 * 删除我的消息列表中的某项，仅从本地删除
	 * 
	 * @param chatId
	 */
	public void removeMyMessage(ListMessage message) {
		PersistorManager.self().removeListMessage(Cache.self().getMyUid(), message.getChatId());
	}
	
	/**
	 * 删除我的所有未读消息，从服务器上删除
	 * @param listener
	 */
	public void removeMyMessages(final EventListener listener) {
		List<ListMessage> messages = PersistorManager.self().getListMessages(
				Cache.self().getMyUid());
		
		List<String> singleUids = new ArrayList<String>();
		List<AppointId> appointIds = new ArrayList<AppointId>();
		for(ListMessage message : messages) {
			if(message.isSingle()) {
				singleUids.add(message.getChatId().getSingleId());
			} else {
				appointIds.add(message.getChatId().getGroupId());
			}
		}
		
		final MutablePair<Boolean, Boolean> completeFlag = MutablePair.create(false, false);
		final RemoveSingleMessagesProcess singleProcess = new RemoveSingleMessagesProcess();
		singleProcess.setMyUid(Cache.self().getMyUid());
		singleProcess.setTargetUids(singleUids);
		singleProcess.run(new ResponseListener() {
			
			@Override
			public void onResponse(String requestId) {
				// 不关注返回值
				completeFlag.first = true;
				if(completeFlag.first && completeFlag.second) {
					PersistorManager.self().clearListMessagesUnreadCount(Cache.self().getMyUid());
					fireStatusEvent(listener, OperErrorCode.Success);
				}
			}
		});
		
		final RemoveGroupMessagesProcess groupProcess = new RemoveGroupMessagesProcess();
		groupProcess.setMyUid(Cache.self().getMyUid());
		groupProcess.setTargetAppointIds(appointIds);
		groupProcess.run(new ResponseListener() {
			
			@Override
			public void onResponse(String requestId) {
				// 不关注返回值
				completeFlag.second = true;
				if(completeFlag.first && completeFlag.second) {
					PersistorManager.self().clearListMessagesUnreadCount(Cache.self().getMyUid());
					fireStatusEvent(listener, OperErrorCode.Success);
				}
			}
		});
	}

	/**
	 * 开始聊天
	 * 
	 * @param chatId
	 * @return
	 */
	public ChatHandler startChat(ChatId chatId) {
		ChatHandler handler = new ChatHandler(chatId);
		handler.startChat();
		return handler;
	}
	
	private void getChatMessageOuser(final List<ChatMessage> messages, final EventListener listener) {
		List<String> uids = new ArrayList<String>();
		for(ChatMessage message : messages) {
			String uid = message.getOuser().getUid();
			if(!uids.contains(uid)) {
				uids.add(uid);
			}
		}
		LogicFactory.self().getProfile().getSimples(uids, new EventListener() {

			@Override
			public void onEvent(EventId id, EventArgs args) {
				OusersEventArgs ousersArgs = (OusersEventArgs) args;
				OperErrorCode errCode = ousersArgs.getErrCode();
				Ousers ousers = ousersArgs.getOusers();

				ChatMessagesEventArgs messageArgs = null;
				if (errCode == OperErrorCode.Success) {
					for (ChatMessage message : messages) {
						for (Ouser ouser : ousers) {
							if (message.getOuser().isSame(ouser)) {
								message.setOuser(ouser);
								break;
							}
						}
					}
					messageArgs = new ChatMessagesEventArgs(messages);
				} else {
					messageArgs = new ChatMessagesEventArgs(errCode);
				}
				fireEvent(listener, messageArgs);
			}
		});
	}

	/**
	 * 消息处理器
	 * 
	 * @author hanlixin
	 * @remark 开启聊天界面使用
	 */
	public class ChatHandler {

		/** 消息id */
		private ChatId mChatId = null;

		/** 最后一条消息 */
		private ChatMessage mLastChatMessage = null;

		/** 服务器消息推送器 */
		private ChatMessagePusher mChatMessagePusher = null;

		private String mCacheAppointContent = "";

		private EventListener mListener = new EventListener() {

			@Override
			public void onEvent(EventId id, EventArgs args) {
				ChatMessagesEventArgs chatArgs = (ChatMessagesEventArgs) args;
				List<ChatMessage> messages = chatArgs.getChatMessages();
				if (!messages.isEmpty()) {
					mLastChatMessage = messages.get(messages.size() - 1);
				}
			}
		};

		public ChatHandler(ChatId chatId) {
			mChatId = chatId;
		}

		public void startChat() {

			// 设置消息推送监听器
			mChatMessagePusher = new ChatMessagePusher(mChatId);

			// 监听消息推送
			PusherManager.self().startListen(mChatMessagePusher, "chat", Const.MessageChatInterval,
					true);

			// 关注推送事件，获取最后一条消息
			EventCenter.self().addListener(EventId.ePushMessage, mListener);
		}

		public void stopChat() {

			// 停止监听消息推送
			PusherManager.self().stopListen("chat");
			mChatMessagePusher = null;

			// 设置列表消息中的最后一条消息
			if (mLastChatMessage != null) {
				saveLastListMessage();
			} else {
				PersistorManager.self().clearListMessageUnreadCount(Cache.self().getMyUid(),
						mChatId);
			}

			// 取消关注推送事件
			EventCenter.self().removeListener(mListener);
		}
		
		public void setCacheAppointContent(String value) {
			mCacheAppointContent = value;
		}

		public void getAllMessage(final EventListener listener) {
			final List<ChatMessage> messages =
				PersistorManager.self().getChatMessages(Cache.self().getMyUid(), mChatId);
			new ChatLogic().getChatMessageOuser(messages, listener);
		}

		public void removeMessage(List<ChatMessage> messages) {
			// TODO 查找ChatMessage，如果没有了的话删除ListMessage的消息
			PersistorManager.self().removeChatMessage(messages);
		}

		public void clearMessage() {
			// TODO 删除ListMessage的消息
			PersistorManager.self().clearChatMessage(Cache.self().getMyUid(), mChatId);
		}

		public ChatMessage sendText(String text) {
			ChatMessage message = new ChatMessage();
			message.setContent(text);
			message.setType(ChatMessage.Type.Text);
			send(message);
			Stat.onEvent(mChatId.isSingle() ? StatId.SingleChatText : StatId.GroupChatText);
			return message;
		}

		public ChatMessage sendImage(Bitmap image) {
			ChatMessage message = new ChatMessage();
			message.setContent(ImageUtil.toBase64(image));
			message.setType(ChatMessage.Type.Image);
			send(message);
			Stat.onEvent(mChatId.isSingle() ? StatId.SingleChatImage : StatId.GroupChatImage);
			return message;
		}

		public ChatMessage sendAudio(String fileName) {
			ChatMessage message = new ChatMessage();
			message.setContent(FileUtil.toBase64(fileName));
			message.setType(ChatMessage.Type.Audio);
			send(message);
			Stat.onEvent(mChatId.isSingle() ? StatId.SingleChatVoice : StatId.GroupChatVoice);
			return message;
		}

		public ChatMessage sendLocation(Location location, String place) {
			ChatMessage message = new ChatMessage();
			message.getLocation().setLocation(location);
			message.getLocation().setPlace(place);
			message.setType(ChatMessage.Type.Location);
			send(message);
			Stat.onEvent(mChatId.isSingle() ? StatId.SingleChatLocation : StatId.GroupChatLocation);
			return message;
		}

		private void send(final ChatMessage message) {
			message.setChatId(mChatId);
			message.setTime(System.currentTimeMillis());
			message.getOuser().setUid(Cache.self().getMyUid());
			message.setSend(true);

			PersistorManager.self().appendMessage(Cache.self().getMyUid(), message);

			// 设置最后一条消息
			mLastChatMessage = message;

			SendMessageBaseProcess process = null;
			if (mChatId.isSingle()) {
				process = new SendSingleMessageProcess();
			} else {
				process = new SendGroupMessageProcess();
			}
			process.setMyUid(Cache.self().getMyUid());
			process.setMessage(message);
			final SendMessageBaseProcess fProcess = process;

			fProcess.run(new ResponseListener() {

				@Override
				public void onResponse(String requestId) {
					OperErrorCode errCode = Util.convertFromStatus(fProcess.getStatus());
					ChatMessageEventArgs args = null;
					if (errCode == OperErrorCode.Success) {
						args = new ChatMessageEventArgs(message);
					} else {
						args = new ChatMessageEventArgs(errCode);
					}
					fireEvent(EventId.eSendMessage, args);
				}
			});
		}

		private void saveLastListMessage() {
			ListMessage listMessage = new ListMessage();
			listMessage.setChatId(mChatId);
			listMessage.setContent(convertMessageContent(mLastChatMessage));
			listMessage.setCount(0);
			listMessage.setTime((int) (mLastChatMessage.getTime() / 1000));

			if (!listMessage.isSingle()) {
				// 设置友约内容
				ListMessage message = PersistorManager.self().getListMessage(
						Cache.self().getMyUid(), mChatId);
				if (message == null) {
					// 还没有进行保存
					if (StringUtil.isEmpty(mCacheAppointContent)) {
						AppointId appointId = mLastChatMessage.getChatId().getGroupId();
						LogicFactory.self().getAppointInfo().get(appointId, new EventListener() {

							@Override
							public void onEvent(EventId id, EventArgs args) {
								AppointEventArgs appointArgs = (AppointEventArgs) args;
								if (appointArgs.getErrCode() == OperErrorCode.Success) {
									if (!appointArgs.getAppoint().isDeleted()) {
										PersistorManager.self()
												.setListMessageLastMessageAppointContent(
														Cache.self().getMyUid(), mChatId,
														appointArgs.getAppoint().getContent());
										fireEvent(EventId.eMessageListChanged, new EventArgs());
									}
								}
							}
						});
					} else {
						listMessage.getAppoint().setContent(mCacheAppointContent);
					}
				} else {
					// 已经保存过，不必设置了，数据库不会更新友约名
				}

			}

			// 最后说话人
			String saveUid = "";
			if (listMessage.isSingle()) {
				saveUid = mChatId.getSingleId();
			} else {
				saveUid = mLastChatMessage.getOuser().getUid();
			}
			listMessage.getOuser().setUid(saveUid);

			// 保存
			PersistorManager.self().setListMessageLastMessage(Cache.self().getMyUid(), listMessage);
			fireEvent(EventId.eMessageListChanged, new EventArgs());
		}

		private String convertMessageContent(ChatMessage message) {
			switch (message.getType()) {
			case Text:
				return message.getContent();
			case Image:
				return "[图片信息]";
			case Location:
				return "[位置信息]";
			case Audio:
				return "[语音信息]";
			case Invite:
				return "[友约邀请]";
			default:
				return message.getContent();
			}
		}
	}

	private static class ChatMessagePusher implements Pusher {

		private ChatId mChatId = null;

		public ChatMessagePusher(ChatId value) {
			mChatId = value;
		}

		@Override
		public void onRequest() {

			GetMessageBaseProcess process = null;
			if (mChatId.isSingle()) {
				GetSingleMessageProcess sProcess = new GetSingleMessageProcess();
				sProcess.setTargetUid(mChatId.getSingleId());
				process = sProcess;
			} else {
				GetGroupMessageProcess gProcess = new GetGroupMessageProcess();
				gProcess.setAppointId(mChatId.getGroupId());
				process = gProcess;
			}
			process.setMyUid(Cache.self().getMyUid());
			final GetMessageBaseProcess fProcess = process;

			fProcess.run(new ResponseListener() {

				@Override
				public void onResponse(String requestId) {
					OperErrorCode errCode = Util.convertFromStatus(fProcess.getStatus());
					if (errCode != OperErrorCode.Success) {
						return;
					}
					List<ChatMessage> messages = fProcess.getResult();
					if (messages.isEmpty()) {
						return;
					}
					PersistorManager.self().appendMessages(Cache.self().getMyUid(), messages);
					new ChatLogic().getChatMessageOuser(messages, new EventListener() {
						
						@Override
						public void onEvent(EventId id, EventArgs args) {
							EventCenter.self().fireEvent(EventId.ePushMessage, args);
						}
					});
				}
			});
		}
	}
}
