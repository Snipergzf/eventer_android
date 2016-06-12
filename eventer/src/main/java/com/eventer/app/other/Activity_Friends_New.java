package com.eventer.app.other;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.eventer.app.R;
import com.eventer.app.adapter.NewFriendsAdapter;
import com.eventer.app.db.InviteMessgeDao;
import com.eventer.app.entity.InviteMessage;
import com.eventer.app.main.BaseActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

public class Activity_Friends_New extends BaseActivity {
ListView listView;
    private Context context;
    NewFriendsAdapter adapter;
    private List<InviteMessage> msgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newfriendsmsg);
        context=Activity_Friends_New.this;
        setBaseTitle(R.string.new_friend);
        initView();
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.listview);
        listView.setEmptyView(findViewById(R.id.tv_empty));
        //加载近期好友请求的信息
        InviteMessgeDao dao = new InviteMessgeDao(this);
        msgs = dao.getMessagesList();
        adapter = new NewFriendsAdapter(this, msgs);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                InviteMessage msg=msgs.get(position);
                Intent intent=new Intent();
                intent.putExtra("user", msg.getId()+"");
                intent.setClass(context,Activity_UserInfo.class);
                startActivity(intent);
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
