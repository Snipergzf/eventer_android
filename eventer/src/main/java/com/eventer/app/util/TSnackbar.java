package com.eventer.app.util;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;



public final class TSnackbar {

//    private void animateViewIn() {
//
//        Animation anim = AnimationUtils.loadAnimation(mView.getContext(), R.anim.top_in);
//        anim.setInterpolator(com.androidadvance.topsnackbar.AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
//        anim.setDuration(ANIMATION_DURATION);
//        anim.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                if (mCallback != null) {
//                    mCallback.onShown(TSnackbar.this);
//                }
//
//            }
//
//            @Override
//            public void onAnimationStart(Animation animation) {
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//            }
//        });
////        mView.startAnimation(anim);
//
//    }
//
//    private void animateViewOut(final int event) {
//
//        Animation anim = AnimationUtils.loadAnimation(Context, R.anim.top_out);
//        anim.setInterpolator(com.eventer.app.util.AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
//        anim.setDuration(ANIMATION_DURATION);
//        anim.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationEnd(Animation animation) {
////                onViewHidden(event);
//            }
//
//            @Override
//            public void onAnimationStart(Animation animation) {
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//            }
//        });
////        mView.startAnimation(anim);
//
//    }


    public static abstract class Callback {
       
        public static final int DISMISS_EVENT_SWIPE = 0;
        
        public static final int DISMISS_EVENT_ACTION = 1;
        
        public static final int DISMISS_EVENT_TIMEOUT = 2;
        
        public static final int DISMISS_EVENT_MANUAL = 3;
        
        public static final int DISMISS_EVENT_CONSECUTIVE = 4;

        
        @IntDef({DISMISS_EVENT_SWIPE, DISMISS_EVENT_ACTION, DISMISS_EVENT_TIMEOUT,
                DISMISS_EVENT_MANUAL, DISMISS_EVENT_CONSECUTIVE})
        @Retention(RetentionPolicy.SOURCE)
        public @interface DismissEvent {
        }

        
        public void onDismissed(TSnackbar TSnackbar, @DismissEvent int event) {
            
        }

        
        public void onShown(TSnackbar TSnackbar) {
            
        }
    }

    
    @IntDef({LENGTH_INDEFINITE, LENGTH_SHORT, LENGTH_LONG})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Duration {
    }

    
    public static final int LENGTH_INDEFINITE = -2;
    
    public static final int LENGTH_SHORT = -1;
    
    public static final int LENGTH_LONG = 0;
    private static final int ANIMATION_DURATION = 250;



    private final ViewGroup mParent;
    private final Context mContext;
    private int mDuration;
    private Callback mCallback;

    private TSnackbar(ViewGroup parent) {
        mParent = parent;
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);

    }

    
    @NonNull
    public static TSnackbar make(@NonNull View view, @NonNull CharSequence text,
                                 @Duration int duration) {
        TSnackbar TSnackbar = new TSnackbar(findSuitableParent(view));
        TSnackbar.setText(text);
        TSnackbar.setDuration(duration);
        return TSnackbar;
    }

    
    @NonNull
    public static TSnackbar make(@NonNull View view, @StringRes int resId, @Duration int duration) {
        return make(view, view.getResources().getText(resId), duration);
    }

    private static ViewGroup findSuitableParent(View view) {
        return (ViewGroup) view;
    }




    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }
    
    @NonNull
    public TSnackbar setAction(@StringRes int resId, View.OnClickListener listener) {
        return setAction(mContext.getText(resId), listener);
    }

    
    @NonNull
    public TSnackbar setAction(CharSequence text, final View.OnClickListener listener) {

        return this;
    }

    
    @NonNull
    public TSnackbar setText(@NonNull CharSequence message) {
//        final TextView tv = mView.getMessageView();
//        tv.setText(message);
        return this;
    }

    
    @NonNull
    public TSnackbar setText(@StringRes int resId) {
        return setText(mContext.getText(resId));
    }

    
    @NonNull
    public TSnackbar setDuration(@Duration int duration) {
        mDuration = duration;
        return this;
    }

    
    @Duration
    public int getDuration() {
        return mDuration;
    }

