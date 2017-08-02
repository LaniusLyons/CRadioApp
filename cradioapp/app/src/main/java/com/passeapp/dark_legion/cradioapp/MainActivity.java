package com.passeapp.dark_legion.cradioapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements FragmentTabStream.OnFragmentInteractionListener, FragmentTabSponsors.OnFragmentInteractionListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    public static boolean isReadyStream = false;
    private String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    public String feedLink = "http://llegaraqui.com/feed/json";
    public static AppDataBase database;
    public static ArrayList<SponsorsClass> sponsorsList = new ArrayList<>();
    public static Float density;

    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mMessageReceiver);
            finish();
            stopService(new Intent(getApplicationContext(),RadioService.class));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        //LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("closing-app"));

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        initDB();
        density = getResources().getDisplayMetrics().density;

        if(hasPermissions() && OnlineConnectClass.isOnline(this)){
            new GetSponsorsTask().execute(feedLink);
        }else{
            requestPerm();
        }

    }

    public void initDB(){
        this.database = new AppDataBase(this,"DataBase",null,1);
    }

    public boolean hasPermissions(){
        int res=0;

        for(String perms : permissions){
            res = checkCallingOrSelfPermission(perms);
            if(!(res== PackageManager.PERMISSION_GRANTED)){
                return false;
            }
        }

        return true;
    }

    public void requestPerm(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(permissions,REQUEST_CODE_ASK_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean allowed = true;
        switch (requestCode){
            case REQUEST_CODE_ASK_PERMISSIONS:
                for(int res : grantResults){
                    allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
                }
                break;
            default:
                allowed = false;
                break;
        }

        if(allowed){
            Log.i("permissions","permisos aceptados");
            new GetSponsorsTask().execute(feedLink);
        }else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    Log.i("permissions","no hay permisos de caching");
                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.


    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }
    */

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return PlaceholderFragment.newInstance(position + 1);
            switch (position) {
                case 0:
                    return new FragmentTabSponsors();
                case 1:
                    return new FragmentTabStream();
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "AUSPICIANTES";
                case 1:
                    return "RADIO";
            }
            return null;
        }
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent serviceIntent = new Intent(this,RadioService.class);
        serviceIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
        startService(serviceIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(RadioService.prepared){
            Intent serviceIntent = new Intent(this,RadioService.class);
            serviceIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
            startService(serviceIntent);
        }else{
            if(OnlineConnectClass.isOnline(this)){
                Intent serviceIntent = new Intent(this,RadioService.class);
                serviceIntent.setAction(Constants.ACTION.MAIN_ACTION);
                startService(serviceIntent);
            }else{
                Snackbar.make(mViewPager,"No dispones de conexion a Internet. Intentalo mas tarde",Snackbar.LENGTH_INDEFINITE).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this,RadioService.class));
        super.onDestroy();
    }

    private class GetSponsorsTask extends AsyncTask<String,Integer,ArrayList<String>> {


        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            return getsSponsors(strings[0]);
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
                    SponsorsClass sponsor = new SponsorsClass(i,aux.getString("id"),aux.getString("url"),coors.getDouble("lat"),coors.getDouble("lng"),coors.getString("address"),aux.getString("image"));
                    sponsorsList.add(sponsor);
                }
                if(!sponsorsList.isEmpty()){
                    addingSponsorsToDB(sponsorsList);
                }

            } catch (JSONException e) {
                Log.e("error en json parse",e.getLocalizedMessage());
            } catch (IOException e) {
                Log.e("error obtener links",e.getLocalizedMessage());
            }
            return  sponsorsLinks;
        }

        private void addingSponsorsToDB(ArrayList<SponsorsClass> sponsors){
            database.deleteAllSponsor(database.db);
            for (SponsorsClass sponsor:sponsors) {
                database.setSponsor(database.db,sponsor);
            }
        }
    }


}
