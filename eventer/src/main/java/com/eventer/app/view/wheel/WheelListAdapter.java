package com.eventer.app.view.wheel;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eventer.app.R;

public class WheelListAdapter extends AbstractWheelTextAdapter {

	
	
	
	private LayoutInflater inflater;
	
	private String names[];
	
	private int layoutId;
	
	private WheelView wheelView;
	
	private List<TextView> listTexts;
	public List<TextView> getListTexts() {
		return listTexts;
	}

	public void setListTexts(List<TextView> listTexts) {
		this.listTexts = listTexts;
	}

	private int currentIndex;
	 /**
     * Constructor
     */
	public WheelListAdapter(Context context) {
        super(context, R.layout.wheel_layout, NO_RESOURCE);
        
        setItemTextResource(R.id.tv_name);
    }
    
    public WheelListAdapter(final Context context,String names[],int layoutId,final WheelView wheelView) {
        super(context,layoutId, NO_RESOURCE);
        this.names=names;
        this.layoutId=layoutId;
        this.wheelView=wheelView;
        
        listTexts=new ArrayList<TextView>();
        setItemTextResource(R.id.tv_name);
        
        
        
        
    }

    

    @Override
    public View getItem(int index, View cachedView, ViewGroup parent) {
        View view = super.getItem(index, cachedView, parent);
//        ImageView img = (ImageView) view.findViewById(R.id.flag);
//        img.setImageResource(flags[index]);
        
        final TextView textView=(TextView) view.findViewById(R.id.tv_name);
        listTexts.add(textView);
        
       /* wheelView.addScrollingListener( new OnWheelScrollListener() {
            public void onScrollingStarted(WheelView wheel) {
            }
            public void onScrollingFinished(WheelView wheel) {
            	TextView textView= listTexts.get(wheelView.getCurrentItem());
            	currentIndex=wheelView.getCurrentItem();
            	textView.setTextColor(context.getResources().getColor(R.color.gaode_blue));
            	for(int i=0;i<listTexts.size();i++)	{
            		if(i==currentIndex)	{
            			textView.setTextColor(context.getResources().getColor(R.color.gaode_blue));
            		}
            		else {
            			textView.setTextColor(context.getResources().getColor(R.color.E_black_light_3));
            		}
            	}
            	
               ;
            }
        });
        */
        
        
        return view;
    }
    
    @Override
    public int getItemsCount() {
        return names.length;
    }
    
    @Override
    protected CharSequence getItemText(int index) {
        return names[index];
    }
}


	
