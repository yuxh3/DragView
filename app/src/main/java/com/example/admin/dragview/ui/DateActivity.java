package com.example.admin.dragview.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.admin.dragview.MyListView.MyListView;
import com.example.admin.dragview.R;
import com.example.admin.dragview.utils.Cheeses;

/**
 * Created by admin on 2016/7/1.
 */
public class DateActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);

        final MyListView mListView = (MyListView) findViewById(R.id.lv);
        final View mHeaderView = View.inflate(this, R.layout.view_header, null);

         final ImageView mImage = (ImageView) mHeaderView.findViewById(R.id.iv);
         mListView.setOverScrollMode(View.OVER_SCROLL_NEVER);//关闭下拉时候的闪光
         mListView.addHeaderView(mHeaderView);
         mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mListView.setParallaxImage(mImage);

                mHeaderView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });


        mListView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, Cheeses.NAMES));
    }
}
