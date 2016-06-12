package com.eventer.app.other;

import android.annotation.SuppressLint;
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
import com.eventer.app.view.swipeback.SwipeBackActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

@SuppressLint({"InflateParams","SetTextI18n"})
public class ChatRoomActivity extends SwipeBackActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mychatroom);
        setBaseTitle(R.string.chatgroup);
        initView();
    }

    private void initView() {
        ChatroomDao dao = new ChatroomDao(this);
        List<ChatRoom> grouplist = dao.getRoomList();

        ListView groupListView = (ListView) findViewById(R.id.groupListView);

        View footerView = LayoutInflater.from(this).inflate(
                R.layout.item_mychatroom_footer, null);
        TextView tv_total = (TextView) footerView.findViewById(R.id.tv_total);
        tv_total.setText(
                String.valueOf(grouplist.size()) + "个群聊");

        ChatRoomAdapter groupAdapter = new ChatRoomAdapter(this, grouplist);

        groupListView.addFooterView(footerView);
        groupListView.setAdapter(groupAdapter);
        groupListView.setEmptyView(findViewById(R.id.tv_empty));

        ImageView iv_add = (ImageView) this.findViewById(R.id.iv_add);
        iv_add.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatRoomActivity.this,
                        ChatRoomCreatActivity.class));
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
    }


}