//
//    @NonNull
//    public View getView() {
//        return mView;
//    }

    
    public void show() {

    }

    
    public void dismiss() {
        dispatchDismiss(Callback.DISMISS_EVENT_MANUAL);
    }

    private void dispatchDismiss(@Callback.DismissEvent int event) {
//        SnackbarManager.getInstance().dismiss(mManagerCallback, event);
    }

    
    @NonNull
    public TSnackbar setCallback(Callback callback) {
        mCallback = callback;
        return this;
    }

    


    final void showView() {
//        if (mView.getParent() == null) {
//            final ViewGroup.LayoutParams lp = mView.getLayoutParams();
//            if (lp instanceof CoordinatorLayout.LayoutParams) {
//
//                final Behavior behavior = new Behavior();
//                behavior.setStartAlphaSwipeDistance(0.1f);
//                behavior.setEndAlphaSwipeDistance(0.6f);
//                behavior.setSwipeDirection(SwipeDismissBehavior.SWIPE_DIRECTION_START_TO_END);
//                behavior.setListener(new SwipeDismissBehavior.OnDismissListener() {
//                    @Override
//                    public void onDismiss(View view) {
//                        dispatchDismiss(Callback.DISMISS_EVENT_SWIPE);
//                    }
//
//                    @Override
//                    public void onDragStateChanged(int state) {
//                        switch (state) {
//                            case SwipeDismissBehavior.STATE_DRAGGING:
//                            case SwipeDismissBehavior.STATE_SETTLING:
//
//                                SnackbarManager.getInstance().cancelTimeout(mManagerCallback);
//                                break;
//                            case SwipeDismissBehavior.STATE_IDLE:
//
//                                SnackbarManager.getInstance().restoreTimeout(mManagerCallback);
//                                break;
//                        }
//                    }
//                });
//                ((CoordinatorLayout.LayoutParams) lp).setBehavior(behavior);
//            }
//            mParent.addView(mView);
//        }
//        if (ViewCompat.isLaidOut(mView)) {
//
//            animateViewIn();
//        } else {
//
//            mView.setOnLayoutChangeListener(new SnackbarLayout.OnLayoutChangeListener() {
//                @Override
//                public void onLayoutChange(View view, int left, int top, int right, int bottom) {
//                    animateViewIn();
//                    mView.setOnLayoutChangeListener(null);
//                }
//            });
//        }
    }




    public static class SnackbarLayout extends LinearLayout {
        private TextView mMessageView;
        private Button mActionView;
        private int mMaxWidth;
        private int mMaxInlineActionWidth;

        interface OnLayoutChangeListener {
            public void onLayoutChange(View view, int left, int top, int right, int bottom);
        }

        private OnLayoutChangeListener mOnLayoutChangeListener;

        public SnackbarLayout(Context context) {
            this(context, null);
        }

        public SnackbarLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
//            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SnackbarLayout);
//            mMaxWidth = a.getDimensionPixelSize(R.styleable.SnackbarLayout_android_maxWidth, -1);
//            mMaxInlineActionWidth = a.getDimensionPixelSize(
//                    R.styleable.SnackbarLayout_maxActionInlineWidth, -1);
//            if (a.hasValue(R.styleable.SnackbarLayout_elevation)) {
//                ViewCompat.setElevation(this, a.getDimensionPixelSize(
//                        R.styleable.SnackbarLayout_elevation, 0));
//            }

            setClickable(true);
            
            
            
//            LayoutInflater.from(context).inflate(R.layout.tsnackbar_layout_include, this);
        }

        @Override
        protected void onFinishInflate() {
            super.onFinishInflate();
//            mMessageView = (TextView) findViewById(R.id.snackbar_text);
//            mActionView = (Button) findViewById(R.id.snackbar_action);
        }

        TextView getMessageView() {
            return mMessageView;
        }

        Button getActionView() {
            return mActionView;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            if (mMaxWidth > 0 && getMeasuredWidth() > mMaxWidth) {
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxWidth, MeasureSpec.EXACTLY);
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }

            final boolean isMultiLine = mMessageView.getLayout().getLineCount() > 1;
            boolean remeasure = false;
            if (isMultiLine && mMaxInlineActionWidth > 0
                    && mActionView.getMeasuredWidth() > mMaxInlineActionWidth) {

            } else {

            }
            if (remeasure) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        }

        void animateChildrenIn(int delay, int duration) {
            ViewCompat.setAlpha(mMessageView, 0f);
            ViewCompat.animate(mMessageView).alpha(1f).setDuration(duration)
                    .setStartDelay(delay).start();
            if (mActionView.getVisibility() == VISIBLE) {
                ViewCompat.setAlpha(mActionView, 0f);
                ViewCompat.animate(mActionView).alpha(1f).setDuration(duration)
                        .setStartDelay(delay).start();
            }
        }

        void animateChildrenOut(int delay, int duration) {
            ViewCompat.setAlpha(mMessageView, 1f);
            ViewCompat.animate(mMessageView).alpha(0f).setDuration(duration)
                    .setStartDelay(delay).start();
            if (mActionView.getVisibility() == VISIBLE) {
                ViewCompat.setAlpha(mActionView, 1f);
                ViewCompat.animate(mActionView).alpha(0f).setDuration(duration)
                        .setStartDelay(delay).start();
            }
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);
            if (changed && mOnLayoutChangeListener != null) {
                mOnLayoutChangeListener.onLayoutChange(this, l, t, r, b);
            }
        }


    }


}