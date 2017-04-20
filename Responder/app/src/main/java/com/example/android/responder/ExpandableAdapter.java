package com.example.android.responder;

/**
 * Created by GrayShadow on 4/12/17.
 */

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.List;

public class ExpandableAdapter extends BaseAdapter{


    List<Item> items;
    Context context;

    public class Row{
        AppCompatTextView mTvTitle;
        AppCompatTextView mTvDescription;
        FrameLayout mFlWrapper;
        ImageView mIvArrow;
    }

    public ExpandableAdapter(Context context, List<Item> items){
        this.items = items;
        this.context = context;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Row theRow;

        if(convertView == null){
            theRow = new Row();
            convertView = LayoutInflater.from(context).inflate(R.layout.row_item, parent, false);
            theRow.mFlWrapper = (FrameLayout) convertView.findViewById(R.id.fl_wrapper);
            theRow.mTvTitle = (AppCompatTextView) convertView.findViewById(R.id.tv_title);
            theRow.mTvDescription = (AppCompatTextView) convertView.findViewById(R.id.tv_description);
            theRow.mIvArrow = (ImageView) convertView.findViewById(R.id.iv_arrow);

            convertView.setTag(theRow);
        }else{

            theRow = (Row) convertView.getTag();
        }

        // Update the View
        Item item = items.get(position);
        if(item.isExpanded){
            theRow.mFlWrapper.setVisibility(View.VISIBLE);
            theRow.mIvArrow.setRotation(180f);
        }else{
            theRow.mFlWrapper.setVisibility(View.GONE);
            theRow.mIvArrow.setRotation(0f);
        }

        theRow.mTvTitle.setText(item.title);
        theRow.mTvDescription.setText(item.description);


        // return the view
        return convertView;
    }
}
