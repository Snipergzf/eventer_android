package com.eventer.app.main;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
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
import com.eventer.app.http.LoadDataFromHTTP;
import com.eventer.app.http.UploadPicToServer;
import com.eventer.app.http.LoadDataFromHTTP.DataCallBack;
import com.eventer.app.other.MyUserInfoActivity;
import com.eventer.app.util.LocalUserInfo;
import com.eventer.app.widget.AbstractSpinerAdapter.IOnItemSelectListener;
import com.eventer.app.widget.SpinerPopWindow;

public class FillInUserInfoActivity extends Activity {
    private EditText et_usernick,et_emial;
    private TextView tv_sex;
    private TextView  tv_year,tv_school,tv_major,tv_class;
    private TextView[] tv_list;
    private Button btn_register;
    private ImageView iv_avatar;
    private Context context;
    private String imageName;
    private int index;
    private List<String> valueList = new ArrayList<String>();
    private List<String> yearList,schoolList,majorList,classList;
    private String year,school,major,mclass,name,sex="2",email;
    private String[] classinfo=new String[4];
    private SpinerPopWindow mSpinerPopWindow;
    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// ����
    private static final int PHOTO_REQUEST_GALLERY = 2;// �������ѡ��
    private static final int PHOTO_REQUEST_CUT = 3;// ���
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fill_in_user_info);
		context=this;
		initView();
	}
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
		tv_sex.setText("Ů");
		tv_sex.setOnClickListener(new MyListener());
		btn_register.setOnClickListener(new MyListener());
		iv_avatar.setOnClickListener(new MyListener());
		tv_class.setOnClickListener(new ClassListener());
		tv_major.setOnClickListener(new ClassListener());
		tv_school.setOnClickListener(new ClassListener());
		tv_year.setOnClickListener(new ClassListener());
		
		yearList=new ArrayList<String>();
		yearList.add("2014");
		schoolList=new ArrayList<String>();
		schoolList.add("������Ϣ��ͨ��ѧԺ");
		majorList=new ArrayList<String>();
		majorList.add("ͨ�Ź���");
		classList=new ArrayList<String>();
		classList.add("1��");
		classList.add("2��");
		classList.add("ͨ��Ӣ");
		classList.add("����Ӣ");
		
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
	            	showPhotoDialog();
	            	break;
	           
	    		default:
	    			break;
	        }

	    }
	 }
	 
	 class ClassListener implements OnClickListener {
	        @Override
	       public void onClick(View v) {
		        switch (v.getId()) {
				case R.id.tv_year:
					index=0;
					valueList=yearList;
					mSpinerPopWindow.refreshData(valueList, 0);		
					break;
				case R.id.tv_school:
				    index=1;
				    valueList=schoolList;
				    mSpinerPopWindow.refreshData(valueList, 0);
				    break;
				case R.id.tv_major:
				    index=2;
				    valueList=majorList;
				    mSpinerPopWindow.refreshData(valueList, 0);
				    break;
				case R.id.tv_class:
				    index=3;
				    valueList=classList;
				    mSpinerPopWindow.refreshData(valueList, 0);	    
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

	 
	 private void showPhotoDialog() {
	        final AlertDialog dlg = new AlertDialog.Builder(context).create();
	        dlg.show();
	        Window window = dlg.getWindow();
	        window.setContentView(R.layout.alertdialog);
	        TextView tv_paizhao = (TextView) window.findViewById(R.id.tv_content1);
	        tv_paizhao.setText("��һ����Ƭ");
	        tv_paizhao.setOnClickListener(new View.OnClickListener() {
	            @SuppressLint("SdCardPath")
	            public void onClick(View v) {
	            	imageName = (System.currentTimeMillis()/1000) + ".png";
	                File cameraFile = new File(Constant.IMAGE_PATH,
	                		imageName);
	                
	                 cameraFile.getParentFile().mkdirs();
	                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	                // ָ������������պ���Ƭ�Ĵ���·��
	                intent.putExtra(MediaStore.EXTRA_OUTPUT,
	                        Uri.fromFile(cameraFile));
	                startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
	                dlg.cancel();
	            }
	        });
	        TextView tv_xiangce = (TextView) window.findViewById(R.id.tv_content2);
	        tv_xiangce.setText("�������ѡ����Ƭ");
	        tv_xiangce.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	                imageName = (System.currentTimeMillis()/1000) + ".png";
	                Intent intent = new Intent(Intent.ACTION_PICK, null);
	                intent.setDataAndType(
	                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
	                startActivityForResult(intent, PHOTO_REQUEST_GALLERY);

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
	                // * ��ؼ��ڴˣ���options.inJustDecodeBounds = true;
	                // * ������decodeFile()�����ص�bitmapΪ��
	                // * ������ʱ����options.outHeightʱ���Ѿ�������ͼƬ�ĸ���
	                // */
	                // options.inJustDecodeBounds = true;
	                Bitmap bitmap = BitmapFactory.decodeFile(Constant.IMAGE_PATH
	                        + imageName);
	                iv_avatar.setImageBitmap(bitmap);
	                break;

	            }
	            super.onActivityResult(requestCode, resultCode, data);

	        }
	    }
        /***
         * ��ͼƬ���м���
         * @param uri1
         * @param size
         */
	    @SuppressLint("SdCardPath")
	    private void startPhotoZoom(Uri uri1, int size) {
	        Intent intent = new Intent("com.android.camera.action.CROP");
	        intent.setDataAndType(uri1, "image/*");
	        // cropΪtrue�������ڿ�����intent��������ʾ��view���Լ���
	        intent.putExtra("crop", "true");

	        // aspectX aspectY �ǿ�ߵı���
	        intent.putExtra("aspectX", 1);
	        intent.putExtra("aspectY", 1);

	        // outputX,outputY �Ǽ���ͼƬ�Ŀ��
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
	/***
	 * ������Ϣ��
	 * ѡ��Щ��
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
        tv_title.setText("�Ա�");
        TextView tv_paizhao = (TextView) window.findViewById(R.id.tv_content1);
        tv_paizhao.setText("��");
        tv_paizhao.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SdCardPath")
            public void onClick(View v) {                
                tv_sex.setText("��");
                sex="1";
                dlg.cancel();
            }
        });
        TextView tv_xiangce = (TextView) window.findViewById(R.id.tv_content2);
        tv_xiangce.setText("Ů");
        tv_xiangce.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tv_sex.setText("Ů");
                sex="2";
                dlg.cancel();
            }
        });
    }
	
	/***
	 * �ϴ�������Ϣ
	 */
	private void updateSelfInfo(){
		 Map<String, String> maps = new HashMap<String, String>();
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
	                    Log.e("1", code+"");
	                    if (code == 0) {
	                    	//��������Ϣд��LocalUserInfo
	                    	LocalUserInfo.getInstance(getApplicationContext()).setUserInfo("nick", name);
	                    	LocalUserInfo.getInstance(getApplicationContext()).setUserInfo("sex", sex);
	                    	LocalUserInfo.getInstance(getApplicationContext()).setUserInfo("email", email);
	                    	LocalUserInfo.getInstance(getApplicationContext()).setUserInfo("user_rank", "0");
	                    	LocalUserInfo.getInstance(getApplicationContext()).setUserInfo("grade", classinfo[0]);
	                    	LocalUserInfo.getInstance(getApplicationContext()).setUserInfo("school", classinfo[1]);
	                    	LocalUserInfo.getInstance(getApplicationContext()).setUserInfo("class", classinfo[3]);
	                    	LocalUserInfo.getInstance(getApplicationContext()).setUserInfo("major", classinfo[2]);
	                    	if(imageName!=null&&imageName!=""){
	    	            		updateAvatarInServer(imageName);}
//	    	            	else{
					        	Intent intent = new Intent();
					    		intent.setClass(context, MainActivity.class);
					    		startActivity(intent);
					    		finish();
//	    	            	}
	                    } else {

	                        Toast.makeText(FillInUserInfoActivity.this, "��Ϣ����ʧ��...",
	                                Toast.LENGTH_SHORT).show();
	                    } 

	                } catch (JSONException e) {

	                    Toast.makeText(FillInUserInfoActivity.this, "���ݽ�������...",
	                            Toast.LENGTH_SHORT).show();
	                    e.printStackTrace();
	                }

	            }

	        });
	}
	/***
	 * �ϴ�ͷ�񵽷�����
	 * @param image 
	 * ͼƬ��ַ
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
                    	Intent intent = new Intent();
			    		intent.setClass(context, MainActivity.class);
			    		startActivity(intent);
			    		finish();                  
                    } else if (code == 2) {

                        Toast.makeText(getApplicationContext(), "����ʧ��...",
                                Toast.LENGTH_SHORT).show();
                    } else if (code == 3) {

                        Toast.makeText(getApplicationContext(), "ͼƬ�ϴ�ʧ��...",
                                Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(getApplicationContext(), "��������æ������...",
                                Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {

                    Toast.makeText(getApplicationContext(), "���ݽ�������...",
                            Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }

        });

    }
	public void back(View view){
		finish();
	}
}
