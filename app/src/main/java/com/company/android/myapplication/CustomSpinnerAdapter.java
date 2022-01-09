package com.company.android.myapplication;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomSpinnerAdapter extends BaseAdapter{
    Context mContext;
    int[] images;
    String[] difficulty;
    LayoutInflater mInflater;

    public CustomSpinnerAdapter(Context mContext, int[] images, String[] difficulty)
    {
        this.mContext = mContext;
        this.images = images;
        this.difficulty = difficulty;
        mInflater = (LayoutInflater.from(mContext));
    }


    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.spinner_content, null);

        TextView tv = convertView.findViewById(R.id.spinner_text);
        ImageView iv = convertView.findViewById(R.id.spinner_image);

        tv.setText(difficulty[position]);
        iv.setImageResource(images[position]);

        return convertView;
    }
}
