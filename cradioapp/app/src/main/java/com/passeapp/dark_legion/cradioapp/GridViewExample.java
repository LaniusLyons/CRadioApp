package com.passeapp.dark_legion.cradioapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Arrays;

public class GridViewExample extends AppCompatActivity {

    public String[] items = {
            "https://upload.wikimedia.org/wikipedia/commons/8/87/Google_Chrome_icon_%282011%29.png",
            "http://www.llegaraqui.com/wp-content/uploads/2017/07/Screen-Shot-2017-06-14-at-9.37.37-AM.png",
            "https://upload.wikimedia.org/wikipedia/commons/8/87/Google_Chrome_icon_%282011%29.png",
            "https://stackoverflow.com/questions/22143157/android-picasso-placeholder-and-error-image-styling"
    };
    public ArrayList<String> list = new ArrayList<>(Arrays.asList(items));
    private GridView gridView;
    private GridViewAdapter gridViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_view_example);


        gridView = (GridView)findViewById(R.id.gridexample);
        gridViewAdapter = new GridViewAdapter(GridViewExample.this,list);
        gridView.setAdapter(gridViewAdapter);
    }
}
