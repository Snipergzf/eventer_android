package com.eventer.app.other;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.util.HanziToPinyin;
import com.easemob.util.HanziToPinyin.Token;
import com.eventer.app.R;
import com.eventer.app.adapter.ContactAdapter;
import com.eventer.app.db.UserDao;
import com.eventer.app.entity.User;
import com.eventer.app.main.BaseActivity;
import com.eventer.app.view.Sidebar;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
@SuppressLint({"SimpleDateFormat","SetTextI18n"})
public class Activity_Contact extends BaseActivity{

    private ContactAdapter adapter;
    private List<User> contactList = new ArrayList<>();
    ListView listView;
    Sidebar sidebar;
    private TextView tv_total;
    LayoutInflater infalter;
    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contactlist);
        context = this;
        setBaseTitle(R.string.contact);
        initView();
        initData();

    }



    /***
     * 初始化控件，给控件添加事件响应
     */
    private void initView() {

        listView = (ListView) findViewById(R.id.list);

        infalter=LayoutInflater.from(this);
        View headView = infalter.inflate(R.layout.item_contact_list_header,
                listView, false);
        listView.addHeaderView(headView);

        View footerView = infalter.inflate(R.layout.item_contact_list_footer,
                listView, false);
        listView.addFooterView(footerView);
        tv_total = (TextView) footerView.findViewById(R.id.tv_total);


        sidebar = (Sidebar) findViewById(R.id.sidebar);
        sidebar.setListView(listView);

        // 设置通讯录的adapter
        adapter = new ContactAdapter(this, R.layout.item_contact_list,
                contactList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if(position!=0&&position!=contactList.size()+1){
                    User user=contactList.get(position-1);
                    Intent intent=new Intent();
                    intent.setClass(Activity_Contact.this,Activity_UserInfo.class);
                    intent.putExtra("user", user.getUsername());
                    startActivity(intent);
                }
            }
        });



        RelativeLayout re_newfriends = (RelativeLayout) headView.findViewById(R.id.re_newfriends);
        RelativeLayout re_chatroom = (RelativeLayout) headView.findViewById(R.id.re_chatroom);
        RelativeLayout re_phone = (RelativeLayout) headView.findViewById(R.id.re_phone);
        RelativeLayout re_search = (RelativeLayout) headView.findViewById(R.id.re_search);
        re_newfriends.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                startActivity(new Intent(Activity_Contact.this,Activity_Friends_New.class));

            }

        });
        re_chatroom.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,ChatRoomActivity.class));
            }

        });
        re_phone.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,LocalContactActivity.class));
            }

        });
        re_search.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,Activity_Friends_Search.class));
            }

        });
    }

    /***
     * 加载数据
     */
    private void initData() {
        getContactList();
        adapter.notifyDataSetChanged();
        tv_total.setText(String.valueOf(contactList.size()) + "位联系人");
    }

    /**
     * 获取联系人列表，并排序
     */
    private void getContactList() {
        contactList.clear();
        // 获取本地好友列表
        UserDao dao=new UserDao(context);
        List<User> users=dao.getFriendList();
        for (User user : users) {
            contactList.add(user);
        }
        // 对list进行排序
        Collections.sort(contactList, new FullPinyinComparator() {
        });
    }

    /***
     * 通过拼音对用户进行排序
     * @author LiuNana
     *
     */
    public class FullPinyinComparator implements Comparator<User> {

        @Override
        public int compare(User o1, User o2) {
            String py1 = o1.getNick();
            String py2 = o2.getNick();
            py1=getPinYin(py1);
            py2=getPinYin(py2);
            // 判断是否为空""
            if (TextUtils.isEmpty(py1) && isEmpty(py2))
                return 0;
            if (isEmpty(py1))
                return -1;
            if (isEmpty(py2))
                return 1;
            try {
                py1 = py1.toUpperCase();
                py2 = py2.toUpperCase();
            } catch (Exception e) {
                System.out.println("某个str为\" \" 空");
            }
            return py1.compareTo(py2);
        }

        private boolean isEmpty(String str) {
            return "".equals(str.trim());
        }
    }

    /***
     * 获取字符串的拼音
     */
    public static String getPinYin(String input) {
        ArrayList<Token> tokens = HanziToPinyin.getInstance().get(input);


        StringBuilder sb = new StringBuilder();
        if (tokens != null && tokens.size() > 0) {
            for (Token token : tokens) {
                if (Token.PINYIN == token.type) {
                    sb.append(token.target);
                } else {
                    sb.append(token.source);
                }
            }
        }
        return sb.toString().toLowerCase();
    }



    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        initData();
    }

}
