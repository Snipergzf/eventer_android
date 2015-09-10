package com.eventer.app.other;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.eventer.app.Constant;
import com.eventer.app.MyApplication;
import com.eventer.app.R;
import com.eventer.app.http.HttpUnit;
import com.eventer.app.http.LoadDataFromHTTP;
import com.eventer.app.http.UploadPicToServer;
import com.eventer.app.http.UploadPicToServer.DataCallBack;
import com.eventer.app.main.ProfileFragment;
import com.eventer.app.task.LoadUserAvatar;
import com.eventer.app.task.LoadUserAvatar.ImageDownloadedCallBack;
import com.eventer.app.util.BitmapCache;
import com.eventer.app.util.FileUtil;
import com.eventer.app.util.LocalUserInfo;
import com.eventer.app.util.PreferenceUtils;

@SuppressLint("SdCardPath")
public class MyUserInfoActivity extends Activity {

    private RelativeLayout re_avatar;
    private RelativeLayout re_name;
    private RelativeLayout re_sex;
    private RelativeLayout re_exit;
    private RelativeLayout re_grade,re_school,re_major,re_class;
    
    private TextView tv_grade,tv_school,tv_major,tv_class;

    private ImageView iv_avatar;
    private TextView tv_name;
    private TextView tv_sex;

