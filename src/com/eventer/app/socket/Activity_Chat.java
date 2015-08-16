
package com.eventer.app.socket;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.easemob.chat.EMChatManager;
import com.eventer.app.Constant;
import com.eventer.app.MyApplication;
import com.eventer.app.R;
import com.eventer.app.adapter.ExpressionAdapter;
import com.eventer.app.adapter.ExpressionPagerAdapter;
import com.eventer.app.adapter.MessageAdapter;
import com.eventer.app.db.ChatEntityDao;
import com.eventer.app.db.ChatroomDao;
import com.eventer.app.db.UserDao;
import com.eventer.app.entity.ChatEntity;
import com.eventer.app.entity.ChatRoom;
import com.eventer.app.entity.User;
import com.eventer.app.entity.UserInfo;
import com.eventer.app.main.MainActivity;
import com.eventer.app.main.MessageFragment;
import com.eventer.app.other.AddFriendsFinalActivity;
import com.eventer.app.other.ChatRoomSettingActivity;
import com.eventer.app.task.LoadDataFromHTTP;
import com.eventer.app.task.LoadDataFromHTTP.DataCallBack;
import com.eventer.app.util.SmileUtils;
import com.eventer.app.widget.ExpandGridView;
import com.eventer.app.widget.swipeback.SwipeBackActivity;



/**
 * ����ҳ��
 * 
 */
@SuppressLint("HandlerLeak")
@SuppressWarnings("deprecation")
public class Activity_Chat extends SwipeBackActivity implements OnClickListener {

    private static final int REQUEST_CODE_EMPTY_HISTORY = 2;
    public static final int REQUEST_CODE_CONTEXT_MENU = 3;
    public static final int REQUEST_CODE_TEXT = 5;
    public static final int REQUEST_CODE_NET_DISK = 9;
 
    public static final int REQUEST_CODE_SELECT_USER_CARD = 16;
    public static final int REQUEST_CODE_SEND_USER_CARD = 17;
    public static final int REQUEST_CODE_LOCAL = 19;
    public static final int REQUEST_CODE_GROUP_DETAIL = 21;
    public static final int REQUEST_CODE_ADD_TO_BLACKLIST = 25;
    public static final int REQUEST_CODE_COPY_AND_PASTE = 11;
    public static final int RESULT_CODE_DELETE = 2;
    public static final int RESULT_CODE_FORWARD = 3;
    public static final int RESULT_CODE_OPEN = 4;
    public static final int RESULT_CODE_DWONLOAD = 5;
    public static final int RESULT_CODE_TO_CLOUD = 6;
    public static final int RESULT_CODE_EXIT_GROUP = 7;

    public static final int CHATTYPE_SINGLE = 1;
    public static final int CHATTYPE_GROUP = 2;
    
 
    private ImageView micImage;
    private ListView listView;
    private EditText mEditTextContent;
    private View buttonSetModeKeyboard;
    private View buttonSetModeVoice;
    private View buttonSend;
    private View buttonPressToSpeak;
    // private ViewPager expressionViewpager;
    private LinearLayout emojiIconContainer;
    private LinearLayout btnContainer;
 
    private View more;
    private ViewPager expressionViewpager;
    private InputMethodManager manager;
    private List<String> reslist;
    private Drawable[] micImages;
    private int chatType;
 
    public static Activity_Chat instance = null;
    // ��˭������Ϣ
    private MessageAdapter adapter;

    public static int resendPos;
    private List<ChatEntity> mData=new ArrayList<ChatEntity>();
    private Queue<String> toastqueue = new LinkedList<String>();
    private Handler mHandler;

    private ImageView iv_emoticons_normal;
    private ImageView iv_emoticons_checked;
    private RelativeLayout edittext_layout;
    private ProgressBar loadmorePB;
    private boolean isloading;
    private final int pagesize = 20;
    private boolean haveMoreData = true;
    private Button btnMore;
    public String playMsgId;
    private Context context;
    public String talker;
    private ImageView iv_back;
    private TextView tv_name;
    private User user=new User();
    // �������Ƭ
    String iamge_path = null;
    // ���ð�ť
    private ImageView iv_setting;
    private ImageView iv_setting_group;
    
