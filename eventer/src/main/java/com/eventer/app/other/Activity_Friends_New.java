package com.eventer.app.other;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eventer.app.R;
import com.eventer.app.adapter.NewFriendsAdapter;
import com.eventer.app.db.InviteMessgeDao;
import com.eventer.app.entity.InviteMessage;
import com.eventer.app.widget.swipeback.SwipeBackActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

public class Activity_Friends_New extends SwipeBackActivity {
    ListView listView;
    RelativeLayout contact_rl;
    private Context context;
    NewFriendsAdapter adapter;
    private List<InviteMessage> msgs;
    InviteMessgeDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newfriendsmsg);
        // DemoApplication.getInstance().addActivity(this);
        context=Activity_Friends_New.this;
        setBaseTitle(R.string.new_friend);
        initView();
    }

    private void initView() {
        // TODO Auto-generated method stub
        listView = (ListView) findViewById(R.id.listview);
        contact_rl=(RelativeLayout)findViewById(R.id.addFriend_contact_rl);
        TextView et_search = (TextView) findViewById(R.id.et_search);
        //跳转至搜索好友
        et_search.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(Activity_Friends_New.this,
                        Activity_Friends_Search.class));
            }

        });
        //跳转至手机联系人
        contact_rl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                startActivity(new Intent(Activity_Friends_New.this,
                        LocalContactActivity.class));
            }
        });
        //加载近期好友请求的信息
        dao = new InviteMessgeDao(this);
        msgs = dao.getMessagesList();
        adapter = new NewFriendsAdapter(this, msgs);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                InviteMessage msg=msgs.get(position);
                Intent intent=new Intent();
                intent.putExtra("user", msg.getId()+"");
                intent.setClass(context,Activity_UserInfo.class);
                startActivity(intent);
            }
        });

    }

    public void back(View v) {
        finish();
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
