package com.ouser.ui.chat;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.ouser.R;
import com.ouser.ui.base.BaseActivity;
import com.ouser.ui.component.HeadBar;

public class ImageActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.actvy_image);
		
		HeadBar headBar = new HeadBar();
		headBar.onCreate(findViewById(R.id.layout_head_bar), this);
		
		Bitmap bitmap = (Bitmap)getIntent().getParcelableExtra("data");
		ImageView image = (ImageView)findViewById(R.id.image_image);
		image.setImageBitmap(bitmap);
	}
}
