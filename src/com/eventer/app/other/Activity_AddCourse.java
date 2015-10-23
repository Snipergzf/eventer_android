package com.eventer.app.other;

import java.util.List;
import java.util.Map;

import android.app.Instrumentation;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.eventer.app.R;
import com.umeng.analytics.MobclickAgent;

public class Activity_AddCourse extends FragmentActivity implements
		OnClickListener {
	private TextView tv_title;
	private LinearLayout ll_changeview;
	private ImageView iv_back;
	private TextView tv_finish;
	private PopupWindow popupdownWindow;// �����˵�
	private Fragment[] fragments;
	private Fragment_AddManually fragment_AddManually;
	private Fragment_Addkc_Search fragscan_addkc_search;
	public static Activity_AddCourse instance;
	private int index;
    private int currentIndex;
    private String[] views;
	private TextView shoudong_add, search_add;
	private ProgressDialog Webdowndialog;
	private List<Map<String, Object>> receiveList;
     
	public Activity_AddCourse() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_course_add);
        initView();
        instance=this;
        views=getResources().getStringArray(R.array.add_course);
		
		RecevierMainActivityIntent();

	}
	
	public void back(){
		this.finish();
	}

	private void initView() {
		// TODO Auto-generated method stub
		fragment_AddManually = new Fragment_AddManually();
	    fragscan_addkc_search = new Fragment_Addkc_Search();
	   
	    fragments = new Fragment[] { fragscan_addkc_search,fragment_AddManually};

     // �����ʾ��һ��fragment
	    getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragments[0])
                .add(R.id.fragment_container, fragments[1])
                .hide(fragments[1]).show(fragments[0]).commit();
	    
	    iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		tv_title = (TextView) findViewById(R.id.add_popdownmenu_text);
		ll_changeview=(LinearLayout)findViewById(R.id.ll_changeview);
		ll_changeview.setOnClickListener(this);
		
		
		tv_finish=(TextView)findViewById(R.id.tv_add_finish);
		tv_finish.setOnClickListener(this);
		
	}

	private void RecevierMainActivityIntent() {
		Intent FS_searchIntent = getIntent();
		if (FS_searchIntent != null) {
			Bundle bundlesao = FS_searchIntent.getExtras();
			if (bundlesao != null) {
				String search = bundlesao.getString("search");
				if (search != null && search.equals("openscanR")) {
					changeView(1);
				}
			}
		}
	}

	private void initpopwindows() {
		// TODO Auto-generated method stub
		if (popupdownWindow == null) {
			View view = getLayoutInflater().inflate(
					R.layout.addcourse_popmenu, null);
			popupdownWindow = new PopupWindow(view, 300, 350);
			inittv_title(view);
			// ʹ��ۼ� ��Ҫ������˵���ؼ����¼��ͱ���Ҫ���ô˷���
			popupdownWindow.setFocusable(true);
			// ����������������ʧ
			popupdownWindow.setOutsideTouchable(true);
			// ���ñ����������Ϊ�˵��������Back��Ҳ��ʹ����ʧ�����Ҳ�����Ӱ����ı���
			popupdownWindow.setBackgroundDrawable(new BitmapDrawable());
			// �����˵��Ĺر��¼�
			popupdownWindow.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss() {
					// �ı���ʾ�İ�ťͼƬΪ����״̬
					// chickshoudongadd();
				}
			});

			// ���������¼�
			popupdownWindow.setTouchInterceptor(new OnTouchListener() {
				public boolean onTouch(View view, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
						// �ı���ʾ�İ�ťͼƬΪ����״̬
					}

					return false;
				}
			});
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ll_changeview:
			if (popupdownWindow != null && popupdownWindow.isShowing()) {
				popupdownWindow.dismiss();
				popupdownWindow = null;
			} else {
				initpopwindows();
				popupdownWindow.showAsDropDown(v, 0, 5);
			}
			break;
		case R.id.iv_back:
			new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Instrumentation instrumentation = new Instrumentation();
					instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
				}
			}).start();
			break;
		case R.id.tv_add_finish:
			if(index==1){
				Fragment_AddManually.instance.saveCourse();
			}
			break;
		}
	}

	private void inittv_title(View view) {
		shoudong_add = (TextView) view.findViewById(R.id.pop_shoudong);		
		search_add = (TextView) view.findViewById(R.id.pop_search);

		search_add.setOnClickListener(new onpopchicklistener());
		shoudong_add.setOnClickListener(new onpopchicklistener());

	}

	class onpopchicklistener implements OnClickListener {

		@Override
		public void onClick(View v) {//ѡ��ѡ�η�ʽ
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.pop_search:
				index=0;
				tv_finish.setVisibility(View.GONE);
				break;
			case R.id.pop_shoudong:
				index=1;
				tv_finish.setVisibility(View.VISIBLE);
				break;
			}
			popupdownWindow.dismiss();
			if (currentIndex != index) {
	            changeView(index);
	        }
		}
	}
	
	//�л���ͼ
	private void changeView(int index2){
		FragmentTransaction trx = getSupportFragmentManager()
                .beginTransaction();
        trx.hide(fragments[currentIndex]);
        if (!fragments[index2].isAdded()) {
            trx.add(R.id.fragment_container, fragments[index2]);
        }
        trx.show(fragments[index2]).commit();	           
        currentIndex=index2;
        tv_title.setText(views[index]);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
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