    private MsgReceiver msgReceiver;  
    @SuppressLint("HandlerLeak")
    private Handler micImageHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            // �л�msg�л�ͼƬ
            micImage.setImageDrawable(micImages[msg.what]);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        context=Activity_Chat.this;
        instance = this;   
        initView();
        setUpView();
        iamge_path = this.getIntent().getStringExtra("iamge_path");
        if (iamge_path != null && !iamge_path.equals("")) {
          //  sendPicture(iamge_path, true);
        }
        mHandler = new Handler(){       	
			@Override
			public  void handleMessage(Message msg) {
				super.handleMessage(msg);
				Log.e("a", msg.what+"");
				switch (msg.what) {
				case 33:
					ChatEntity entity = new ChatEntity();
					Bundle b=(Bundle) msg.obj;
					String body=b.getString("body");
					if(body!=null&&!body.equals("")){
						entity.setType(1);
						entity.setFrom(talker);
						entity.setContent(body);
						entity.setMsgTime(b.getLong("time"));
						entity.setStatus(0);
						entity.setMsgID(System.currentTimeMillis());
						mData.add(0,entity);
						adapter = new MessageAdapter(context, talker, mData,chatType);
						listView.setAdapter(adapter);
						adapter.refresh();
//						adapter.notifyDataSetChanged();
						listView.setSelection(listView.getCount() - 1);
						ChatEntityDao dao=new ChatEntityDao(context);
						dao.saveMessage(entity);
					}	
					break;

				default:
					break;
				}
			}
			
		};
		
