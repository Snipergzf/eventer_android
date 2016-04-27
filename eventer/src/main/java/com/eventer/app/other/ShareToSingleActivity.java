package com.eventer.app.other;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.eventer.app.Constant;
import com.eventer.app.R;
import com.eventer.app.adapter.PickContactAdapter;
import com.eventer.app.db.ChatEntityDao;
import com.eventer.app.db.EventDao;
import com.eventer.app.db.SchedualDao;
import com.eventer.app.db.UserDao;
import com.eventer.app.entity.ChatEntity;
import com.eventer.app.entity.Event;
import com.eventer.app.entity.Schedual;
import com.eventer.app.entity.User;
import com.eventer.app.http.HttpParamUnit;
import com.eventer.app.http.LoadDataFromHTTP;
import com.eventer.app.http.LoadDataFromHTTP.DataCallBack;
import com.eventer.app.main.MainActivity;
import com.eventer.app.util.LocalUserInfo;
import com.eventer.app.view.swipeback.SwipeBackActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
@SuppressLint("SetTextI18n")
public class ShareToSingleActivity extends SwipeBackActivity {
    private ImageView iv_search;
    private TextView tv_checked;
    private ListView listView;

    private PickContactAdapter contactAdapter;
    /** group中一开始就有的成员 */
    List<String> exitingMembers = new ArrayList<>();
    // 可滑动的显示选中用户的View
    private LinearLayout menuLinerLayout;
    // 选中用户总数,右上角显示
    int total = 0;
    ProgressDialog progressDialog;

