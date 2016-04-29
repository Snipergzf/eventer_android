
package com.eventer.app.other;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.eventer.app.Constant;
import com.eventer.app.R;
import com.eventer.app.task.LoadImage;
import com.eventer.app.task.LoadImage.ImageDownloadedCallBack;
import com.eventer.app.view.CircleProgressBar;
import com.eventer.app.view.photoview.PhotoView;
import com.eventer.app.view.swipeback.SwipeBackActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * 下载显示大图
 *
 */
public class ShowBigImage extends SwipeBackActivity {
	private Context context;
	PhotoView image;
	LoadImage loadAvatar;
	private CircleProgressBar progressBar;
	String avatar;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_show_big_image);
		super.onCreate(savedInstanceState);
		context=this;
		image = (PhotoView) findViewById(R.id.image);
		progressBar=(CircleProgressBar)findViewById(R.id.progress);
		progressBar.setColorSchemeResources(android.R.color.holo_orange_light);

		avatar = getIntent().getExtras().getString("avatar");
		loadAvatar=new LoadImage(ShowBigImage.this, Constant.IMAGE_PATH);
		//本地存在，直接显示本地的图片
		if (avatar != null && !avatar.equals("")&&!avatar.equals("default")) {
			Bitmap bitmap = loadAvatar.loadImage(image, avatar,
					new ImageDownloadedCallBack() {
						@Override
						public void onImageDownloaded(ImageView imageView, Bitmap bitmap, int status) {
							Log.e("1", status + "");
							if(status==-1&&bitmap!=null){
								imageView.setImageBitmap(bitmap);
								progressBar.setVisibility(View.GONE);
							}else{
								Toast.makeText(context,"图片获取失败！",Toast.LENGTH_LONG).show();
								finish();
							}
						}

					});
			if (bitmap != null){
				image.setImageBitmap(bitmap);
				progressBar.setVisibility(View.GONE);
			}

		}else if(avatar != null && avatar.equals("default")){
			image.setBackgroundResource(R.drawable.default_avatar);
			progressBar.setVisibility(View.GONE);
		}

		image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}


	@Override
	public void onBackPressed() {
		super.onBackPressed();
		setResult(RESULT_OK);
		finish();
	}


	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

}
