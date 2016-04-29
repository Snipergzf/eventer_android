package com.eventer.app.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.eventer.app.Constant;
import com.eventer.app.R;
import com.eventer.app.db.MajorDao;
import com.eventer.app.http.LoadDataFromHTTP;
import com.eventer.app.http.LoadDataFromHTTP.DataCallBack;
import com.eventer.app.http.UploadPicToServer;
import com.eventer.app.util.LocalUserInfo;
import com.eventer.app.view.AbstractSpinerAdapter.IOnItemSelectListener;
import com.eventer.app.view.CircleProgressBar;
import com.eventer.app.view.SpinerPopWindow;
import com.soundcloud.android.crop.Crop;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FillInUserInfoActivity extends BaseActivity {
	private EditText et_usernick,et_emial;
	private TextView tv_sex;
	TextView  tv_year,tv_school,tv_major,tv_class;
	private TextView[] tv_list;
	private Button btn_register;
	private ImageView iv_avatar;
	private Context context;
	private String imageName;
	private int index;
	private List<String> valueList = new ArrayList<>();
	private List<String> yearList = new ArrayList<>() ;
	private String name,sex="2",email;
	private String[] classinfo=new String[4];
	private SpinerPopWindow mSpinerPopWindow;
	AlertDialog upload_dlg;

	private MajorDao dao;
	private Activity activity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fill_in_user_info);
		context = this;
		activity = this;
		setBaseTitle(R.string.fillin_info);
		dao = new MajorDao(context);
		initView();
	}
	//初始化页面
	private void initView() {
		// TODO Auto-generated method stub
		et_emial=(EditText)findViewById(R.id.et_email);
		et_usernick=(EditText)findViewById(R.id.et_usernick);
		tv_sex=(TextView)findViewById(R.id.tv_sex);
		btn_register=(Button)findViewById(R.id.btn_register);
		iv_avatar=(ImageView)findViewById(R.id.iv_photo);
		tv_class=(TextView)findViewById(R.id.tv_class);
		tv_major=(TextView)findViewById(R.id.tv_major);
		tv_school=(TextView)findViewById(R.id.tv_school);
		tv_year=(TextView)findViewById(R.id.tv_year);
		tv_list=new TextView[]{tv_year,tv_school,tv_major,tv_class};
		et_usernick.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				int len=s.length();
				if(len>0){
					btn_register.setEnabled(true);
				}else{
					btn_register.setEnabled(false);
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
				// TODO Auto-generated method stub

			}
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		tv_sex.setText("女");
		tv_sex.setOnClickListener(new MyListener());
		btn_register.setOnClickListener(new MyListener());
		iv_avatar.setOnClickListener(new MyListener());
		tv_class.setOnClickListener(new ClassListener());
		tv_major.setOnClickListener(new ClassListener());
		tv_school.setOnClickListener(new ClassListener());
		tv_year.setOnClickListener(new ClassListener());


		String[] grade = getResources().getStringArray(R.array.grade);
		Collections.addAll(yearList, grade);
		mSpinerPopWindow = new SpinerPopWindow(this);
		mSpinerPopWindow.refreshData(valueList, 0);
		mSpinerPopWindow.setItemListener(new IOnItemSelectListener() {

			@Override
			public void onItemClick(int pos) {
				// TODO Auto-generated method stub
				if (pos >= 0 && pos <= valueList.size()){
					String value = valueList.get(pos);
					tv_list[index].setText(value);
					classinfo[index]=value;
				}
			}
		});


	}
	/**
	 *处理控件的点击事件
	 */
	class MyListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.tv_sex:
					showSexDialog();
					break;
				case R.id.btn_register:
					updateSelfInfo();

					break;
				case R.id.iv_photo:
					Crop.pickImage(activity);
					break;

				default:
					break;
			}

		}
	}

	/**
	 *处理控件的点击事件
	 */
	class ClassListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.tv_year:
					index = 0;
					valueList = yearList;
					mSpinerPopWindow.refreshData(valueList, 0);
					break;
				case R.id.tv_school:
					String year = tv_year.getText().toString().trim();
					if (!TextUtils.isEmpty(year)){
						valueList = dao.getSchool(year);
						mSpinerPopWindow.refreshData(valueList, 0);
						index = 1;
					} else {
						Toast.makeText(context, "请先选择年级~", Toast.LENGTH_SHORT).show();
						index = -1;
					}

					break;
				case R.id.tv_major:

					year = tv_year.getText().toString().trim();
					String school = tv_school.getText().toString().trim();
					if (!TextUtils.isEmpty(year) && !TextUtils.isEmpty(school)){
						valueList = dao.getMajor(year, school);
						mSpinerPopWindow.refreshData(valueList, 0);
						index=2;
					}  else {
						Toast.makeText(context, "请先选择年级和学院~", Toast.LENGTH_SHORT).show();
						index=-1;
					}
					break;
				case R.id.tv_class:

					year = tv_year.getText().toString().trim();
					school = tv_school.getText().toString().trim();
					String major = tv_major.getText().toString().trim();
					if (!TextUtils.isEmpty(year) && !TextUtils.isEmpty(school) && !TextUtils.isEmpty(major)){
						valueList = dao.getClass(year, school, major);
						mSpinerPopWindow.refreshData(valueList, 0);
						index=3;
					}  else {
						Toast.makeText(context, "请先选择年级、学院和专业~", Toast.LENGTH_SHORT).show();
						index=-1;
					}
					break;
				default:
					index=-1;
					break;
			}
			if(index!=-1){
				showSpinWindow();
			}
		}
	}

	private void showSpinWindow(){
		Log.e("", "showSpinWindow");
		mSpinerPopWindow.setWidth(tv_list[index].getWidth());
		mSpinerPopWindow.showAsDropDown(tv_list[index]);
		mSpinerPopWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				tv_list[index].setSelected(false);
			}
		});
	}



	private void beginCrop(Uri source) {
		if(TextUtils.isEmpty(imageName)){
			imageName = getNowTime() + ".png";
		}
		File cameraFile = new File(Constant.IMAGE_PATH,
				imageName);
		cameraFile.getParentFile().mkdirs();
		Uri destination = Uri.fromFile(new File(Constant.IMAGE_PATH, imageName));
		Log.e("1", destination + "");
		Crop.of(source, destination).asSquare().start(this);
	}

	/**
	 *处理图片结果，对图片进行压缩处理
	 */
	private void handleCrop(int resultCode, Intent result) {
		if (resultCode == RESULT_OK&&!TextUtils.isEmpty(imageName)) {
			String filePath = Constant.IMAGE_PATH + imageName;
			BitmapFactory.Options measureOptions = new BitmapFactory.Options();
			measureOptions.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filePath, measureOptions);
			int scale = Math.min(measureOptions.outWidth, measureOptions.outHeight) / 240;
			scale = Math.max(scale, 1);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			options.inJustDecodeBounds = false;
			options.inSampleSize = scale;
			Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
			iv_avatar.setImageBitmap(bitmap);
		} else if (resultCode == Crop.RESULT_ERROR) {
			Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	@SuppressLint("SimpleDateFormat")
	private String getNowTime() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmssSS");
		return dateFormat.format(date);
	}


	private void showDialog(){
		upload_dlg = new AlertDialog.Builder(this).create();
		upload_dlg.show();
		Window window = upload_dlg.getWindow();
		window.setContentView(R.layout.upload_dialog);
		CircleProgressBar progress=(CircleProgressBar)window.findViewById(R.id.progress);
		progress.setColorSchemeResources(android.R.color.holo_orange_light);
	}


	/***
	 * 弹出消息框
	 * 选择些别
	 */
	private void showSexDialog() {
		final AlertDialog dlg = new AlertDialog.Builder(context).create();
		dlg.show();
		Window window = dlg.getWindow();
		window.setContentView(R.layout.alertdialog);
		LinearLayout ll_title = (LinearLayout) window
				.findViewById(R.id.ll_title);
		ll_title.setVisibility(View.VISIBLE);
		TextView tv_title = (TextView) window.findViewById(R.id.tv_title);
		tv_title.setText("性别");
		TextView tv_paizhao = (TextView) window.findViewById(R.id.tv_content1);
		tv_paizhao.setText("男");
		tv_paizhao.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("SdCardPath")
			public void onClick(View v) {
				tv_sex.setText("男");
				sex = "1";
				dlg.cancel();
			}
		});
		TextView tv_xiangce = (TextView) window.findViewById(R.id.tv_content2);
		tv_xiangce.setText("女");
		tv_xiangce.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				tv_sex.setText("女");
				sex = "2";
				dlg.cancel();
			}
		});
	}

	/***
	 * 上传个人信息
	 */
	private void updateSelfInfo(){
		Map<String, String> maps = new HashMap<>();
		email=et_emial.getText().toString();
		name=et_usernick.getText().toString();
		maps.put("uid", Constant.UID+"");
		maps.put("token", Constant.TOKEN);
		maps.put("name", name);
		maps.put("email", email);
		maps.put("sex", sex);
		maps.put("grade", classinfo[0]);
		maps.put("school", classinfo[1]);
		maps.put("major", classinfo[2]);
		maps.put("class", classinfo[3]);
		maps.put("user_rank", "0");
		LoadDataFromHTTP task = new LoadDataFromHTTP(
				FillInUserInfoActivity.this, Constant.URL_UPDATE_SELFINFO, maps);
		task.getData(new DataCallBack() {

			@SuppressLint("ShowToast")
			@Override
			public void onDataCallBack(JSONObject data) {
				try {
					int code = data.getInteger("status");
					Log.e("1", code + "");
					if (code == 0) {
						//将个人信息写入LocalUserInfo
						LocalUserInfo.getInstance(getApplicationContext()).setUserInfo("nick", name);
						LocalUserInfo.getInstance(getApplicationContext()).setUserInfo("sex", sex);
						LocalUserInfo.getInstance(getApplicationContext()).setUserInfo("email", email);
						LocalUserInfo.getInstance(getApplicationContext()).setUserInfo("user_rank", "0");
						LocalUserInfo.getInstance(getApplicationContext()).setUserInfo("grade", classinfo[0]);
						LocalUserInfo.getInstance(getApplicationContext()).setUserInfo("school", classinfo[1]);
						LocalUserInfo.getInstance(getApplicationContext()).setUserInfo("class", classinfo[3]);
						LocalUserInfo.getInstance(getApplicationContext()).setUserInfo("major", classinfo[2]);
						if (imageName != null && !imageName.equals("")) {
							showDialog();
							updateAvatarInServer(imageName);
						} else {
							Intent intent = new Intent();
							intent.setClass(context, MainActivity.class);
							startActivity(intent);
							finish();
						}
					} else {
						if (Constant.isConnectNet) {
							Toast.makeText(getApplicationContext(), getText(R.string.no_network),
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getApplicationContext(), "个人信息上传失败...",
									Toast.LENGTH_SHORT).show();
						}

					}

				} catch (JSONException e) {

					Toast.makeText(FillInUserInfoActivity.this, "数据解析错误...",
							Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				} catch (Exception e) {
					// TODO: handle exception
				}

			}

		});
	}
	/***
	 * 上传头像到服务器
	 * @param image
	 * 图片地址
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

		task.getData(new  com.eventer.app.http.UploadPicToServer.DataCallBack() {

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

					}else{
						if(Constant.isConnectNet)
							Toast.makeText(getApplicationContext(), "头像上传失败...",
									Toast.LENGTH_SHORT).show();
						else
							Toast.makeText(getApplicationContext(), getText(R.string.no_network),
									Toast.LENGTH_SHORT).show();
					}

				} catch (JSONException e) {

					Toast.makeText(getApplicationContext(), "数据解析错误...",
							Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}finally {
					Intent intent = new Intent();
					if(upload_dlg != null){
						upload_dlg.cancel();
					}
					intent.setClass(context, MainActivity.class);
					startActivity(intent);
					finish();
				}

			}

		});

	}


	@SuppressLint("SdCardPath")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
			beginCrop(data.getData());
		} else if (requestCode == Crop.REQUEST_CROP) {
			handleCrop(resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);

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
