package com.passeapp.dark_legion.cradioapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;


public class ListViewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<SponsorsClass> items;

    public ListViewAdapter(Context context, ArrayList<SponsorsClass> items) {
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

        if(view == null){
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.custom_row,viewGroup,false);
        }

        SponsorsClass sponsor = (SponsorsClass) getItem(i);
        TextView textView = (TextView) view.findViewById(R.id.lblSite);
        TextView lblDistance = (TextView) view.findViewById(R.id.lblDistance);
        ImageView imageView = (ImageView) view.findViewById(R.id.iconSite);

        textView.setText(sponsor.getTitle());
        lblDistance.setText(sponsor.getStringDistance());

        Picasso.with(context).load(items.get(i).getImageLink()).placeholder(R.drawable.ads).error(R.drawable.ads).into(imageView);


        return view;
    }
}
