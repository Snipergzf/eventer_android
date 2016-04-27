package com.eventer.app.other;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.eventer.app.Constant;
import com.eventer.app.R;
import com.eventer.app.db.ChatroomDao;
import com.eventer.app.entity.ChatRoom;
import com.eventer.app.http.LoadDataFromHTTP;
import com.eventer.app.main.BaseActivity;

import java.util.HashMap;
import java.util.Map;

public class ResetGroupNameActivity extends BaseActivity{
    private EditText et_name;
    private Button btn_save;
    private Context context;
    private String groupId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_group_name);
        setBaseTitle(R.string.group_name);
        groupId = getIntent().getStringExtra("groupId");
        if(TextUtils.isEmpty(groupId)){
            return;
        }
        context = this;
        initView();
        initData();
    }



    private void initView() {
        et_name = (EditText) findViewById(R.id.et_group_name);
        btn_save = (Button) findViewById(R.id.btn_save);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = et_name.getText().toString();
                if(!TextUtils.isEmpty(name)){
                    updateGroupName(name);
                }else{
                    Toast.makeText(context, "群名称不能为空~", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void updateGroupName(final String name) {

        Map<String, String> map = new HashMap<>();
        map.put("group_id", groupId);
        map.put("uid", Constant.UID);
        map.put("update_item",name);
        map.put("token", Constant.TOKEN);
        LoadDataFromHTTP task = new LoadDataFromHTTP(
                context, Constant.URL_UPDATE_GROUP_UPDATE, map);
        task.getData(new LoadDataFromHTTP.DataCallBack() {
            @SuppressLint("ShowToast")
            @Override
            public void onDataCallBack(JSONObject data) {
                try {
                    int status = data.getInteger("status");
                    if (status == 0) {
                        ContentValues values = new ContentValues();
                        values.put(ChatroomDao.COLUMN_NAME_ROOMNAME, name);
                        ChatroomDao dao = new ChatroomDao(context);
                        dao.update(values, groupId);
                        Toast.makeText(context, "群名更新成功!",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else if (status == 27) {

                        Toast.makeText(context, "不存在该群组信息...",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else {

                        Toast.makeText(context, "服务器繁忙请重试...",
                                Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(context, "群名更新失败...",
                            Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }

    private void initData() {

        ChatroomDao dao = new ChatroomDao(context);
        ChatRoom room = dao.getRoom(groupId);
        if(room != null){
            String groupName = room.getRoomname();
            if(!TextUtils.isEmpty(groupName)){
                et_name.setText(groupName);
                CharSequence text = et_name.getText();
                if (text instanceof Spannable) {
                    Spannable spanText = (Spannable)text;
                    Selection.setSelection(spanText, text.length());
                }
           }
        }
    }

}