    // 添加的列表
    private List<String> addList = new ArrayList<>();
    private Context context;
    private String eid,sid;
    private Event event;
    private Schedual schedual;
    private int shareType=0;
    public static int SHARE_EVENT=2;
    public static int SHARE_SCHEDUAL=3;
    public static ShareToSingleActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_to_single);
        setBaseTitle(R.string.share_activity);
        context=this;
        instance=this;
        progressDialog = new ProgressDialog(this);
        shareType=getIntent().getIntExtra("sharetype", 0);
        Log.e("type",shareType+"");
        if(shareType == SHARE_EVENT){
            eid=getIntent().getStringExtra("event_id");
            if(!TextUtils.isEmpty(eid)){
                EventDao d=new EventDao(context);
                event=d.getEvent(eid);
            }
            if(event==null)
                finish();
        }
        else if(shareType == SHARE_SCHEDUAL){

            sid=getIntent().getStringExtra("schedual_id");
            Log.e("id",sid);
            if(!TextUtils.isEmpty(sid)){
                SchedualDao dao=new SchedualDao(context);
                schedual = dao.getSchedualById(sid);
            }
            if(schedual==null)
            {
                Toast.makeText(context, "日程不存在或者已过期~", Toast.LENGTH_SHORT).show();
                finish();
            }

        }else{
            finish();
        }

        tv_checked = (TextView) this.findViewById(R.id.tv_checked);
        // 获取好友列表
        final List<User> alluserList = new ArrayList<>();
        UserDao dao=new UserDao(context);
        List<User> users=dao.getFriendList();
        for (User user : users) {
            alluserList.add(user);
        }

        // 对list进行排序
        Collections.sort(alluserList, new PinyinComparator() {
        });

        listView = (ListView) findViewById(R.id.list);

        menuLinerLayout = (LinearLayout) this
                .findViewById(R.id.linearLayoutMenu);

        final EditText et_search = (EditText) this.findViewById(R.id.et_search);

        et_search.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.length() > 0) {
                    String str_s = et_search.getText().toString().trim();
                    List<User> users_temp = new ArrayList<>();
                    for (User user : alluserList) {
                        String usernick = user.getNick();
                        if (usernick.contains(str_s)) {

                            users_temp.add(user);
                        }
                        contactAdapter = new PickContactAdapter(
                                context,
                                R.layout.item_contactlist_listview_checkbox,
                                users_temp);
                        listView.setAdapter(contactAdapter);
                    }

                } else {
                    contactAdapter = new PickContactAdapter(
                            context,
                            R.layout.item_contactlist_listview_checkbox,
                            alluserList);
                    listView.setAdapter(contactAdapter);
                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {

            }
        });
        contactAdapter = new PickContactAdapter(this,
                R.layout.item_contactlist_listview_checkbox, alluserList);
        listView.setAdapter(contactAdapter);

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                checkBox.toggle();

            }
        });
        tv_checked.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                save();
            }

        });

        iv_search = (ImageView) this.findViewById(R.id.iv_search);
    }

    // 即时显示被选中用户的头像和昵称。

    public void showCheckImage(Bitmap bitmap, User glufineid) {
        if (exitingMembers.contains(glufineid.getUsername())) {
            return;
        }
        if (addList.contains(glufineid.getUsername())) {
            return;
        }
        total++;

        // 包含TextView的LinearLayout
        // 参数设置
        android.widget.LinearLayout.LayoutParams menuLinerLayoutParames = new LinearLayout.LayoutParams(
                108, 108, 1);
        View view = LayoutInflater.from(this).inflate(
                R.layout.item_chatroom_header_item_1, new LinearLayout(this), false);
        ImageView images = (ImageView) view.findViewById(R.id.iv_avatar1);
        menuLinerLayoutParames.setMargins(6, 6, 6, 6);

        // 设置id，方便后面删除
        view.setTag(glufineid);
        if (bitmap == null) {
            images.setImageResource(R.drawable.default_avatar);
        } else {
            images.setImageBitmap(bitmap);
        }

        menuLinerLayout.addView(view, menuLinerLayoutParames);
        tv_checked.setText("分享(" + total + ")");
        if (total > 0) {
            if (iv_search.getVisibility() == View.VISIBLE) {
                iv_search.setVisibility(View.GONE);
            }
        }
        addList.add(glufineid.getUsername());
    }

    public void deleteImage(User glufineid) {
        View view =  menuLinerLayout.findViewWithTag(glufineid);
        menuLinerLayout.removeView(view);
        total--;
        tv_checked.setText("确定(" + total + ")");
        addList.remove(glufineid.getUsername());
        if (total < 1) {
            if (iv_search.getVisibility() == View.GONE) {
                iv_search.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 确认选择的members
     *
     */
    public void save() {
        if (addList.size() == 0) {
            Toast.makeText(context, "请选择用户",
                    Toast.LENGTH_LONG).show();
            return;
        }
        if (!Constant.isConnectNet) {
            Toast.makeText(context, getText(R.string.no_network), Toast.LENGTH_SHORT).show();
            return;
        }
        String shareTo = null;
        if (shareType == SHARE_EVENT) {
            for (String user : addList) {
                String body ;
                String content ;
                int type ;
                String shareId = "";
                try {
                    JSONObject content_json = new JSONObject();

                    content_json.put("event_id", event.getEventID());
                    content_json.put("event_title", event.getTitle());
                    type = 2;
                    ShareFeedBack();
                    JSONObject send_json = new JSONObject();
                    send_json.put("action", "send");
                    send_json.put("data", content_json);
                    send_json.put("shareId", shareId);
                    send_json.put("type", type);
                    content = content_json.toJSONString();
                    body = send_json.toJSONString();
                    Log.e("1", body);
                    ChatEntity msg = new ChatEntity();
                    long time = System.currentTimeMillis();
                    msg.setType(shareType);
                    msg.setFrom(user);
                    msg.setContent(content);
                    msg.setMsgTime(time / 1000);
                    msg.setStatus(2);
                    msg.setMsgID(time);
                    msg.setShareId(shareId);
                    ChatEntityDao dao = new ChatEntityDao(context);
                    dao.saveMessage(msg);
                    MainActivity.instance.newMsg("1", user, body, 17);


                shareTo = user;

            }catch(JSONException e){
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            }
            startActivity(new Intent().setClass(context, Activity_Chat.class)
                    .putExtra("userId", shareTo));
            finish();
        }else if (shareType == SHARE_SCHEDUAL) {
            if(TextUtils.isEmpty(schedual.getShareId())){
                creatGroupSchedual(schedual);
            }else{
                shareGroupSchedual(schedual.getShareId());
            }
        }

    }

    private void shareGroupSchedual(String a_id) {
        String shareTo = null;
        for (String room : addList) {
            JSONObject content_json = new JSONObject();
            content_json.put("member", Constant.UID);
            content_json.put("event_id", a_id);
            content_json.put("event_name", schedual.getTitle());
            content_json.put("nick", LocalUserInfo.getInstance(context).getUserInfo("nick"));
            JSONObject send_json = new JSONObject();
            send_json.put("action", "send");
            send_json.put("data", content_json);
            send_json.put("type", 24);
            String content=content_json.toJSONString();
            String body = send_json.toJSONString();
            ChatEntity msg=new ChatEntity();
            long time=System.currentTimeMillis();
            msg.setType(24);
            msg.setFrom(room);
            msg.setContent(content);
            msg.setMsgTime(time/1000);
            msg.setStatus(2);
            msg.setMsgID(time);
            msg.setShareId(a_id);
            ChatEntityDao dao1 =new ChatEntityDao(context);
            dao1.saveMessage(msg);
            MainActivity.instance.newMsg("1", room, body, 17);
            shareTo=room;
        }
        if(!TextUtils.isEmpty(shareTo)){
            startActivity(new Intent().setClass(context, Activity_Chat.class)
                    .putExtra("userId", shareTo));
            finish();
        }
    }

    private void creatGroupSchedual(final Schedual schedual){
        Map<String,String> map= HttpParamUnit.activityCreate(schedual);
        LoadDataFromHTTP task=new LoadDataFromHTTP(context, Constant.URL_ACTIVITY_CREATE, map);
        task.getData(new DataCallBack() {
            @Override
            public void onDataCallBack(JSONObject data) {
                // TODO Auto-generated method stub
                try {
                    int status = data.getInteger("status");
                    if (status == 0) {
                        Log.e("create activity", status + "");

                        JSONObject action = data.getJSONObject("web_action");
                        String a_id = action.getString("a_id");
                        Schedual s=schedual;
                        s.setFriend(Constant.UID);
                        s.setShareId(a_id);
                        s.setSharer(Constant.UID);
                        SchedualDao dao=new SchedualDao(context);
                        dao.saveSchedual(s);
                        shareGroupSchedual(a_id);
                    }else if(status == 34){
                        Toast.makeText(context, "分享失败", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context, "发生异常~", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    Toast.makeText(context, "error~", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void ShareFeedBack(){
        Map<String,String> map= HttpParamUnit.eventAddFeedback(eid, "1", "", "");
        LoadDataFromHTTP task=new LoadDataFromHTTP(context, Constant.URL_SEND_EVENT_FEEDBACK, map);
        task.getData(new DataCallBack() {
            @Override
            public void onDataCallBack(JSONObject data) {
                // TODO Auto-generated method stub
                try {
                    int status = data.getInteger("status");
                    if (status == 0) {
                        Log.e("1", status + "");
                    }

                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        });
    }


    @SuppressLint("DefaultLocale")
    public class PinyinComparator implements Comparator<User> {

        @SuppressLint("DefaultLocale")
        @Override
        public int compare(User o1, User o2) {
            // TODO Auto-generated method stub
            String py1 = o1.getHeader();
            String py2 = o2.getHeader();
            // 判断是否为空""
            if (isEmpty(py1) && isEmpty(py2))
                return 0;
            if (isEmpty(py1))
                return -1;
            if (isEmpty(py2))
                return 1;
            String str1 = "";
            String str2 = "";
            try {
                str1 = ((o1.getHeader()).toUpperCase()).substring(0, 1);
                str2 = ((o2.getHeader()).toUpperCase()).substring(0, 1);
            } catch (Exception e) {
                System.out.println("某个str为\" \" 空");
            }
            return str1.compareTo(str2);
        }

        private boolean isEmpty(String str) {
            return "".equals(str.trim());
        }
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
