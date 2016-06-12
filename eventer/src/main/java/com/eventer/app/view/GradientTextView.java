package com.eventer.app.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.TextView;

import com.eventer.app.R;

/**
 * @version BY LiuNaNa 2015-12-30 14:45:28
 * extends TextView,make the textColor change  constantly
 **/
public class GradientTextView extends TextView {

    private LinearGradient mLinearGradient;
    private Matrix mGradientMatrix;
    private Paint mPaint;
    private int mViewHeight = 0,mViewWidth=0;
    private int mTranslate = 0;
    private final static int  VERTICAL=0;
    private static final int DEFAULT_START_COLOR = 0x33ff0000;
    private static final int DEFAULT_MIDDLE_COLOR = 0xff00ff66;
    private static final int DEFAULT_END_COLOR = 0x33000033;

    private int orientation=0;

    private boolean mAnimating = true;
    private int startColor = DEFAULT_START_COLOR;
    private int middleColor = DEFAULT_MIDDLE_COLOR;
    private int endColor = DEFAULT_END_COLOR;
    private int[] colorSet=new int[3];
    private Shader.TileMode tileMode=Shader.TileMode.REPEAT;
    private int tileModeIndex=0;
    private int duration = 300;
    private int textStyle=0;

    public GradientTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.GradientText);
        orientation = ta.getInt(R.styleable.GradientText_orientation, VERTICAL);
        textStyle = ta.getInt(R.styleable.GradientText_textStyle, VERTICAL);
        startColor = ta.getColor(R.styleable.GradientText_startColor, DEFAULT_START_COLOR);
        middleColor = ta.getColor(R.styleable.GradientText_middleColor, DEFAULT_MIDDLE_COLOR);
        endColor = ta.getColor(R.styleable.GradientText_endColor, DEFAULT_END_COLOR);
        tileModeIndex = ta.getInt(R.styleable.GradientText_type, 1);
        duration = ta.getInt(R.styleable.GradientText_duration,300);
        colorSet = new int[]{startColor, middleColor, endColor};
        switch (tileModeIndex){
            case 0:
                tileMode = Shader.TileMode.CLAMP;
                break;
            case 1:
                tileMode = Shader.TileMode.REPEAT;
                break;
            case 2:
                tileMode = Shader.TileMode.MIRROR;
                break;
            default:
                break;
        }

        if(textStyle==0){
            String text=getText().toString();
            text=getTextHtoV(text);
//            if ("".equals(text) || text == null || text.length() == 0) {
//                return;
//            }
//            int m = text.length();
//            StringBuffer sb = new StringBuffer();
//            for (int i = 0; i < m; i++) {
//                CharSequence index = text.toString().subSequence(i, i + 1);
//                sb.append(index + "\n");
//            }
            setText(text);
        }
        ta.recycle();
    }

    public static String getTextHtoV(String strText){
        String strResult = "";
        String br = "\n";      //断行标记，这里可改用逗号或分号等字符
        String strArr[] = strText.split(br);
        int nMaxLen = 0;      //各行最多字符数
        int nLines = strArr.length;    //总共的行数
        char charArr[][] = new char[nLines][];    //字符陈列（即二维数组）
        for (int i = 0; i < nLines; i++) {
            String str = strArr[i];
            int n = str.length();

            //以最长的行的字符数（即原列数）作为目标陈列的行数
            if (n > nMaxLen) nMaxLen = n;
            charArr[i] = strArr[i].toCharArray();
        }

        //行列转换
        for (int i = 0; i < nMaxLen; i++) {
            for (int j = 0; j < nLines; j++) {
                //若短句字符已“用完”则以空格代替
                char c = i < charArr[j].length ? charArr[j][i] : '　';
                strResult += String.valueOf(c);

                //两列文字之间加空格，不需要的话请注释掉下面一行
                if (j < nLines - 1) strResult += " ";  //★
            }
            strResult += br;   //添加换行符
        }

        return strResult;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        switch (orientation){
            case 0:
                if (mViewHeight == 0) {
                    mViewHeight = getMeasuredHeight();
                    if (mViewHeight > 0) {
                        mPaint = getPaint();
                        mLinearGradient = new LinearGradient(0,-mViewHeight, 0, 0,
                                colorSet, new float[] { 0, 0.5f, 1 }, tileMode);
                        mPaint.setShader(mLinearGradient);
                        mGradientMatrix = new Matrix();
                    }
                }
                break;
            case 1:
                if (mViewWidth == 0) {
                    mViewWidth = getMeasuredWidth();
                    if (mViewWidth > 0) {
                        mPaint = getPaint();
                        mLinearGradient = new LinearGradient(-mViewWidth,0, 0, 0,
                                colorSet, new float[] { 0, 0.5f, 1 }, tileMode);
                        mPaint.setShader(mLinearGradient);
                        mGradientMatrix = new Matrix();
                    }
                }
                break;
            default:
                break;
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (orientation){
            case 0:
                if (mAnimating && mGradientMatrix != null) {
                    mTranslate += mViewHeight / 10;
                    if (mTranslate > 2 * mViewHeight) {
                        mTranslate = -mViewHeight;
                    }
                    mGradientMatrix.setTranslate(0, mTranslate);
                    mLinearGradient.setLocalMatrix(mGradientMatrix);
                    postInvalidateDelayed(duration);
                }
                break;
            case 1:
                if (mAnimating && mGradientMatrix != null) {
                    mTranslate += mViewWidth / 10;
                    if (mTranslate > 2 * mViewWidth) {
                        mTranslate = -mViewWidth;
                    }
                    mGradientMatrix.setTranslate(mTranslate,0);
                    mLinearGradient.setLocalMatrix(mGradientMatrix);
                    postInvalidateDelayed(duration);
                }
                break;
            default:
                break;
        }

    }

}
