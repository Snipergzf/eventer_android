
package com.eventer.app.other;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.eventer.app.Constant;
import com.eventer.app.R;
import com.eventer.app.task.LoadBigAvatar;
import com.eventer.app.task.LoadBigAvatar.ImageDownloadedCallBack;
import com.eventer.app.ui.base.BaseActivity;
import com.eventer.app.util.LocalUserInfo;
import com.eventer.app.widget.photoview.PhotoView;
import com.umeng.analytics.MobclickAgent;

/**
 * 下载显示大图
 * 
 */
public class ShowBigImage extends BaseActivity {

	private PhotoView image;
	private boolean isDownloaded;
	private LoadBigAvatar loadAvatar;
	private Context context;
	private String avatar;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_show_big_image);
		super.onCreate(savedInstanceState);
        context=this;
		image = (PhotoView) findViewById(R.id.image);
		avatar = getIntent().getExtras().getString("avatar");
		loadAvatar=new LoadBigAvatar(ShowBigImage.this, Constant.IMAGE_PATH);
		//本地存在，直接显示本地的图片
        if (avatar != null && !avatar.equals("")&&!avatar.equals("default")) {
            Bitmap bitmap = loadAvatar.loadImage(image, avatar,
                    new ImageDownloadedCallBack() {
                        @Override
                        public void onImageDownloaded(PhotoView imageView,
                                Bitmap bitmap,int status) {
                        	Log.e("1",status+"");
                        	if(status==-1){                       		
	                            imageView.setImageBitmap(bitmap);
                        	}else{
                        	   LocalUserInfo.getInstance(context).setUserInfo("avatar", null);
                           }
                            
                        }

                    });
            if (bitmap != null)
                image.setImageBitmap(bitmap);
        }else if(avatar.equals("default")){
        	image.setBackgroundResource(R.drawable.default_avatar);
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
		if (isDownloaded)
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
