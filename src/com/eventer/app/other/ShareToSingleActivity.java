package com.eventer.app.other;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import com.eventer.app.main.MainActivity;

public class ShareToSingleActivity extends Activity {
	private ImageView iv_search;
    private TextView tv_checked;
    private ListView listView;
    /** �Ƿ�Ϊ��ѡ */
    private boolean isSignleChecked;
    private PickContactAdapter contactAdapter;
    /** group��һ��ʼ���еĳ�Ա */
    private List<String> exitingMembers = new ArrayList<String>();
    // �ɻ�������ʾѡ���û���View
    private LinearLayout menuLinerLayout;
    // ѡ���û�����,���Ͻ���ʾ
    int total = 0;
    private ProgressDialog progressDialog;

    // ��ӵ��б�
    private List<String> addList = new ArrayList<String>();
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
        context=this;    
        instance=this;        
        progressDialog = new ProgressDialog(this);
        shareType=getIntent().getIntExtra("sharetype", 0);
        if(shareType==SHARE_EVENT){
        	 eid=getIntent().getStringExtra("event_id");
        	 if(!TextUtils.isEmpty(eid)){
             	EventDao d=new EventDao(context);
         		event=d.getEvent(eid);
             }
        	 if(event==null)
        		 finish();
        }
       else if(shareType==SHARE_SCHEDUAL){
    	   sid=getIntent().getStringExtra("schedual_id");
    	   if(!TextUtils.isEmpty(sid)){
           	SchedualDao dao=new SchedualDao(context);
           	schedual=dao.getSchedual(sid);
           }
    	   if(schedual==null)
    		   finish();
      }else{
    	  finish();
      }
        
        tv_checked = (TextView) this.findViewById(R.id.tv_checked);
        // ��ȡ�����б�
        final List<User> alluserList = new ArrayList<User>();
        UserDao dao=new UserDao(context);
        List<User> users=dao.getFriendList();
        for (User user : users) {
        	alluserList.add(user);
		}
 
        // ��list��������
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
                    List<User> users_temp = new ArrayList<User>();
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

    // ��ʱ��ʾ��ѡ���û���ͷ����ǳơ�

    public void showCheckImage(Bitmap bitmap, User glufineid) {
        if (exitingMembers.contains(glufineid.getUsername())) {
            return;
        }
        if (addList.contains(glufineid.getUsername())) {
            return;
        }
        total++;

        // ����TextView��LinearLayout
        // ��������
        android.widget.LinearLayout.LayoutParams menuLinerLayoutParames = new LinearLayout.LayoutParams(
                108, 108, 1);
        View view = LayoutInflater.from(this).inflate(
                R.layout.item_chatroom_header_item_1, null);
        ImageView images = (ImageView) view.findViewById(R.id.iv_avatar1);
        menuLinerLayoutParames.setMargins(6, 6, 6, 6);

        // ����id���������ɾ��
        view.setTag(glufineid);
        if (bitmap == null) {
            images.setImageResource(R.drawable.default_avatar);
        } else {
            images.setImageBitmap(bitmap);
        }

        menuLinerLayout.addView(view, menuLinerLayoutParames);
        tv_checked.setText("����(" + total + ")");
        if (total > 0) {
            if (iv_search.getVisibility() == View.VISIBLE) {
                iv_search.setVisibility(View.GONE);
            }
        }
        addList.add(glufineid.getUsername());
    }

    public void deleteImage(User glufineid) {
        View view = (View) menuLinerLayout.findViewWithTag(glufineid);
        menuLinerLayout.removeView(view);
        total--;
        tv_checked.setText("ȷ��(" + total + ")");
        addList.remove(glufineid.getUsername());
        if (total < 1) {
            if (iv_search.getVisibility() == View.GONE) {
                iv_search.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * ȷ��ѡ���members
     * 
     * @param v
     */
    public void save() {
        if (addList.size() == 0) {
            Toast.makeText(context, "��ѡ���û�",
                    Toast.LENGTH_LONG).show();
            return;
        }

        for (String user : addList) {
        	String body="";
        	String content="";
        	try {	
	        	JSONObject content_json = new JSONObject();
	        	if(shareType==SHARE_EVENT){
        			content_json.put("event_id", event.getEventID());
    	        	content_json.put("event_title", event.getTitle());
        		}else if(shareType==SHARE_SCHEDUAL){
        			content_json.put("schedual_place", schedual.getPlace());
        			content_json.put("schedual_detail", schedual.getDetail());
        			content_json.put("schedual_title", schedual.getTitle());
        			content_json.put("schedual_time", schedual.getStarttime());
        			content_json.put("schedual_f", schedual.getFrequency());
        		}
	        	JSONObject send_json = new JSONObject();
	        	send_json.put("action", "send");			
				send_json.put("data", content_json);
				content=content_json.toJSONString();
				body = send_json.toJSONString();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.e("1",body);
			ChatEntity msg=new ChatEntity();
            long time=System.currentTimeMillis();
            msg.setType(shareType);
            msg.setFrom(user);
            msg.setContent(content);
            msg.setMsgTime(time/1000);
            msg.setStatus(2);
            msg.setMsgID(time);
            ChatEntityDao dao =new ChatEntityDao(context);
            dao.saveMessage(msg);
			MainActivity.instance.newMsg(shareType+"", user, body, 17);
			
		}
        finish();

    }

    public void back(View view) {
        finish();
    }

    @SuppressLint("DefaultLocale")
    public class PinyinComparator implements Comparator<User> {

        @SuppressLint("DefaultLocale")
        @Override
        public int compare(User o1, User o2) {
            // TODO Auto-generated method stub
            String py1 = o1.getHeader();
            String py2 = o2.getHeader();
            // �ж��Ƿ�Ϊ��""
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
                System.out.println("ĳ��strΪ\" \" ��");
            }
            return str1.compareTo(str2);
        }

        private boolean isEmpty(String str) {
            return "".equals(str.trim());
        }
    }

}
