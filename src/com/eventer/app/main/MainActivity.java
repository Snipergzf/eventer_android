package com.eventer.app.main;


import hirondelle.date4j.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.eventer.app.Constant;
import com.eventer.app.MyApplication;
import com.eventer.app.R;
import com.eventer.app.db.ChatEntityDao;
import com.eventer.app.db.DBManager;
import com.eventer.app.db.EventDao;
import com.eventer.app.db.InviteMessgeDao;
import com.eventer.app.db.UserDao;
import com.eventer.app.entity.A;
import com.eventer.app.entity.ChatEntity;
import com.eventer.app.entity.Event;
import com.eventer.app.entity.InviteMessage;
import com.eventer.app.entity.InviteMessage.InviteMesageStatus;
import com.eventer.app.entity.Msg.Container;
import com.eventer.app.entity.Schedual;
import com.eventer.app.entity.User;
import com.eventer.app.http.HttpUnit;
import com.eventer.app.other.Activity_NewFriends;
import com.eventer.app.other.Calendar_ViewSchedual;
import com.eventer.app.socket.Activity_Chat;
import com.eventer.app.socket.SocketService;
import com.eventer.app.task.LoadDataFromHTTP;
import com.eventer.app.task.LoadDataFromHTTP.DataCallBack;
import com.eventer.app.util.PreferenceUtils;
import com.eventer.app.widget.calendar.AlarmReceiver;

@SuppressLint("SimpleDateFormat")
public class MainActivity extends FragmentActivity{

	private int index=0;
    // ��ǰfragment��index
    private int currentTabIndex=0;
    private ImageView[] imagebuttons;
    private TextView[] textviews;
    private Fragment[] fragments;
    private TextView unreadLabel;
    public ScheduleFragment homefragment;
    private ActivityFragment activityfragment;
    private MessageFragment msgfragment;
    private ProfileFragment profilefragment;
    private FragmentManager fm;
    public static MainActivity instance = null;
    private static final  String[] FRAGMENT_TAG = {"homefrag","activityfrag","msgfrag","profilefrag"};
    private static final String PRV_INDEX="pre_index";
    public static Map<String, Schedual> Alarmlist= new HashMap<String, Schedual>();
    final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public AlarmManager am;// ��Ϣ������
    public Queue<A> queued = new LinkedList<A>();
    public SocketService.SocketSendBinder binder;
    private MsgReceiver msgReceiver;
    private EventReceiver eventReceiver; 
    private NotificationManager manager;
    private static boolean isExit=false;
    private boolean alert,alert_detail,alert_shake,alert_voice;
    private static Handler mHandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			isExit=false;
		}
	};
    private Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);		
		context=this;
		instance=this;
		if(TextUtils.isEmpty(Constant.UID)){
			System.exit(0);
		}
		initData();
