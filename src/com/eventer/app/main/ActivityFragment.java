package com.eventer.app.main;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.eventer.app.R;
import com.eventer.app.adapter.EventAdapter;
import com.eventer.app.adapter.ThemeAdapter;
import com.eventer.app.db.EventDao;
import com.eventer.app.entity.Event;
import com.eventer.app.other.Activity_EventDetail;
import com.eventer.app.widget.HorizontalListView;


public  class ActivityFragment extends Fragment implements OnScrollListener {

	private ListView listView;    
	private EventAdapter listViewAdapter;   
	private List<Event> listItems;   
	private View loadMoreView;   
	private HorizontalListView theme_listview;
	private ThemeAdapter themeAdapter;

	View footer;// �ײ����֣�
	private int visibleLastIndex = 0;   //���Ŀ���������    
	ArrayList<Event> event_list = new ArrayList<Event>();
	private Button loadMoreButton;    
	private int datasize = 5;  
	private int visibleItemCount;       // ��ǰ���ڿɼ������� 
	private Context context;  
	public static ActivityFragment instance;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		final View rootView = inflater.inflate(R.layout.fragment_activity, container, false);
		context=getActivity();
		instance=ActivityFragment.this;
		loadMoreView = inflater.inflate(R.layout.loadmore, null);
		initView(rootView);
		initData();
		return rootView;
	}

	
	private void initView(View rootView) {
		// TODO Auto-generated method stub
		  
        loadMoreButton = (Button)loadMoreView.findViewById(R.id.loadMoreButton);
		listView = (ListView)rootView.findViewById(R.id.listview); 
		theme_listview=(HorizontalListView)rootView.findViewById(R.id.theme_listview);
		listView.addFooterView(loadMoreView);  
        listItems = getListItems(); 
        //��б��������
        listViewAdapter = new EventAdapter(this.getActivity(), listItems); //����������   
        listView.setAdapter(listViewAdapter);   		
        listView.setOnScrollListener(this); 
        loadMoreButton.setOnClickListener(new View.OnClickListener() {           
           @Override  
            public void onClick(View v) {  
//            	Intent intent = new Intent();
//       		    intent.setClass(getActivity(),moreselected.class);
//         		startActivity(intent);
         };  
        });
        //�item����¼�
        listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Event e=listItems.get(position);
				Intent intent=new Intent();
				intent.setClass(getActivity(), Activity_EventDetail.class);
				intent.putExtra("event_id", e.getEventID());
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.in_from_right, 0);
			}
		});
        
        theme_listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub			
				themeAdapter.setPos(position);
				themeAdapter.notifyDataSetChanged();
				theme_listview.setSelection(position);
			}
		});    
	}
	private void initData() {
		// TODO Auto-generated method stub
		ArrayList<String> lists = new ArrayList<String>();
		lists.add("�Ƽ�");
		lists.add("����");
		lists.add("����");
		lists.add("����");
		lists.add("�");
		lists.add("�ƹ�");
		lists.add("����");
		lists.add("��ʳ");

		initThemeListView(lists);
	}

	
	private void initThemeListView(ArrayList<String> lists) {
		themeAdapter = new ThemeAdapter(context,lists);
		theme_listview.setAdapter(themeAdapter);
	}
	 private List<Event> getListItems() {   
	        List<Event> listItems = new ArrayList<Event>();
	        EventDao dao=new EventDao(context);
	        listItems=dao.getEventList();
	        return listItems;   
	    }
	
   public void addEvent(Event event){
		listItems.add(0,event);
	    //listViewAdapter.addItem(event);
        listViewAdapter.notifyDataSetChanged();		
   }
	 
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		 int itemsLastIndex = listViewAdapter.getCount()-1;  //���ݼ����һ�������    
         int lastIndex = itemsLastIndex + 1;  
         if (scrollState == OnScrollListener.SCROLL_STATE_IDLE  
                 && visibleLastIndex == lastIndex) {  
             // ������Զ�����,��������������첽�������ݵĴ���  
         } 
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		this.visibleItemCount = visibleItemCount;  
        visibleLastIndex = firstVisibleItem + visibleItemCount - 1;  
        //������еļ�¼ѡ��������ݼ���������������б�ײ���ͼ  
        if(totalItemCount == datasize+1){  
            listView.addFooterView(loadMoreView);  
        }  
	}

}
  		
     
