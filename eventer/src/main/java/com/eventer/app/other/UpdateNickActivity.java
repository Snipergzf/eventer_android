package com.eventer.app.other;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.eventer.app.Constant;
import com.eventer.app.R;
import com.eventer.app.http.LoadDataFromHTTP;
import com.eventer.app.main.BaseActivity;
import com.eventer.app.util.LocalUserInfo;
import com.eventer.app.view.MyToast;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

public class UpdateNickActivity extends BaseActivity {
    private Context context;
    private String nick="";
    private EditText et_nick;
    TextView tv_save;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_nick);
        setBaseTitle(R.string.update_nick);
        context = this;
        nick = LocalUserInfo.getInstance(UpdateNickActivity.this).getUserInfo(
                "nick");
        initView();
    }

    /***
     * 初始化控件，给控件添加点击响应
     */
    private void initView() {
        // TODO Auto-generated method stub
        et_nick= (EditText) this.findViewById(R.id.et_nick);
        tv_save= (TextView) this.findViewById(R.id.tv_save);
        tv_save.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                String newNick = et_nick.getText().toString().trim();
                if(nick.equals(newNick) || newNick.equals("")) {
                    return;
                }
                updateNick(newNick);
            }

        });
    }
    /***
     * 向服务器发送请求，修改昵称
     */
    public void updateNick(final String newNick) {
        Map<String, String> map = new HashMap<>();
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
        task.getData(new com.eventer.app.http.LoadDataFromHTTP.DataCallBack() {

            @SuppressLint("ShowToast")
            @Override
            public void onDataCallBack(JSONObject data) {
                try {
                    int code = data.getInteger("status");
                    if (code == 0) {
                        MyToast.makeText(context, "昵称修改成功~",
                                Toast.LENGTH_SHORT).show();
                        LocalUserInfo.getInstance(context)
                                .setUserInfo("nick", newNick);
                        MyUserInfoActivity.instance.refreshNick();
                        finish();

                    } else {
                        if(!Constant.isConnectNet){
                            MyToast.makeText(context, getText(R.string.no_network), Toast.LENGTH_SHORT).show();
                        }else{
                            MyToast.makeText(context, "更新失败,请稍后重试",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    MyToast.makeText(context, "更新失败,请稍后重试",
                            Toast.LENGTH_SHORT).show();
                }

            }

        });
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
