package com.eventer.app.socket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.LinkedList;
import java.util.Queue;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.eventer.app.Constant;
import com.eventer.app.entity.Msg.Container;

public class SocketService extends Service{
	private SocketSendBinder binder = new SocketSendBinder();
	private boolean quit;
	private Socket socket;
	private DataOutputStream output;
	private InputStream input;
	private static Queue<Container> taskQueue = new LinkedList<Container>();
	private Thread send,recv;
	private Intent intent = new Intent("com.eventer.app.socket.RECEIVER"); 
	private Intent intent_a = new Intent("com.eventer.app.activity");
	private int index=0;
	private int total=50;
	private myX509TrustManager xtm = new myX509TrustManager();  
	
	public class SocketSendBinder extends Binder{
		public boolean sendOne(Container msg){
			taskQueue.add(msg);
			return true;
		}
	}
	 class myX509TrustManager implements X509TrustManager  
	    {  
	  
	        public void checkClientTrusted(X509Certificate[] chain, String authType)  
	        {  
	        }  
	  
	        public void checkServerTrusted(X509Certificate[] chain, String authType)  
	        {  
	             System.out.println("cert: " + chain[0].toString() + ", authType: "  
	                    + authType);  
	        }  
	  
	        public X509Certificate[] getAcceptedIssuers()  
	        {  
	            return null;  
	        }  
	    }  
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		Log.e("SocketService", "onCreate");	
		send = new Thread(){
			@Override
			public void run() {
				super.run();
				try {
					SSLContext context;
					try {
						context = SSLContext.getInstance("SSL");
						context.init(null, new X509TrustManager[]{xtm}, null); 
				        SSLSocketFactory factory = context.getSocketFactory();  
				        socket = (SSLSocket) factory.createSocket(Constant.DomainName, 1430);
				        
					} catch (NoSuchAlgorithmException | KeyManagementException | IOException e) {
						e.printStackTrace();
					}
					String msgBody="{\"token\":\""+Constant.TOKEN+"\"}";
		            long time=System.currentTimeMillis()/1000;
		            Container msg = Container.newBuilder()
		               		.setMID("1").setSID(Constant.UID+"").setRID(Constant.UID+"")
		               		.setTYPE(2).setSTIME(time).setBODY(msgBody)
		               		.build();
		            taskQueue.add(msg);					
					while (!quit){
						Thread.sleep(1000);
						index++;
						try {
							if(socket!=null){
								 output = new DataOutputStream(socket.getOutputStream());
					        		
						         if (!taskQueue.isEmpty()) {
						        	 Container _msg = taskQueue.poll();
						        	 byte[] msg_con = _msg.toByteArray();  //msg为protocbuf数据
							         int m_len = msg_con.length;
							         byte[] msg_len=GetBytes(m_len);//int转byte[]
							         byte[] smsg=new byte[m_len+4];  //初始化发送到服务器的数据
							         for (int i = 0; i < 4; i++) {         
							        	 smsg[i] = msg_len[i];                              
					                 } 
							         for (int i = 0; i < m_len; i++) {          
							        	 smsg[i+4] = msg_con[i];                              
					                 } 
							         output.write(smsg, 0, m_len+4);
							         output.flush();
							         Log.e("1", "send:" +(m_len+4)+"dddd");
							         index=0;
								}else if(index>total){
									msgBody="{\"token\":\""+Constant.TOKEN+"\"}";
						            time=System.currentTimeMillis()/1000;
						            Container msg1 = Container.newBuilder()
						               		.setMID("1").setSID(Constant.UID+"").setRID("")
						               		.setTYPE(1|4).setSTIME(time).setBODY(msgBody)
						               		.build();
						            taskQueue.add(msg1);
						            index=0;
						            total=50+(int)((Math.random())*7-3);
								}
							}
					        
						} catch(IOException e) {
							e.printStackTrace();
						}
					
					}
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			
		};
		send.start();
		recv = new Thread(){
			@Override
			public void run() {
				super.run();
				while (!quit){
					try {
						Thread.sleep(1000);
						if(socket!=null){
							input = socket.getInputStream();
							int buff=socket.getReceiveBufferSize();
							byte[] buf=new byte[(buff<1?1:buff)];        
					        int len = input.read(buf); 
					        if(len==-1){
					        	SSLContext context;
								try {
									context = SSLContext.getInstance("SSL");
									context.init(null, new X509TrustManager[]{xtm}, null); 
							        SSLSocketFactory factory = context.getSocketFactory();  
							        socket = (SSLSocket) factory.createSocket(Constant.DomainName, 1430);
							        
								} catch (NoSuchAlgorithmException | KeyManagementException | IOException e) {
									e.printStackTrace();
								}
								String msgBody="{\"token\":\""+Constant.TOKEN+"\"}";
					            long time=System.currentTimeMillis()/1000;
					            Container msg = Container.newBuilder()
					               		.setMID("1").setSID(Constant.UID+"").setRID(Constant.UID+"")
					               		.setTYPE(2).setSTIME(time).setBODY(msgBody)
					               		.build();
					            taskQueue.add(msg);
							}
							if (len>=4) {
								byte[] rece_len = new byte[4];
						        byte[] rece_data = new byte[len-4];
						        for (int i = 0; i < 4; i++) {   	                        
						        	 rece_len[i] = buf[i];  //数据长度                            
				                } 
						         for (int i = 4; i < len; i++) {   
				                       
						        	 rece_data[i-4] = buf[i]; //数据内容                            
				                }
						        Log.e("1", rece_data.length+"");
						        Container rmsg = Container.parseFrom(rece_data);
						        if(rmsg!= null && (rmsg.getSID().equals("00000001"))){
						        	 String msgBody="{\"type\":\"receipt\",\"mid\":\""+rmsg.getMID()+"\"}";
						        	 long time=System.currentTimeMillis()/1000;
							         Container msg = Container.newBuilder()
							               		.setMID("").setSID(rmsg.getRID()).setRID(rmsg.getSID())
							               		.setTYPE(1).setSTIME(time).setBODY(msgBody)
							               		.build();
							            taskQueue.add(msg);
						        		intent_a.putExtra("msg", rmsg.getBODY());						
					                    sendBroadcast(intent_a);						
							   } else if (rmsg!= null && (!rmsg.getBODY().equals(""))) {
//								   switch (rmsg.getTYPE()) {
//									case 17:
//									   intent.putExtra("msg", rmsg.getBODY());
//									   intent.putExtra("talker",rmsg.getSID());
//									   intent.putExtra("mid", rmsg.getMID());
//									   intent.putExtra("time", rmsg.getSTIME());
//				                       sendBroadcast(intent);
//									   break;
//									case 49:
//									   intent.putExtra("msg", rmsg.getBODY());
//									   intent.putExtra("talker",rmsg.getSID());
//									   intent.putExtra("mid", rmsg.getMID());
//									   intent.putExtra("time", rmsg.getSTIME());
//					                   sendBroadcast(intent);
//									   break;
//	
//									default:
//										break;
//								}
								   intent.putExtra("msg", rmsg.getBODY());
								   intent.putExtra("talker",rmsg.getSID());
								   intent.putExtra("mid", rmsg.getMID());
								   intent.putExtra("time", rmsg.getSTIME());
			                       sendBroadcast(intent);
									   	  
								} 
								 Log.e("socket", "received:MID="+rmsg.getMID()+"; RID=" + rmsg.getRID()+"; SID="+rmsg.getSID()+"; Type="+rmsg.getTYPE()+"; Time="+rmsg.getSTIME()+"; Body="+rmsg.getBODY());
								
							}
							
						}
//						else {						
//							if(error>1&&!auth_.isAlive()){
//								socket=null;
//								auth_.start();								
//							 }
//							error++;
//						}
						
					
					} catch (IOException | InterruptedException e) {
						e.printStackTrace();
						
						
					}  
				}
			}
		};
		recv.start();
//		heart_msg= new Thread(){
//			@Override
//			public void run() {
//				super.run();
//				try {
//					while (!quit){
//						Thread.sleep(1000);
//						if(i>25){
//							String msgBody="{\"token\":\""+Constant.TOKEN+"\"}";
//				            long time=System.currentTimeMillis()/1000;
//				            Container msg = Container.newBuilder()
//				               		.setMID("1").setSID(Constant.UID+"").setRID("")
//				               		.setTYPE(1|4).setSTIME(time).setBODY("")
//				               		.build();
//				            taskQueue.add(msg);
//				            i=0;
//						}
//					}
//				} catch (InterruptedException e1) {
//					e1.printStackTrace();
//				}
//			}
//			
//		};
//		heart_msg.start();
	}

	public static byte[] GetBytes(int value)
    {
        ByteBuffer buffer = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN);
        buffer.putInt(value);
        return buffer.array();
    }
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		this.quit=true;
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			
		
	}
	

}
