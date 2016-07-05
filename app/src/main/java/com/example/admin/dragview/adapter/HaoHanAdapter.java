package com.example.admin.dragview.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.admin.dragview.R;
import com.example.admin.dragview.bean.Person;
import com.example.admin.dragview.ui.SwipeLayout;

import java.util.ArrayList;

/**
 * Created by admin on 2016/6/30.
 */
public class HaoHanAdapter extends BaseAdapter {

    private final Context mContext;
    private final ArrayList<Person> persons;
    private ArrayList<SwipeLayout> opendItems;

    public HaoHanAdapter(Context context, ArrayList<Person> persons) {
        this.mContext = context;
        this.persons = persons;
        opendItems = new ArrayList<SwipeLayout>();

    }

    @Override
    public int getCount() {
        return persons.size();
    }

    @Override
    public Object getItem(int position) {
        return persons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if(convertView == null){
            view = view.inflate(mContext, R.layout.item_list, null);
        }
        ViewHolder mViewHolder = ViewHolder.getHolder(view);
        SwipeLayout sl = (SwipeLayout) view;

        sl.setSwipeLayoutListener(new SwipeLayout.OnSwipeLayoutListener() {
            @Override
            public void onClose(SwipeLayout mSwipeLayout) {

                opendItems.remove(mSwipeLayout);
            }

            @Override
            public void onOpen(SwipeLayout mSwipeLayout) {

                opendItems.add(mSwipeLayout);
            }

            @Override
            public void onDraging(SwipeLayout mSwipeLayout) {

            }

            @Override
            public void onStartClose(SwipeLayout mSwipeLayout) {

            }

            @Override
            public void onStartOpen(SwipeLayout mSwipeLayout) {

                for (SwipeLayout layout : opendItems){
                    layout.close();
                }
                opendItems.clear();

            }
        });

        Person p = persons.get(position);
        mViewHolder.tv_name.setText(p.getName());

        return view;
    }

    static class ViewHolder {
        TextView tv_call;
        TextView tv_dell;
        TextView tv_name;

        public static ViewHolder getHolder(View view) {
            Object tag = view.getTag();
            if(tag != null){
                return (ViewHolder)tag;
            }else {
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.tv_call = (TextView)view.findViewById(R.id.tv_call);
                viewHolder.tv_dell = (TextView)view.findViewById(R.id.tv_dell);
                viewHolder.tv_name = (TextView)view.findViewById(R.id.tv_name);
                tag = viewHolder;
                view.setTag(tag);
                return viewHolder;
            }

        }

    }

}
