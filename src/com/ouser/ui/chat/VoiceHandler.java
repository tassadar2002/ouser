package com.ouser.ui.chat;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import com.ouser.R;
import com.ouser.ui.base.BaseActivity;
import com.ouser.ui.helper.AudioHandler;
import com.ouser.ui.widget.VoiceView;

@SuppressLint("HandlerLeak")
class VoiceHandler {

	// 当前状态
	private enum RecordState {
		None,
		RecordOk,
		RecordCancel,
	}
	
	private InputLayout mLayout = null;
	
	private RecordState mRecordState = RecordState.None;
	
	private View mHintLayout = null;
	private VoiceView mImageVolume = null;
	private View mImageCancel = null;
	private TextView mTxt = null;
	
	// 显示音量
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			double amp = AudioHandler.getRecorder().getAmplitude();
			mImageVolume.setValue((int)amp);
			sendEmptyMessageDelayed(0, 100);
		}
	};
	
	public void setInputLayout(InputLayout value) {
		mLayout = value;
	}
	
	public BaseActivity getActivity() {
		return mLayout.getActivity();
	}
	
	public void onCreate() {
		getActivity().findViewById(R.id.txt_record).setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				//按下语音录制按钮时返回false执行父类OnTouch
				return false;
			}
		});
		mHintLayout = getActivity().findViewById(R.id.layout_voice_hint);
		mImageVolume = (VoiceView)getActivity().findViewById(R.id.image_voice_volume);
		mImageCancel = getActivity().findViewById(R.id.image_voice_cancel);
		mTxt = (TextView)getActivity().findViewById(R.id.txt_voice_tip);
	}

	public void handleTouch(MotionEvent event) {

		Rect validRect = getValidRect();
		
		if(event.getAction() ==  MotionEvent.ACTION_DOWN) {
			if(mRecordState == RecordState.None) {
				mRecordState = RecordState.RecordOk;
				AudioHandler.getRecorder().start();
				mHandler.sendEmptyMessage(0);
				mHintLayout.setVisibility(View.VISIBLE);
				mImageVolume.setVisibility(View.VISIBLE);
				mTxt.setText("手指上划，取消发送");
			}
		}
		if(event.getAction() == MotionEvent.ACTION_UP) {
			if(mRecordState == RecordState.RecordOk) {
				AudioHandler.getRecorder().stop();
				mHandler.removeMessages(0);
				if(AudioHandler.getRecorder().isValid()) {
					mLayout.sendAudio(AudioHandler.getRecorder().getFileName());
				}
			}
			if(mRecordState == RecordState.RecordCancel) {
				AudioHandler.getRecorder().cancel();
				mHandler.removeMessages(0);
			}
			mRecordState = RecordState.None;
			mImageCancel.setVisibility(View.GONE);
			mImageVolume.setVisibility(View.GONE);
			mHintLayout.setVisibility(View.GONE);
		}
		if(event.getAction() == MotionEvent.ACTION_MOVE) {
			if(mRecordState != RecordState.None) {
				if(inRect(event.getX(), event.getY(), validRect)) {
					mRecordState = RecordState.RecordOk;
					mImageVolume.setVisibility(View.VISIBLE);
					mImageCancel.setVisibility(View.GONE);
					mTxt.setText("手指上划，取消发送");
				} else {
					mRecordState = RecordState.RecordCancel;
					mImageVolume.setVisibility(View.GONE);
					mImageCancel.setVisibility(View.VISIBLE);
					mTxt.setText("手指松开，取消发送");
				}
			}
		}
	}
	
	private Rect getValidRect() {
		View txtRecord = getActivity().findViewById(R.id.txt_record);
		int[] location = new int[2];
		txtRecord.getLocationInWindow(location);
		int left = location[0];
		int top = location[1];
		int right = left + txtRecord.getWidth();
		int bottom = top + txtRecord.getHeight();		
		return new Rect(left, top, right, bottom);
	}
	
	private boolean inRect(float x, float y, Rect rect) {
		return x > rect.left && x < rect.right && y > rect.top && y < rect.bottom;
	}
}
