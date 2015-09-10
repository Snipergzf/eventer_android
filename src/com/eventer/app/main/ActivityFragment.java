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

	View footer;// 底部布局；
	private int visibleLastIndex = 0;   //最后的可视项索引    
	ArrayList<Event> event_list = new ArrayList<Event>();
	private Button loadMoreButton;    
	private int datasize = 5;  
	private int visibleItemCount;       // 当前窗口可见项总数 
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
        //活动列表的适配器
        listViewAdapter = new EventAdapter(this.getActivity(), listItems); //创建适配器   
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
        //活动item点击事件
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
		lists.add("推荐");
		lists.add("讲座");
		lists.add("招新");
		lists.add("比赛");
		lists.add("活动");
		lists.add("推广");
		lists.add("新闻");
		lists.add("美食");

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
		 int itemsLastIndex = listViewAdapter.getCount()-1;  //数据集最后一项的索引    
         int lastIndex = itemsLastIndex + 1;  
         if (scrollState == OnScrollListener.SCROLL_STATE_IDLE  
                 && visibleLastIndex == lastIndex) {  
             // 如果是自动加载,可以在这里放置异步加载数据的代码  
         } 
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		this.visibleItemCount = visibleItemCount;  
        visibleLastIndex = firstVisibleItem + visibleItemCount - 1;  
        //如果所有的记录选项等于数据集的条数，则出现列表底部视图  
        if(totalItemCount == datasize+1){  
            listView.addFooterView(loadMoreView);  
        }  
	}

}
  		
     
