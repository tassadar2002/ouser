package com.ouser.ui.chat.adapter;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ouser.R;
import com.ouser.ui.helper.AudioHandler;
import com.ouser.util.Const;

@SuppressLint("HandlerLeak")
class ItemVoiceViewHandler extends ItemMessageHandler {

	private LayoutInflater mInflater = null;

	public ItemVoiceViewHandler() {
		mInflater = LayoutInflater.from(Const.Application);
	}

	@Override
	public void createView(ChatItem item, ViewHolder holder, ViewGroup parent) {
		View v = mInflater.inflate(R.layout.layout_chat_item_voice, parent);
		holder.layoutRoot = parent;
		holder.txtContent = (TextView) v.findViewById(R.id.txt_hint);
		holder.imageContent = (ImageView) v.findViewById(R.id.btn_play_stop);
		holder.progressVoice = (ProgressBar) v.findViewById(R.id.progress_voice);
	}

	@Override
	public int getItemViewType(ChatItem item) {
		return item.getMessage().isSend() ? ViewType.AudioSend : ViewType.AudioRecv;
	}

	@Override
	public void setContent(ChatItem item, ViewHolder holder) {
		super.setContent(item, holder);
		
		holder.layoutRoot.setOnClickListener(new OnClickListener(item));
		if (item.isPlaying()) {
			holder.txtContent.setText("[正在播放 点击停止]");
			holder.progressVoice.setVisibility(View.VISIBLE);
			if (item.getAudioLength() != 0) {
				holder.progressVoice.setMax(item.getAudioLength());
			}
			holder.progressVoice.setProgress(item.getAudioCurrentPos());
		} else {
			holder.txtContent.setText("[语音信息 点击播放]");
			holder.progressVoice.setVisibility(View.GONE);
		}
	}

	@Override
	public void setEditing(boolean value) {
		super.setEditing(value);
		if(value) {
			for(ChatItem item : getOnActionListener().getItems().getItems()) {
				if(item.isPlaying()) {
					AudioHandler.getPlayer().stop();
				}
			}
		}
	}

	private Handler mVoicePlayHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			ChatItem item = (ChatItem) msg.obj;
			if (item.isPlaying()) {
				if (item.getAudioLength() != 0) {
					item.setAudioCurrentPos(AudioHandler.getPlayer().getCurrentPosition());
				}
				sendMessageDelayed(obtainMessage(0, item), 200);
			}
			getOnActionListener().notifyDataSetChanged();
		}
	};

	private class OnClickListener implements View.OnClickListener {
		private ChatItem mItem = null;

		public OnClickListener(ChatItem item) {
			mItem = item;
		}

		@Override
		public void onClick(View v) {
			if(isEditing()) {
				onClickItem(mItem);
			} else {
				if (mItem.isPlaying()) {
					AudioHandler.getPlayer().stop();
				} else {
					for(ChatItem item : getOnActionListener().getItems().getItems()) {
						if(item.isPlaying()) {
							AudioHandler.getPlayer().stop();
							break;
						}
					}
					mItem.setPlaying(true);
					PlayerListener listener = new PlayerListener(mItem);
					AudioHandler.getPlayer().start(mItem.getMessage().getContent(), listener);
					mVoicePlayHandler.sendMessage(mVoicePlayHandler.obtainMessage(0, mItem));
				}
			}
		}
	}

	private static class PlayerListener implements AudioHandler.Player.OnActionListener {

		private ChatItem mItem = null;

		public PlayerListener(ChatItem value) {
			mItem = value;
		}

		@Override
		public void onCompletion() {
			mItem.setPlaying(false);
		}

		@Override
		public void onDuration(int millsecond) {
			mItem.setAudioLength(millsecond);
		}
	}
}
