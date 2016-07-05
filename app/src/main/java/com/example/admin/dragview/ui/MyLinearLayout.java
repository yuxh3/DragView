package com.example.admin.dragview.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by admin on 2016/6/29.
 */
public class MyLinearLayout extends LinearLayout {
    private DragView mDragLayout;

    public MyLinearLayout(Context context) {
        super(context);
    }

    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDraglayout(DragView mDragLayout){
        this.mDragLayout = mDragLayout;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mDragLayout.getStatus() == DragView.Status.Open){
            return true;
        }else {
            return super.onInterceptTouchEvent(ev);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mDragLayout.getStatus() == DragView.Status.Close){
            return super.onTouchEvent(event);
        }else {
            if (event.getAction() == MotionEvent.ACTION_UP){
                mDragLayout.close();
            }
            return true;
        }

    }
}
