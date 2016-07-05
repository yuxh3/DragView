package com.example.admin.dragview.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.admin.dragview.R;
import com.example.admin.dragview.adapter.HaoHanAdapter;
import com.example.admin.dragview.bean.Person;
import com.example.admin.dragview.utils.Cheeses;
import com.example.admin.dragview.utils.Utils;
import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MainActivity extends Activity {

    private ListView mLeftList;
    private ListView mMianList;
    private int nextInt;
    private ImageView mHeaderImage;
    private ArrayList<Person> persons;
    private TextView tv_center;
    private QuickIndexBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        mLeftList = (ListView) findViewById(R.id.IvLeft);
        mMianList = (ListView) findViewById(R.id.lvMain);
        mHeaderImage = (ImageView) findViewById(R.id.IvMain);

        tv_center = (TextView) findViewById(R.id.tv_center);

        bar = (QuickIndexBar) findViewById(R.id.bar);
        bar.setListener(new QuickIndexBar.OnLetterUpdateListener() {
            @Override
            public void onLetterUpdate(String letter) {
                showLetter(letter);

                for (int i = 0; i < persons.size(); i++) {
                    Person person = persons.get(i);
                    String l = person.getPinyin().charAt(0) + "";
                    if (TextUtils.equals(letter, l)) {
                        mMianList.setSelection(i);
                        break;
                    }
                }

            }
        });

        persons = new ArrayList<Person>();

        fillAndSortData(persons);

        MyLinearLayout mLinearLayout = (MyLinearLayout) findViewById(R.id.llMain);

        DragView mDragLayout = (DragView) findViewById(R.id.dl);

        mLinearLayout.setDraglayout(mDragLayout);

        mDragLayout.setDragStatusListener(new DragView.OnDragStatusChangeListener() {
            @Override
            public void onClose() {

                Utils.showToast(MainActivity.this, "关闭");

                bar.setVisibility(View.VISIBLE);
                shakeHeader();
            }

            @Override
            public void onOpen() {
                Utils.showToast(MainActivity.this, "打开");

                bar.setVisibility(View.INVISIBLE);
                Random random = new Random();
                nextInt = random.nextInt(30);
                mLeftList.smoothScrollToPosition(nextInt);

            }

            @Override
            public void onDraging(float percent) {
                Utils.showToast(MainActivity.this, "拖拽");

                ViewHelper.setAlpha(mHeaderImage, 1 - percent);
                ViewHelper.setAlpha(bar, 1 - percent);
            }
        });

        mLeftList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Cheeses.sCheeseStrings) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView mText = (TextView) view.findViewById(android.R.id.text1);
                if (position == nextInt) {

                    mText.setTextColor(Color.WHITE);
                } else {
                    mText.setTextColor(Color.BLACK);
                }
                return view;
            }
        });

        mMianList.setAdapter(new HaoHanAdapter(MainActivity.this,persons));
        mMianList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,DateActivity.class);
                startActivity(intent);
            }
        });


   }

    private void fillAndSortData(ArrayList<Person> persons) {

        for (int i = 0;i<Cheeses.NAMES.length;i++){
            String name = Cheeses.NAMES[i];
            persons.add(new Person(name));
        }

        Collections.sort(persons);
    }
   private Handler mHandler = new Handler();
    private void showLetter(String letter) {

        tv_center.setVisibility(View.VISIBLE);
        tv_center.setText(letter);
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tv_center.setVisibility(View.GONE);
            }
        },2000);

    }

    private void shakeHeader() {
        mHeaderImage.startAnimation(AnimationUtils.loadAnimation(this,R.anim.shake));
    }
}
