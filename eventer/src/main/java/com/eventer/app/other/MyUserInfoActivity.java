package com.eventer.app.other;

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

import com.alibaba.fastjson.JSONObject;
import com.eventer.app.Constant;
import com.eventer.app.MyApplication;
import com.eventer.app.R;
import com.eventer.app.http.HttpUnit;
import com.eventer.app.http.LoadDataFromHTTP;
import com.eventer.app.http.UploadPicToServer;
import com.eventer.app.http.UploadPicToServer.DataCallBack;
import com.eventer.app.main.BaseActivity;
import com.eventer.app.main.CheckPhoneActivity;
import com.eventer.app.main.ProfileFragment;
import com.eventer.app.task.LoadImage;
import com.eventer.app.task.LoadImage.ImageDownloadedCallBack;
import com.eventer.app.util.BitmapCache;
import com.eventer.app.util.FileUtil;
import com.eventer.app.util.LocalUserInfo;
import com.eventer.app.util.PreferenceUtils;
import com.eventer.app.view.CircleProgressBar;
import com.eventer.app.view.MyToast;
import com.soundcloud.android.crop.Crop;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
@SuppressLint("SdCardPath")
public class MyUserInfoActivity extends BaseActivity {

    RelativeLayout re_avatar;
    LinearLayout re_name, re_sex;
    LinearLayout re_grade, re_school, re_major, re_class;

    private TextView tv_grade;
    private TextView tv_school;
    private TextView tv_major;
    private TextView tv_class;

    AlertDialog upload_dlg;
    private ImageView iv_avatar;
    private TextView tv_name;
    private TextView tv_sex;
    public static int EDIT_CLASS=0x29;
    private String imageName;
    private static final int UPDATE_NICK = 5;// 结果
    private LoadImage avatarLoader;
    private boolean isUpload=false;