//		DBManager db=new DBManager(context);
//		db.deleteDatabase(context);
		
		fm=getSupportFragmentManager();
		if(savedInstanceState != null) {
            //��ȡ��һ�ν���Save��ʱ��tabѡ�е�״̬
			currentTabIndex=savedInstanceState.getInt(PRV_INDEX,currentTabIndex);
			activityfragment = (ActivityFragment) fm.findFragmentByTag(FRAGMENT_TAG[0]);
            homefragment = (ScheduleFragment) fm.findFragmentByTag(FRAGMENT_TAG[1]);         
            msgfragment = (MessageFragment) fm.findFragmentByTag(FRAGMENT_TAG[2]);
            profilefragment = (ProfileFragment) fm.findFragmentByTag(FRAGMENT_TAG[3]);
        }
	}
	private void initData() {
		// TODO Auto-generated method stub
		EventDao dao=new EventDao(context);
	    List<String> list=dao.getEventIDList();
	    MyApplication.getInstance().setCacheByKey("EventList", list);
	    loadFriendList();	
		bindService(new Intent(this, SocketService.class),
	            internetServiceConnection, Context.BIND_AUTO_CREATE);
		 msgReceiver = new MsgReceiver();  
	     IntentFilter intentFilter = new IntentFilter();  
	     intentFilter.addAction("com.eventer.app.socket.RECEIVER");  
	     registerReceiver(msgReceiver, intentFilter); 
	     manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//	     DBManager db=new DBManager(context);
//	     db.deleteDatabase(context);
	     initView();
	     
	     eventReceiver = new EventReceiver();  
	     IntentFilter intentFilter1 = new IntentFilter();  
	     intentFilter1.addAction("com.eventer.app.activity");  
	     registerReceiver(eventReceiver, intentFilter1); 	
	}
	public ServiceConnection internetServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName arg0, IBinder service) {
        	Log.e("1", "here internetServiceConnection");
        	binder = (SocketService.SocketSendBinder) service;
        	A a;
        	while(!queued.isEmpty()){
        		if((a = queued.poll())!=null){
        			sendToService(a);
        		}
        	}
        }
        public void onServiceDisconnected(ComponentName arg0) {
        	Log.e("1", "service disconnected");
        	binder = null;
        }
    };
    
    public boolean newMsg(String MID, String RID, String body,int type){
    	if(binder == null){
    		Log.e("newMsg", "binder is null");
    		queued.add(new A(MID, RID, body,type));
			bindService(new Intent(this, SocketService.class),
		            internetServiceConnection, Context.BIND_AUTO_CREATE);			
			return false;
		}
    	else {
    		sendToService(new A(MID, RID, body,type));
		}
		return true;
    }
    
    
    
    public void sendToService(A a){
    	Container msg = Container.newBuilder()
           		.setMID(String.valueOf(a.MID)).setSID(String.valueOf(Constant.UID)).setRID(String.valueOf(a.RID))
           		.setTYPE(a.type).setSTIME(a.time).setBODY(a.body)
           		.build();
    	Log.e("1", msg.toString());
    	//binder.setCurrentActivity(this);
    	binder.sendOne(msg);
    }
    
    public boolean newMsg(String MID, String RID,String SID, String body,int type){
    	Container msg = Container.newBuilder()
           		.setMID(String.valueOf(MID)).setSID(String.valueOf(SID)).setRID(String.valueOf(RID))
           		.setTYPE(type).setSTIME(System.currentTimeMillis()/1000).setBODY(body)
           		.build();
    	Log.e("1", msg.toString());
    	//binder.setCurrentActivity(this);
    	binder.sendOne(msg);
    	return false;
    }
    
    /**
     * ��ȡδ����Ϣ��
     * 
     * @return
     */
    public int getUnreadMsgCountTotal() {
        int unreadMsgCountTotal = 0;
        ChatEntityDao dao=new ChatEntityDao(context);
        unreadMsgCountTotal=dao.getUnreadMsgCount();
        return unreadMsgCountTotal;
    }
    
    /**
     * ˢ��δ����Ϣ��
     */
    public void updateUnreadLabel() {
        int count = getUnreadMsgCountTotal();
        if (count > 0) {
            unreadLabel.setText(String.valueOf(count));
            unreadLabel.setVisibility(View.VISIBLE);
        } else {
            unreadLabel.setVisibility(View.INVISIBLE);
        }
    }
    
    protected void loadFriendList() {
		// TODO Auto-generated method stub
		Map<String, String> map=new HashMap<String, String>();
	    map.put("uid", Constant.UID+"");
	    map.put("token", Constant.TOKEN);
	    LoadDataFromHTTP task=new LoadDataFromHTTP(context, Constant.URL_GET_FRIENDLIST, map);
	    task.getData(new DataCallBack() {			
			@Override
			public void onDataCallBack(JSONObject data) {
				// TODO Auto-generated method stub
				int code = data.getInteger("status");
				if(code==0){
					Log.e("1", "friendlist");
					JSONObject obj=data.getJSONObject("friend_action");
					JSONArray friends=obj.getJSONArray("friends");
					List<String> list=MyApplication.getInstance().getContactIDList();
					List<String> friend=new ArrayList<String>();
					List<String> delFriend=new ArrayList<String>();
					List<String> addFriend=new ArrayList<String>();
					for(int i=0;i<friends.size();i++){
						friend.add(friends.get(i)+"");
					}
					for (String string : list) {
						if(!friend.contains(string)){
							delFriend.add(string);
						}
					}
					for (String string : friend) {
						if(!list.contains(string)){
							addFriend.add(string);
						}
					}
					UserDao dao=new UserDao(context);
					dao.updateUsers(delFriend);
					AddFriendList(addFriend);
	
				}else if(code==13){
					UserDao dao=new UserDao(context);
					dao.updateUsers(MyApplication.getInstance().getContactIDList());
					Toast.makeText(getApplicationContext(), "�����ڻ�û�к��ѣ�",
                            Toast.LENGTH_SHORT).show();
				}else if(code==13){
					Toast.makeText(getApplicationContext(), " ��ѯ���ݿ�ʧ�ܣ�",
                            Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(getApplicationContext(), "���غ����б�ʧ�ܣ�",
                            Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
    
    public void AddFriendList(final Object... params) {
		new AsyncTask<Object, Object,Integer>() {
			@SuppressWarnings("unchecked")
			@Override
			protected Integer doInBackground(Object... params) {
				int status=0;
			  try {
		    	     final List<User> users=HttpUnit.searchFriendListRequest((List<String>) params[0]);
		    	     runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							 UserDao dao=new UserDao(context);
				    	     dao.saveContactList(users);
						}
					 });
		    	   return status;			
				} catch (Throwable e) {
					Log.e("1", e.toString());
					return -1;

				}
			}
			protected void onPostExecute(Integer status) {
				 if(status==0){

			      }				
			};

		}.execute(params);}
	
    
    
			@SuppressLint("NewApi")
		public class EventReceiver extends BroadcastReceiver{  
		  
		        @Override  
		        public void onReceive(Context context, Intent intent) {  
		            //�õ����ȣ�����UI  
		        	String msg=intent.getStringExtra("msg");
	            	JSONObject recvJs; 
					try {
						recvJs = JSONObject.parseObject(msg);
						String id=recvJs.getString("_id");
						@SuppressWarnings("unchecked")
						List<String> list=(List<String>)MyApplication.getInstance().getCacheByKey("EventList");
			        	if(list!=null&&list.size()>0){	
			        	}else{
			        		list=new ArrayList<String>();
			        	}
			        	if(!list.contains(id+"")){
		                    
						String provider = recvJs.getString("cEvent_provider");
						Log.e("1", provider);
						String content=recvJs.getString("cEvent_content");
						String theme=recvJs.getString("cEvent_theme");
						String place=recvJs.getString("cEvent_place");						
						String name=recvJs.getString("cEvent_name");						
						String time=recvJs.getString("cEvent_time");//ʱ��ɶԣ������ж��ʱ��
						String pubtime=recvJs.getString("cEvent_publish");
						long issuetime=Long.parseLong(pubtime);
						time=time.replace("null,", "");
						Event event=new Event();
						event.setEventID(id);
						event.setContent(content);
						event.setPublisher(provider);
						event.setIssueTime(issuetime);
						event.setTime(time);
						event.setTitle(name);
						event.setTheme(theme);
						event.setPublisher(provider);
						event.setPlace(place);
					    EventDao dao=new EventDao(context);
					    dao.saveEvent(event);
						ActivityFragment.instance.addEvent(event);
						list.add(id+"");
	                    MyApplication.getInstance().setCacheByKey("EventList", list);
			        }
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();						
				}
		 }  	
			
	}  

    
	 public void setAlarmList(){
	    	Log.e("1","alarmlist");
	    	Alarmlist= new HashMap<String, Schedual>();    	
			DBManager dbHelper;
			dbHelper = new DBManager(MainActivity.this);
	        dbHelper.openDatabase();        
			String today=formatter.format(new Date());
			String[] today_time=today.split(" ");
			DateTime today_dt=new DateTime(today+":00");
	    	Cursor c=dbHelper.findList(true, "dbSchedule", new String[]{"scheduleID","status","endTime","startTime","remindTime","frequency"},
	    			"status>?", new String[]{"0"}, null, null,"remindTime",null);
	        while (c.moveToNext()) {
	        	Log.e("1","alarmlist1");
	        	int _f=c.getInt(c.getColumnIndex("frequency"));
	        	long id=c.getLong(c.getColumnIndex("scheduleID"));
	        	int status=c.getInt(c.getColumnIndex("status"));
	        	String EndTime=c.getString(c.getColumnIndex("endTime"));
	        	String StartTime=c.getString(c.getColumnIndex("startTime"));
	        	//String[] end_time=EndTime.split(" ");
	        	
	        	DateTime End_dt=new DateTime(EndTime+":00"); 
	        	DateTime Satrt_dt=new DateTime(StartTime+":00");
	        	String RemindTime=c.getString(c.getColumnIndex("remindTime"));
	        	String[] remind_time=RemindTime.split(" ");
	        	DateTime Remind_dt=new DateTime(RemindTime+":00");
	        	DateTime time_db=new DateTime(remind_time[0]+" 00:00:00");
	        	DateTime today_db=new DateTime(today_time[0]+" 00:00:00");
	        	int diff=today_db.numDaysFrom(time_db);
	        	
	        	Schedual s=new Schedual();
	        	
	        	boolean IsTodayEvent=false;
	        	
	        	switch(_f){
	        	  case 0:
	        		  IsTodayEvent=true;
	        		  break;
	        	  case 1:
	        		  if(today_time[0].compareTo(remind_time[0])>=0){
	        			  IsTodayEvent=true;  
	        		  }
	        		  break;
	        	  case 2:
	        		 // int i=Remind_dt.getWeekDay();
	        		  if(Remind_dt.getWeekDay()>1&&Remind_dt.getWeekDay()<7&&today_time[0].compareTo(remind_time[0])>=0){
	        			  IsTodayEvent=true;	
	        		  }
	        		  break;
	        	  case 3:
	        		  if(today_dt.getWeekDay()==Remind_dt.getWeekDay()&&today_time[0].compareTo(remind_time[0])>=0){
	        			  IsTodayEvent=true;	  
	        		  }
	        		  break;
	        	  case 4:
	        		  if(today_dt.getDay()==Remind_dt.getDay()&&today_time[0].compareTo(remind_time[0])>=0){
	        			  
	        		  }
	        		  break;
	        	  case 5:
	        		  int month1=today_dt.getMonth();
					  int day1=today_dt.getDay();
					  int month2=Remind_dt.getMonth();
					  int day2=Remind_dt.getDay();
					  if (month1==month2&&day1==day2&&today_time[0].compareTo(remind_time[0])>=0) {					
								IsTodayEvent=true;	  	
					  }
					  break;
	        	}  
	        	if(IsTodayEvent){
	        		if(diff>0){
		        		Remind_dt=Remind_dt.plusDays(diff);
		        		End_dt=End_dt.plusDays(diff);
		        		Satrt_dt=Satrt_dt.plusDays(diff);
	        		}
	        		
	        		if(!today_dt.gt(End_dt)){
	        			 if(!Alarmlist.containsKey(id+"")){
	        				  s.setSchdeual_ID(id);
		           			  s.setEndtime(End_dt.toString().substring(0, 16));
		           			  s.setRemindtime(Remind_dt.toString().substring(0, 16));
		           			  s.setStarttime(Satrt_dt.toString().substring(0, 16));
		           			  s.setStatus(status);       			
		           			  Alarmlist.put(id+"", s);
		           			  Log.e("1",0+"     "+s.toString());
	        			 }
	        			  
	        		  }
	        	}
	        }  
	        dbHelper.closeDatabase();
	    }
	 
	 class AlarmListThread  implements Runnable{
			@Override
			public void run() {
				// TODO Auto-generated method stub
				setAlarmList();	
			}
	    	
	    }
  
     public void TurnToDetail(String sid,String date){
	      	 Intent intent=new Intent();
	   	     intent.setClass(MainActivity.this, Calendar_ViewSchedual.class);
	   		 Bundle bundle = new Bundle();                           //����Bundle����   
	   		 bundle.putString(Calendar_ViewSchedual.ARGUMENT_ID, sid);     //װ������   
	   		 bundle.putString(Calendar_ViewSchedual.ARGUMENT_DATE, date);
	   		 bundle.putInt(Calendar_ViewSchedual.ARGUMENT_LOC, -1);
	   		 intent.putExtras(bundle); 
	   	     startActivityForResult(intent,0);  
	   }
	    
	
	private void initView(){
		
		homefragment = new ScheduleFragment();
	    activityfragment = new ActivityFragment();
	    msgfragment = new MessageFragment();
	    profilefragment = new ProfileFragment();
	    fragments = new Fragment[] {activityfragment, homefragment, 
	                msgfragment, profilefragment };
	    // �����ʾ��һ��fragment
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, activityfragment,FRAGMENT_TAG[0])
                .add(R.id.fragment_container, homefragment,FRAGMENT_TAG[1])
                .add(R.id.fragment_container, msgfragment,FRAGMENT_TAG[2])
                .add(R.id.fragment_container, profilefragment,FRAGMENT_TAG[3])
                .hide(homefragment).hide(profilefragment)
                .hide(msgfragment).show(activityfragment).commit();
		
		imagebuttons = new ImageView[4];
		imagebuttons[0] = (ImageView) findViewById(R.id.ib_activity);
        imagebuttons[1] = (ImageView) findViewById(R.id.ib_schedual);       
        imagebuttons[2] = (ImageView) findViewById(R.id.ib_message);
        imagebuttons[3] = (ImageView) findViewById(R.id.ib_profile);
        imagebuttons[0].setSelected(true);
        
        textviews = new TextView[4];
        textviews[0] = (TextView) findViewById(R.id.tv_activity);
        textviews[1] = (TextView) findViewById(R.id.tv_schedual);        
        textviews[2] = (TextView) findViewById(R.id.tv_message);
        textviews[3] = (TextView) findViewById(R.id.tv_profile);
        textviews[0].setTextColor(0xFF45C01A);
        if (am == null) {
			am = (AlarmManager) getSystemService(ALARM_SERVICE);
		}
        try {
			Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class); 
			//intent.putExtra("msg", "no no !");
			//Intent intent = new Intent(myListActivity, CallAlarm.class);
			PendingIntent sender = PendingIntent.getBroadcast(this,
					0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			Long now_time=System.currentTimeMillis();
			am.setRepeating(AlarmManager.RTC_WAKEUP, now_time+3,60*1000, sender);
		} catch (Exception e) {
			e.printStackTrace();
		}
        unreadLabel=(TextView)findViewById(R.id.unread_msg_number);
        updateUnreadLabel();
	}
	
	
	
	 public void onTabClick(View view) {
	        switch (view.getId()) {
	        case R.id.re_schedual:
	            index=1;
	            break;
	        case R.id.re_activity:
	            index=0;
	            break;
	        case R.id.re_message:
	            index=2;
	            break;
	        case R.id.re_profile:
	            index=3;
	            break;
	        }
	        if (currentTabIndex != index) {
	            FragmentTransaction trx = getSupportFragmentManager()
	                    .beginTransaction();
	            trx.hide(fragments[currentTabIndex]);
	            if (!fragments[index].isAdded()) {
	                trx.add(R.id.fragment_container, fragments[index]);
	            }
	            trx.show(fragments[index]).commit();
	            
	            imagebuttons[currentTabIndex].setSelected(false);
		        // �ѵ�ǰtab��Ϊѡ��״̬
		        imagebuttons[index].setSelected(true);
		        textviews[currentTabIndex].setTextColor(0xFF999999);
		        textviews[index].setTextColor(0xFF45C01A);
		        currentTabIndex = index;
	        }
	        
	    }
	 
	    /** 
	     * �㲥������ 
	     * @author len 
	     * 
	     */  
	   
		@SuppressLint("NewApi")
		public class MsgReceiver extends BroadcastReceiver{  
	  
	        @Override  
	        public void onReceive(Context context, Intent intent) {  
	            //�õ����ȣ�����UI  
	            String id=intent.getStringExtra("talker");  
	            String mid=intent.getStringExtra("mid");
	            String msg=intent.getStringExtra("msg");
	            Pattern p = Pattern.compile("[0-9]*"); 
	            Matcher m = p.matcher(mid); 
	            	JSONObject recvJs;
					try {
							String chat_talker="";
							if(Activity_Chat.instance!=null){
								chat_talker=Activity_Chat.instance.talker;
							}
							if(chat_talker==null){
								chat_talker="";
							}
							if(mid.equals("ADD")){
								String title="";
								String ticker="";
								String msgBody="";
								 
								recvJs = JSONObject.parseObject(msg);
								String bodyString = recvJs.getString("data");
								String certificate=recvJs.getString("certificate");
								int status=recvJs.getInteger("type");
								Log.e("1","main:msgRecv+"+bodyString);
								String name=recvJs.getString("name");
								String avatar=recvJs.getString("avatar");
								InviteMessage invite = new InviteMessage();														
								invite.setId(Integer.parseInt(id));
								invite.setReason(bodyString);
								invite.setCertification(certificate);
								invite.setFrom(name);
								
								invite.setAvatar(avatar);
								invite.setTime(System.currentTimeMillis()/1000);
								User u=new User();
								u.setAvatar(avatar);
								u.setNick(name);
								u.setUsername(id+"");
								if(status == 4){
									invite.setStatus(InviteMesageStatus.BEINVITEED);
									ticker="�յ�һ����������";
									title="��������";
									msgBody=id+"����һ����������";								
									u.setType(22);
									
								}		
								else if(status == 2){
									invite.setStatus(InviteMesageStatus.BEAGREED);
									ticker=id+"ͬ�������ĺ�������";
									title="��������";
									msgBody=id+"ͬ�������ĺ�������";
									u.setType(1);
								}
									
								else if(status == 3){
									invite.setStatus(InviteMesageStatus.BEREFUSED);
									ticker=id+"�ܾ������ĺ�������";
									title="��������";
									msgBody=id+"�ܾ������ĺ�������";
									u.setType(22);	
								}
									
								else if(status == 1){
									invite.setStatus(InviteMesageStatus.BEAPPLYED);
									ticker="�յ�һ����������";
									title="��������";
									msgBody=id+"����һ����������";
									u.setType(22);
								}
								
								UserDao dao1=new UserDao(context);
								dao1.saveUser(u);								
								InviteMessgeDao dao=new InviteMessgeDao(context);
								dao.saveMessage(invite);
								Intent intent1=new Intent(context,Activity_NewFriends.class);
								intent1.putExtra("userId", id);
								
								notifyMsg(ticker, title, msgBody, intent1,33);
							}
						else if(mid.indexOf("@")!=-1&&!chat_talker.equals(mid)){
							recvJs = JSONObject.parseObject(msg);
							String bodyString = recvJs.getString("data");
							Log.e("1","main:msgRecv+"+bodyString);
							Intent intent1=new Intent(context,Activity_Chat.class);
							intent1.putExtra("groupId", mid);
							intent1.putExtra("chatType", Activity_Chat.CHATTYPE_GROUP);
							notifyMsg("�յ�����Ⱥ�����Ϣ��", "��Ϣ֪ͨ", id+"������Ϣ:"+bodyString, intent1,49);
							long time=intent.getLongExtra("time",-1); 
							ChatEntity entity = new ChatEntity();						
							entity.setType(1);
							entity.setFrom(mid);
							entity.setContent(id+":\n"+bodyString);
							if(time!=-1){
								entity.setMsgTime(time);
							}else{
								entity.setMsgTime(System.currentTimeMillis()/1000);
							}
							entity.setStatus(1);
							entity.setMsgID(System.currentTimeMillis());
							ChatEntityDao dao=new ChatEntityDao(context);
							dao.saveMessage(entity);
							updateUnreadLabel();
							MessageFragment.instance.refreshView();
						}else if(mid.equals("DEL")){
							UserDao dao=new UserDao(context);
							List<String> delFriend=new ArrayList<String>();
							delFriend.add(id);							
							MyApplication.getInstance().clearContact();							
							dao.updateUsers(delFriend);
						}else if(m.matches()&&mid.length()==1&&!chat_talker.equals(id)&&MyApplication.getInstance().getContactIDList().contains(id)){
							recvJs = JSONObject.parseObject(msg);
							String bodyString = recvJs.getString( "data");
							Log.e("1","main:msgRecv+"+bodyString);						
							Intent intent1=new Intent(context,Activity_Chat.class);
							intent1.putExtra("userId", id);
							 notifyMsg("�յ����Ժ��ѵ���Ϣ��", "��Ϣ֪ͨ", id+"������Ϣ:"+bodyString, intent1,33);						
							long time=intent.getLongExtra("time",-1); 
							ChatEntity entity = new ChatEntity();						
							entity.setType(Integer.parseInt(mid));
							entity.setFrom(id);
							entity.setContent(bodyString);
							if(time!=-1){
								entity.setMsgTime(time);
							}else{
								entity.setMsgTime(System.currentTimeMillis());
							}
							
							entity.setStatus(1);
							entity.setMsgID(System.currentTimeMillis());
							ChatEntityDao dao=new ChatEntityDao(context);
							dao.saveMessage(entity);
							updateUnreadLabel();
							MessageFragment.instance.refreshView();
						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        }  
	          
	    }  
		
//		class RefreshThread  implements Runnable{
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				updateUnreadLabel();
//				MessageFragment.instance.refreshView();
//			}   	
//	    }
		@SuppressLint("NewApi")
		private void notifyMsg(String ticker,String title,String content,Intent intent,int notify){
			 alert=PreferenceUtils.getInstance().getMsgAlert();
			 if(alert){
				 alert_detail=PreferenceUtils.getInstance().getMsgAlertDetail();
				 alert_shake=PreferenceUtils.getInstance().getMsgAlertShake();
				 alert_voice=PreferenceUtils.getInstance().getMsgAlertVoice();
				 if(!alert_detail){
					 content="";
				 }
				PendingIntent pendingIntent=PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
				Notification.Builder mBuilder = new Notification.Builder(context);
				mBuilder.setSmallIcon(R.drawable.ic_launcher)
				.setTicker(ticker)
				.setContentTitle(title)
				.setContentText(content)
				.setContentIntent(pendingIntent);
				
				final Notification mNotification = mBuilder.build();		
				mNotification.flags = Notification.FLAG_AUTO_CANCEL;//FLAG_ONGOING_EVENT �ڶ�����פ�����Ե���������������ȥ��  FLAG_AUTO_CANCEL  ������������ȥ��
				if(alert_voice){
					if(alert_shake){
						mNotification.defaults= Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE|Notification.DEFAULT_LIGHTS;
					}else{
						mNotification.defaults= Notification.DEFAULT_SOUND|Notification.DEFAULT_LIGHTS;
					}	
				}else{
					if(alert_shake){
						mNotification.defaults= Notification.DEFAULT_VIBRATE|Notification.DEFAULT_LIGHTS;
					}else{
						mNotification.defaults= Notification.DEFAULT_LIGHTS;
					}
				}
				mNotification.when=System.currentTimeMillis(); 									
				manager.notify(notify, mNotification);
			 }	
		}
	    
		 class SaveMsg  implements Runnable{
				@Override
				public void run() {
					// TODO Auto-generated method stub
					setAlarmList();	
				}
		    	
		}
		 
		 
		class MsgHandler extends Handler{

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				Bundle b=(Bundle)msg.obj;
				String body=b.getString("msg");
				String id=b.getString("id");
				switch (msg.what) {
				case 33:
					break;

				default:
					break;
				}
			}
			 
		}
		 
		 
		    /**
		    * ��ʱ�������������η��ؼ����˳�����
		    * exit()
		    */
			public boolean onKeyDown(int keyCode,KeyEvent event){
				//�ж������ü��Ƿ���back��
				if(keyCode==KeyEvent.KEYCODE_BACK){
					exit();
					return true;
				}
				return super.onKeyDown(keyCode, event);
				
			}
			private void exit() {
				//�������������������back�����˳�����
				// TODO Auto-generated method stub
				if(!isExit){
				   isExit=true;
				  //ToastҪ�ǵü���show()
				   Toast.makeText(getApplicationContext(), "�ٰ�һ���˳�����", Toast.LENGTH_SHORT).show();
				   mHandler.sendEmptyMessageDelayed(0, 2000);
				}
				else{
					System.exit(0);
					//System.exit(0);
				}
			}
			
			@Override
			 public void onSaveInstanceState(Bundle outState) {  
				    // TODO Auto-generated method stub       
				    //Log.v("LH", "onSaveInstanceState"+outState);  
				    //super.onSaveInstanceState(outState);   //����һ��ע�͵�����ֹactivity����fragment��״̬
//				    outState.putInt(PRV_INDEX,currentTabIndex);
//			        super.onSaveInstanceState(outState);
				}
			 
			 @Override
				protected void onStart()
				{		
					super.onStart();
				    AlarmListThread thread_alarm=new AlarmListThread();//�����µ�Runnable��	
					Thread thread=new Thread(thread_alarm);//����Runnable��������Thread
					thread.start();
					AlarmReceiver.notify_id_list.clear();
					Log.e("1", "Main___onStart");
				}
			    @Override
			    protected void onDestroy() {
			        super.onDestroy();
			        Log.e("1", "Main___onDestroy");
			        instance = null;
//			        DBManager.getInstance().closeDatabase();
			        unbindService(internetServiceConnection);
			        unregisterReceiver(msgReceiver);
			        unregisterReceiver(eventReceiver);
			    }
			    

}
