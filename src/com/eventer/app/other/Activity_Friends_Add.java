package com.eventer.app.other;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eventer.app.Constant;
import com.eventer.app.MyApplication;
import com.eventer.app.R;
import com.eventer.app.db.InviteMessgeDao;
import com.eventer.app.entity.InviteMessage;
import com.eventer.app.entity.InviteMessage.InviteMesageStatus;
import com.eventer.app.http.HttpUnit;
import com.eventer.app.main.MainActivity;
import com.eventer.app.util.LocalUserInfo;

public class Activity_Friends_Add extends Activity {
	private TextView tv_send;
	private EditText et_reason;
	private Context context;
	private String nick;
	private String avatar;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriends_final);
        
        context=this;
        final String id =this.getIntent().getStringExtra("id");
        nick=getIntent().getStringExtra("nick");
        avatar=getIntent().getStringExtra("avatar");
        Log.e("1", nick+")))"+avatar);
        tv_send= (TextView) this.findViewById(R.id.tv_send);
        et_reason= (EditText) this.findViewById(R.id.et_reason);
        
        tv_send.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                addContact(id,et_reason.getText().toString().trim());
            }
            
        });
    }
    
    /**
     * 娣诲姞contact
     * 
     * @param view
     */
    @SuppressLint("ShowToast")
    public void addContact(final String glufine_id,final String myreason) {
        if (glufine_id == null || glufine_id.equals("")) {
            return;
        }

        if (glufine_id.equals(Constant.UID+"")) {
//            startActivity(new Intent(this, AlertDialog.class).putExtra("msg",
//                    "不能添加自己"));
            Toast.makeText(getApplicationContext(),
                    "不能添加自己", Toast.LENGTH_LONG).show();
            return;
        }

        if (MyApplication.getInstance().getContactIDList().contains(glufine_id)) {
//            startActivity(new Intent(this, AlertDialog.class).putExtra("msg",
//                    "此用户已是你的好友"));
        	Toast.makeText(getApplicationContext(),
                    "此用户已是你的好友", Toast.LENGTH_LONG).show();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在发送请求...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        
       
        new Thread(new Runnable() {
            public void run() {

                try {
                    // 在reason封装请求者的昵称/头像/时间等信息，在通知中显示

                	Map<String,String> params=new HashMap<String,String>();
        			params.put("uid", Constant.UID+"");
        			params.put("token", Constant.TOKEN);
        			params.put("friend_id", glufine_id);
        			Map<String,Object> result=HttpUnit.sendFriendRequest(params);
        			int status=(int)result.get("status");
        			final String info=(String)result.get("info");
        			if(status==0){
        				String reason= et_reason.getText().toString().trim();
        				JSONObject send_json = new JSONObject();
        			    send_json.put("action", "friend_request");
        			    send_json.put("type", 1);
        			    send_json.put("data", reason);
        			    send_json.put("certificate", info);
        			    send_json.put("name", LocalUserInfo.getInstance(context)
        			    		.getUserInfo("nick"));
        			    send_json.put("avatar",LocalUserInfo.getInstance(context)
        		                .getUserInfo("avatar"));
        			    send_json.put("user_rank",LocalUserInfo.getInstance(context)
        		                .getUserInfo("user_rank"));
        				String send_body = send_json.toString();
        				
        				InviteMessage invite = new InviteMessage();														
						invite.setId(Integer.parseInt(glufine_id));
						invite.setReason(reason);
						invite.setCertification(info);
						invite.setTime(System.currentTimeMillis()/1000);
						invite.setFrom(nick);
						invite.setAvatar(avatar);
						Log.e("1", nick+")))"+avatar);
						invite.setStatus(InviteMesageStatus.INVITE);
						InviteMessgeDao dao=new InviteMessgeDao(context);
						dao.saveMessage(invite);
        				MainActivity.instance.newMsg("ADD", glufine_id, send_body,1|16);
        				runOnUiThread(new Runnable() {
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(),
                                        "发送请求成功,等待对方验证", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent();
                                intent.setClass(context, Activity_Friends_New.class);
                                startActivity(intent);                  
                                finish();
                            }
                        });
        			}else {
        				runOnUiThread(new Runnable() {
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(),
                                        info, Toast.LENGTH_SHORT).show();
                                
                                finish();
                            }
                        });
        			}
        			
                    

                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),
                                    "请求添加好友失败!", Toast.LENGTH_SHORT ).show();
                        }
                    });
                }
            }
        }).start();
    }
    
    public void back(View view ){
        
        finish();
    }
}
