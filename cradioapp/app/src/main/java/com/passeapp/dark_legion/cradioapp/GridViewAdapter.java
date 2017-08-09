package com.passeapp.dark_legion.cradioapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GridViewAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<String> items;


    public GridViewAdapter(Context context, ArrayList<String> items) {
        super();
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView img;
        if(view == null){
            img = new ImageView(context);
            view = img;
            img.setPadding(10,10,10,10);
        }else{
            img = (ImageView) view;
        }

        Picasso.with(context).load(items.get(i)).placeholder(R.drawable.broken).error(R.drawable.broken).resize(150,150).into(img);
        return view;
    }
}
