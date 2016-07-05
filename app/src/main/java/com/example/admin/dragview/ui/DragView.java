package com.example.admin.dragview.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.nineoldandroids.view.ViewHelper;

/**
 * Created by admin on 2016/6/28.
 */
public class DragView extends FrameLayout {

    private static final String TAG = "DragView";
    private ViewDragHelper mDragHelper;
    private ViewGroup mLeftContent;
    private ViewGroup mMainContent;
    private int mWidth;
    private int mHeight;
    private int mRange;
    private Status mStatus = Status.Close;
    private OnDragStatusChangeListener mListener;
    private DragView draglayout;

    public void setDraglayout(DragView draglayout) {
        this.draglayout = draglayout;
    }

    public static enum Status{
       Close,Open,Draging;
    }
    interface OnDragStatusChangeListener{
        void onClose();
        void onOpen();
        void onDraging(float percent);
    }
    public Status getStatus() {
        return mStatus;
    }


    public void setDragStatusListener(OnDragStatusChangeListener listener){
        this.mListener = listener;
    }

    public DragView(Context context) {
        this(context, null);
    }

    public DragView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mDragHelper = ViewDragHelper.create(this, mCallBack);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount()<2){
            throw new IllegalStateException("布局至少要有两孩子");
        }if (!(getChildAt(0) instanceof ViewGroup && getChildAt(1) instanceof ViewGroup)){
            throw new IllegalArgumentException("孩子必须是ViewGroup");
        }

        mLeftContent = (ViewGroup) getChildAt(0);
        mMainContent = (ViewGroup) getChildAt(1);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = mMainContent.getMeasuredWidth();
        mHeight = mMainContent.getMeasuredHeight();

        //mLightWidth = mLeftContent.getMeasuredWidth();

        mRange = (int) (mWidth * 0.6f);

    }
    ViewDragHelper.Callback mCallBack = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            Log.i(TAG, "tryCaptureView: " + child);
            return true;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return mRange;
        }

        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            Log.i(TAG, "onViewCaptured: " + capturedChild);
            // 当capturedChild被捕获时,调用.
            super.onViewCaptured(capturedChild, activePointerId);
        }
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == mMainContent){
                left = fixLeft(left);
            }
            return left;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            int newLeft = left;
            if (changedView == mLeftContent){
                newLeft = mMainContent.getLeft() + dx;
            }
            newLeft = fixLeft(newLeft);
            if (changedView == mLeftContent){
                mLeftContent.layout(0,0,0+mWidth,0+mHeight);
            }

            dispatchDragEvent(newLeft);

            invalidate();

        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);

            if (xvel == 0 && mMainContent.getLeft()> mRange / 2.0f){
                open();
            }else if (xvel >0){
                open();
            }else {
                close();
            }
        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
        }
    };

    protected void close() {
        close(true);
    }
    private void close(boolean isSmooth) {

        int finalLeft =0;
        if (isSmooth){
            if (mDragHelper.smoothSlideViewTo(mMainContent,finalLeft,0));
            ViewCompat.postInvalidateOnAnimation(this);
        }else {
            mMainContent.layout(finalLeft,0,finalLeft + mWidth,finalLeft + mHeight);
        }
    }

    protected void open() {
        open(true);
    }
    private void open(boolean isSmooth) {

        int finalLeft = mRange;
        if (isSmooth){
            if (mDragHelper.smoothSlideViewTo(mMainContent,finalLeft,0));
            ViewCompat.postInvalidateOnAnimation(this);
            Log.d(TAG, "onViewReleased: " + "xvel: " + " yvel: ");
        }else {
            mMainContent.layout(finalLeft, 0, finalLeft + mRange, 0 + mHeight);
        }


    }

    private int fixLeft(int left) {
        if (left <0){
            return 0;
        }else if(left > mRange){
            return mRange;
        }
        return left;
    }

    private void dispatchDragEvent(int newLeft) {
        float precent = newLeft * 1.0f / mRange;
        if (mListener != null){
            mListener.onDraging(precent);
        }
        Status preStatus = mStatus;
        mStatus = upDateStatus(precent);
        if (mStatus != preStatus){
            if (mStatus == Status.Close){
                if (mListener != null) {
                    mListener.onClose();
                }
            }else if (mStatus == Status.Open){
                if (mListener != null){
                    mListener.onOpen();
                }
            }
        }
        animViews(precent);

    }

    private void animViews(float precent) {

        ViewHelper.setScaleX(mLeftContent,evaluate(precent,0.5f,1.0f));
        ViewHelper.setScaleY(mLeftContent, 0.5f + 0.5f * precent);

        ViewHelper.setTranslationX(mLeftContent, evaluate(precent, -mWidth / 2.0f, 0));
        ViewHelper.setAlpha(mLeftContent, evaluate(precent, 0.5f, 1.0f));

        ViewHelper.setScaleX(mMainContent, evaluate(precent, 1.0f, 0.8f));
        ViewHelper.setScaleY(mMainContent, evaluate(precent, 1.0f, 0.8f));

        getBackground().setColorFilter((Integer)evaluateColor(precent, Color.BLACK, Color.TRANSPARENT), PorterDuff.Mode.SRC_OVER);
    }

    public Object evaluateColor(float fraction, Object startValue, Object endValue) {
        int startInt = (Integer) startValue;
        int startA = (startInt >> 24) & 0xff;
        int startR = (startInt >> 16) & 0xff;
        int startG = (startInt >> 8) & 0xff;
        int startB = startInt & 0xff;

        int endInt = (Integer) endValue;
        int endA = (endInt >> 24) & 0xff;
        int endR = (endInt >> 16) & 0xff;
        int endG = (endInt >> 8) & 0xff;
        int endB = endInt & 0xff;

        return (int)((startA + (int)(fraction * (endA - startA))) << 24) |
                (int)((startR + (int)(fraction * (endR - startR))) << 16) |
                (int)((startG + (int)(fraction * (endG - startG))) << 8) |
                (int)((startB + (int)(fraction * (endB - startB))));
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mDragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    protected Float evaluate(float fraction,Number startValue,Number endValue){
        float startFloat = startValue.floatValue();
        return startFloat + fraction*(endValue.floatValue() - startFloat);
    }
    private Status upDateStatus(float percent){
        if (percent == 0){
            return Status.Close;
        }else if (percent == 1){
            return Status.Open;
        }
        return Status.Draging;
    }
    private int clampResult(int tempValue, int defaultValue) {
        Integer minLeft = null;
        Integer maxLeft = null;
        return 0;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        mDragHelper.processTouchEvent(event);
        return true;
    }
}
