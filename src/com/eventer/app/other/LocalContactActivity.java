package com.eventer.app.other;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.easemob.util.HanziToPinyin;
import com.easemob.util.HanziToPinyin.Token;
import com.eventer.app.Constant;
import com.eventer.app.MyApplication;
import com.eventer.app.R;
import com.eventer.app.db.PhoneDao;
import com.eventer.app.db.UserDao;
import com.eventer.app.entity.Phone;
import com.eventer.app.entity.UserInfo;
import com.eventer.app.http.HttpUnit;
import com.eventer.app.task.Contact;
import com.eventer.app.task.LoadUserAvatar;
import com.eventer.app.task.LoadUserAvatar.ImageDownloadedCallBack;

public class LocalContactActivity extends Activity{

	private MyAdapter adapter;
	private List<Map<String, String>> SourceData=new ArrayList<Map<String,String>>();
	private List<Phone> mData=new ArrayList<Phone>();
	private Map<String,UserInfo> isExist=new HashMap<String, UserInfo>();
    private ListView listView;
    private boolean hidden;
    private ImageView back;
    private int MAX_COUNT=15;//表示服务器总共有MAX_COUNT条数据
    private final int EACH_COUNT=15;//表示每次加载的条数
    private final int LOAD_STATE_IDLE=0;//没有在加载，并且服务器上还有数据没加载
    private final int LOAD_STATE_LOADING=1;//正在加载状态
    private final int LOAD_STATE_FINISH=2;//表示服务器上的全部数据都已加载完毕
    private int loadState=LOAD_STATE_IDLE;//记录加载的状态

