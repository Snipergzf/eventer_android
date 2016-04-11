/**
 * @author yxw
 * date : 2014年4月17日 下午7:27:35 
 */
package com.eventer.app.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

import com.eventer.app.R;
import com.eventer.app.entity.ClassInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hirondelle.date4j.DateTime;

@SuppressLint("SimpleDateFormat")
@SuppressWarnings({"UnusedDeclaration"})
public class CourseView extends View implements OnTouchListener {

	private Paint mPaint; // 画笔,包含了画几何图形、文本等的样式和颜色信息
	private int startX = 0;//画布的原点X（所有的画图操作，都是基于这个原点的，touch中只要修改这个值）
	private int startY = 0;//画布的原点Y（所有的画图操作，都是基于这个原点的，touch中只要修改这个值）
	private static final int sidewidth = 80;//左边，上面bar的宽度
	private static final int sideheight = 95;//左边，上面bar的高度	
	private static int eachBoxH = 140;//每个格子的高度
	private static final int week=1;
	private static int eachBoxW = 120;//每个格子的宽度，后面根据屏幕对它做了均分
	private int focusX = -1;//当前手指焦点的位置坐标
	private int focusY = -1;//当前手指焦点的位置坐标
	private static int classTotal = 12;//左边栏总格子数
	private static int dayTotal = 7;//顶部栏总共格子数
	private Bitmap bmp;
	private String[] weekdays;//星期
	private boolean isMove = false; // 判断是否移动
	Context context;
	private float bmpScale;
	public static int TEXT_SIZE = 12;
	private static DateTime StartWeekday,StartCourseDay;
	private int month;
	private static int firstWeekday = 0;//每周开始日，1表示周日
	SimpleDateFormat   sDateFormat   =   new   SimpleDateFormat("yyyy-MM-dd");

	// 监听器
	private OnItemClassClickListener onItemClassClickListener;

	// 数据
	private List<ClassInfo> classList;

	// 颜色
	public static final int contentBg = Color.argb(255, 255, 255, 255);
	public static final int barBg = Color.argb(255, 225, 225, 225);
	public static final int bayText = Color.argb(255, 150, 150, 150);
	public static final int barBgHrLine = Color.argb(255, 150, 150, 150);
	public static final int classBorder = Color.argb(180, 150, 150, 150);
	public static final int markerBorder = Color.argb(100, 150, 150, 150);

	//预设格子背景颜色数组
	public static final int[] classBgColors = { Color.argb(200, 71, 154, 199),
			Color.argb(200, 230, 91, 62), Color.argb(200, 50, 178, 93),
			Color.argb(200, 255, 225, 0), Color.argb(200, 102, 204, 204),
			Color.argb(200, 51, 102, 153), Color.argb(200, 102, 153, 204)

	};

	public CourseView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		weekdays = context.getResources().getStringArray(R.array.weeks);
		TEXT_SIZE=spTopx(context, 12);
		mPaint = new Paint();
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) getContext()
				.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		int screenHeight = dm.heightPixels;
