package com.eventer.app.other;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.eventer.app.R;
import com.eventer.app.adapter.ChatRoomAdapter;
import com.eventer.app.db.ChatroomDao;
import com.eventer.app.entity.ChatRoom;
import com.eventer.app.widget.swipeback.SwipeBackActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

@SuppressLint({"InflateParams","SetTextI18n"})
public class ChatRoomActivity extends SwipeBackActivity {
    ListView groupListView;
    protected List<ChatRoom> grouplist;
    ChatRoomAdapter groupAdapter;
    TextView tv_total;
    public static ChatRoomActivity instance;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mychatroom);
        setBaseTitle(R.string.chatgroup);
        context=this;
        instance = this;
        ChatroomDao dao=new ChatroomDao(context);
        grouplist = dao.getRoomList();

        groupListView = (ListView) findViewById(R.id.groupListView);
        View headerView = LayoutInflater.from(this).inflate(
                R.layout.item_mychatroom_header, null);
        View footerView = LayoutInflater.from(this).inflate(
                R.layout.item_mychatroom_footer, null);
        tv_total = (TextView) footerView.findViewById(R.id.tv_total);
        tv_total.setText(String.valueOf(grouplist.size()) + "个群聊");
        groupAdapter = new ChatRoomAdapter(this, grouplist);
        groupListView.addHeaderView(headerView);
        groupListView.addFooterView(footerView);
        groupListView.setAdapter(groupAdapter);

        final ImageView iv_add = (ImageView) this.findViewById(R.id.iv_add);
        //ImageView iv_search = (ImageView) this.findViewById(R.id.iv_search);
        iv_add.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,ChatRoomCreatActivity.class));
            }

        });

    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
    }


}
