package com.eventer.app.socket;

import java.util.LinkedList;
import java.util.Queue;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.provider.Settings.Global;
import android.util.Log;

import com.eventer.app.Constant;
import com.eventer.app.entity.Msg.Container;

abstract class SocketActivity extends Activity{
	private class A{
		int RID;
		int MID;
		String body;
		long time;
		public A(int MID, int RID, String body){
			this.RID = RID;
			this.MID = MID;
			this.time =System.currentTimeMillis()/1000;
			this.body = body;
		}
	}
	public SocketService.SocketSendBinder binder;
	public Queue<A> queued = new LinkedList<A>();
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
    public boolean newMsg(int MID, int RID, String body){
    	if(binder == null){
    		Log.e("newMsg", "binder is null");
    		queued.add(new A(MID, RID, body));
			bindService(new Intent(this, SocketService.class),
		            internetServiceConnection, Context.BIND_AUTO_CREATE);			
			return false;
		}
    	else {
    		sendToService(new A(MID, RID, body));
		}
		return true;
    }
    
    private void sendToService(A a){
    	Container msg = Container.newBuilder()
           		.setMID(String.valueOf(a.MID)).setSID(String.valueOf(Constant.UID)).setRID(String.valueOf(a.RID))
           		.setTYPE(17).setSTIME(a.time).setBODY(a.body)
           		.build();
    	binder.setCurrentActivity(this);
    	binder.sendOne(msg);
    }
    abstract void callback(String body);
    
    @Override
    protected void onPause() {
        super.onPause();
      
    }

}