//		BitmapFactory.Options measureOptions = new BitmapFactory.Options();
//		measureOptions.inJustDecodeBounds = true;
//		BitmapFactory.decodeResource(
//				getResources(), R.drawable.course_bg, measureOptions);
//		int scale = Math.min(measureOptions.outWidth / screenWidth, measureOptions.outHeight / screenHeight);
//		scale = Math.max(scale, 1);
//
//		BitmapFactory.Options options = new BitmapFactory.Options();
//		options.inPreferredConfig = Bitmap.Config.RGB_565;
//		options.inJustDecodeBounds = false;
//		options.inSampleSize = scale;
		bmp = BitmapFactory.decodeResource(
				context.getResources(), R.drawable.course_bg);
		setOnTouchListener(this);

	}


	@SuppressLint("ClickableViewAccessibility")
	public void setBackgroundRes(Bitmap bmp){
		this.bmp=bmp;
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 *

	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return int
	 */
	public static int spTopx(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	/**
	 * 将dip或dp值转换为px值，保证尺寸大小不变
	 *
	 *            （DisplayMetrics类中属性density）
	 * @return int
	 */
	public static int dipTopx(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		//initDate();
		float scalex=(float)getWidth()/bmp.getWidth();
		float scaley=(float)getHeight()/bmp.getHeight();
		bmpScale=scalex>scaley?scalex:scaley;
		eachBoxW = (getWidth() - sidewidth) / 7;
		int height=eachBoxW*classTotal+sideheight;
		if(height>getHeight()){
			eachBoxH=eachBoxW;
		}else{
			eachBoxH = (getHeight() - sideheight) / classTotal;
		}
		printMarker(canvas);
		printContent(canvas);
		printLeftBar(canvas);
		printTopBar(canvas);
	}


//	private void initDate(){
//		   
//		String   time =sDateFormat.format(new   Date());
//		DateTime Today=new DateTime(time);
//		int weekday=Today.getWeekDay();
//		Log.e("1",Today.getWeekIndex()+"");
//		StartWeekday=Today.minusDays(weekday-1);		
//	}

	public void setWeek(int week){

		StartWeekday=StartCourseDay.plusDays(7*(week-1));
	}


	public void initSetting(Map<String,Object> params){
		classTotal=(Integer) params.get("classTotal");
		firstWeekday=(Integer) params.get("startWeekday");
		String start=(String) params.get("StartDay");
		StartCourseDay=new DateTime(start);
	}

	/**
	 * 区分课间隔，画交线处的十字
	 *
	 */
	private void printMarker(Canvas canvas) {
		Matrix matrix=new Matrix();
		matrix.postScale(bmpScale, bmpScale);
		Bitmap dstbmp=Bitmap.createBitmap(bmp,0,0,bmp.getWidth(),
				bmp.getHeight(),matrix,true);
		canvas.drawBitmap(dstbmp, 0, 0, null);
		mPaint.setColor(markerBorder);
		for (int i = 0; i < dayTotal - 1; i++) {
			for (int j = 0; j < classTotal - 1; j++) {
				// 画交线处的十字
				mPaint.setStyle(Style.STROKE);
				canvas.drawRect(startX + sidewidth + eachBoxW * (i + 1)
						- eachBoxW / 20, startY + sideheight + eachBoxH
						* (j + 1) - 1, startX + sidewidth + eachBoxW * (i + 1)
						+ eachBoxW / 20, startY + sideheight + eachBoxH
						* (j + 1), mPaint);
				canvas.drawRect(
						startX + sidewidth + eachBoxW * (i + 1) - 1,
						startY + sideheight + eachBoxH * (j + 1) - eachBoxW / 20,
						startX + sidewidth + eachBoxW * (i + 1), startY
								+ sideheight + eachBoxH * (j + 1) + eachBoxW
								/ 20, mPaint);
			}
		}
	}

	/**
	 * 画中间主体部分
	 *
	 */
	private void printContent(Canvas canvas) {
		if (classList != null && classList.size() > 0) {
			mPaint.setTextSize(TEXT_SIZE);
			ClassInfo classInfo;
			for (int i = 0; i < classList.size(); i++) {
				classInfo = classList.get(i);
				int weekday=(classInfo.getWeekday()+7-firstWeekday)%7;
				int fromX = startX + sidewidth + eachBoxW
						* weekday;
				int fromY = startY + sideheight + eachBoxH
						* (classInfo.getFromClassNum() - 1);
				int toX = startX + sidewidth + eachBoxW
						* (weekday+1);
				int toY = startY
						+ sideheight
						+ eachBoxH
						* (classInfo.getFromClassNum()
						+ classInfo.getClassNumLen() - 1);
				classInfo.setPoint(fromX, fromY, toX, toY);
				// 画classbg
				mPaint.setStyle(Style.FILL);
				mPaint.setAntiAlias(true);
				mPaint.setColor(classBgColors[i % classBgColors.length]);
				RectF oval3 = new RectF(fromX+1, fromY+1, toX - 3, toY - 3);// 设置个新的长方形  
				canvas.drawRoundRect(oval3, 15, 15, mPaint);//第二个参数是x半径，第三个参数是y半径

//				canvas.drawRect(fromX, fromY, toX - 2, toY - 2, mPaint);
				// 画文字
				mPaint.setColor(Color.WHITE);

				String className = classInfo.getClassname();
				String croom=classInfo.getClassRoom();
				if(croom!=null&&croom.trim().length()!=0){
					className+="@"+ classInfo.getClassRoom();
				}
				Rect textRect1 = new Rect();
				mPaint.getTextBounds(className, 0, className.length(),
						textRect1);

				float height=textRect1.height();//文字的高度


				int th = textRect1.bottom - textRect1.top;
				int tw = textRect1.right - textRect1.left;
				//计算行数


				int col= (int)Math.floor((float)(eachBoxH*classInfo.getClassNumLen()-8)/height);

//				int classHeight=eachBoxH*classInfo.getClassNumLen()-6;
//				TextPaint textPaint = new TextPaint();
//				textPaint.setARGB(0xFF, 0xFF, 0xFF, 0xFF);
//				textPaint.setTextSize(TEXT_SIZE);	
//				int lay_height;
//				StaticLayout layout = new StaticLayout(className,textPaint,eachBoxW-6,Alignment.ALIGN_CENTER,1.0F,0.0F,true);
//				lay_height=layout.getHeight();
//				int len=className.length();
//				while(lay_height>classHeight){
//					len--;
//					layout=new StaticLayout(className, 0, len, textPaint,eachBoxW-6,Alignment.ALIGN_CENTER,1.0F,0.0F,true);
//					lay_height=layout.getHeight();
//				}
//				canvas.translate(fromX,fromY);
//				layout.draw(canvas);
//				canvas.translate(-fromX,-fromY);




				List<Integer> lenlist=getStrLenlist(className);
				String str=className.substring(lenlist.get(0),lenlist.get(1));
				float width1=mPaint.measureText(str);//文字的宽度
				canvas.drawText(className, lenlist.get(0), lenlist.get(1),
						fromX+1 +(eachBoxW-width1)/2, fromY +3 + th, mPaint);
				for(int j=1;j<lenlist.size()-1;j++){
					if(j<col){
						String str1=className.substring(lenlist.get(j),lenlist.get(j+1));
						float width2=mPaint.measureText(str1);//文字的宽度
						canvas.drawText(className, lenlist.get(j), lenlist.get(j+1),
								fromX+1 +(eachBoxW-width2)/2, fromY +3 + th * (j + 1), mPaint);
					}
				}

				// 画边框
				mPaint.setColor(classBorder);
				mPaint.setStyle(Style.STROKE);

//				p.setStyle(Paint.Style.FILL);//充满  

//		        p.setAntiAlias(true);// 设置画笔的锯齿效果  


				//canvas.drawRoundRect(oval3, 10, 10, mPaint);//第二个参数是x半径，第三个参数是y半径
//				canvas.drawRect(fromX, fromY, toX - 2, toY - 2, mPaint);
			}
		}
	}


	private List<Integer> getStrLenlist(String className){
		List<Integer> lenlist=new ArrayList<>();
		int j=0;
		lenlist.add(0);
		for(int i=0;i<className.length()+1;i++){
			String temp=className.substring(j, i);
			float width1=mPaint.measureText(temp);
			if((float)(eachBoxW-4)<width1){
				lenlist.add(i-1);
				j=i-1;
			}

		}
		if(!lenlist.contains(className.length())){
			lenlist.add(className.length());
		}
		return lenlist;
	}

	private int getByteLength(String str){
		int len=0;
		for(int i = 0 ;i<str.length();i++){
			char ch = str.charAt(i);
			if(checkChar(ch)){
				len+=2;
			}
			else{
				len++;
			}

		}
		len=Math.round((float)len/2);
		return len;
	}

	private boolean checkChar(char oneChar){
		String str=oneChar+"";
		return str.getBytes().length>=2;
	}

	/**
	 * 画左边课时bar
	 *
	 */
	private void printLeftBar(Canvas canvas) {
		// =================画左边课时栏=================
		mPaint.setColor(Color.TRANSPARENT);
		mPaint.setStyle(Style.FILL);
		mPaint.setTextSize(30);
		// 课时栏背景
		canvas.drawRect(0, startY + sideheight, sidewidth, sideheight + startY
				+ eachBoxH * classTotal, mPaint);
		mPaint.setColor(barBgHrLine);

		// 居中处理
		Rect textRect1 = new Rect();
		mPaint.getTextBounds("1", 0, 1, textRect1);
		float mTextWidth1, mTextHeight,mTextWidth2;
		mTextWidth1 = mPaint.measureText("1"); // Use measureText to calculate width
		mTextWidth2 = mPaint.measureText("10"); // Use measureText to calculate width
		mTextHeight = textRect1.height();

		for (int i = 1; i < classTotal + 1; i++) {
			// 画边框

			canvas.drawRect(0, startY + sideheight + eachBoxH * i - 1,
					sidewidth, startY + eachBoxH * i + sideheight, mPaint);
			// 画文字
			float mTextWidth = mTextWidth1 + (mTextWidth2 - mTextWidth1) * (i / 10);
			canvas.drawText(i + "",startX+ (sidewidth / 2f) - mTextWidth / 2, startY + sideheight
					+ eachBoxH * (i - 1) + eachBoxH / 2 + (mTextHeight / 2f), mPaint);
		}
		// =========左上角正方形============		
		mPaint.setColor(barBgHrLine);
		canvas.drawRect(sidewidth-1, startY,
				sidewidth,  eachBoxH *classTotal + sideheight, mPaint);
//		canvas.drawRect(0, startY + sideheight-1,
//				sidewidth, startY  + sidewidth, mPaint);
//		mPaint.setStyle(Style.STROKE);
//		canvas.drawRect(0, 0, sidewidth, sidewidth, mPaint);


	}

	/**
	 * 画顶部星期bar
	 *
	 */
	private void printTopBar(Canvas canvas) {
		// =================画顶部星期栏==================
//		mPaint.setColor(Color.parseColor("")));
//		mPaint.setStyle(Style.FILL);
//		// 星期栏背景
//		canvas.drawRect(startX, 0, sidewidth + startX + eachBoxW
//				* dayTotal, sideheight, mPaint);
		Matrix matrix=new Matrix();
		matrix.postScale(bmpScale, bmpScale);
		Bitmap dstbmp=Bitmap.createBitmap(bmp,0,0,bmp.getWidth(),
				(int)(sideheight/bmpScale), matrix,true);
		canvas.drawBitmap(dstbmp, 0, 0, null);

		mPaint.setColor(barBgHrLine);

		canvas.drawRect(startX - 1, sideheight - 1, startX + getWidth() - 1, sideheight, mPaint);
		// 画第一个边框线
		mPaint.setTextSize(30);
		String month_str=StartWeekday.getMonth()+"月";

		// 居中处理
		Rect textBounds = new Rect();
		mPaint.getTextBounds(weekdays[0], 0, weekdays[0].length(), textBounds);
		int textHeight = textBounds.bottom - textBounds.top;
		int textWidth = textBounds.right - textBounds.left;
		float mText = mPaint.measureText(month_str);
		float numWidth = mPaint.measureText("1");
		canvas.drawText(month_str, startX + sidewidth / 2 - mText / 2, textHeight + 15
				, mPaint);
		DateTime weekday;
		int day;
		for (int i = 1; i < dayTotal + 1; i++) {
			int week_day=i-1+firstWeekday;
			weekday=StartWeekday.plusDays(week_day-1);
			day=weekday.getDay();
			if(day<10){
				if(day==1&&weekday.getWeekDay()!=1){
					canvas.drawText(weekday.getMonth()+"月", startX + sidewidth + eachBoxW
							* (i - 1) + eachBoxW / 2 - mText / 2, textHeight+15
							, mPaint);
				}else{
					canvas.drawText(day+"", startX + sidewidth + eachBoxW
							* (i - 1) + eachBoxW / 2 - numWidth / 2, textHeight+15
							, mPaint);
				}
			}else{
				canvas.drawText(day+"", startX + sidewidth + eachBoxW
						* (i - 1) + eachBoxW / 2 - numWidth, textHeight+15
						, mPaint);
			}

			// 画边框线
			canvas.drawRect(startX + sidewidth + eachBoxW * (i-1) - 1, 0, startX
					+ eachBoxW * (i-1) + sidewidth, sideheight, mPaint);

			// 画文字
			canvas.drawText(weekdays[week_day%7], startX + sidewidth + eachBoxW
					* (i - 1) + eachBoxW / 2 - textWidth / 2, sideheight-15, mPaint);
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			focusX = (int) event.getX();
			focusY = (int) event.getY();
			isMove = false;
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			int dx = (int) (event.getX() - focusX);
			int dy = (int) (event.getY() - focusY);
			if (!isMove && Math.abs(dx) < 5 && Math.abs(dy) < 5) {
				isMove = false;
				return false;
			}
			isMove = true;
			//判断是否超出左右边框
			if (startX + dx < 0
					&& startX + dx + eachBoxW * dayTotal + sidewidth >= getWidth()) {
				startX += dx;
			}
			//判断是否超出上下边框
			if (startY + dy < 0
					&& startY + dy + eachBoxH * classTotal + sideheight >= getHeight()) {
				startY += dy;
			}
			//重新获得焦点坐标
			focusX = (int) event.getX();
			focusY = (int) event.getY();
			//重绘
			invalidate();
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			if (!isMove) {
				int focusX = (int) event.getX();
				int focusY = (int) event.getY();
				// 是点击效果，遍历是哪个课程的点击效果
				List<ClassInfo> list = new ArrayList<>();
				for (int i = 0; i < classList.size(); i++) {
					ClassInfo classInfo = classList.get(i);
					if (focusX > classInfo.getFromX()
							&& focusX < classInfo.getToX()
							&& focusY > classInfo.getFromY()
							&& focusY < classInfo.getToY()) {
						Log.e("course_onclick",classInfo.getClassname());
						list.add(classInfo);
					}
				}
				if(onItemClassClickListener != null && list.size()>0){
					onItemClassClickListener.onClick(list);
				}

			}
		}
		return true;
	}

	public interface OnItemClassClickListener {
		 void onClick(List<ClassInfo> classInfo);
	}

	public OnItemClassClickListener getOnItemClassClickListener() {
		return onItemClassClickListener;
	}

	public void setOnItemClassClickListener(
			OnItemClassClickListener onItemClassClickListener) {
		this.onItemClassClickListener = onItemClassClickListener;
	}

	public List<ClassInfo> getClassList() {
		return classList;
	}

	public void setClassList(List<ClassInfo> classList) {
		this.classList = classList;
		invalidate();// 刷新页面
	}

}
