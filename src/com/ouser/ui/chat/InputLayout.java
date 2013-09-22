package com.ouser.ui.chat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Parcelable;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.ouser.R;
import com.ouser.logic.LogicFactory;
import com.ouser.module.ChatMessage;
import com.ouser.module.Location;
import com.ouser.ui.component.ResizeLayout;
import com.ouser.ui.helper.Alert;
import com.ouser.ui.helper.PhotoFetcher;
import com.ouser.ui.map.LocationSelectActivity;
import com.ouser.ui.widget.EmotionEditText;
import com.ouser.util.Const;
import com.ouser.util.StringUtil;
import com.ouser.util.SystemUtil;

class InputLayout extends ChatBaseLayout {

	// 标志位
	private boolean mIsKeyboardShown = false;
	private boolean mIsEmotionShown = false;
	private boolean mIsRecordShown = false;
	private boolean mIsAttachmentShown = false;

	// 辅助变量
	private PhotoFetcher mPhotoFetcher = new PhotoFetcher();
	private VoiceHandler mVoiceHandler = new VoiceHandler();

	// 控件
	private EmotionEditText mEditText = null;
	private View mLayoutEmotion = null;
	private View mLayoutAttachment = null;
	private View mTxtRecord = null;

	// 表情
	private EmotionFragment mEmotionFragment = null;
	private EmotionFragment.OnActionListener mEmotionListener = new EmotionFragment.OnActionListener() {

		@Override
		public void onSend() {
			sendText();
		}

		@Override
		public void onBack() {
			mEditText.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
			mEditText.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
		}

		@Override
		public void onAppend(Bitmap image) {
			mEditText.appendImage(LogicFactory.self().getEmotion().getText(image));
		}
	};

