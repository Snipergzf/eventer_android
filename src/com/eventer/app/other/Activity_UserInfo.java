package com.eventer.app.other;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.eventer.app.Constant;
import com.eventer.app.MyApplication;
import com.eventer.app.R;
import com.eventer.app.db.ChatEntityDao;
import com.eventer.app.db.UserDao;
import com.eventer.app.entity.ChatEntity;
import com.eventer.app.entity.User;
import com.eventer.app.entity.UserDetail;
import com.eventer.app.http.HttpUnit;
import com.eventer.app.http.LoadDataFromHTTP;
import com.eventer.app.http.LoadDataFromHTTP.DataCallBack;
import com.eventer.app.main.MainActivity;
import com.eventer.app.task.LoadUserAvatar;
import com.eventer.app.task.LoadUserAvatar.ImageDownloadedCallBack;
import com.eventer.app.util.LocalUserInfo;

public class Activity_UserInfo extends Activity {
	 private TextView tv_name,tv_email,tv_school,tv_grade,tv_major,tv_nick;
	 private Context context;
	 private ImageView iv_avatar;
	 private LoadUserAvatar avatarLoader;
	 private boolean try_again=false;
	 private UserDetail user;
	 private Button  btn_sendmsg;
	 private ImageView iv_action;
	 private ImageView iv_sex;
	 boolean is_friend = false;
	 private  String id;
	 private RelativeLayout rl_email;
	 private LinearLayout ll_class_info;
	    @SuppressLint("SdCardPath")
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_userinfo);
	        context =Activity_UserInfo.this;
	        avatarLoader = new LoadUserAvatar(this, Constant.IMAGE_PATH);
	        id = this.getIntent().getStringExtra("user");
	        initView();
	        user=new UserDetail();
	        getData();
		     if(!TextUtils.isEmpty(id)){
		        	 UserDao dao=new UserDao(context);     
		 	         user=dao.getUserDetail(id);
		 	         if(user!=null){
		 	        	 initData();
		 	         }else{
		 	        	 getData();
		 	         }
		 	         
		     }else{
		        	this.finish();
		     }
	    }

		private void initView() {
			// TODO Auto-generated method stub
			btn_sendmsg = (Button) this.findViewById(R.id.btn_sendmsg);
	        iv_avatar = (ImageView) this.findViewById(R.id.iv_avatar);
	        iv_sex = (ImageView) this.findViewById(R.id.iv_sex);
	        tv_name = (TextView) this.findViewById(R.id.tv_name);
	        rl_email=(RelativeLayout)this.findViewById(R.id.re_email);
	        ll_class_info=(LinearLayout)this.findViewById(R.id.ll_class_info);
	        tv_email=(TextView)this.findViewById(R.id.tv_email);
	        tv_grade=(TextView)this.findViewById(R.id.tv_grade);
	        tv_school=(TextView)this.findViewById(R.id.tv_school);
	        tv_major=(TextView)this.findViewById(R.id.tv_major);
	        iv_action=(ImageView)this.findViewById(R.id.iv_action);
	        tv_nick=(TextView)this.findViewById(R.id.tv_nick);
	        if(MyApplication.getInstance().getContactIDList().contains(id)){
	        	is_friend=true;
	        	iv_action.setVisibility(View.VISIBLE);
	        }else{
	        	is_friend=false;
	        	iv_action.setVisibility(View.GONE);
	        }
	        iv_action.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					 AddPopWindow addPopWindow = new AddPopWindow(Activity_UserInfo.this);
		                addPopWindow.showPopupWindow(iv_action);
				}
			});
	        
		}
		
		 private void initData() {
				// TODO Auto-generated method stub		    	     
		     String nick = user.getNick();
		     String avatar = user.getAvatar();
		     String sex = user.getSex();
		     String major=user.getMajor();
		     String school=user.getSchool();
		     String grade=user.getGrade();
		     String email=user.getEmail();
		     
		     
		      if (nick != null && id != null) {
		            tv_name.setText(nick);
		            tv_nick.setText("昵称:"+nick);
		            
		      if(is_friend){
		             btn_sendmsg.setText("发消息");
		             User u=MyApplication.getInstance().getContactList().get(id);
		             String beizhu=u.getBeizhu();
		             if(!TextUtils.isEmpty(beizhu)){
		            	 tv_name.setText(beizhu);
		            	 tv_nick.setVisibility(View.VISIBLE);
		             }
		            	 
		        }else{
		           btn_sendmsg.setText("加入通讯录");
		        }          
		       }
		        if(sex!=null){
		        	 if (sex.equals("1")) {
			                iv_sex.setImageResource(R.drawable.ic_sex_male);
			            } else if (sex.equals("2")) {
			                iv_sex.setImageResource(R.drawable.ic_sex_female);
			            } else {
			                iv_sex.setVisibility(View.GONE);
			            }
		        }else{
		        	iv_sex.setVisibility(View.GONE);
		        }
		        
		        if(avatar!=null&&!avatar.equals("default")){
		        	 showUserAvatar(iv_avatar, avatar);
		        }else{
		        	iv_avatar.setImageResource(R.drawable.default_avatar);
		        }
		        
		        if(!TextUtils.isEmpty(email)){
		        	rl_email.setVisibility(View.VISIBLE);
		        	tv_email.setText(email);
		        }else{
		        	rl_email.setVisibility(View.GONE);
		        }
		        
		        if(!TextUtils.isEmpty(major)){
		        	ll_class_info.setVisibility(View.VISIBLE);
		        	tv_grade.setText(grade);
		        	tv_major.setText(major);
		        	tv_school.setText(school);
		        }else{
		        	ll_class_info.setVisibility(View.GONE);
		        }
		        
		        
		        btn_sendmsg.setOnClickListener(new OnClickListener() {
		            @Override
		            public void onClick(View v) {
		                if (is_friend) {
		                    Intent intent = new Intent();
		                    intent.putExtra("userId", id);	           
		                    intent.putExtra("userNick", user.getNick());
		                    intent.setClass(Activity_UserInfo.this, Activity_Chat.class);
		                    startActivity(intent);
		                } else {

		                    Intent intent = new Intent();
		                    intent.putExtra("id", id);
		                    intent.putExtra("avatar", user.getAvatar());
		                    intent.putExtra("nick", user.getNick());
		                    intent.setClass(Activity_UserInfo.this,
		                            Activity_Friends_Add.class);
		                    startActivity(intent);

		                }
		            }

		        });
		}

		 private void getData() {
				// TODO Auto-generated method stub
				 Map<String, String> maps = new HashMap<String, String>();		 
			     maps.put("uid", id);
			     LoadDataFromHTTP task = new LoadDataFromHTTP(
			                context, Constant.URL_GET_SELFINFO, maps);
			     task.getData(new DataCallBack() {			
					@Override
					public void onDataCallBack(JSONObject data) {
						// TODO Auto-generated method stub
						try {
		                    int code = data.getInteger("status");
		                    if (code == 0) {
		                    	JSONObject json=data.getJSONObject("user_action");
		                    	JSONObject info=json.getJSONObject("info");
		                    	String name=info.getString("name");
		                        user=new UserDetail();
		                    	if(name!=null&&name!=""){
		                    		user.setNick(name);
		                    		user.setSex(info.getString("sex"));
		                    		user.setAvatar(info.getString("avatar"));
		                    		user.setC_class(info.getString("class"));
		                    		user.setEmail(info.getString("email"));
		                    		user.setSchool(info.getString("school"));
		                    		user.setGrade(info.getString("grade"));
		                    		user.setMajor(info.getString("major"));
		                    		user.setUsername(id);
		                    		user.setUserrank(info.getInteger("user_rank"));
		                    		if(is_friend){
		                    			user.setType(1);
		                    		}else{
		                    		    user.setType(22);
		                    		}
		                    		UserDao dao=new UserDao(context);               		
		                    		dao.saveDetail(user);
		                    		initData();	
		                    	}
		                    } else if(code==1){
		                    	Toast.makeText(context, "该用户不存在！",
		                                Toast.LENGTH_SHORT).show();
		                    }else if (code == 2) {

		                        Toast.makeText(context, "信息获取失败失败...",
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
		public void back(View view) {
	        finish();
	    }
	    
	    private void showUserAvatar(final ImageView iamgeView, String avatar) {
	        final String url_avatar = avatar;
	        iamgeView.setTag(url_avatar);
	        if (url_avatar != null && url_avatar.indexOf("http")!=-1) {
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
	        	if(!try_again){
	        		Map<String, String> map = new HashMap<String, String>();           
		            map.put("uid", Constant.UID+"");
		            GetAvatar(map);
	        	}else{
	        		Toast.makeText(context, "头像获取失败！", Toast.LENGTH_LONG)
					.show();
	        	}
	        	
	        }
	    }
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
		                        try_again=true;
					        	showUserAvatar(iv_avatar, info);		        					        	
					        }else {
					        	
					        	
					        }
					  }	 				
				    };
				    
			}.execute(params);}
	    
	    class AddPopWindow extends PopupWindow{
	    	private View conentView;   
	    	@SuppressLint("InflateParams")
	    	public AddPopWindow(final Activity context) {
	            LayoutInflater inflater = (LayoutInflater) context
	                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            conentView = inflater.inflate(R.layout.popupwindow_add, null);	     
	            this.setContentView(conentView);
	            this.setWidth(LayoutParams.WRAP_CONTENT);
	            this.setHeight(LayoutParams.WRAP_CONTENT);
	            this.setFocusable(true);
	            this.setOutsideTouchable(true);
	            // 刷新状态
	            this.update();
	            ColorDrawable dw = new ColorDrawable(0000000000);
	            this.setBackgroundDrawable(dw);	  
	            // 设置SelectPicPopupWindow弹出窗体动画效果
	            this.setAnimationStyle(R.style.AnimationPreview);
	            
	            
	            RelativeLayout   re_beizhu =(RelativeLayout) conentView.findViewById(R.id.re_beizhu);
	            RelativeLayout   re_delete =(RelativeLayout) conentView.findViewById(R.id.re_delete);
	            re_beizhu.setOnClickListener(new OnClickListener(){
	                @Override
	                public void onClick(View v) {
	                    showNameAlert();
	                    AddPopWindow.this.dismiss();	              
	                }
	                
	            } );
	            re_delete.setOnClickListener(new OnClickListener(){

	                @Override
	                public void onClick(View v) {
	                	showDeleteAlert();
	                    AddPopWindow.this.dismiss();
	                    
	                }
	                
	            } );     
	        }

	        /**
	         * 显示popupWindow
	         * 
	         * @param parent
	         */
	        public void showPopupWindow(View parent) {
	            if (!this.isShowing()) {
	                // 以下拉方式显示popupwindow
	                this.showAsDropDown(parent, 0, 0);
	            } else {
	                this.dismiss();
	            }
	        }
	    }
	    private void showNameAlert() {

	    	final AlertDialog dlg = new AlertDialog.Builder(this).create();
	    	dlg.show();
	    	Window window = dlg.getWindow();
	    	// *** 主要就是在这里实现这种效果的.
	    	// 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
	    	window.setContentView(R.layout.beizhu_alertdialog);
	    	// 设置能弹出输入法
	    	dlg.getWindow().clearFlags(
	    	        WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
	    	// 为确认按钮添加事件,执行退出应用操作
	    	Button ok = (Button) window.findViewById(R.id.btn_ok);
	    	final EditText ed_name = (EditText) window.findViewById(R.id.ed_name);
	    	
	    	ok.setOnClickListener(new View.OnClickListener() {
	    	    @SuppressLint("ShowToast")
	    	    public void onClick(View v) {
	    	        final String newName = ed_name.getText().toString().trim();
	    	
	    	        if (TextUtils.isEmpty(newName)) {
	    	            return;
	    	        }
	    	
	    	        try {
	                    UserDao dao=new UserDao(context);
	                    if(dao.updateBeizhu(id, newName)){
	                    	Toast.makeText(context, "修改成功",
		    	                    Toast.LENGTH_LONG).show();
	                    	 User u=MyApplication.getInstance().getContactList().get(id);
	                    	 u.setBeizhu(newName);
	                    	 
	                    	 MyApplication.getInstance().clearContact();
	                    	 tv_name.setText(newName);
	                    	 tv_nick.setVisibility(View.VISIBLE);
	                    }    
	    	        } catch (Exception e) {
	    	            Toast.makeText(context, "修改失败",
	    	                    Toast.LENGTH_LONG).show();
	    	        }
	    	
	    	        dlg.cancel();
	    	    }
	    	});
	    	// 关闭alert对话框架
	    	Button cancel = (Button) window.findViewById(R.id.btn_cancel);
	    	cancel.setOnClickListener(new View.OnClickListener() {
	    	    public void onClick(View v) {
	    	        dlg.cancel();
	    	    }
	    	});

	    }
	    
	    private void showDeleteAlert() {

	    	final AlertDialog dlg = new AlertDialog.Builder(this).create();
	    	dlg.show();
	    	Window window = dlg.getWindow();
	    	// *** 主要就是在这里实现这种效果的.
	    	// 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
	    	window.setContentView(R.layout.info_alertdialog);
	    	// 设置能弹出输入法
	    	dlg.getWindow().clearFlags(
	    	        WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
	    	// 为确认按钮添加事件,执行退出应用操作
	    	Button ok = (Button) window.findViewById(R.id.btn_ok);
	    	TextView title=(TextView)window.findViewById(R.id.tv_title);
	    	title.setText("你确定要删除该好友吗?");
	    	
	    	ok.setOnClickListener(new View.OnClickListener() {
	    	    @SuppressLint("ShowToast")
	    	    public void onClick(View v) {
	    	    	Map<String,String> map=new HashMap<String, String>();
	    	    	map.put("uid", Constant.UID);
	    	    	map.put("friend_id", id);
	    	    	map.put("token", Constant.TOKEN);
	    	        LoadDataFromHTTP task=new LoadDataFromHTTP(context, Constant.URL_DEL_FRIEND, map);
	    	        task.getData(new DataCallBack() {
						
						@Override
						public void onDataCallBack(JSONObject data) {
							// TODO Auto-generated method stub
							try {
							int status=data.getInteger("status");
							switch (status) {
							case 0:
								Toast.makeText(context, "删除成功！", Toast.LENGTH_SHORT).show();
								UserDao dao=new UserDao(context);
								List<String> delFriend=new ArrayList<String>();
								delFriend.add(id);
								ChatEntityDao d=new ChatEntityDao(context);
								d.deleteMessageByUser(id);
								MyApplication.getInstance().clearContact();
								JSONObject obj=new JSONObject();
						        obj.put("action", "delete");
						        obj.put("data", "");
								MainActivity.instance.newMsg("DEL", id, obj.toJSONString(), 17);
								dao.updateUsers(delFriend);
								break;

							case 8:
								Toast.makeText(context, "你们还不是好友！不能执行删除操作！", Toast.LENGTH_SHORT).show();
								break;
						    default:
						    	Toast.makeText(context, "删除失败！稍后重试~~", Toast.LENGTH_SHORT).show();
						    	break;
							}
							}catch(Exception e){
								Toast.makeText(context, "数据解析错误...",
			                            Toast.LENGTH_SHORT).show();
			                    e.printStackTrace();
							}
						}
					});
	    	        dlg.cancel();
	    	    }
	    	});
	    	// 关闭alert对话框架
	    	Button cancel = (Button) window.findViewById(R.id.btn_cancel);
	    	cancel.setOnClickListener(new View.OnClickListener() {
	    	    public void onClick(View v) {
	    	        dlg.cancel();
	    	    }
	    	});

	    }
	    
	}
