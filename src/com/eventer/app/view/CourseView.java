/**
 * @author yxw
 * date : 2014��4��17�� ����7:27:35 
 */
package com.eventer.app.view;

import hirondelle.date4j.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.eventer.app.R;
import com.eventer.app.entity.ClassInfo;

@SuppressLint("SimpleDateFormat")
public class CourseView extends View implements OnTouchListener {

	private Paint mPaint; // ����,�����˻�����ͼ�Ρ��ı��ȵ���ʽ����ɫ��Ϣ
	private int startX = 0;//������ԭ��X�����еĻ�ͼ���������ǻ������ԭ��ģ�touch��ֻҪ�޸����ֵ��
	private int startY = 0;//������ԭ��Y�����еĻ�ͼ���������ǻ������ԭ��ģ�touch��ֻҪ�޸����ֵ��
	private static final int sidewidth = 80;//��ߣ�����bar�Ŀ��
	private static final int sideheight = 95;//��ߣ�����bar�ĸ߶�	
	private static int eachBoxH = 140;//ÿ�����ӵĸ߶�
	private static final int week=1;
	private static int eachBoxW = 120;//ÿ�����ӵĿ�ȣ����������Ļ�������˾���
	private int focusX = -1;//��ǰ��ָ�����λ������
	private int focusY = -1;//��ǰ��ָ�����λ������
	private static int classTotal = 12;//������ܸ�����
	private static int dayTotal = 7;//�������ܹ�������
	private Bitmap bmp;
	private String[] weekdays;//����
	private boolean isMove = false; // �ж��Ƿ��ƶ�
	private Context context;
	private float bmpScale;
	public static int TEXT_SIZE = 12;
	private static DateTime StartWeekday,StartCourseDay;
	private int month;
	private static int firstWeekday = 0;//ÿ�ܿ�ʼ�գ�1��ʾ����
	SimpleDateFormat   sDateFormat   =   new   SimpleDateFormat("yyyy-MM-dd");  

	// ������
	private OnItemClassClickListener onItemClassClickListener;

	// ����
	private List<ClassInfo> classList;

	// ��ɫ
	public static final int contentBg = Color.argb(255, 255, 255, 255);
	public static final int barBg = Color.argb(255, 225, 225, 225);
	public static final int bayText = Color.argb(255, 150, 150, 150);
	public static final int barBgHrLine = Color.argb(255, 150, 150, 150);
	public static final int classBorder = Color.argb(180, 150, 150, 150);
	public static final int markerBorder = Color.argb(100, 150, 150, 150);

