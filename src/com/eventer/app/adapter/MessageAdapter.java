package com.eventer.app.adapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.Html;
import android.text.Spannable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.alibaba.fastjson.JSONObject;
import com.easemob.util.DateUtils;
import com.eventer.app.Constant;
import com.eventer.app.MyApplication;
import com.eventer.app.R;
import com.eventer.app.db.ChatEntityDao;
import com.eventer.app.entity.ChatEntity;
import com.eventer.app.entity.User;
import com.eventer.app.entity.UserInfo;
import com.eventer.app.other.Activity_EventDetail;
import com.eventer.app.other.Activity_UserInfo;
import com.eventer.app.other.MyUserInfoActivity;
import com.eventer.app.socket.Activity_Chat;
import com.eventer.app.task.LoadDataFromHTTP;
import com.eventer.app.task.LoadDataFromHTTP.DataCallBack;
import com.eventer.app.task.LoadUserAvatar;
import com.eventer.app.task.LoadUserAvatar.ImageDownloadedCallBack;
import com.eventer.app.util.LocalUserInfo;
import com.eventer.app.util.SmileUtils;


@SuppressLint({ "SdCardPath", "InflateParams" })
public class MessageAdapter extends BaseAdapter {
	
    private static final int MESSAGE_TYPE_TXT = 1;
    private static final int MESSAGE_TYPE_EVENT=2;
    private static final int MESSAGE_TYPE_SCHEDUAL=3;
//    public static final int MESSAGE_TYPE_PICTURE =2;
    
    private static final int Recv_IsRead=0;
    private static final int Recv_NotRead=1;
    private static final int Send_Success=2;
   

    private LayoutInflater inflater;
    private LoadUserAvatar avatarLoader;
    private List<ChatEntity> msglist=new ArrayList<ChatEntity>();
    private Context context;
    private int chatType;

