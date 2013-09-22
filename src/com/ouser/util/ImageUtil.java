package com.ouser.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

public class ImageUtil {

	/** 
	 * 将Bitmap转换成字符串
	 */
	public static String toBase64(Bitmap bitmap) {  
		String result = null;
		ByteArrayOutputStream baos = null;
		try {
			if (bitmap != null) {
				baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

				baos.flush();
				baos.close();

				byte[] bitmapBytes = baos.toByteArray();
				result = Base64.encode(bitmapBytes);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null) {
					baos.flush();
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public static Bitmap fromBase64(String text) {
		byte[] bytes = Base64.decode(text);
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
	}
	
	public static Bitmap drawableToBitmap(Drawable drawable) {
		 Bitmap bitmap = Bitmap.createBitmap(
                 drawable.getIntrinsicWidth(),
                 drawable.getIntrinsicHeight(),
                 drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                 : Bitmap.Config.RGB_565);

		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}
}
