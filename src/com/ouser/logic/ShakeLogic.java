package com.ouser.logic;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;

import com.ouser.stat.Stat;
import com.ouser.stat.StatId;
import com.ouser.util.Const;

public class ShakeLogic extends BaseLogic implements SensorEventListener {
	
	static class Factory implements BaseLogic.Factory {

		@Override
		public BaseLogic create() {
			return new ShakeLogic();
		}
	}
	
	ShakeLogic() {
		mSensorManager = (SensorManager) Const.Application.getSystemService(Context.SENSOR_SERVICE);
		mVibrator = (Vibrator) Const.Application.getSystemService(Context.VIBRATOR_SERVICE);
	}
	
	public interface OnActionListener {
		void onShake();
	}

	// 速度阈值，当摇晃速度达到这值后产生作用
	private static final int SPEED_SHRESHOLD = 3000;

	// 两次检测的时间间隔
	private static final int UPTATE_INTERVAL_TIME = 70;

	// Sensor管理器
	private SensorManager mSensorManager = null;

	// 震动
	private Vibrator mVibrator = null;

	// 手机上一个位置时重力感应坐标
	private float lastX = 0;
	private float lastY = 0;
	private float lastZ = 0;

	// 上次检测时间
	private long lastUpdateTime = 0;
	
	private OnActionListener mListener = null;

	public void start(OnActionListener listener) {
		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_GAME);
		mListener = listener;
	}
	
	public void stop() {
		mSensorManager.unregisterListener(this);
		mListener = null;
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// 现在检测时间
		long currentUpdateTime = System.currentTimeMillis();
		// 两次检测的时间间隔
		long timeInterval = currentUpdateTime - lastUpdateTime;
		// 判断是否达到了检测时间间隔
		if (timeInterval < UPTATE_INTERVAL_TIME)
			return;
		// 现在的时间变成last时间
		lastUpdateTime = currentUpdateTime;

		// 获得x,y,z坐标
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];

		// 获得x,y,z的变化值
		float deltaX = x - lastX;
		float deltaY = y - lastY;
		float deltaZ = z - lastZ;

		// 将现在的坐标变成last坐标
		lastX = x;
		lastY = y;
		lastZ = z;

		double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ)
				/ timeInterval * 10000;
		// 达到速度阀值，发出提示
		if (speed >= SPEED_SHRESHOLD) {
			mVibrator.vibrate(500);
			
			if(mListener != null) {
				mListener.onShake();
				Stat.onEvent(StatId.Shake);
			}
		}
	}
}
