package com.example.admin.dragview.Animation;

import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.Transformation;
import android.widget.ImageView;

/**
 * Created by admin on 2016/7/1.
 */
public class ResetAnimation extends Animation {

    private final ImageView mImage;
    private final int startHeight;
    private final int endHeight;

    public ResetAnimation(ImageView mImage,int startHeight,int endHeight) {

        this.mImage = mImage;
        this.startHeight = startHeight;
        this.endHeight = endHeight;

        setInterpolator(new OvershootInterpolator());
        setDuration(500);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {


        Integer newHeight = evaluate(interpolatedTime, startHeight, endHeight);

        mImage.getLayoutParams().height = newHeight;
        mImage.requestLayout();
        super.applyTransformation(interpolatedTime, t);
    }
    public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
        int startInt = startValue;
        return (int)(startInt + fraction * (endValue - startInt));
    }
}
