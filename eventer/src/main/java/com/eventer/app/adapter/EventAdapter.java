package com.eventer.app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eventer.app.Constant;
import com.eventer.app.R;
import com.eventer.app.db.EventOpDao;
import com.eventer.app.entity.Event;
import com.eventer.app.util.FileUtil;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

@SuppressLint({ "ViewHolder", "SimpleDateFormat" ,"SetTextI18n"})
public class EventAdapter extends BaseAdapter {
	private Context context;                        //运行上下文
	private List<Event> listItems;
	private LayoutInflater mInflater;
	private FileUtil fileUtil;


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



	@Override
	public Object getItem(int position) {
		return listItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public static class ViewHolder {
		TextView theme,title,tag,click;
		ImageView iv_pic;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		final Event card =  listItems.get(position);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_event_list, parent ,false);
			holder=new ViewHolder();

			holder.title = (TextView) convertView.findViewById(R.id.tv_title);
			holder.tag = (TextView) convertView.findViewById(R.id.tv_tag);
			holder.click = (TextView) convertView.findViewById(R.id.tv_click);
			holder.theme=(TextView)convertView.findViewById(R.id.tv_theme);
			holder.iv_pic=(ImageView)convertView.findViewById(R.id.iv_pic);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder)convertView.getTag();
		}


		holder.iv_pic.setImageResource(R.color.transparent);

		holder.tag.setText(
				card.getTag() == null ? "" : card.getTag());

		holder.theme.setText(
				card.getTheme() == null ? "" : card.getTheme());

		holder.title.setText(
				card.getTitle() == null ? "标题被偷走了~" : card.getTitle());

		int click = card.getReadCount();


		holder.click.setText( click > 0 ? "阅读量 " + click: "");

		EventOpDao dao = new EventOpDao(context);
		boolean isCheck = dao.getIsVisit(card.getEventID());
		if(isCheck){

			holder.title.setAlpha(0.6f);
			holder.tag.setAlpha(0.6f);
			holder.click.setAlpha(0.6f);
		}else{

			holder.title.setAlpha(1f);
			holder.tag.setAlpha(1f);
			holder.click.setAlpha(1f);
		}
		showEventPicture(holder.iv_pic, card.getCover());


		return convertView;
	}

	private void showEventPicture(final ImageView iv_pic, String url) {
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
						int scale = Math.min(measureOptions.outWidth, measureOptions.outHeight) / 120;
						scale = Math.max(scale, 1);

						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inPreferredConfig = Bitmap.Config.RGB_565;
						options.inJustDecodeBounds = false;

						options.inSampleSize = scale;
						bitmap = BitmapFactory.decodeFile(filepath, options);
					}

					if(bitmap!=null){
						return bitmap;
					}else {
						Log.e("url_img",url_avatar);
						is = (InputStream) new URL(url_avatar).getContent();
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inPreferredConfig = Bitmap.Config.RGB_565;
						options.inJustDecodeBounds = false;
						options.inSampleSize = 1;
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

}