	// 附件
	private View.OnClickListener mAttachmentListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_location:
				Intent intent = new Intent(getActivity(), LocationSelectActivity.class);
				getActivity().startActivityForResult(intent, Const.RequestCode.Location);
				break;
			case R.id.btn_album:
				mPhotoFetcher.getFromAlbum();
				break;
			case R.id.btn_camera:
				mPhotoFetcher.getFromCamera();
				break;
			case R.id.btn_emotion:
				if (mEmotionFragment == null) {
					mEmotionFragment = new EmotionFragment();
					mEmotionFragment.setActionListener(mEmotionListener);
					getActivity().getSupportFragmentManager().beginTransaction()
							.replace(R.id.layout_emotion, mEmotionFragment).commit();
				}
				showEmotion(true);
				break;
			}
			showAttachment(false);
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();

		// 输入法显示隐藏监控
		((ResizeLayout) getActivity().findViewById(R.id.layout_root))
				.setListener(new ResizeLayout.Listener() {

					@Override
					public void onInputMethodVisibleChanged(boolean show) {
						mIsKeyboardShown = show;
					}
				});

		// 文本输入
		mEditText = (EmotionEditText) getActivity().findViewById(R.id.edit_content);
		mEditText.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				switch (keyCode) {
				case KeyEvent.KEYCODE_ENTER:
					if (event.getAction() == KeyEvent.ACTION_DOWN) {
						sendText();
					}
					return true;
				}
				return false;
			}
		});
		mEditText.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					int inType = mEditText.getInputType();
					mEditText.setInputType(InputType.TYPE_NULL);
					mEditText.onTouchEvent(event);
					mEditText.setInputType(inType);
					mEditText.setSelection(mEditText.getText().length());

					showKeyboard(true);
				}
				return true;
			}
		});

		// 录音键盘切换按钮
		View btnAudio = getActivity().findViewById(R.id.btn_audio);
		btnAudio.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(mIsRecordShown) {
					showRecord(false);
					showKeyboard(true);
					setVoiceButton(true);
				} else {
					showRecord(true);
					setVoiceButton(false);
				}
			}
		});

		// 录音按钮
		mTxtRecord = getActivity().findViewById(R.id.txt_record);

		// 附件按钮
		View btnAttachment = getActivity().findViewById(R.id.btn_attachment);
		btnAttachment.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showAttachment(!mIsAttachmentShown);
			}
		});

		// 表情面板
		mLayoutEmotion = getActivity().findViewById(R.id.layout_emotion);

		// 发送按钮
		getActivity().findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				sendText();
			}
		});

		// 消息列表，点击隐藏
		getActivity().findViewById(R.id.list_chat).setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				showKeyboard(false);
				showEmotion(false);
				showAttachment(false);
				return false;
			}
		});
		
		// 附件面板
		mLayoutAttachment = getActivity().findViewById(R.id.layout_attachment);
		mLayoutAttachment.getBackground().setAlpha(200);
		getActivity().findViewById(R.id.btn_location).setOnClickListener(mAttachmentListener);
		getActivity().findViewById(R.id.btn_album).setOnClickListener(mAttachmentListener);
		getActivity().findViewById(R.id.btn_camera).setOnClickListener(mAttachmentListener);
		getActivity().findViewById(R.id.btn_emotion).setOnClickListener(mAttachmentListener);

		// 辅助变量
		mVoiceHandler.setInputLayout(this);
		mVoiceHandler.onCreate();
		
		mPhotoFetcher.setActivity(getActivity());
		mPhotoFetcher.setOnActionListener(new PhotoFetcher.OnActionListener() {

			@Override
			public void onComplete(Parcelable data) {				
				sendImage((Bitmap)data);
			}

			@Override
			public void onCancel() {
			}
		});
	}

	@Override
	public void onPause() {
		showKeyboard(false);
		showEmotion(false);
		showAttachment(false);
		super.onPause();
	}

	@Override
	public void onActivityResult(int type, int result, Intent intent) {
		switch (type) {
		case Const.RequestCode.Location:
			if (result == Activity.RESULT_OK) {
				Location location = new Location();
				location.fromBundle(intent.getBundleExtra("location"));
				String place = intent.getStringExtra("place");
				sendLocation(location, place);
			}
			break;
		default:
			mPhotoFetcher.handleActivityResult(type, result, intent);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(mIsRecordShown) {
			mVoiceHandler.handleTouch(event);
		}
		return super.onTouchEvent(event);
	}
	
	public void show(boolean show) {
		View layoutInput = getActivity().findViewById(R.id.layout_chat_input);
		if(show) {
			layoutInput.setVisibility(View.VISIBLE);
		} else {
			showAttachment(false);
			showEmotion(false);
			showKeyboard(false);
			layoutInput.setVisibility(View.GONE);
		}
	}
	
	private void showKeyboard(boolean show) {
		if (mIsKeyboardShown == show) {
			return;
		}
		if (show) {
			showAttachment(false);
			showEmotion(false);
			showRecord(false);
			setVoiceButton(true);
			mEditText.requestFocus();
			SystemUtil.showKeyboard(mEditText);
		} else {
			SystemUtil.hideKeyboard(mEditText);
		}
	}

	private void showEmotion(boolean show) {
		if (mIsEmotionShown == show) {
			return;
		}
		if (show) {
			showAttachment(false);
			showKeyboard(false);
			showRecord(false);
			if (mIsKeyboardShown) {
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						mLayoutEmotion.setVisibility(View.VISIBLE);
						mEditText.requestFocus();
					}
				}, 200);
			} else {
				mLayoutEmotion.setVisibility(View.VISIBLE);
				mEditText.requestFocus();
			}
		} else {
			mLayoutEmotion.setVisibility(View.GONE);
		}
		mIsEmotionShown = show;
	}

	private void showRecord(boolean show) {
		if (mIsRecordShown == show) {
			return;
		}
		showAttachment(false);
		showEmotion(false);
		if (show) {
			showKeyboard(false);
			mEditText.setVisibility(View.GONE);
			mTxtRecord.setVisibility(View.VISIBLE);
		} else {
			mEditText.setVisibility(View.VISIBLE);
			mTxtRecord.setVisibility(View.GONE);
		}
		mIsRecordShown = show;
	}

	private void showAttachment(boolean show) {
		if (mIsAttachmentShown == show) {
			return;
		}
		if (show) {
			mLayoutAttachment.setVisibility(View.VISIBLE);
		} else {
			mLayoutAttachment.setVisibility(View.GONE);
		}
		mIsAttachmentShown = show;
	}
	
	private void setVoiceButton(boolean canVoice) {
		((ImageView)getActivity().findViewById(R.id.btn_audio))
			.setImageResource(canVoice ? R.drawable.chat_input_voice : R.drawable.chat_input_keyboard);
	}

	private void sendLocation(Location location, String place) {
		ChatMessage message = getChatHandler().sendLocation(location, place);
		getLayoutFactory().getMessage().onSend(message);
	}
	
	private void sendImage(Bitmap image) {
		ChatMessage message = getChatHandler().sendImage(image);
		getLayoutFactory().getMessage().onSend(message);
	}
	
	void sendAudio(String fileName) {
		ChatMessage message = getChatHandler().sendAudio(fileName);
		getLayoutFactory().getMessage().onSend(message);
	}

	private void sendText() {
		String text = mEditText.getText().toString();
		if(StringUtil.isEmpty(text)) {
			Alert.Toast("不能发送空消息");
			return;
		}
		ChatMessage message = getChatHandler().sendText(text);
		getLayoutFactory().getMessage().onSend(message);
		mEditText.setText("");
	}
}