	//Ԥ����ӱ�����ɫ����
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
		bmp=BitmapFactory.decodeResource(context.getResources(), R.drawable.course_bg);
		setOnTouchListener(this);
		
	}
	
	
	@SuppressLint("ClickableViewAccessibility")
	public void setBackgroundRes(Bitmap bmp){
		this.bmp=bmp;
	}

	/**
     * ��spֵת��Ϊpxֵ����֤���ִ�С����
     * 
     * @param spValue
     * @param fontScale
     *            ��DisplayMetrics��������scaledDensity��
     * @return
     */ 
    public static int spTopx(Context context, float spValue) { 
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity; 
        return (int) (spValue * fontScale + 0.5f); 
    } 
    
    /**
     * ��dip��dpֵת��Ϊpxֵ����֤�ߴ��С����
     * 
     * @param dipValue
     * @param scale
     *            ��DisplayMetrics��������density��
     * @return
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
	 * ���ֿμ���������ߴ���ʮ��
	 * 
	 * @param canvas
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
				// �����ߴ���ʮ��
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
	 * ���м����岿��
	 * 
	 * @param canvas
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
				// ��classbg
				mPaint.setStyle(Style.FILL);
				mPaint.setAntiAlias(true);
				mPaint.setColor(classBgColors[i % classBgColors.length]);
				RectF oval3 = new RectF(fromX+1, fromY+1, toX - 3, toY - 3);// ���ø��µĳ�����  
		        canvas.drawRoundRect(oval3, 15, 15, mPaint);//�ڶ���������x�뾶��������������y�뾶
				
//				canvas.drawRect(fromX, fromY, toX - 2, toY - 2, mPaint);
				// ������
				mPaint.setColor(Color.WHITE);
				
				String className = classInfo.getClassname();
				String croom=classInfo.getClassRoom();
				if(croom!=null&&croom.trim().length()!=0){
					className+="@"+ classInfo.getClassRoom();
				}
				Rect textRect1 = new Rect();
				int className_len=getByteLength(className);
				mPaint.getTextBounds(className, 0, className.length(),
						textRect1);
				
				float width=mPaint.measureText(className);//���ֵĿ��
				float height=textRect1.height();//���ֵĸ߶�

				
				int th = textRect1.bottom - textRect1.top;
				int tw = textRect1.right - textRect1.left;
				//��������
				
				int row = (int) ((tw + 30) / eachBoxW + 1);
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
				float width1=mPaint.measureText(str);//���ֵĿ��
				canvas.drawText(className, lenlist.get(0), lenlist.get(1),
						fromX+1 +(eachBoxW-width1)/2, fromY +3 + th * (1), mPaint);
				for(int j=1;j<lenlist.size()-1;j++){
					if(j<col){
						String str1=className.substring(lenlist.get(j),lenlist.get(j+1));
						float width2=mPaint.measureText(str1);//���ֵĿ��
						canvas.drawText(className, lenlist.get(j), lenlist.get(j+1),
								fromX+1 +(eachBoxW-width2)/2, fromY +3 + th * (j + 1), mPaint);
					}
				}

				// ���߿�
				mPaint.setColor(classBorder);
				mPaint.setStyle(Style.STROKE);
				
//				p.setStyle(Paint.Style.FILL);//����  
		       
//		        p.setAntiAlias(true);// ���û��ʵľ��Ч��  
  
		       
		        //canvas.drawRoundRect(oval3, 10, 10, mPaint);//�ڶ���������x�뾶��������������y�뾶
//				canvas.drawRect(fromX, fromY, toX - 2, toY - 2, mPaint);
			}
		}
	}
	
	
	private List<Integer> getStrLenlist(String className){
		List<Integer> lenlist=new ArrayList<Integer>();
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
		if(str.getBytes().length>=2)
		   return true;
		return false;
	}

	/**
	 * ����߿�ʱbar
	 * 
	 * @param canvas
	 */
	private void printLeftBar(Canvas canvas) {
		// =================����߿�ʱ��=================
		mPaint.setColor(Color.TRANSPARENT);
		mPaint.setStyle(Style.FILL);
		mPaint.setTextSize(30);
		// ��ʱ������
		canvas.drawRect(0, startY + sideheight, sidewidth, sideheight + startY
				+ eachBoxH * classTotal, mPaint);
		mPaint.setColor(barBgHrLine);

		// ���д���
		Rect textRect1 = new Rect();
		mPaint.getTextBounds("1", 0, 1, textRect1);
		float mTextWidth1, mTextHeight,mTextWidth2;
		mTextWidth1 = mPaint.measureText("1"); // Use measureText to calculate width
		mTextWidth2 = mPaint.measureText("10"); // Use measureText to calculate width
		mTextHeight = textRect1.height();

		for (int i = 1; i < classTotal + 1; i++) {
			// ���߿�
			
			canvas.drawRect(0, startY + sideheight + eachBoxH * i - 1,
					sidewidth, startY + eachBoxH * i + sideheight, mPaint);
			// ������
			float mTextWidth = mTextWidth1 + (mTextWidth2 - mTextWidth1) * (i / 10);
			canvas.drawText(i + "",startX+ (sidewidth / 2f) - mTextWidth / 2, startY + sideheight
					+ eachBoxH * (i - 1) + eachBoxH / 2 + (mTextHeight / 2f), mPaint);
		}
		// =========���Ͻ�������============		
		mPaint.setColor(barBgHrLine);
		canvas.drawRect(sidewidth-1, startY,
				sidewidth,  eachBoxH *classTotal + sideheight, mPaint);
//		canvas.drawRect(0, startY + sideheight-1,
//				sidewidth, startY  + sidewidth, mPaint);
//		mPaint.setStyle(Style.STROKE);
//		canvas.drawRect(0, 0, sidewidth, sidewidth, mPaint);
		

	}

	/**
	 * ����������bar
	 * 
	 * @param canvas
	 */
	private void printTopBar(Canvas canvas) {
		// =================������������==================
//		mPaint.setColor(Color.parseColor("")));
//		mPaint.setStyle(Style.FILL);
//		// ����������
//		canvas.drawRect(startX, 0, sidewidth + startX + eachBoxW
//				* dayTotal, sideheight, mPaint);
		Matrix matrix=new Matrix();
		matrix.postScale(bmpScale, bmpScale);
         Bitmap dstbmp=Bitmap.createBitmap(bmp,0,0,bmp.getWidth(),
        		 (int)(sideheight/bmpScale), matrix,true);
         canvas.drawBitmap(dstbmp, 0, 0, null); 
		
		mPaint.setColor(barBgHrLine);
		
		canvas.drawRect(startX  - 1, sideheight-1, startX + getWidth()-1, sideheight, mPaint);
		// ����һ���߿���
		mPaint.setTextSize(30);
		String month_str=StartWeekday.getMonth()+"��";

		// ���д���
		Rect textBounds = new Rect();
		mPaint.getTextBounds(weekdays[0], 0, weekdays[0].length(), textBounds);
		int textHeight = textBounds.bottom - textBounds.top;
		int textWidth = textBounds.right - textBounds.left;
		float mText = mPaint.measureText(month_str);
		float numWidth = mPaint.measureText("1");
		canvas.drawText(month_str, startX + sidewidth/2 - mText / 2, textHeight+15
				, mPaint);
		DateTime weekday;
		int day;
		for (int i = 1; i < dayTotal + 1; i++) {
			int week_day=i-1+firstWeekday;
			weekday=StartWeekday.plusDays(week_day-1);
			day=weekday.getDay();
			if(day<10){
				if(day==1&&weekday.getWeekDay()!=1){
				canvas.drawText(weekday.getMonth()+"��", startX + sidewidth + eachBoxW
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
			
			// ���߿���
			canvas.drawRect(startX + sidewidth + eachBoxW * (i-1) - 1, 0, startX
					+ eachBoxW * (i-1) + sidewidth, sideheight, mPaint);
			
			// ������
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
			//�ж��Ƿ񳬳����ұ߿�
			if (startX + dx < 0
					&& startX + dx + eachBoxW * dayTotal + sidewidth >= getWidth()) {
				startX += dx;
			}
			//�ж��Ƿ񳬳����±߿�
			if (startY + dy < 0
					&& startY + dy + eachBoxH * classTotal + sideheight >= getHeight()) {
				startY += dy;
			}
			//���»�ý�������
			focusX = (int) event.getX();
			focusY = (int) event.getY();
			//�ػ�
			invalidate();
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			if (!isMove) {
				int focusX = (int) event.getX();
				int focusY = (int) event.getY();
				// �ǵ��Ч�����������ĸ��γ̵ĵ��Ч��
				for (int i = 0; i < classList.size(); i++) {
					ClassInfo classInfo = classList.get(i);
					if (focusX > classInfo.getFromX()
							&& focusX < classInfo.getToX()
							&& focusY > classInfo.getFromY()
							&& focusY < classInfo.getToY()) {
						if (onItemClassClickListener != null) {
							onItemClassClickListener.onClick(classInfo);
						}
						break;
					}
				}
			}
		}
		return true;
	}

	public interface OnItemClassClickListener {
		public void onClick(ClassInfo classInfo);
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
		invalidate();// ˢ��ҳ��
	}

}
