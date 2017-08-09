package com.passeapp.dark_legion.cradioapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GridViewAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<SponsorsClass> items;


    public GridViewAdapter(Context context, ArrayList<SponsorsClass> items) {
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
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_grid_element, viewGroup, false);

            /*img = new ImageView(context);
            view = img;
            img.setPadding(10,10,10,10);*/
        }else{
            //img = (ImageView) view;

        }
        ImageView imageView = (ImageView) view.findViewById(R.id.icono);

        // 0.75 if it's LDPI
        // 1.0 if it's MDPI
        // 1.5 if it's HDPI
        // 2.0 if it's XHDPI
        // 3.0 if it's XXHDPI
        // 4.0 if it's XXXHDPI
        int iconSizeHeight;
        int iconSizeWidth;
        Float density = MainActivity.density;
        if(density <= 0.75f){
            iconSizeHeight = 170;
            iconSizeWidth = 100;
        }else if( density > 0.75f && density <= 1.0f){
            iconSizeHeight = 200;
            iconSizeWidth = 150;
        }else if( density > 1.0f && density <= 1.5f){
            iconSizeHeight = 220;
            iconSizeWidth = 165;
        }else if( density > 1.5f && density <= 2.0f){
            iconSizeHeight = 340;
            iconSizeWidth = 260;
        }else if( density > 2.0f && density <= 3.0f){
            iconSizeHeight = 440;
            iconSizeWidth = 350;
        }else if( density > 3.0f && density <= 4.0f){
            iconSizeHeight = 500;
            iconSizeWidth = 400;
        }else{
            iconSizeHeight = 450;
            iconSizeWidth = 345;
        }

        Picasso.with(context).load(items.get(i).getImageLink()).placeholder(R.drawable.broken).error(R.drawable.broken).resize(iconSizeWidth,iconSizeHeight).into(imageView);
        return imageView;
    }
}