    public Context context;
    private LoadUserAvatar avatarLoader;
    private List<String> phonelist=new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_phone_contact);
		context=this;
		avatarLoader = new LoadUserAvatar(context, Constant.IMAGE_PATH);
		 listView = (ListView) findViewById(R.id.list);
		 back=(ImageView)findViewById(R.id.iv_back);
		 back.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		 adapter = new MyAdapter(context);
		    listView.setAdapter(adapter);
		    listView.setOnItemClickListener(new OnItemClickListener() {
		            @Override
		            public void onItemClick(AdapterView<?> parent, View view,
		                    int position, long id) {


		            }
		        });
		PhoneDao dao=new PhoneDao(context);
		mData=dao.getPhoneList();
		phonelist=dao.getTelList();
		UserDao d=new UserDao(context);
		if(mData==null||mData.size()==0){
	        getContactList();
		}else{
			for (Phone p: mData) {
				String user=p.getUserId();
				if(!TextUtils.isEmpty(user)){
					UserInfo info=d.getInfo(user);
					isExist.put(p.getTel()+"", info);
				}
				Map<String, String> map=new HashMap<String, String>();
				map.put("phone", p.getTel());
				map.put("name", p.getRelName());
				SourceData.add(map);
			}			
			handleTel(SourceData);	
			UpdateContact thread1=new UpdateContact();//创建新的Runnable，	
			Thread thread=new Thread(thread1);//利用Runnable对象生成Thread
			thread.start();
		} 	
	}


    @Override
    public void onResume() {
        super.onResume();
        if (!hidden) {
            refresh();
        }
    }


    public class MyAdapter extends BaseAdapter {  
    	  
        private LayoutInflater mInflater;  
  
        public MyAdapter(Context context) {  
            this.mInflater = LayoutInflater.from(context);  
        }  
  
        @Override  
        public int getCount() {  
            // TODO Auto-generated method stub  
            return mData.size();  
        }  
  
        @Override  
        public Object getItem(int position) {  
            // TODO Auto-generated method stub  
            return mData.get(position);  
        }  
  
        @Override  
        public long getItemId(int position) {  
            // TODO Auto-generated method stub  
            return position;  
        }  
        //****************************************final方法  
             //注意原本getView方法中的int position变量是非final的，现在改为final  
        @SuppressLint("ViewHolder")
		public View getView(final int position, View convertView, ViewGroup parent) {  
             ViewHolder holder = null;  
//            if (convertView == null) {                  
                holder=new ViewHolder();                    
                //可以理解为从vlist获取view  之后把view返回给ListView                 
                convertView = mInflater.inflate(R.layout.item_phone_contact_list, null);  
                holder.name = (TextView)convertView.findViewById(R.id.tv_name);  
                holder.phone = (TextView)convertView.findViewById(R.id.tv_eventer_id);
                holder.avatar=(ImageView)convertView.findViewById(R.id.iv_avatar);
                holder.add=(Button)convertView.findViewById(R.id.tv_add);
                holder.text=(TextView)convertView.findViewById(R.id.tv_text);
//                convertView.setTag(holder);               
//            }else {               
//                holder = (ViewHolder)convertView.getTag();  
//            }         
            String phone=mData.get(position).getTel();
            String name=mData.get(position).getRelName();          
            holder.name.setText(name);  
            holder.phone.setText(phone+"");
            boolean isEventer=false;
            UserInfo u=new UserInfo(); 
            if(isExist.containsKey(phone)){
            	u=isExist.get(phone);
            	int type=u.getType();
            	if(type==1){
            		holder.add.setVisibility(View.GONE);
            		holder.text.setVisibility(View.VISIBLE);
            		holder.text.setText("已添加");
            	}else{
            		holder.text.setVisibility(View.GONE);
            		holder.add.setVisibility(View.VISIBLE);
            		holder.add.setBackgroundResource(R.drawable.btn_blue_bg);
            		holder.add.setText("添加");
            		isEventer=true;
            	}
            	String avatar=u.getAvatar();
            	showUserAvatar(holder.avatar, avatar);
            }else{
            	holder.text.setVisibility(View.GONE);
        		holder.add.setVisibility(View.VISIBLE);
        		holder.add.setBackgroundResource(R.drawable.btn_register_bg);
        		holder.add.setText("邀请");
            }
            final boolean isTrue=isEventer;
            final UserInfo temp_user=u;
            holder.add.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(isTrue){
						Intent intent=new Intent();
						intent.setClass(context, AddFriendsFinalActivity.class);
						intent.putExtra("id", temp_user.getUsername());
						intent.putExtra("avatar", temp_user.getAvatar());
						intent.putExtra("nick", temp_user.getNick());
						context.startActivity(intent);
					}else{
						//发送短信邀请
					}	
				}
			});
            return convertView;  
        }  
    }
    public final class ViewHolder {   
       TextView name;
       TextView phone; 
       ImageView avatar;
       Button add;
       TextView text;
    }  
    // 刷新ui
    public void refresh() {
        try {
            // 可能会在子线程中调到这方法
           this.runOnUiThread(new Runnable() {
                public void run() {
                	PhoneDao dao=new PhoneDao(context);
            		mData=dao.getPhoneList();
                    adapter.notifyDataSetChanged(); 
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    
    private void showUserAvatar(ImageView iamgeView, String avatar) {
		 if(avatar==null||avatar.equals("")||avatar.equals("default")) return;
	        final String url_avatar =avatar;
	        iamgeView.setTag(url_avatar);
	            Bitmap bitmap = avatarLoader.loadImage(iamgeView, url_avatar,
	                    new ImageDownloadedCallBack() {

	                        @Override
	                        public void onImageDownloaded(ImageView imageView,
	                                Bitmap bitmap,int status) {
	                        	if(status==-1){
	                        		if (imageView.getTag() == url_avatar) {
	                                    imageView.setImageBitmap(bitmap);
	                                }
	                        	}  
	                        }
	                    });
	            if (bitmap != null)
	                iamgeView.setImageBitmap(bitmap);

	    }

	
	 /**
     * 获取联系人列表，并过滤掉黑名单和排序
     */
    private void getContactList() {
    	SourceData.clear();
    	mData.clear();
        // 获取本地好友列表
        Contact contact=new Contact(context);
        SourceData=contact.getPhoneContactsList();
        // 对list进行排序
//        Collections.sort(SourceData, new FullPinyinComparator() {
//        });
        handleTel(SourceData);
    }
    
    /**
	 * 执行异步任务
	 * 
	 * @param params
	 *      
	 */
	public void handleTel(final Object... params) {
		new AsyncTask<Object, Object,Map<String,UserInfo>>() {
			@SuppressWarnings("unchecked")
			@Override
			protected Map<String,UserInfo> doInBackground(Object... params) {
				Map<String,UserInfo> result=new HashMap<String, UserInfo>();			
			   try {
				   List<Map<String, String>> list=(List<Map<String, String>>) params[0];
				   Map<String,UserInfo> user=MyApplication.getInstance().getUserList();
				   PhoneDao dao=new PhoneDao(context);
				   for (Map<String, String> map1 : list) {
					   String string=map1.get("phone");
					   String realname=map1.get("name");
					   if(!isExist.containsKey(string)){
						   Map<String,String> map=new HashMap<String, String>();
						   map.put("search_name", string);
					       map.put("uid", Constant.UID+"");
					       map.put("token", Constant.TOKEN);
						   Map<String,Object> info=HttpUnit.sendSerachFriendRequest(map);
						   int status=(int) info.get("status");
						   if(status==0){
							   String s=(String) info.get("info");
							   JSONObject jsonObject= new  JSONObject(s);
								String uid=jsonObject.getString("id");
								String avatar=jsonObject.getString("avatar");
								String name=jsonObject.getString("name");
								 if(user.containsKey(uid)){
									UserInfo userinfo=user.get(uid);
									isExist.put(string, userinfo);
								}else{
									UserInfo userinfo=new UserInfo();
	        						userinfo.setAvatar(avatar);
	        						userinfo.setNick(name);
	        						userinfo.setType(22);
	        						userinfo.setUsername(uid);
	        						MyApplication.getInstance().addUser(userinfo);
	        						isExist.put(string, userinfo);
								}	
								 Phone p=new Phone();
								   p.setRelName(realname);
								   p.setTel(string);
								   p.setUserId(uid);
								   dao.savePhone(p);
						   }else{
							   Phone p=new Phone();
							   p.setRelName(realname);
							   p.setTel(string);
							   dao.savePhone(p);							   
						   }
					   }					   
				  }	    	        
		    	    return result;				
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					Log.e("1", e.toString());
					return null;
				}
			}
			protected void onPostExecute(Map<String,UserInfo> result) {
				 // 设置adapter
				 refresh();
		       		        
			};			    
		}.execute(params);}
    
    
//    private void LoadData(){
//    	 int dataIndex;//要加载的数据的index(从0开始)
//         int count = adapter.getCount();
//         //如果服务端还有数据，则继续加载更多
//         for (dataIndex = count; dataIndex < Math.min(count+EACH_COUNT, MAX_COUNT); dataIndex++) {
//        	 Map<String, String> item = new HashMap<String, String>();
//             item=SourceData.get(dataIndex);
//             Phone p=new Phone();
//             p=
//             mData.add(object);
//         }
//         //如果服务器上的全部数据都已加载完毕
//         if (dataIndex==MAX_COUNT) {
//             loadState=LOAD_STATE_FINISH;
//         }
//         else {
//             loadState=LOAD_STATE_IDLE;
//         }
//    }
//    
//    private void loadMore(){
//          LoadData();
//          adapter.notifyDataSetChanged();
//    }
//    
//    
//    
//    public void onScroll(AbsListView view, int firstVisibleItem,
//            int visibleItemCount, int totalItemCount) {
//        Log.e("1", "firstVisibleItem"+firstVisibleItem+" visibleItemCount"+visibleItemCount+" totalItemCount"+totalItemCount);
//        if (firstVisibleItem+visibleItemCount==totalItemCount) {
//            if(loadState==LOAD_STATE_IDLE)
//            {
//                loadState=LOAD_STATE_LOADING;
//                loadMore();
//            }
//        }
// 
//    }
// 
//    public void onScrollStateChanged(AbsListView arg0, int scrollState) {
//    }
    
    
    
  

    @SuppressLint("DefaultLocale")
    public class FullPinyinComparator implements Comparator<Map<String,String>> {

        @SuppressLint("DefaultLocale")
        @Override
        public int compare(Map<String,String> o1, Map<String,String> o2) {
            // TODO Auto-generated method stub
            String py1 = o1.get("name");
            String py2 = o2.get("name");
            py1=getPinYin(py1);
            py2=getPinYin(py2);
           // Log.e("1",py1+py2+getPinYin("$$#"));
            // 判断是否为空""
            if (isEmpty(py1) && isEmpty(py2))
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
    
    class UpdateContact implements Runnable{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Contact contact=new Contact(context);
			 List<Map<String, String>> list=contact.getPhoneContactsList();
			 Map<String,UserInfo> user=MyApplication.getInstance().getUserList();
			 try{
				 PhoneDao dao=new PhoneDao(context);
				   for (Map<String, String> map1 : list) {
					   String string=map1.get("phone");
					   String realname=map1.get("name");
					   if(!phonelist.contains(string)){
						   Map<String,String> map=new HashMap<String, String>();
						   map.put("search_name", string);
					       map.put("uid", Constant.UID+"");
					       map.put("token", Constant.TOKEN);
						   Map<String,Object> info=HttpUnit.sendSerachFriendRequest(map);
						   int status=(int) info.get("status");
						   if(status==0){
							   String s=(String) info.get("info");
							   JSONObject jsonObject= new  JSONObject(s);
								String uid=jsonObject.getString("id");
								String avatar=jsonObject.getString("avatar");
								String name=jsonObject.getString("name");
								 if(user.containsKey(uid)){	
								}else{
									UserInfo userinfo=new UserInfo();
	        						userinfo.setAvatar(avatar);
	        						userinfo.setNick(name);
	        						userinfo.setType(22);
	        						userinfo.setUsername(uid);
	        						MyApplication.getInstance().addUser(userinfo);
								}	
								 Phone p=new Phone();
								   p.setRelName(realname);
								   p.setTel(string);
								   p.setUserId(uid);
								   dao.savePhone(p);
						   }else{
							   Phone p=new Phone();
							   p.setRelName(realname);
							   p.setTel(string);
							   dao.savePhone(p);							   
						   }
					   }					   
				  }	    	 
			 }catch(Exception e){				 
			 }
				  
		}
    	
    }
    	
    
    



}
