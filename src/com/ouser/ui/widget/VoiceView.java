package com.ouser.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.ouser.R;

public class VoiceView extends ImageView {

	private Bitmap mMinImage = null;
	private Bitmap mMaxImage = null;

	private int mImageHeight = 0;
	private int mImageWidth = 0;
	
	private int mValue = 0;
	private int mMaxValue = 20;

	public VoiceView(Context paramContext) {
		super(paramContext);
		init();
	}

	public VoiceView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		init();
	}

	private void init() {
		mMinImage = BitmapFactory.decodeResource(getResources(), R.drawable.chat_min_voice);
		mMaxImage = BitmapFactory.decodeResource(getResources(), R.drawable.chat_max_voice);

		mImageHeight = mMaxImage.getHeight();
		mImageWidth = mMaxImage.getWidth();

		setImageBitmap(mMinImage);
		setScaleType(ImageView.ScaleType.FIT_XY);
	}
	
	public void setValue(int value) {
		mValue = value;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		int topHeight = mValue * mImageHeight / mMaxValue;
		if(topHeight != 0) {
			Bitmap image = Bitmap.createBitmap(mImageWidth, topHeight, Bitmap.Config.ARGB_8888);
			Rect rectDest = new Rect(0, 0, mImageWidth, topHeight);
			Rect rectSrc = new Rect(0, mImageHeight - topHeight, mImageWidth, mImageHeight);
			new Canvas(image).drawBitmap(mMaxImage, rectSrc, rectDest, null);
			canvas.drawBitmap(image, 0, mImageHeight - topHeight, null);
		}
	}
}
