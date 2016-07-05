package com.example.admin.dragview.ui;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.example.admin.dragview.utils.Utils;

/**
 * Created by admin on 2016/7/1.
 */
public class SwipeLayout extends FrameLayout {

    private View mBackView;
    private View mFrontView;
    private int mHeight;
    private int mWidth;
    private int mRange;
    private ViewDragHelper mDragHelper;

    private Status status = Status.Close;
    private OnSwipeLayoutListener swipeLayoutListener;

    public static enum Status {
        Close, Open, Draging;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public OnSwipeLayoutListener getSwipeLayoutListener() {
        return swipeLayoutListener;
    }

    public void setSwipeLayoutListener(OnSwipeLayoutListener listener) {
        this.swipeLayoutListener = listener;
    }

    public static interface OnSwipeLayoutListener {
        void onClose(SwipeLayout mSwipeLayout);

        void onOpen(SwipeLayout mSwipeLayout);

        void onDraging(SwipeLayout mSwipeLayout);

        // 要去关闭
        void onStartClose(SwipeLayout mSwipeLayout);

        // 要去开启
        void onStartOpen(SwipeLayout mSwipeLayout);
    }

    public SwipeLayout(Context context) {
        this(context, null);
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDragHelper = ViewDragHelper.create(this, 1.0f, mCallback);
    }

    ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {
        // c. 重写监听
        @Override
        public boolean tryCaptureView(View view, int id) {
            return true;
        }

        //限定水平距离
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == mFrontView) {
                if (left < -mRange) {
                    return -mRange;
                } else if (left > 0) {
                    return 0;
                }
            } else if (child == mBackView) {
                if (left > mWidth) {
                    return mWidth;
                } else if (left < mWidth - mRange) {
                    return mRange;
                }
            }

            return left;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if (changedView == mFrontView) {
                mBackView.offsetLeftAndRight(dx);
            } else if (changedView == mBackView) {
                mFrontView.offsetLeftAndRight(dx);
            }

            dispatchSwipeEvent();

            invalidate();
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {

            if (xvel == 0 && mFrontView.getLeft() < -mRange / 2.0f) {
                open();
            } else if (xvel < 0) {
                open();
            } else {
                close();
            }
        }
    };


    @Override
    public boolean onTouchEvent(MotionEvent event) {
            try {
                mDragHelper.processTouchEvent(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        layoutContent(false);
    }

    public void close() {
        Utils.showToast(getContext(), "Close");
        close(true);
    }

    public void close(boolean isSmoooth) {
        int finalLeft = 0;
        if (isSmoooth) {
            if (mDragHelper.smoothSlideViewTo(mFrontView, finalLeft, 0)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        } else {
            layoutContent(false);
        }
    }

    private void layoutContent(boolean isOpen) {
        Rect frontRect = computeFrontViewRect(isOpen);

        mFrontView.layout(frontRect.left, frontRect.top, frontRect.right, frontRect.bottom);

        Rect backRect = computeBackViewFront(frontRect);

        mBackView.layout(backRect.left, backRect.top, backRect.right, backRect.bottom);
        //调整顺序，把mFrontView前置
        bringChildToFront(mFrontView);

    }

    private Rect computeBackViewFront(Rect frontRect) {

        int left = frontRect.right;

        return new Rect(left, 0, left + mRange, 0 + mHeight);
    }

    public Rect computeFrontViewRect(boolean isOpen) {

        int left = 0;
        if (isOpen) {
            left = -mRange;
        }
        return new Rect(left, 0, left + mWidth, 0 + mHeight);
    }

    public void open() {

        Utils.showToast(getContext(), "Open");

        open(true);
    }

    public void open(boolean isSmooth) {
        int finalLeft = -mRange;
        if (isSmooth) {
            if (mDragHelper.smoothSlideViewTo(mFrontView, finalLeft, 0)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        } else {
            layoutContent(true);
        }
    }


    //主要功能是计算拖动的位移量、更新背景、设置要显示的屏幕(setCurrentScreen(mCurrentScreen);)
    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }

    }

    protected void dispatchSwipeEvent() {

        if (swipeLayoutListener != null) {
            swipeLayoutListener.onDraging(this);
        }

        Status preStatus = status;
        status = updateStatus();
        if (preStatus != status && swipeLayoutListener != null) {
            if (status == Status.Close) {
                swipeLayoutListener.onClose(this);
            } else if (status == Status.Open) {
                swipeLayoutListener.onOpen(this);
            } else if (status == Status.Draging) {
                if (preStatus == Status.Close) {
                    swipeLayoutListener.onStartOpen(this);
                } else if (preStatus == Status.Open) {
                    swipeLayoutListener.onStartClose(this);
                }
            }
        }
    }

    private Status updateStatus() {

        int left = mFrontView.getLeft();
        if (left == 0) {
            return Status.Close;
        } else if (left == -mRange) {
            return Status.Open;
        }
        return Status.Draging;

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mBackView = getChildAt(0);
        mFrontView = getChildAt(1);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = mFrontView.getMeasuredHeight();
        mWidth = mFrontView.getMeasuredWidth();

        mRange = mBackView.getMeasuredWidth();
    }


}