    public MessageAdapter(Context context, String username,List<ChatEntity> msglist,int chatType) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.msglist = msglist;
        this.chatType=chatType;
        avatarLoader = new LoadUserAvatar(context, Constant.IMAGE_PATH);
    }

    /**
     * 获取item数
     */
    public int getCount() {
        return msglist.size();
    }

    /**
     * 刷新页面
     */
    public void refresh() {
        notifyDataSetChanged();
    }

    public ChatEntity getItem(int position) {
        return msglist.get(position);
    }
    
    public void addItem(ChatEntity msg){
    	msglist.add(msg);
    }

    public long getItemId(int position) {
        return position;
    }


    @SuppressLint("InflateParams")
    private View createViewByMessage(ChatEntity message) {
    	if(message.getStatus()!=-1){  //需要控制getStatus=-1的情况，在加入mData之前处理掉
	        switch (message.getType()) {
	        default:
	            // 语音电话
	            return message.getStatus()>1? inflater
	                    .inflate(R.layout.row_sent_message, null) : inflater
	                    .inflate(R.layout.row_received_message, null);
	        }
        }
    	return null;
    }

    @SuppressWarnings("null")
	@SuppressLint("NewApi")
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ChatEntity message = getItem(msglist.size()-position-1);  
        if (message.getFrom().equals("admin")) {
            return convertView;
        } else {
        	ViewHolder holder; 	      
        	holder = new ViewHolder();
        	convertView=createViewByMessage(message);	
    		final View view=convertView;

                	try {
                        holder.pb = (ProgressBar) view
                                .findViewById(R.id.pb_sending);
                        holder.head_iv = (ImageView) view
                                .findViewById(R.id.iv_userhead);
                        // 这里是文字内容
                        holder.tv = (TextView) view
                                .findViewById(R.id.tv_chatcontent);
                    } catch (Exception e) {
                    }
    
            holder.tv.setOnLongClickListener(new OnLongClickListener() {			
				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
		               showMyDialog("消息", message,position);
		               return true;
				}
			});
            
            holder.tv.setOnClickListener(new OnClickListener() {			
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String txt = message.getContent();
			        if (message.getStatus()>1) {
			        }else{
			        	if(chatType==Activity_Chat.CHATTYPE_GROUP){
			        		int i=message.getContent().indexOf(":\n"); 
			        		txt="";
			        		if(i!=-1){
			              		  txt=message.getContent().substring(i+2);
			        		}           	
			        	}
			        }
			        int type=message.getType();
			        switch(type){
				         case 2:
				        	 try{
						        	JSONObject json=JSONObject.parseObject(txt);
						            String event_id=json.getString("event_id");
			                        Intent intent=new Intent();
			                        intent.setClass(context, Activity_EventDetail.class);
			                        intent.putExtra("event_id", event_id);
			                        context.startActivity(intent);
						        }catch(Exception e){

						        }
				        	 break;
				         case 3:
				        	 break;
			        	 default:
			        		 break;
			        	 
			        }
			        
				}
			});
            
            
            
            TextView timestamp = (TextView) view
                    .findViewById(R.id.timestamp);
            if (position == 0) {
                timestamp.setText(DateUtils.getTimestampString(new Date(message
                        .getMsgTime()*1000)));
                timestamp.setVisibility(View.VISIBLE);
            } else {
                // 两条消息时间离得如果稍长，显示时间
                if (DateUtils.isCloseEnough(message.getMsgTime()*1000, msglist
                        .get(position - 1).getMsgTime()*1000)) {
                    timestamp.setVisibility(View.GONE);
                } else {
                    timestamp.setText(DateUtils.getTimestampString(new Date(
                            message.getMsgTime()*1000)));
                    timestamp.setVisibility(View.VISIBLE);
                }
            }
            String toChat="";
            if(message.getStatus()>1){
            	toChat=Constant.UID;
            	final String avatar=LocalUserInfo.getInstance(context).getUserInfo("avatar");          	
                holder.head_iv.setTag(avatar);
                if (avatar != null && !avatar.equals("")&&!avatar.equals("default")) {
                    Bitmap bitmap = avatarLoader.loadImage(holder.head_iv,
                            avatar, new ImageDownloadedCallBack() {
                                @Override
                                public void onImageDownloaded(
                                        ImageView imageView, Bitmap bitmap,int status) {                               	 
                                    if (imageView.getTag() == avatar&&status==-1) {
                                        imageView.setImageBitmap(bitmap);
                                    }
                                }
                            });
                    if (bitmap != null) {
                        holder.head_iv.setImageBitmap(bitmap);
                    }
                }  
            }else{
            	Map<String,User> uList=MyApplication.getInstance().getContactList();
            	if(chatType==Activity_Chat.CHATTYPE_SINGLE){
            		toChat=message.getFrom(); 
            	}else{
            		if(message.getContent()!=null&&!message.getContent().equals("")){
            			int i=message.getContent().indexOf(":\n"); 
                		if(i!=-1){
                      		  toChat=message.getContent().substring(0,i);
                		}
            		}	
            	}
            	final String speaker=toChat;
        		if(speaker.equals(Constant.UID)){
        			String avatar=LocalUserInfo.getInstance(context).getUserInfo("avatar");			   
        			showUserAvatar(holder.head_iv, avatar);
        		}else if(MyApplication.getInstance().getContactList().containsKey(speaker)){
        			User u=MyApplication.getInstance().getContactList().get(speaker);
        			String avatar=u.getAvatar();
        			showUserAvatar(holder.head_iv, avatar);
        		}else if(MyApplication.getInstance().getUserList().containsKey(speaker)){
        			UserInfo u=MyApplication.getInstance().getUserList().get(speaker);
        			String avatar=u.getAvatar();
        			showUserAvatar(holder.head_iv, avatar);
        		}else{
        			Map<String,String> map=new HashMap<String, String>();
        			map.put("uid", speaker);
        			LoadDataFromHTTP task=new LoadDataFromHTTP(context, Constant.URL_GET_USERINFO, map);
        			task.getData(new DataCallBack() {			
        				@Override
        				public void onDataCallBack(JSONObject data) {
        					// TODO Auto-generated method stub
        					int status=data.getInteger("status");
        					switch (status) {
        					case 0:
        						JSONObject user_action=data.getJSONObject("user_action");
        						JSONObject info=user_action.getJSONObject("info");
        						String name=info.getString("name");						
        						String avatar=info.getString("avatar");						
        						showUserAvatar(((ImageView) view.findViewById(R.id.iv_userhead)), avatar);
        						UserInfo user=new UserInfo();
        						user.setAvatar(avatar);
        						user.setNick(name);
        						user.setType(22);
        						user.setUsername(speaker);
        						MyApplication.getInstance().addUser(user);
        						break;
        					default:
//        						Toast.makeText(context, "获取用户信息失败！", Toast.LENGTH_SHORT).show();
        						Log.e("1", "获取用户信息失败：");
        						break;
        					}
        				}
        			});
        		}
        	}
            final String user=toChat;
            holder.head_iv.setOnClickListener(new OnClickListener() {			
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(!user.equals(Constant.UID)){
						Intent intent=new Intent();
						intent.putExtra("user", user);
						intent.setClass(context, Activity_UserInfo.class);
						context.startActivity(intent);
					}else{
						Intent intent=new Intent();
						intent.setClass(context,MyUserInfoActivity.class);
						context.startActivity(intent);
					}
						
					
				}
			});
            handleTextMessage(message, holder);     
          }
        return convertView;
    }
    private void showMyDialog(String title, final ChatEntity message, final int position) {

        final AlertDialog dlg = new AlertDialog.Builder(context).create();
        dlg.show();
        Window window = dlg.getWindow();
        // 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
        window.setContentView(R.layout.alertdialog);
        window.findViewById(R.id.ll_title).setVisibility(View.VISIBLE);

        TextView tv_title = (TextView) window.findViewById(R.id.tv_title);
        tv_title.setText(title);
        TextView tv_content1 = (TextView) window.findViewById(R.id.tv_content1);
        final String username = message.getFrom();
        // 是否已经置顶
//            tv_content1.setText("置顶聊天");
        tv_content1.setVisibility(View.GONE);
        TextView tv_content2 = (TextView) window.findViewById(R.id.tv_content2);
        tv_content2.setText("删除该消息");
        tv_content2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ChatEntityDao dao=new ChatEntityDao(context);
                dao.deleteMessage(message.getMsgID()+"");
                msglist.remove(msglist.size()-1-position);
                refresh();
                 
              ((Activity_Chat) context).refresh();
                dlg.cancel();

            }
        });

    }
 
    
    /**
     * 文本消息
     * 
     * @param message
     * @param holder
     * @param position
     */
    private void handleTextMessage(ChatEntity message, ViewHolder holder) {
        String txt = message.getContent();
        if (message.getStatus()>1) {}else{
        	if(chatType==Activity_Chat.CHATTYPE_GROUP){
        		int i=message.getContent().indexOf(":\n"); 
        		txt="";
        		if(i!=-1){
              		  txt=message.getContent().substring(i+2);
        		}           	
        	}
        }
        Spannable span;
        switch (message.getType()) {
		case 1:
			span = SmileUtils
            .getSmiledText(context, txt);
            holder.tv.setText(span, BufferType.SPANNABLE);
			break;
		case 2:
			try{
	        	JSONObject json=JSONObject.parseObject(txt);
	            String event_title=json.getString("event_title");
	            String share_txt="<font color=" + "\"" + "#AAAAAA" + "\">"   + "【活动分享】" + "</font><br/>" 
	            + "<font color=" + "\"" + "#666666" + "\">" +event_title
	            + "</font>" ;
	            holder.tv.setText(Html.fromHtml(share_txt));
	        }catch(Exception e){ 
	        }
			break;
		case 3:
			try {
				JSONObject json=JSONObject.parseObject(txt);
	            String title=json.getString("schedual_title");
	            String share_txt="<font color=" + "\"" + "#AAAAAA" + "\">"   + "【日程分享】" + "</font><br/>" 
	            + "<font color=" + "\"" + "#666666" + "\">" +title
	            + "</font>" ;
	            holder.tv.setText(Html.fromHtml(share_txt));
			} catch (Exception e) {
				// TODO: handle exception
			}
			break;

		default:
			span = SmileUtils
            .getSmiledText(context, txt);
            holder.tv.setText(span, BufferType.SPANNABLE);
			break;
		}
        if(holder.pb!=null)
            holder.pb.setVisibility(View.GONE);
        
        
        
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

 

    public static class ViewHolder {
        public ImageView iv;
        public TextView tv;
        public ProgressBar pb;
        public ImageView staus_iv;
        public ImageView head_iv;
        public TextView tv_userId;
    }

}