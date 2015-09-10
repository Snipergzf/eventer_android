package com.eventer.app.other;

import java.util.List;

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
import com.eventer.app.ui.base.BaseActivity;

@SuppressLint("InflateParams")
public class ChatRoomActivity extends BaseActivity {
    private ListView groupListView;
    protected List<ChatRoom> grouplist;
    private ChatRoomAdapter groupAdapter;
    TextView tv_total;
    public static ChatRoomActivity instance;
    private Context context;

 
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mychatroom);
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
        tv_total.setText(String.valueOf(grouplist.size()) + "¸öÈºÁÄ");
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
    }

    /**
     * ·µ»Ø
     * 
     * @param view
     */
    public void back(View view) {
        finish();
    }
}
