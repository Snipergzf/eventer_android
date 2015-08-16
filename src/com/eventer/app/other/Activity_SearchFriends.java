package com.eventer.app.other;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eventer.app.Constant;
import com.eventer.app.MyApplication;
import com.eventer.app.R;
import com.eventer.app.adapter.PickContactAdapter;
import com.eventer.app.db.PhoneDao;
import com.eventer.app.db.UserDao;
import com.eventer.app.entity.Phone;
import com.eventer.app.entity.User;
import com.eventer.app.entity.UserInfo;
import com.eventer.app.http.HttpUnit;
import com.eventer.app.other.LocalContactActivity.UpdateContact;
import com.eventer.app.other.LocalContactActivity.ViewHolder;
import com.eventer.app.task.Contact;

public class Activity_SearchFriends extends Activity {
	private String search_info;
	private Context context;
	private ProgressDialog dialog;
	private ListView listview;
	private EditText et_search;
	private TextView tv_search;
	private RelativeLayout re_search;
	private LinearLayout ll_list;
	private List<Phone> mData;
	private List<Phone> totaldata;
	private  MyAdapter adapter;
	private List<String> phonelist=new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriends_two);
        context=this;
        
        initView();
        initData();
    }
	private void initView() {
		// TODO Auto-generated method stub
		    listview=(ListView)findViewById(R.id.listview);
	        re_search = (RelativeLayout) findViewById(R.id.re_search);
	        tv_search = (TextView) findViewById(R.id.tv_search);
	        et_search = (EditText) findViewById(R.id.et_search);
	        ll_list=(LinearLayout)findViewById(R.id.ll_list);
	        
	        et_search.addTextChangedListener(new TextWatcher() {
	            public void onTextChanged(CharSequence s, int start, int before,
	                    int count) {
	                if (s.length() > 0) {
	                    re_search.setVisibility(View.VISIBLE);
	                    tv_search.setText(et_search.getText().toString().trim());
	                } else {

	                    re_search.setVisibility(View.GONE);
	                    tv_search.setText("");

	                }
	                if (s.length() > 0) {
	                    String str_s = et_search.getText().toString().trim();
	                    int len=str_s.length();
	                    mData = new ArrayList<Phone>();
	                    if(totaldata!=null&&totaldata.size()>0){
	                    	for (Phone p: totaldata) {
	                            String tel = p.getTel();
	                            if(tel!=null&&tel.length()>len){
	                            	tel=tel.substring(0, len);
	                            }
	                            if (tel.equals(str_s)) {

	                               mData.add(p);
	                            }
	                            if(mData.size()>0){
	                            	adapter = new  MyAdapter(context);
	                                listview.setAdapter(adapter);
	                                ll_list.setVisibility(View.VISIBLE);
	                            }
	                        }
	                    }
	                } else {
	                    ll_list.setVisibility(View.GONE);
	                }


	            }

	            public void beforeTextChanged(CharSequence s, int start, int count,
	                    int after) {
	            }

	            public void afterTextChanged(Editable s) {

	            }
	        });
	        re_search.setOnClickListener(new OnClickListener() {

	            @Override
	            public void onClick(View v) {

	                String friend = et_search.getText().toString().trim();
	                if (friend == null || friend.equals("")) {
	                    return;
	                }
	                search_info=friend;
	                searchUser(friend);

	            }

	        });
	        
	        listview.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					Phone p=mData.get(position);
					String tel=p.getTel();
					if(tel!=null&&tel.length()>0){
						et_search.setText(tel);
						searchUser(tel);
					}
				}
			});
	}

	private void initData() {
		// TODO Auto-generated method stub
    	PhoneDao dao=new PhoneDao(context);
        totaldata=dao.getPhoneList();
		phonelist=dao.getTelList();
		if(phonelist==null){
			phonelist=new ArrayList<String>();
			UpdateContact thread1=new UpdateContact();//创建新的Runnable，	
			Thread thread=new Thread(thread1);//利用Runnable对象生成Thread
			thread.start();
		} 
	}

	private void searchUser(String friend_info) {
        dialog = new ProgressDialog(
                Activity_SearchFriends.this);
        dialog.setMessage("正在查找联系人...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();
        Map<String, String> map = new HashMap<String, String>();
        map.put("search_name", friend_info);
        map.put("uid", Constant.UID+"");
        map.put("token", Constant.TOKEN);
        FriendSearch(map);
    }

    public void back(View view) {
        finish();
    }
   
    /**
	 * 执行异步任务
	 * 
	 * @param params
	 *      
	 */
	public void FriendSearch(final Object... params) {
		new AsyncTask<Object, Object,Map<String,Object>>() {

			@SuppressWarnings("unchecked")
			@Override
			protected Map<String,Object> doInBackground(Object... params) {
				Map<String,Object>  map=new HashMap<String, Object>();
			     try {
		    	        map=HttpUnit.sendSerachFriendRequest((Map<String, String>)params[0]);
		    	        return map;
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.e("1", e.toString());
					return null;
				}
			}
			protected void onPostExecute(Map<String,Object> map) {
				 int status=(int)map.get("status");
				 String info=(String)map.get("info");
				 if(status==0){ 
					 try {
						JSONObject jsonObject= new  JSONObject(info);
						String uid=jsonObject.getString("id");
						String avatar=jsonObject.getString("avatar");
						String user_rank=jsonObject.getString("user_rank");
						String name=jsonObject.getString("name");
					
						User u=new User();
						u.setAvatar(avatar);
						u.setNick(name);
						u.setUsername(uid);
						u.setType(22);
						UserDao dao=new UserDao(context);
						if(!dao.isExistContactID(uid+"")){
							dao.saveContact(u);
						}

						Intent intent=new Intent();
	                    intent.putExtra("user", uid);
	                    intent.setClass(Activity_SearchFriends.this,Activity_UserInfo.class);
	                    startActivity(intent);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}finally{
						if(dialog!=null)
						   dialog.dismiss();
					}
					
			     }else {
			    	 Toast.makeText(context,info, Toast.LENGTH_LONG)
						.show();
			     }
				 if(dialog!=null)
					   dialog.dismiss();
				
			    };
		}.execute(params);}
	
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
	        @SuppressLint("ViewHolder")
			public View getView(final int position, View convertView, ViewGroup parent) {  
	             ViewHolder holder = null;  
	            if (convertView == null) {                  
	                holder=new ViewHolder();                                    
	                convertView = mInflater.inflate(R.layout.item_phone_search_list, null);  
	                holder.name = (TextView)convertView.findViewById(R.id.tv_name);  
	                holder.phone = (TextView)convertView.findViewById(R.id.tv_eventer_id);               
	                convertView.setTag(holder);               
	            }else {               
	                holder = (ViewHolder)convertView.getTag();  
	            }         
	            String phone=mData.get(position).getTel();
	            String name=mData.get(position).getRelName();          
	            holder.name.setText(name);  
	            holder.phone.setText("手机号:"+phone);
	            return convertView;  
	        }  
	    }
	    public final class ViewHolder {   
	       TextView name;
	       TextView phone; 
	    }  
	    
	    class UpdateContact implements Runnable{
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Contact contact=new Contact(context);
				 List<Map<String, String>> list=contact.getPhoneContactsList();
				 try{
					 PhoneDao dao=new PhoneDao(context);
					   for (Map<String, String> map1 : list) {
						   String string=map1.get("phone");
						   String realname=map1.get("name");
						   if(!phonelist.contains(string)){							  
								   Phone p=new Phone();
								   p.setRelName(realname);
								   p.setTel(string);
								   dao.savePhone(p);							   
						   }					   
					  }	    	 
				 }catch(Exception e){				 
				 }
					  
			}
	    	
	    }

}