    String sex;
    String nick;
    String avatar;
    String grade,school,major,mclass;
    private Context context;
    private Activity activity;
    public static MyUserInfoActivity instance;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfo);
        avatarLoader = new LoadImage(this, Constant.IMAGE_PATH);
        context=this;
        activity=this;
        setBaseTitle(R.string.my_info);
        instance=this;
        initView();
        initData();

    }

    /***
     * 初始化控件，给控件添加点击响应
     */
    private void initView() {

        re_avatar = (RelativeLayout) this.findViewById(R.id.re_avatar);
        re_name = (LinearLayout) this.findViewById(R.id.re_name);
        re_sex = (LinearLayout) this.findViewById(R.id.re_sex);
        re_grade=(LinearLayout)findViewById(R.id.re_grade);
        re_school=(LinearLayout)findViewById(R.id.re_school);
        re_class=(LinearLayout)findViewById(R.id.re_class);
        re_major=(LinearLayout)findViewById(R.id.re_major);
        iv_avatar = (ImageView) this.findViewById(R.id.iv_avatar);
        tv_name = (TextView) this.findViewById(R.id.tv_name);
        tv_sex = (TextView) this.findViewById(R.id.tv_sex);
        tv_grade = (TextView) this.findViewById(R.id.tv_grade);
        tv_school = (TextView) this.findViewById(R.id.tv_school);
        tv_major = (TextView) this.findViewById(R.id.tv_major);
        tv_class = (TextView) this.findViewById(R.id.tv_class);
        TextView tv_exit = (TextView) findViewById(R.id.tv_exit);
        TextView tv_reset_pwd = (TextView) this.findViewById(R.id.tv_reset_pwd);
        ImageView iv_edit = (ImageView) this.findViewById(R.id.iv_edit);

        re_avatar.setOnClickListener(new MyListener());
        re_name.setOnClickListener(new MyListener());
        re_sex.setOnClickListener(new MyListener());
        iv_avatar.setOnClickListener(new MyListener());
        iv_edit.setOnClickListener(new MyListener());
        tv_exit.setOnClickListener(new MyListener());
        tv_reset_pwd.setOnClickListener(new MyListener());

    }

    /***
     * 初始化页面的数据
     */
    private void initData(){
        nick = LocalUserInfo.getInstance(context).getUserInfo(
                "nick");
        sex = LocalUserInfo.getInstance(context).getUserInfo(
                "sex");
        avatar = LocalUserInfo.getInstance(context).getUserInfo(
                "avatar");
        grade = LocalUserInfo.getInstance(context).getUserInfo(
                "grade");
        school = LocalUserInfo.getInstance(context).getUserInfo(
                "school");
        major = LocalUserInfo.getInstance(context).getUserInfo(
                "major");
        mclass = LocalUserInfo.getInstance(context).getUserInfo(
                "class");

        tv_name.setText(nick);
        tv_grade.setText(grade);
        tv_school.setText(school);
        tv_major.setText(major);
        tv_class.setText(mclass);

        switch (sex) {
            case "1":
                tv_sex.setText("男");
                break;
            case "2":
                tv_sex.setText("女");
                break;
            default:
                tv_sex.setText("");
                break;
        }

        showUserAvatar(iv_avatar, avatar);
    }

    /**
     * 页面控件的点击事件
     */
    class MyListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            if( !isUpload ){
                switch (v.getId()) {
                    case R.id.re_avatar:
                        Crop.pickImage(activity);
                        break;
                    case R.id.re_name:
                        startActivityForResult(new Intent(context,
                                UpdateNickActivity.class),UPDATE_NICK);
                        break;
                    case R.id.re_sex:
                        showSexDialog();
                        break;
                    case R.id.tv_reset_pwd:
                        startActivity(new Intent()
                                .setClass(context, CheckPhoneActivity.class)
                                .putExtra("isLogin", true));
                        break;
                    case R.id.tv_exit:
                        exit();
                        break;
                    case R.id.iv_edit:
                        startActivityForResult(new Intent()
                                .setClass(context, Activity_ClassInfo_Edit.class)
                                .putExtra("grade", grade)
                                .putExtra("school", school)
                                .putExtra("major", major)
                                .putExtra("class", mclass), EDIT_CLASS);
                        break;
                    case R.id.iv_avatar:
                        avatar = LocalUserInfo.getInstance(context)
                                .getUserInfo("avatar");
                        Intent intent = new Intent(context, ShowBigImage.class);
                        intent.putExtra("avatar", avatar);
                        startActivity(intent);
                        break;

                }
            } else{
                MyToast.makeText(context, "更新中，请稍等...",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }


    /**
     * 开始剪切图片
     */
    private void beginCrop(Uri source) {
        if(TextUtils.isEmpty(imageName)){
            imageName = getNowTime() + ".png";
        }
        File cameraFile = new File(Constant.IMAGE_PATH,
                imageName);

        cameraFile.getParentFile().mkdirs();
        Uri destination = Uri.fromFile(new File(Constant.IMAGE_PATH, imageName));
        Crop.of(source, destination).asSquare().start(this);
    }


    /**
     * 处理剪切的结果
     */
    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK && !TextUtils.isEmpty(imageName)) {
            isUpload=true;
            showDialog();
            updateAvatarInServer(imageName);
        } else if (resultCode == Crop.RESULT_ERROR) {
             MyToast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("SimpleDateFormat")
    private String getNowTime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmssSS");
        return dateFormat.format(date);
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
                            }else if(status ==400){
                                Log.e("myinfo-bitmap","server error");
                            }

                        }

                    });
            if (bitmap != null)
                iamgeView.setImageBitmap(bitmap);
        }else if(avatar.equals("default")){
            iamgeView.setImageResource(
                    R.drawable.default_avatar);
        }else{
            Map<String, String> map = new HashMap<>();
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

        Map<String, String> map = new HashMap<>();
        if ((new File(Constant.IMAGE_PATH + image)).exists()) {
            map.put("upload", Constant.IMAGE_PATH + image);
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
                    Log.e("1", "upload pic status:" + code + "");
                    if (code == 0) {
                        JSONObject json = data.getJSONObject("user_action");
                        String avatar = json.getString("avatar");
                        LocalUserInfo.getInstance(context)
                                .setUserInfo("avatar", avatar);
                        if (avatar != null) {
                            String filename = avatar
                                    .substring(avatar.lastIndexOf("/") + 1);
                            Bitmap bitmap;
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inJustDecodeBounds = false;
                            options.inSampleSize = 1;
                            bitmap = BitmapFactory.decodeFile(Constant.IMAGE_PATH
                                    + imageName, options);
                            iv_avatar.setImageBitmap(bitmap);
                            File f = new File(Constant.IMAGE_PATH, filename);
                            if (f.exists()) {
                                if (f.delete()) {
                                    Log.e("1", "my_info del file");
                                }
                            }
                            try {
                                FileOutputStream out = new FileOutputStream(f);
                                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                                out.flush();
                                out.close();
                            } catch (Exception e) {
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
                                boolean file_del = file.delete();
                                Log.e("1", "myinfo file del:" + file_del);
                            }
                            MyApplication.getInstance().setValueByKey("set_avatar", true);
                        }

                    } else if (code == 2) {

                        MyToast.makeText(context, "更新失败...",
                                Toast.LENGTH_SHORT).show();
                    } else if (code == 3) {

                        MyToast.makeText(context, "图片上传失败...",
                                Toast.LENGTH_SHORT).show();

                    } else if (code == -2) {
                        MyToast.makeText(context, "图片过大，无法上传...",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        if (!Constant.isConnectNet) {
                            MyToast.makeText(context, getText(R.string.no_network) + "图片上传失败！", Toast.LENGTH_SHORT).show();
                        } else {
                            MyToast.makeText(context, "服务器繁忙请重试...",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }

                } catch (Exception e) {
                    MyToast.makeText(context, "更新失败...",
                            Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } finally {
                    isUpload = false;
                    if (upload_dlg != null) {
                        upload_dlg.cancel();
                    }
                }

            }

        });

    }

    private void showDialog(){
        upload_dlg = new AlertDialog.Builder(this).create();
        upload_dlg.show();
        Window window = upload_dlg.getWindow();
        // *** 主要就是在这里实现这种效果的.
        // 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
        window.setContentView(R.layout.upload_dialog);
        CircleProgressBar progress=(CircleProgressBar)window.findViewById(R.id.progress);
        progress.setColorSchemeResources(android.R.color.holo_orange_light);
    }

    /***
     * 从服务器端获取头像
     */
    public void GetAvatar(final Object... params) {
        new AsyncTask<Object, Object,Map<String, Object>>() {

            @SuppressWarnings("unchecked")
            @Override
            protected Map<String, Object> doInBackground(Object... params) {
                Map<String, Object> status;
                try {
                    status=HttpUnit.sendGetAvatarRequest((Map<String, String>) params[0]);
                    return status;
                } catch (Throwable e) {
                    return null;
                }
            }
            protected void onPostExecute(Map<String, Object> result) {
                if(result != null){
                    int status = (int) result.get("status");
                    String info = (String) result.get("info");
                    if(status == 0){
                        LocalUserInfo.getInstance(context)
                                .setUserInfo("avatar", info);
                        showUserAvatar(iv_avatar, info);
                    }else {
                        if(!Constant.isConnectNet){
                            MyToast.makeText(context, getText(R.string.no_network), Toast.LENGTH_SHORT).show();
                        }else{
                            MyToast.makeText(context, "头像获取失败！", Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                }
            }

        }.execute(params);
    }


    /***
     * 设置性别的对话框
     */
    private void showSexDialog() {
        final AlertDialog dlg = new AlertDialog.Builder(this).create();
        dlg.show();
        Window window = dlg.getWindow();
        // *** 主要就是在这里实现这种效果的.
        // 设置窗口的内容页面,alertdialog.xml文件中定义view内容
        window.setContentView(R.layout.alertdialog);
        LinearLayout ll_title = (LinearLayout) window
                .findViewById(R.id.ll_title);
        ll_title.setVisibility(View.VISIBLE);
        TextView tv_title = (TextView) window.findViewById(R.id.tv_title);
        tv_title.setText("性别");

        TextView tv1 = (TextView) window.findViewById(R.id.tv_content1);
        tv1.setText("男");
        tv1.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SdCardPath")
            public void onClick(View v) {
                if (!sex.equals("1")) {
                    tv_sex.setText("男");
                    updateSex("1");
                }
                dlg.cancel();
            }
        });
        TextView tv2 = (TextView) window.findViewById(R.id.tv_content2);
        tv2.setText("女");
        tv2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (!sex.equals("2")) {
                    tv_sex.setText("女");
                    updateSex("2");
                }

                dlg.cancel();
            }
        });

    }

    /***
     * 向服务器发送请求，修改性别
     */
    public void updateSex(final String sexnum) {
        isUpload=true;
        Map<String, String> map = new HashMap<>();

        map.put("sex", sexnum);
        map.put("uid", Constant.UID + "");
        map.put("token", Constant.TOKEN);
        map.put("name","");
        map.put("email", "");
        map.put("grade", "");
        map.put("school", "");
        map.put("major", "");
        map.put("class", "");
        map.put("user_rank", "0");
        LoadDataFromHTTP task = new LoadDataFromHTTP(
                context, Constant.URL_UPDATE_SELFINFO, map);

        task.getData(new com.eventer.app.http.LoadDataFromHTTP.DataCallBack() {

            @Override
            public void onDataCallBack(JSONObject data) {
                try {
                    int code = data.getInteger("status");
                    if (code == 0) {
                        MyToast.makeText(context, "更新成功...",
                                Toast.LENGTH_SHORT).show();
                        LocalUserInfo.getInstance(context)
                                .setUserInfo("sex", sexnum);
                    } else {
                        if (!Constant.isConnectNet) {
                            MyToast.makeText(context, getText(R.string.no_network), Toast.LENGTH_SHORT).show();
                        } else {

                            MyToast.makeText(context, "更新失败,请稍后重试！",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    MyToast.makeText(context, "更新失败,请稍后重试！",
                            Toast.LENGTH_SHORT).show();
                } finally {
                    isUpload = false;
                }

            }

        });
    }


    /**
     * 获取和处理返回值
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK ) { //处理图片选取的返回值
            beginCrop(data.getData());
        } else if (requestCode == Crop.REQUEST_CROP) { //处理图片剪切的返回值
            handleCrop(resultCode, data);
        } else if (requestCode == EDIT_CLASS && data != null) { //处理班级信息修改的返回值
            boolean isEdit = data.getBooleanExtra("isEdit",false);
            if( isEdit ) {
                initData();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void exit(){
        PreferenceUtils.getInstance().setLoginPwd("");
        Constant.isLogin=false;
        Constant.isExist=true;
        setResult(ProfileFragment.IS_EXIT,
                new Intent().putExtra("exit", true));
        finish();
    }

    public void refreshNick() {
        nick = LocalUserInfo.getInstance(context).getUserInfo(
                "nick");
        if(tv_name != null)
            tv_name.setText(nick);
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
