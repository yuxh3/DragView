package com.example.admin.dragview.MyListView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.admin.dragview.Animation.ResetAnimation;
import com.example.admin.dragview.utils.Utils;

/**
 * Created by admin on 2016/7/1.
 */
public class MyListView extends ListView {


    private ImageView mImage;
    private int mOrignaHeighht;
    private int drawableHeight;

    public MyListView(Context context) {
        super(context);
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setParallaxImage(ImageView mImage){
        this.mImage = mImage;
        mOrignaHeighht = mImage.getHeight();
        drawableHeight = mImage.getDrawable().getIntrinsicHeight();

        Utils.showToast(getContext(),"mOrignaHeighht:" +mOrignaHeighht +"drawableHeight " +drawableHeight);
    }


    @Override
    protected boolean overScrollBy(int deltaX,
                                   int deltaY, int scrollX, int scrollY,
                                   int scrollRangeX, int scrollRangeY,
                                   int maxOverScrollX, int maxOverScrollY,
                                   boolean isTouchEvent) {
        if (isTouchEvent && deltaY <0){
            if (mImage.getHeight() <= drawableHeight){
                int newHeight = (int) (mImage.getHeight() + Math.abs(deltaY/3.0f));
                mImage.getLayoutParams().height = newHeight;
                mImage.requestLayout();
            }
        }
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        switch (ev.getAction()){
            case MotionEvent.ACTION_UP:
                final int startHeight = mImage.getHeight();
                final int endHeight = mOrignaHeighht;

                ResetAnimation animation = new ResetAnimation(mImage,startHeight,endHeight);
                 startAnimation(animation);

                break;
        }

        return super.onTouchEvent(ev);
    }
}
