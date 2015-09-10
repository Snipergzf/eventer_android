package com.eventer.app.other;

import hirondelle.date4j.DateTime;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.eventer.app.R;
import com.eventer.app.db.DBManager;
import com.eventer.app.db.SchedualDao;
import com.eventer.app.entity.Schedual;

@SuppressLint("SimpleDateFormat")
public class Calendar_AddSchedual extends FragmentActivity implements OnClickListener {
	
	private TextView event_finish;
	private Button btn_schedual,btn_todo;
	private ImageView iv_back;
	private Fragment[] fragments;
	private Fragment_AddSchedual fragment_add_schedual;
	private Fragment_AddTodo fragment_add_todo;
	private int currentIndex=0;
	private Long id;
	private boolean IsNew=true;
	public static final String ARGUMENT = "argument";
	public static final String RESPONSE = "response";
	private Context context;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar_addschedual);
		context=this;		
		id=getIntent().getLongExtra(Calendar_ViewSchedual.ARGUMENT_ID, -1);
		initView();
		Log.e("1",id+"");
	}
	
	private void initView() {
		// TODO Auto-generated method stub
		btn_schedual=(Button)findViewById(R.id.btn_schedual);
		btn_todo=(Button)findViewById(R.id.btn_todo);
		btn_schedual.setOnClickListener(this);
		btn_todo.setOnClickListener(this);
		btn_schedual.setSelected(true);
		
		fragment_add_schedual = new Fragment_AddSchedual();
	    fragment_add_todo=new Fragment_AddTodo();
	    fragments = new Fragment[] { fragment_add_schedual,fragment_add_todo};

     // 添加显示第一个fragment
	    getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragments[0])
                .add(R.id.fragment_container, fragments[1])
                .hide(fragments[1]).show(fragments[0]).commit();
	    
		
			
		iv_back=(ImageView)findViewById(R.id.event_back);
		event_finish=(TextView)findViewById(R.id.event_finish);	
		iv_back.setOnClickListener(this);
		event_finish.setOnClickListener(this); 
		if(id!=-1){
			SchedualDao dao=new SchedualDao(context);
			Schedual s=dao.getSchedual(id+"");
			if(s!=null){
				int type=s.getType();
				if(type==3){
					if(currentIndex!=1){
						FragmentTransaction trx = getSupportFragmentManager()
				                .beginTransaction();
				        trx.hide(fragments[currentIndex]);
				        trx.show(fragments[1]).commit();	           
						currentIndex=1;
						btn_schedual.setSelected(false);
						btn_todo.setSelected(true);
					}
				}
			}
			IsNew=true;
        }

	}

	
	
	
	
	@SuppressLint({ "SimpleDateFormat", "InlinedApi" })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		case R.id.btn_schedual:
			if(currentIndex!=0){
				changeView(0);
				currentIndex=0;
				btn_schedual.setSelected(true);
				btn_todo.setSelected(false);
			}
			break;
		case R.id.btn_todo:
			if(currentIndex!=1){
				changeView(1);
				currentIndex=1;
				btn_schedual.setSelected(false);
				btn_todo.setSelected(true);
			}
			break;
		case R.id.event_back:
			this.finish();
			break;
		case R.id.event_finish:
			 Log.e("1","add or edit");
			if(currentIndex==0){
				Fragment_AddSchedual.instance.finish();
			}else if(currentIndex==1){
			    Fragment_AddTodo.instance.finish();	
			}
	        this.finish();
			break;
		}
	}
	/***
	 * 切换Fragment
	 * @param index2
	 */
	private void changeView(int index2){
		FragmentTransaction trx = getSupportFragmentManager()
                .beginTransaction();
        trx.hide(fragments[currentIndex]);
        if (!fragments[index2].isAdded()) {
            trx.add(R.id.fragment_container, fragments[index2]);
        }
        trx.show(fragments[index2]).commit();	           
        currentIndex=index2;
       
	}


}