    private String imageName;
    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    private static final int UPDATE_NICK = 5;// 结果
    private LoadUserAvatar avatarLoader;
    String hxid;
    String sex;
    String sign;
    String nick;
    String avatar;
    String grade,school,major,mclass;
    private Context context;
    public static MyUserInfoActivity instance;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfo);
        avatarLoader = new LoadUserAvatar(this, Constant.IMAGE_PATH);
        context=this;
        instance=this;
        initView();
        initData();

    }

    private void initView() {
        
        re_avatar = (RelativeLayout) this.findViewById(R.id.re_avatar);
        re_name = (RelativeLayout) this.findViewById(R.id.re_name);
        re_sex = (RelativeLayout) this.findViewById(R.id.re_sex);
        re_exit=(RelativeLayout)findViewById(R.id.re_exit);
        re_grade=(RelativeLayout)findViewById(R.id.re_grade);
        re_school=(RelativeLayout)findViewById(R.id.re_school);
        re_class=(RelativeLayout)findViewById(R.id.re_class);
        re_major=(RelativeLayout)findViewById(R.id.re_major);
        iv_avatar = (ImageView) this.findViewById(R.id.iv_avatar);
        tv_name = (TextView) this.findViewById(R.id.tv_name);
        tv_sex = (TextView) this.findViewById(R.id.tv_sex);
        tv_grade = (TextView) this.findViewById(R.id.tv_grade);
        tv_school = (TextView) this.findViewById(R.id.tv_school);
        tv_major = (TextView) this.findViewById(R.id.tv_major);
        tv_class = (TextView) this.findViewById(R.id.tv_class);
        
        re_avatar.setOnClickListener(new MyListener());
        re_name.setOnClickListener(new MyListener());
        re_sex.setOnClickListener(new MyListener());
        re_exit.setOnClickListener(new MyListener());
        re_class.setOnClickListener(new MyListener());
        re_major.setOnClickListener(new MyListener());
        re_school.setOnClickListener(new MyListener());
        re_grade.setOnClickListener(new MyListener());
        iv_avatar.setOnClickListener(new MyListener());       
        
    }
    
    private void initData(){
        nick = LocalUserInfo.getInstance(context).getUserInfo(
                "nick");
        sex = LocalUserInfo.getInstance(context).getUserInfo(
                "sex");
        sign = LocalUserInfo.getInstance(context).getUserInfo(
                "sign");
        avatar = LocalUserInfo.getInstance(context)
                .getUserInfo("avatar");
        grade=LocalUserInfo.getInstance(context).getUserInfo("grade");
        school=LocalUserInfo.getInstance(context).getUserInfo("school");
        major=LocalUserInfo.getInstance(context).getUserInfo("major");
        mclass=LocalUserInfo.getInstance(context).getUserInfo("class");
        Log.e("1", avatar);
        tv_name.setText(nick);
        
        if (sex.equals("1")) {
            tv_sex.setText("男");

        } else if (sex.equals("2")) {
            tv_sex.setText("女");

        } else {
            tv_sex.setText("");
        }
         
        if(!TextUtils.isEmpty(grade)){
        	tv_grade.setText(grade);
        }else{
        	tv_grade.setText("");
        }
        if(!TextUtils.isEmpty(school)){
        	tv_school.setText(school);
        }else{
        	tv_school.setText("");
        }
        if(!TextUtils.isEmpty(major)){
        	tv_major.setText(major);
        }else{
        	tv_major.setText("");
        }
        if(!TextUtils.isEmpty(mclass)){
        	tv_class.setText(mclass);
        }else{
        	tv_class.setText("");
        }


        showUserAvatar(iv_avatar, avatar);
    }

    class MyListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.re_avatar:
                showPhotoDialog();
                break;
            case R.id.re_name:
                startActivityForResult(new Intent(context,
                        UpdateNickActivity.class),UPDATE_NICK);
                break;
            case R.id.re_sex:
                showSexDialog();
                break;
            case R.id.re_exit:
            	PreferenceUtils.getInstance().setLoginPwd("");
            	Constant.isLogin=false;
            	setResult(ProfileFragment.IS_EXIT, new Intent().putExtra("exit", true));
    			finish();
            	break;
            case R.id.re_grade:
            case R.id.re_school:
            case R.id.re_major:
            case R.id.re_class:
            	break;
            case R.id.iv_avatar:
            	 avatar = LocalUserInfo.getInstance(context)
                 .getUserInfo("avatar");
            	Intent intent = new Intent(context, ShowBigImage.class);
                intent.putExtra("avatar", avatar);
                startActivity(intent);
            	break;

            }
        }

    }

    private void showPhotoDialog() {
        final AlertDialog dlg = new AlertDialog.Builder(this).create();
        dlg.show();
        Window window = dlg.getWindow();
        // *** 主要就是在这里实现这种效果的.
        // 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
        window.setContentView(R.layout.alertdialog);
        // 为确认按钮添加事件,执行退出应用操作
        TextView tv_paizhao = (TextView) window.findViewById(R.id.tv_content1);
        tv_paizhao.setText("拍一张照片");
        tv_paizhao.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SdCardPath")
            public void onClick(View v) {
            	imageName = getNowTime() + ".png";
                File cameraFile = new File(Constant.IMAGE_PATH,
                		imageName);
                
                 cameraFile.getParentFile().mkdirs();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // 指定调用相机拍照后照片的储存路径
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(cameraFile));
                startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
                dlg.cancel();
            }
        });
        TextView tv_xiangce = (TextView) window.findViewById(R.id.tv_content2);
        tv_xiangce.setText("从相册中选择照片");
        tv_xiangce.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                imageName = getNowTime() + ".png";
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, PHOTO_REQUEST_GALLERY);

                dlg.cancel();
            }
        });

    }
   /***
    * 设置性别的对话框
    */
    private void showSexDialog() {
        final AlertDialog dlg = new AlertDialog.Builder(this).create();
        dlg.show();
        Window window = dlg.getWindow();
        // *** 主要就是在这里实现这种效果的.
        // 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
        window.setContentView(R.layout.alertdialog);
        LinearLayout ll_title = (LinearLayout) window
                .findViewById(R.id.ll_title);
        ll_title.setVisibility(View.VISIBLE);
        TextView tv_title = (TextView) window.findViewById(R.id.tv_title);
        tv_title.setText("性别");
        // 为确认按钮添加事件,执行退出应用操作
        TextView tv_paizhao = (TextView) window.findViewById(R.id.tv_content1);
        tv_paizhao.setText("男");
        tv_paizhao.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SdCardPath")
            public void onClick(View v) {
                if (!sex.equals("1")) {
                    tv_sex.setText("男");
                    updateSex("1");
                }
                dlg.cancel();
            }
        });
        TextView tv_xiangce = (TextView) window.findViewById(R.id.tv_content2);
        tv_xiangce.setText("女");
        tv_xiangce.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (!sex.equals("2")) {

                    tv_sex.setText("女");
                    updateSex("2");
                }

                dlg.cancel();
            }
        });

    }

    @SuppressLint("SdCardPath")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
            case PHOTO_REQUEST_TAKEPHOTO:

                startPhotoZoom(
                        Uri.fromFile(new File(Constant.IMAGE_PATH, imageName)),
                        480);
                break;

            case PHOTO_REQUEST_GALLERY:
                if (data != null)
                    startPhotoZoom(data.getData(), 480); 
                break;

            case PHOTO_REQUEST_CUT:
                // BitmapFactory.Options options = new BitmapFactory.Options();
                //
                // /**
                // * 最关键在此，把options.inJustDecodeBounds = true;
                // * 这里再decodeFile()，返回的bitmap为空
                // * ，但此时调用options.outHeight时，已经包含了图片的高了
                // */
                // options.inJustDecodeBounds = true;
                Bitmap bitmap = BitmapFactory.decodeFile(Constant.IMAGE_PATH
                        + imageName);
                iv_avatar.setImageBitmap(bitmap);
                updateAvatarInServer(imageName);
                break;

            }
            super.onActivityResult(requestCode, resultCode, data);

        }
    }
   /***
    * 对图片进行剪裁
    * @param uri1 图片地址
    * @param size 图片剪裁尺寸
    */
    @SuppressLint("SdCardPath")
    private void startPhotoZoom(Uri uri1, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri1, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", false);

        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(Constant.IMAGE_PATH, imageName)));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    @SuppressLint("SimpleDateFormat")
    private String getNowTime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmssSS");
        return dateFormat.format(date);
    }

    public void back(View view) {
        finish();
    }
    /***
     * 显示头像
     * @param iamgeView 显示头像的容器
     * @param avatar    图片地址
     */
    private void showUserAvatar(final ImageView iamgeView, String avatar) {
        final String url_avatar = avatar;
        iamgeView.setTag(url_avatar);
        if (url_avatar != null && !url_avatar.equals("")&&!avatar.equals("default")) {
            Bitmap bitmap = avatarLoader.loadImage(iamgeView, url_avatar,
                    new ImageDownloadedCallBack() {

                        @Override
                        public void onImageDownloaded(ImageView imageView,
                                Bitmap bitmap,int status) {
                        	if(status==-1){
                        		if (imageView.getTag() == url_avatar) {
	                                imageView.setImageBitmap(bitmap);
	                            }
                        	}else{
                        	   LocalUserInfo.getInstance(context).setUserInfo("avatar", null);
                           }
                            
                        }

                    });
            if (bitmap != null)
                iamgeView.setImageBitmap(bitmap);
        }else if(avatar.equals("default")){
        	 iamgeView.setBackgroundResource(R.drawable.default_avatar);
        }else{
        	Map<String, String> map = new HashMap<String, String>();           
            map.put("uid", Constant.UID+"");
            GetAvatar(map);
        }
    }
    
	/****
	 * 将头像上传至服务器
	 * @param image 图片的名字
	 */
    @SuppressLint("SdCardPath")
    private void updateAvatarInServer(final String image) {
        Map<String, String> map = new HashMap<String, String>();
        if ((new File(Constant.IMAGE_PATH + image)).exists()) {
            map.put("upload", Constant.IMAGE_PATH + image);
           // map.put("image", image);
        } else {
            return;
        }
        map.put("uid", Constant.UID+"");
        map.put("token", Constant.TOKEN);

        UploadPicToServer task = new UploadPicToServer(
                context, Constant.URL_UPDATE_Avatar, map,Constant.IMAGE_PATH + image,"upload");

        task.getData(new DataCallBack() {

            @SuppressLint("ShowToast")
            @Override
            public void onDataCallBack(JSONObject data) {
                try {
                    int code = data.getInteger("status");
                    Log.e("1", code+"");
                    if (code == 0) {
                    	JSONObject json=data.getJSONObject("user_action");
                    	String avatar=json.getString("avatar");
                        LocalUserInfo.getInstance(context)
                                .setUserInfo("avatar", avatar);
                        if(avatar!=null){
                        	  String filename = avatar
                                      .substring(avatar.lastIndexOf("/") + 1);
                        	  Bitmap bitmap = BitmapFactory.decodeFile(Constant.IMAGE_PATH
                                      + imageName);
                        	  BitmapFactory.Options options = new BitmapFactory.Options();
                              options.inJustDecodeBounds = false;
                              options.inSampleSize = 5; // width，hight设为原来的十分一
                              bitmap = BitmapFactory.decodeFile(Constant.IMAGE_PATH
                                      + imageName, options);
                              File f = new File(Constant.IMAGE_PATH, filename);
                              f.mkdirs();
                              if (f.exists()) {
                                 f.delete();
                              }
                              try {
	                               FileOutputStream out = new FileOutputStream(f);
	                               bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
	                               out.flush();
	                               out.close();
                              } catch (FileNotFoundException e) {
                               // TODO Auto-generated catch block
                               e.printStackTrace();
                              } catch (IOException e) {
                               // TODO Auto-generated catch block
                               e.printStackTrace();
                              }
                              if (bitmap != null) {
                            	  BitmapCache bitmapCache = new BitmapCache();
                            	  FileUtil fileUtil = new FileUtil(context, Constant.IMAGE_PATH);
                                  // 先缓存到内存
                                  bitmapCache.putBitmap(avatar, bitmap);
                                  // 缓存到文件系统
                                  fileUtil.saveBitmap(filename, bitmap);
                              }
                              File file = new File(Constant.IMAGE_PATH, imageName);
                              if (file.exists()) {
                                  file.delete();
                               }
                             MyApplication.getInstance().setValueByKey("set_avatar", true);
                        }
                      
                    } else if (code == 2) {

                        Toast.makeText(context, "更新失败...",
                                Toast.LENGTH_SHORT).show();
                    } else if (code == 3) {

                        Toast.makeText(context, "图片上传失败...",
                                Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(context, "服务器繁忙请重试...",
                                Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {

                    Toast.makeText(context, "数据解析错误...",
                            Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }

        });

    }
    
   /***
    * 从服务器端获取头像
    * @param params 
    */
	public void GetAvatar(final Object... params) {
		new AsyncTask<Object, Object,Map<String, Object>>() {

			@SuppressWarnings("unchecked")
			@Override
			protected Map<String, Object> doInBackground(Object... params) {
				Map<String, Object> status=new HashMap<String, Object>();
			  try {
		    	        status=HttpUnit.sendGetAvatarRequest((Map<String, String>) params[0]);
		    	        return status;
					
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					Log.e("1", e.toString());
					return null;
				}
			}
			protected void onPostExecute(Map<String, Object> result) {
				 if(result!=null){
					 int status=(int)result.get("status");
					 String info=(String)result.get("info");
					 if(status==0){
				        	Log.e("1", "获取头像地址成功！");
	                        LocalUserInfo.getInstance(context)
                          .setUserInfo("avatar", info);
				        	showUserAvatar(iv_avatar, info);		        					        	
				        }else {
				        	Toast.makeText(context, "头像获取失败！", Toast.LENGTH_LONG)
							.show();
				        	
				        }
				  }	 				
			    };
			    
		}.execute(params);}
    
    
	  /***
	    * 修改性别
	    * @param params 
	    */
    public void updateSex(final String sexnum) {
        Map<String, String> map = new HashMap<String, String>();

        map.put("sex", sexnum);
        map.put("uid", Constant.UID+"");
	    map.put("token", Constant.TOKEN);
	    map.put("name","");
	    map.put("email", "");
	    map.put("grade","");
	    map.put("school", "");
	    map.put("major", "");
	    map.put("class", "");
	    map.put("user_rank", "0");
        LoadDataFromHTTP task = new LoadDataFromHTTP(
                context, Constant.URL_UPDATE_SELFINFO, map);

        task.getData(new com.eventer.app.http.LoadDataFromHTTP.DataCallBack() {

            @SuppressLint("ShowToast")
            @Override
            public void onDataCallBack(JSONObject data) {
                try {
                	int code = data.getInteger("status");
                    if (code == 0) {
                    	Toast.makeText(context, "更新成功...",
                                Toast.LENGTH_SHORT).show();
                        LocalUserInfo.getInstance(context)
                                .setUserInfo("sex", sexnum);
                    } else if (code == 17) {

                        Toast.makeText(context, "更新失败,请稍后重试！",
                                Toast.LENGTH_SHORT).show();
                    } else {

                        Toast.makeText(context, "服务器繁忙请重试...",
                                Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {

                    Toast.makeText(context, "数据解析错误...",
                            Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }

        });
    }

	public void refreshNick() {
		// TODO Auto-generated method stub
		nick = LocalUserInfo.getInstance(context).getUserInfo(
                "nick");
		tv_name.setText(nick);
	}

}
