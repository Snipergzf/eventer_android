package com.eventer.app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eventer.app.Constant;
import com.eventer.app.R;
import com.eventer.app.db.EventOpDao;
import com.eventer.app.entity.Event;
import com.eventer.app.util.FileUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@SuppressLint({ "ViewHolder", "SimpleDateFormat" ,"SetTextI18n"})
public class EventAdapter extends BaseAdapter {
	private Context context;                        //运行上下文
	private List<Event> listItems;
	private LayoutInflater mInflater;
	private String image="";
	private FileUtil fileUtil;
	private int IMG_SCALE = 100;

//	public final class ListItemView{                //自定义控件集合
//		public ImageView image;
//		public TextView title;
//		public TextView info;
//		public Button detail;
//	}


	public EventAdapter(Context context,  List<Event>  listItems) {
		this.context = context;
		this.mInflater = LayoutInflater.from(context);   //创建视图容器并设置上下文
		this.listItems = listItems;
		fileUtil = new FileUtil(context, Constant.IMAGE_PATH);
	}


	@Override
	public int getCount() {
		return  listItems.size();
	}


	public void addItem(Event e) {
		listItems.add(0,e);
	}

//	public void clearItem() {
//		listItems=new ArrayList<>();
//	}


	@Override
	public Object getItem(int position) {
		return listItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public static class ViewHolder {
		TextView time,place,theme,content;
		LinearLayout li_collect,li_share,li_comment;
		ImageView iv_collect;
		ImageView iv_pic;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO 自动生成的方法存根
		ViewHolder holder;
		final Event card =  listItems.get(position);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_event_list, parent ,false);
			holder=new ViewHolder();

			holder.content = (TextView) convertView.findViewById(R.id.tv_content);
			holder.time = (TextView) convertView.findViewById(R.id.tv_time);
			holder.place=(TextView)convertView.findViewById(R.id.tv_place);
			holder.theme=(TextView)convertView.findViewById(R.id.tv_theme);
//				holder.publisher=(TextView)convertView.findViewById(R.id.tv_publisher);
			holder.li_collect=(LinearLayout)convertView.findViewById(R.id.li_collect);
			holder.li_comment=(LinearLayout)convertView.findViewById(R.id.li_comment);
			holder.li_share=(LinearLayout)convertView.findViewById(R.id.li_share);
			holder.iv_collect=(ImageView)convertView.findViewById(R.id.iv_collect);
			holder.iv_pic=(ImageView)convertView.findViewById(R.id.iv_pic);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder)convertView.getTag();
		}

//			if (convertView == null) {  
//		      
//				holder=new ViewHolder();
//				convertView = mInflater.inflate(R.layout.list_item, null); 
//				holder.readCount = (TextView) convertView.findViewById(R.id.tv_readCount);
//				holder.content = (TextView) convertView.findViewById(R.id.tv_content);
//				holder.time = (TextView) convertView.findViewById(R.id.tv_time);
//				holder.Good=(Button) convertView.findViewById(R.id.bt_good);
//				holder.Bad=(Button) convertView.findViewById(R.id.bt_bad);
//			 }else {  
//		            holder = (ViewHolder)convertView.getTag();  
//		     } 


		holder.iv_pic.setImageResource(R.color.transparent);

		String place=card.getPlace();
		holder.place.setText("地点: "+place);
		String theme=card.getTheme();
		holder.theme.setText(theme);

		String time=card.getTime();
		JSONArray time1;
		String timeString="";
		try {
			time1 = new JSONArray(time);

			for(int i=0;i<time1.length()/2;i++){
				long begin_time=time1.getLong(2*i);
				long end_time=time1.getLong(2*i+1);
				timeString+=getTimeSpan(begin_time,end_time);
				if(i!=time1.length()/2-1){
					timeString+="\n";
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//时间
		holder.time.setText("时间: "+timeString);
		String title=card.getTitle();
		holder.content.setText(title);

		EventOpDao dao=new EventOpDao(context);
		boolean isCheck=dao.getIsVisit(card.getEventID());
		if(isCheck){
//		    	convertView.setAlpha(0.75f);
//		    	holder.iv_pic.setAlpha(0.6f);
//		    	holder.theme.setAlpha(0.6f);
			holder.content.setAlpha(0.6f);
			holder.place.setAlpha(0.6f);
			holder.time.setAlpha(0.6f);
		}else{
//		    	convertView.setAlpha(1f);
//		    	holder.iv_pic.setAlpha(1f);
//		    	holder.theme.setAlpha(1f);
			holder.content.setAlpha(1f);
			holder.place.setAlpha(1f);
			holder.time.setAlpha(1f);

		}

		image="";
		 Html.fromHtml(card.getContent(), new Html.ImageGetter() {
			@Override
			public Drawable getDrawable(String source) {
				if(TextUtils.isEmpty(image))
					image=source;
				return null;
			}

		}, null);
		showEventPicture(holder.iv_pic,image);
		return convertView;
	}

	private void showEventPicture(final ImageView iv_pic, String url) {
		// TODO Auto-generated method stub
		if(url==null||url.equals("")) {
			iv_pic.setImageResource(R.drawable.default_avatar);
			return;
		}
		final String url_avatar = url;
		iv_pic.setTag(url_avatar);
		new AsyncTask<String, Object,Bitmap>() {

			@Override
			protected Bitmap doInBackground(String... params) {

				InputStream is;
				String filename = url_avatar
						.substring(url_avatar.lastIndexOf("/") + 1)+"_e";
				String filepath = fileUtil.getAbsolutePath() + filename;

				try {
					Bitmap bitmap = null;

					if(fileUtil.isBitmapExists(filename)){

						BitmapFactory.Options measureOptions = new BitmapFactory.Options();
						measureOptions.inJustDecodeBounds = true;
						BitmapFactory.decodeFile(filepath, measureOptions);
						int scale = Math.min(measureOptions.outWidth, measureOptions.outHeight) / 40;
						scale = Math.max(scale, 1);

						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inPreferredConfig = Bitmap.Config.RGB_565;
						options.inJustDecodeBounds = false;

						options.inSampleSize = scale;
						bitmap = BitmapFactory.decodeFile(filepath, options);
					}

					if(bitmap!=null){
//								d = new BitmapDrawable(context.getResources(),bitmap);
						return bitmap;
					}else{
						Log.e("url_img",url_avatar);
						is = (InputStream) new URL(url_avatar).getContent();

						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inPreferredConfig = Bitmap.Config.RGB_565;
						options.inJustDecodeBounds = false;
						options.inSampleSize = 2;
						bitmap = BitmapFactory.decodeStream(is,
								null, options);
						fileUtil.saveBitmap(filename, bitmap);
						is.close();
					}
					return bitmap;
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
			protected void onPostExecute(Bitmap bitmap) {
				if(bitmap!=null&&iv_pic.getTag().equals(url_avatar)){
					iv_pic.setImageBitmap(bitmap);
				}else{
					iv_pic.setImageResource(R.drawable.default_avatar);
				}
			}
		}.execute();
	}


//	public String getFullTime(long now) {
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//		return sdf.format(new Date(now));
//	}

	public String getDate(long now) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(new Date(now));
	}

	private String getTimeSpan(long begin_time, long end_time) {
		// TODO Auto-generated method stub
		String begin=getDate(begin_time*1000);
		String end=getDate(end_time*1000);
		String time;
		if(begin.equals(end)){
			time=begin;
		}else{
			time=begin+" ~ "+end;
		}
		return time;
	}
}