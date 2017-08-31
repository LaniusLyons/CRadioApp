package com.passeapp.dark_legion.cradioapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
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
        new GetSponsorsTask().execute("http://llegaraqui.com/feed/json");

    }


    private class GetSponsorsTask extends AsyncTask<String,Integer,ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            return getsSponsors(strings[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);
            gridViewAdapter = new GridViewAdapter(GridViewExample.this,MainActivity.sponsorsList);
            gridView.setAdapter(gridViewAdapter);
        }

        private ArrayList<String> getsSponsors(String link){
            ArrayList<String> sponsorsLinks = new ArrayList<>();
            try {

                URL url = new URL(link);
                URLConnection con = url.openConnection();
                InputStream in = con.getInputStream();
                String encoding = con.getContentEncoding();
                encoding = encoding == null ? "UTF-8" : encoding;
                String body = IOUtils.toString(in, encoding);

                JSONObject object = new JSONObject(body);
                JSONArray items = object.getJSONArray("items");

                for (int i=0;i<items.length();i++){
                    JSONObject aux = items.getJSONObject(i);
                    JSONObject coors = aux.getJSONObject("position");
                    SponsorsClass sponsor = new SponsorsClass(i,coors.getDouble("lat"),coors.getDouble("lng"),aux.getString("link"),coors.getString("address"),aux.getString("image"),aux.getString("title"));
                    MainActivity.sponsorsList.add(sponsor);
                }

            } catch (JSONException e) {
                Log.e("error en json parse",e.getLocalizedMessage());
            } catch (IOException e) {
                Log.e("error obtener links",e.getLocalizedMessage());
            }
            return  sponsorsLinks;
        }

    }
}
