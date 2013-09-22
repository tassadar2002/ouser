package com.ouser.ui.helper;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;

import com.ouser.logger.Logger;
import com.ouser.util.Const;
import com.ouser.util.FileUtil;
import com.ouser.util.StringUtil;

public class AudioHandler {

	private static final Logger logger = new Logger("audio");

	private static final String AudioPath = Const.WorkDir + "audio/";
	private static final String AudioName = "ouser.audio";

	private static Recorder mRecorder = new Recorder();
	private static Player mPlayer = new Player();

	public static Recorder getRecorder() {
		return mRecorder;
	}

	public static Player getPlayer() {
		return mPlayer;
	}

	// 是否为扬声器模式
	public static boolean isSpeakerPhone() {
		AudioManager audioManager = (AudioManager) Const.Application
				.getSystemService(Context.AUDIO_SERVICE);
		return audioManager.isSpeakerphoneOn();
	}

	public static void setSpeakerPhone(boolean value) {
		AudioManager audioManager = (AudioManager) Const.Application
				.getSystemService(Context.AUDIO_SERVICE);
		if (value) {
			audioManager.setMode(AudioManager.MODE_IN_CALL);
			audioManager.setSpeakerphoneOn(true);
		} else {
			audioManager.setSpeakerphoneOn(false);
			audioManager.setMode(AudioManager.MODE_NORMAL);
		}
		logger.d("set speaker phone " + value);
	}

	public static class Recorder {
		private static final double EMA_FILTER = 0.6;

		private MediaRecorder mRecorder = null;
		private double mEMA = 0.0;

		private long mStartTick = Const.DefaultValue.Time;
		private long mStopTick = Const.DefaultValue.Time;

		private String mCurrentTmpFile = "";

		private Recorder() {
		}

		public String getFileName() {
			return mCurrentTmpFile;
		}

		public boolean isValid() {
			return mStopTick - mStartTick > Const.ChatVoiceMinDuring;
		}

		public boolean start() {
			mCurrentTmpFile = checkFile();
			if (StringUtil.isEmpty(mCurrentTmpFile)) {
				return false;
			}

			mRecorder = new MediaRecorder();
			mRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
			mRecorder.setOutputFile(mCurrentTmpFile);
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

			// on prepare listener ，再显示，否则会录制一些东西
			try {
				mRecorder.prepare();
			} catch (IOException e) {
				logger.e("prepare() failed");
				return false;
			}

			mRecorder.start();
			mStartTick = Calendar.getInstance().getTimeInMillis();
			return true;
		}

		public void cancel() {
			stop();
			File f = new File(mCurrentTmpFile);
			if (f.exists()) {
				f.delete();
			}
		}

		public void stop() {
			mStopTick = Calendar.getInstance().getTimeInMillis();
			mRecorder.stop();
			mRecorder.release();
			mRecorder = null;
			mEMA = 0.0;
		}

		public double getAmplitude() {
			if (mRecorder != null)
				return (mRecorder.getMaxAmplitude() / 1500.0);
			else
				return 0;

		}

		public double getAmplitudeEMA() {
			double amp = getAmplitude();
			mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
			return mEMA;
		}

		private String checkFile() {
			if (!new File(AudioPath).exists()) {
				new File(AudioPath).mkdirs();
			}

			String fileName = AudioPath + AudioName;
			if (new File(fileName).exists()) {
				new File(fileName).delete();
			}
			return fileName;
		}
	}

	public static class Player {
		private MediaPlayer mPlayer = null;

		public interface OnActionListener {
			public void onCompletion();

			public void onDuration(int millsecond);
		}

		private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				stop();
			}
		};
		private MediaPlayer.OnPreparedListener mPrepareListener = new MediaPlayer.OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				if (mActionListener != null) {
					mActionListener.onDuration(mp.getDuration());
				}
			}
		};
		private OnActionListener mActionListener = null;

		public void start(String content, OnActionListener listener) {
			String fileName = checkFile();
			if (StringUtil.isEmpty(fileName)) {
				return;
			}

			mActionListener = listener;
			FileUtil.fromBase64(content, fileName);
			startFile(fileName);
		}

		public void startFile(String fileName) {
			try {
				mPlayer = new MediaPlayer();
				mPlayer.setOnPreparedListener(mPrepareListener);
				mPlayer.setOnCompletionListener(mCompletionListener);
				mPlayer.setDataSource(fileName);
				mPlayer.prepare();
				mPlayer.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void stop() {
			mPlayer.stop();
			mPlayer.release();
			mPlayer = null;

			if (mActionListener != null) {
				mActionListener.onCompletion();
			}
		}

		public int getCurrentPosition() {
			if (mPlayer != null) {
				return mPlayer.getCurrentPosition();
			}
			return 0;
		}

		private String checkFile() {
			if (!new File(AudioPath).exists()) {
				new File(AudioPath).mkdirs();
			}

			String fileName = AudioPath + AudioName;
			if (new File(fileName).exists()) {
				new File(fileName).delete();
			}
			return fileName;
		}
	}
}
