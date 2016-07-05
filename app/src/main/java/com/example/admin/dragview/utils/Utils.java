package com.example.admin.dragview.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by admin on 2016/6/29.
 */
public class Utils {

    public static Toast mToast;

    public static void showToast(Context mContext,String msg){
        if (mToast == null){
            mToast = Toast.makeText(mContext,"",Toast.LENGTH_SHORT);
        }
        mToast.setText(msg);
        mToast.show();
    }
}
