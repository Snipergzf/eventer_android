package com.eventer.app.other;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.eventer.app.Constant;
import com.eventer.app.R;
import com.eventer.app.task.LoadDataFromHTTP;
import com.eventer.app.task.LoadDataFromServer;
import com.eventer.app.task.LoadDataFromServer.DataCallBack;
import com.eventer.app.util.LocalUserInfo;

public class UpdateNickActivity extends Activity{
	private Context context;
	private String nick="";
	private EditText et_nick;
	private TextView tv_save;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_nick);
        context=this;
        nick=LocalUserInfo.getInstance(UpdateNickActivity.this).getUserInfo("nick");
        initView();
    }
    
    private void initView() {
		// TODO Auto-generated method stub
    	 et_nick= (EditText) this.findViewById(R.id.et_nick);
         et_nick.setText(nick);
         tv_save= (TextView) this.findViewById(R.id.tv_save);
         tv_save.setOnClickListener(new OnClickListener(){
         @Override
         public void onClick(View v) {
             String newNick=et_nick.getText().toString().trim();
             if(nick.equals(newNick)||newNick.equals("")||newNick.equals("0")) {
                 return;
             }  
             updateNick(newNick);
         }
           
       });
	}

	public void updateNick(final String newNick) {
        Map<String, String> map = new HashMap<String, String>();

        map.put("sex", "");
        map.put("uid", Constant.UID+"");
	    map.put("token", Constant.TOKEN);
	    map.put("name",newNick);
	    map.put("email", "");
	    map.put("grade","");
	    map.put("school", "");
	    map.put("major", "");
	    map.put("class", "");
	    map.put("user_rank", "0");
        LoadDataFromHTTP task = new LoadDataFromHTTP(
                context, Constant.URL_UPDATE_SELFINFO, map);

        task.getData(new com.eventer.app.task.LoadDataFromHTTP.DataCallBack() {

            @SuppressLint("ShowToast")
            @Override
            public void onDataCallBack(JSONObject data) {
                try {
                	int code = data.getInteger("status");
                    if (code == 0) {
                    	Toast.makeText(context, "���³ɹ�...",
                                Toast.LENGTH_SHORT).show();
                        LocalUserInfo.getInstance(context)
                                .setUserInfo("nick", newNick);
                        MyUserInfoActivity.instance.refreshNick();
                        finish();
                        
                    } else if (code == 17) {
                        Toast.makeText(context, "����ʧ��,���Ժ����ԣ�",
                                Toast.LENGTH_SHORT).show();
                    } else {

                        Toast.makeText(context, "��������æ������...",
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {

                    Toast.makeText(context, "���ݽ�������...",
                            Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }

        });
    }

    public void back(View view) {

        finish();
    }
}