		 msgReceiver = new MsgReceiver();  
	     IntentFilter intentFilter = new IntentFilter();  
	     intentFilter.addAction("com.eventer.app.socket.RECEIVER");  
	     registerReceiver(msgReceiver, intentFilter);  
	     ChatEntityDao dao=new ChatEntityDao(context);  
         dao.ClearUnReadMsg(talker);
	     MessageFragment.instance.refreshData();
    }
    
    /**
     * initView
     */
    protected void initView() {
        
        micImage = (ImageView) findViewById(R.id.mic_image);
        listView = (ListView) findViewById(R.id.list);
        mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);
        buttonSetModeKeyboard = findViewById(R.id.btn_set_mode_keyboard);
        edittext_layout = (RelativeLayout) findViewById(R.id.edittext_layout);
        buttonSetModeVoice = findViewById(R.id.btn_set_mode_voice);
        buttonSend = findViewById(R.id.btn_send);
        buttonPressToSpeak = findViewById(R.id.btn_press_to_speak);
        expressionViewpager = (ViewPager) findViewById(R.id.vPager);
        emojiIconContainer = (LinearLayout) findViewById(R.id.ll_face_container);
        btnContainer = (LinearLayout) findViewById(R.id.ll_btn_container);
        iv_emoticons_normal = (ImageView) findViewById(R.id.iv_emoticons_normal);
        iv_emoticons_checked = (ImageView) findViewById(R.id.iv_emoticons_checked);
        loadmorePB = (ProgressBar) findViewById(R.id.pb_load_more);
        btnMore = (Button) findViewById(R.id.btn_more);
        iv_back=(ImageView)findViewById(R.id.iv_back);
        tv_name=(TextView)findViewById(R.id.name);
        
        iv_emoticons_normal.setVisibility(View.VISIBLE);
        iv_emoticons_checked.setVisibility(View.INVISIBLE);
        iv_emoticons_checked.setOnClickListener(this);
        iv_emoticons_normal.setOnClickListener(this);
        more = findViewById(R.id.more);
        edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_normal);
 
        iv_back.setOnClickListener(this);
        // ����list
        reslist = getExpressionRes(54);
        // ��ʼ������viewpager
        List<View> views = new ArrayList<View>();
        View gv1 = getGridChildView(1);
        View gv2 = getGridChildView(2);
        View gv3 = getGridChildView(3);
        views.add(gv1);
        views.add(gv2);
        views.add(gv3);
        expressionViewpager.setAdapter(new ExpressionPagerAdapter(views));
        edittext_layout.requestFocus();
     //   buttonPressToSpeak.setOnTouchListener(new PressToSpeakListen());
        mEditTextContent.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    edittext_layout
                            .setBackgroundResource(R.drawable.input_bar_bg_active);
                } else {
                    edittext_layout
                            .setBackgroundResource(R.drawable.input_bar_bg_normal);
                }

            }
        });
        mEditTextContent.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                edittext_layout
                        .setBackgroundResource(R.drawable.input_bar_bg_active);
                more.setVisibility(View.GONE);
                iv_emoticons_normal.setVisibility(View.VISIBLE);
                iv_emoticons_checked.setVisibility(View.INVISIBLE);
                emojiIconContainer.setVisibility(View.GONE);
                btnContainer.setVisibility(View.GONE);
            }
        });
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ((PowerManager) getSystemService(Context.POWER_SERVICE))
                .newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");
        // �������ֿ�
        mEditTextContent.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
                if (!TextUtils.isEmpty(s)) {
                    btnMore.setVisibility(View.GONE);
                    buttonSend.setVisibility(View.VISIBLE);
                } else {
                    btnMore.setVisibility(View.VISIBLE);
                    buttonSend.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
     // �жϵ��Ļ���Ⱥ��
        chatType = getIntent().getIntExtra("chatType", CHATTYPE_SINGLE);
        // type=getIntent().getIntExtra("type", 0);

        if (chatType == CHATTYPE_SINGLE) { // ����
        	talker=getIntent().getStringExtra("userId");
            User u=MyApplication.getInstance().getContactList().get(talker);
            if(u!=null){
            	user=u;
            	 if(!TextUtils.isEmpty(u.getBeizhu()))
                     tv_name.setText(u.getBeizhu());
                else  if(!TextUtils.isEmpty(u.getNick()))
                	 tv_name.setText(u.getNick());
            }else{
            	UserInfo info=MyApplication.getInstance().getUserList().get(talker);
            	if(info!=null){
            		user.setAvatar(info.getAvatar());
            		user.setNick(info.getNick());
            	}
            }
           
        } else {
        	talker= getIntent().getStringExtra("groupId");
        	ChatroomDao dao=new ChatroomDao(context);
        	ChatRoom room=dao.getRoom(talker);
        	String roomName="Ⱥ��";
        	if(room!=null){
        		roomName=room.getRoomname();
        	}
        	if(roomName==null||roomName.equals("")){
        		roomName="Ⱥ��";
        	}
            findViewById(R.id.container_voice_call).setVisibility(View.GONE);
//            String groupName = getIntent().getStringExtra("groupName");
            ((TextView) findViewById(R.id.name)).setText(roomName);
        }
        ChatEntityDao dao1=new ChatEntityDao(context);
//        List<ChatEntity> data=dao1.getChatEntityList(null," talker = ? ", new String[]{talker},null,ChatEntityDao.COLUMN_NAME_TIME);
        List<ChatEntity> data=dao1.getMsgList(talker, 0, 5);
        if(data.size()>0)
        mData=data;
        
        adapter = new MessageAdapter(this, talker, mData,chatType);
         //��ʾ��Ϣ
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new ListScrollListener());
        int count = listView.getCount();
        if (count > 0) {
            listView.setSelection(count - 1);
        }

        listView.setOnTouchListener(new OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                more.setVisibility(View.GONE);
                iv_emoticons_normal.setVisibility(View.VISIBLE);
                iv_emoticons_checked.setVisibility(View.INVISIBLE);
                emojiIconContainer.setVisibility(View.GONE);
                btnContainer.setVisibility(View.GONE);
                return false;
            }
        });
    }

    /**
     * ˢ��ҳ��
     */
    public void refresh() {
//    	mData.clear();
//    	ChatEntityDao dao=new ChatEntityDao(context);
////        List<ChatEntity> data=dao.getChatEntityList(null," talker = ? ", new String[]{talker},null,ChatEntityDao.COLUMN_NAME_TIME);
//    	List<ChatEntity> data=dao.getMsgList(talker, 0, 5);
//    	if(data.size()>0)
//        mData=data;
        adapter = new MessageAdapter(this, talker, mData,chatType);
        //��ʾ��Ϣ
        listView.setAdapter(adapter);
        int count=listView.getCount();
        if(count>0){
        	listView.setSelection(count-1);
        }
        
    }
    
    private void setUpView() {
        iv_setting = (ImageView) this.findViewById(R.id.iv_setting);
        iv_setting_group = (ImageView) this.findViewById(R.id.iv_setting_group);
        if (chatType == CHATTYPE_SINGLE) {
            iv_setting.setVisibility(View.VISIBLE);
            
            iv_setting.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
//
//                    startActivity(
//
//                    new Intent(Activity_Chat.this,
//                            ChatSingleSettingActivity.class).putExtra("userId",
//                            toChatUsername));

                }

            });
        } else {
            iv_setting.setVisibility(View.GONE);
            iv_setting_group.setVisibility(View.VISIBLE);
            iv_setting_group.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    startActivityForResult((new Intent(Activity_Chat.this,
                            ChatRoomSettingActivity.class).putExtra("groupId",
                            talker)), REQUEST_CODE_GROUP_DETAIL);

                }

            });
        }

    }



    /**
     * ��ʾ����ͼ��
     * 
     * @param view
     */
    public void setModeKeyboard(View view) {
        // mEditTextContent.setOnFocusChangeListener(new OnFocusChangeListener()
        // {
        // @Override
        // public void onFocusChange(View v, boolean hasFocus) {
        // if(hasFocus){
        // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        // }
        // }
        // });
        edittext_layout.setVisibility(View.VISIBLE);
        more.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
        buttonSetModeVoice.setVisibility(View.VISIBLE);
        // mEditTextContent.setVisibility(View.VISIBLE);
        mEditTextContent.requestFocus();
        // buttonSend.setVisibility(View.VISIBLE);
        buttonPressToSpeak.setVisibility(View.GONE);
        if (TextUtils.isEmpty(mEditTextContent.getText())) {
            btnMore.setVisibility(View.VISIBLE);
            buttonSend.setVisibility(View.GONE);
        } else {
            btnMore.setVisibility(View.GONE);
            buttonSend.setVisibility(View.VISIBLE);
        }

    }

    /**
     * ��ʾ������ͼ�갴ťҳ
     * 
     * @param view
     */
    public void more(View view) {
        if (more.getVisibility() == View.GONE) {
            System.out.println("more gone");
            hideKeyboard();
            more.setVisibility(View.VISIBLE);
            btnContainer.setVisibility(View.VISIBLE);
            emojiIconContainer.setVisibility(View.GONE);
        } else {
            if (emojiIconContainer.getVisibility() == View.VISIBLE) {
                emojiIconContainer.setVisibility(View.GONE);
                btnContainer.setVisibility(View.VISIBLE);
                iv_emoticons_normal.setVisibility(View.VISIBLE);
                iv_emoticons_checked.setVisibility(View.INVISIBLE);
            } else {
                more.setVisibility(View.GONE);
            }

        }

    }

    /**
     * ������������
     * 
     * @param v
     */
    public void editClick(View v) {
        listView.setSelection(listView.getCount() - 1);
        if (more.getVisibility() == View.VISIBLE) {
            more.setVisibility(View.GONE);
            iv_emoticons_normal.setVisibility(View.VISIBLE);
            iv_emoticons_checked.setVisibility(View.INVISIBLE);
        }

    }
    
    public List<String> getExpressionRes(int getSum) {
        List<String> reslist = new ArrayList<String>();
        for (int x = 1; x <= getSum; x++) {
            String filename = "e_" + x;

            reslist.add(filename);

        }
        return reslist;

    }
    /**
     * ��ȡ�����gridview����view
     * 
     * @param i
     * @return
     */
    private View getGridChildView(int i) {
    	View view = View.inflate(this, R.layout.expression_gridview, null);
        ExpandGridView gv = (ExpandGridView) view.findViewById(R.id.gridview);
        List<String> list = new ArrayList<String>();
        if (i == 1) {
            List<String> list1 = reslist.subList(0, 20);
            list.addAll(list1);
        } else if (i == 2) {
            list.addAll(reslist.subList(20, 40));
        }else if(i==3){
        	list.addAll(reslist.subList(40, reslist.size()));
        }
        list.add("back_over");
        final ExpressionAdapter expressionAdapter = new ExpressionAdapter(this,
                1, list);
        gv.setAdapter(expressionAdapter);
        gv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                String filename = expressionAdapter.getItem(position);
                try {
                    // ���������ɼ�ʱ���ſ��������
                    // ��ס˵���ɼ��������������
                    if (buttonSetModeKeyboard.getVisibility() != View.VISIBLE) {

                        if (filename != "back_over") { // ����ɾ��������ʾ����
                            // �����õķ��䣬���Ի�����ʱ��Ҫ����SmileUtils�����
                            @SuppressWarnings("rawtypes")
                            Class clz = Class
                                    .forName("com.eventer.app.util.SmileUtils");
                            Field field = clz.getField(filename);
                            mEditTextContent.append(SmileUtils.getSmiledText(
                                    Activity_Chat.this, (String) field.get(null)));
                        } else { // ɾ�����ֻ��߱���
                            if (!TextUtils.isEmpty(mEditTextContent.getText())) {

                                int selectionStart = mEditTextContent
                                        .getSelectionStart();// ��ȡ����λ��
                                if (selectionStart > 0) {
                                    String body = mEditTextContent.getText()
                                            .toString();
                                    String tempStr = body.substring(0,
                                            selectionStart);
                                    int i = tempStr.lastIndexOf("[");// ��ȡ���һ�������λ��
                                    if (i != -1) {
                                        CharSequence cs = tempStr.substring(i,
                                                selectionStart);
                                        if (SmileUtils.containsKey(cs
                                                .toString()))
                                            mEditTextContent.getEditableText()
                                                    .delete(i, selectionStart);
                                        else
                                            mEditTextContent.getEditableText()
                                                    .delete(selectionStart - 1,
                                                            selectionStart);
                                    } else {
                                        mEditTextContent.getEditableText()
                                                .delete(selectionStart - 1,
                                                        selectionStart);
                                    }
                                }
                            }

                        }
                    }
                } catch (Exception e) {
                }

            }
        });
        return view;
    }
    

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
        unregisterReceiver(msgReceiver);
    }
    
    

    @Override
    protected void onResume() {
        super.onResume();
        // GluGroup group_temp = DemoApplication.getInstance().getGroupsList()
        // .get(toChatUsername);
        // if (group_temp != null)
        // ((TextView) findViewById(R.id.name)).setText(group_temp
        // .getGroupName());
        // adapter.refresh();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * ���������
     */
    private void hideKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }



    /**
     * �����ֻ����ؼ�
     */
    @Override
    public void onBackPressed() {
    	super.onBackPressed();
        if (more.getVisibility() == View.VISIBLE) {
            more.setVisibility(View.GONE);
            iv_emoticons_normal.setVisibility(View.VISIBLE);
            iv_emoticons_checked.setVisibility(View.INVISIBLE);
        } else {         
        	startActivity(new Intent().setClass(context,MainActivity.class));
//			scrollToFinishActivity(); 
        	finish();
        }
    }

    public String getToChatUsername() {
        return talker;
    }


    private void send() {
		String contString = mEditTextContent.getText().toString();
		if (contString.length() > 0) {
			ChatEntity entity = new ChatEntity();
			entity.setType(1);
			entity.setFrom(talker);
			entity.setContent(contString);
			entity.setMsgTime(System.currentTimeMillis()/1000);
			entity.setStatus(2);
			entity.setMsgID(System.currentTimeMillis()/1000);
			mData.add(entity);
			listView.setAdapter(adapter);
			adapter.refresh();
			//adapter.notifyDataSetChanged();
			mEditTextContent.setText("");
			listView.setSelection(listView.getCount() - 1);
		}
	}
    
	 /**
     * ��Ϣͼ�����¼�
     * 
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
		case R.id.btn_send:
			 String s = mEditTextContent.getText().toString();
			 if(chatType==CHATTYPE_SINGLE&&!MyApplication.getInstance().getContactList().containsKey(talker)){
				 showAlert();
			 }else{
				 sendText(s);
			 }   
			break;
		case R.id.iv_back:
			startActivity(new Intent().setClass(context,MainActivity.class));
//			scrollToFinishActivity();
			finish();
			break;
		case R.id.iv_emoticons_checked:
			iv_emoticons_normal.setVisibility(View.VISIBLE);
            iv_emoticons_checked.setVisibility(View.INVISIBLE);
            btnContainer.setVisibility(View.VISIBLE);
            emojiIconContainer.setVisibility(View.GONE);
            more.setVisibility(View.GONE);
            break;
		case R.id.iv_emoticons_normal:
			more.setVisibility(View.VISIBLE);
            iv_emoticons_normal.setVisibility(View.INVISIBLE);
            iv_emoticons_checked.setVisibility(View.VISIBLE);
            btnContainer.setVisibility(View.GONE);
            emojiIconContainer.setVisibility(View.VISIBLE);
            hideKeyboard();
            break;
		default:
			break;
		}
    }
    
    
    private void showAlert() {

    	final AlertDialog dlg = new AlertDialog.Builder(this).create();
    	dlg.show();
    	Window window = dlg.getWindow();
    	// *** ��Ҫ����������ʵ������Ч����.
    	// ���ô��ڵ�����ҳ��,shrew_exit_dialog.xml�ļ��ж���view����
    	window.setContentView(R.layout.info_alertdialog);
    	// �����ܵ������뷨
    	dlg.getWindow().clearFlags(
    	        WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    	// Ϊȷ�ϰ�ť����¼�,ִ���˳�Ӧ�ò���
    	Button ok = (Button) window.findViewById(R.id.btn_ok);
    	TextView title=(TextView)window.findViewById(R.id.tv_title);
    	title.setText("���ǻ����Ǻ��ѣ��Ƿ���Ӹú���?");
    	ok.setText("���");
    	ok.setOnClickListener(new View.OnClickListener() {
    	    @SuppressLint("ShowToast")
    	    public void onClick(View v) {
 	    	
    	       startActivity(new Intent().setClass(context, AddFriendsFinalActivity.class)
    	    		   .putExtra("id", talker).putExtra("avatar", user.getAvatar())
    	    		   .putExtra("nick", user.getNick()));
    	        dlg.cancel();
    	    }
    	});
    	// �ر�alert�Ի����
    	Button cancel = (Button) window.findViewById(R.id.btn_cancel);
    	cancel.setOnClickListener(new View.OnClickListener() {
    	    public void onClick(View v) {
    	        dlg.cancel();
    	    }
    	});

    }
   
    /**
     * �����ı���Ϣ
     * 
     * @param content
     *            message content
     * @param isResend
     *            boolean resend
     */
    private void sendText(String content) {

        if (content.length() > 0) {
            mEditTextContent.setText("");
            ChatEntity msg=new ChatEntity();
            long time=System.currentTimeMillis();
            msg.setType(1);
            Log.e("1", talker);
            msg.setFrom(talker);
            msg.setContent(content);
            msg.setMsgTime(time/1000);
            msg.setStatus(2);
            msg.setMsgID(time);
            ChatEntityDao dao =new ChatEntityDao(context);
            dao.saveMessage(msg);
            mData.add(0,msg);
            
           // adapter = new MessageAdapter(context, toChatUsername, mData);
			listView.setAdapter(adapter);
			adapter.refresh();
			listView.setSelection(listView.getCount() - 1);       
            
            JSONObject send_json = new JSONObject();
			try {
				send_json.put("action", "send");
				send_json.put("data", content);
				String send_body = send_json.toString();
				if(chatType==CHATTYPE_SINGLE)
				   MainActivity.instance.newMsg("1",talker,send_body,1|16);
				else
					MainActivity.instance.newMsg(talker, talker, send_body, 49);
				//newMsg(1,12,send_body);
				send();
			} catch (JSONException e) {
				e.printStackTrace();
			}

            setResult(RESULT_OK);

        }
    }
 
    
    /** 
     * �㲥������ 
     * @author len 
     * 
     */  
    public class MsgReceiver extends BroadcastReceiver{  
  
        @Override  
        public void onReceive(Context context, Intent intent) {  
            //�õ����ȣ�����UI  
            String id=intent.getStringExtra("talker");  
            long time=intent.getLongExtra("time", -1);
            String mid=intent.getStringExtra("mid");
            if(time==-1){
            	time=System.currentTimeMillis()/1000;
            }
            if(talker.equals(id)){
            	String msg=intent.getStringExtra("msg");
            	JSONObject recvJs;
            	Log.e("1", "msg:"+msg);
				try {
					recvJs = new JSONObject(msg);
					String bodyString = recvJs.getString("data");
	            	toastqueue.add(bodyString);
	            	Message m=new Message();
	            	m.what=33;
	                Bundle bundle = new Bundle();    
                    bundle.putString("body",bodyString);  //��Bundle�д������  
                    bundle.putLong("time",time);  //��Bundle��put����  
	            	m.obj=bundle;
	            	mHandler.sendMessage(m);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			
            }else if(talker.equals(mid)){
            	String msg=intent.getStringExtra("msg");
            	Log.e("1", "msg:"+msg);
            	JSONObject recvJs;
				try {
					recvJs = new JSONObject(msg);
					String bodyString = recvJs.getString("data");
	            	toastqueue.add(bodyString);
	            	Message m=new Message();
	            	m.what=33;
	                Bundle bundle = new Bundle();			
                    bundle.putString("body",id+":\n"+bodyString);  //��Bundle�д������  
                    bundle.putLong("time",time);  //��Bundle��put����  
	            	m.obj=bundle;
	            	mHandler.sendMessage(m);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            
        }  
          
    }  
    
    /**
     * onActivityResult
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            finish();
        }

        if (resultCode == RESULT_CODE_EXIT_GROUP) {
            setResult(RESULT_OK);
            finish();
            return;
        }
        if (requestCode == REQUEST_CODE_CONTEXT_MENU) {
            
        }
        if (resultCode == RESULT_OK) { // �����Ϣ
            if (requestCode == REQUEST_CODE_EMPTY_HISTORY) {
                // ��ջỰ
                EMChatManager.getInstance().clearConversation(talker);
                adapter.refresh();
            } else if (requestCode == REQUEST_CODE_TEXT) {
               // resendMessage();
            }  else if (requestCode == REQUEST_CODE_ADD_TO_BLACKLIST) { // ���������
//                EMMessage deleteMsg = (EMMessage) adapter.getItem(data
//                        .getIntExtra("position", -1));
//                //addUserToBlacklist(deleteMsg.getFrom());
            } else if (requestCode == REQUEST_CODE_GROUP_DETAIL) {
                adapter.refresh();
            }
        }
    }
    
    /**
     * listview��������listener
     * 
     */
    private class ListScrollListener implements OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
            case OnScrollListener.SCROLL_STATE_IDLE:
                if (view.getFirstVisiblePosition() == 0 && !isloading
                        && haveMoreData) {
                    loadmorePB.setVisibility(View.VISIBLE);
                    // sdk��ʼ�����ص������¼Ϊ20��������ʱȥdb���ȡ����
                    List<ChatEntity> messages=new ArrayList<ChatEntity>();
                    try {
                        // ��ȡ����messges�����ô˷�����ʱ���db��ȡ��messages
                        	ChatEntityDao dao=new ChatEntityDao(context);
                        	messages=dao.getMsgList(talker,adapter
                                    .getItem(mData.size()-1).getMsgID(),pagesize);

                    } catch (Exception e1) {
                        loadmorePB.setVisibility(View.GONE);
                        return;
                    }
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                    }
                    if (messages.size() != 0) {
                        // ˢ��ui
                    	for (ChatEntity chatEntity : messages) {
							mData.add(chatEntity);
						}
//                    	mData=messages;
                    	Log.e("1", mData.size()+"");
                    	adapter=new MessageAdapter(context, talker, mData,chatType);
                    	listView.setAdapter(adapter);
                    	adapter.refresh();
                        listView.setSelection(messages.size() - 1);
                        if (messages.size() != pagesize)
                            haveMoreData = false;
                    } else {
                        haveMoreData = false;
                    }
                    loadmorePB.setVisibility(View.GONE);
                    isloading = false;
                }
                break;
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                int visibleItemCount, int totalItemCount) {

        }

    }


}
