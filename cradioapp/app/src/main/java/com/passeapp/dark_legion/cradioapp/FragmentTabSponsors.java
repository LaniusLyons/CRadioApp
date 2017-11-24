package com.passeapp.dark_legion.cradioapp;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentTabSponsors.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentTabSponsors#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentTabSponsors extends Fragment implements AdapterView.OnItemClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private static Integer totalIcons = 0;
    private ExpandableHeightListView expandableListView;
    private ListViewAdapter listViewAdapter;
    private ProgressBar progressBar;
    public  static double longitude = -79.899969;
    public  static double latitude = -2.146955;
    public LocationManager lm;

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    public FragmentTabSponsors() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentTabSponsors.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentTabSponsors newInstance(String param1, String param2) {
        FragmentTabSponsors fragment = new FragmentTabSponsors();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_fragment_tab_sponsors, container, false);
        this.expandableListView = (ExpandableHeightListView) fragmentView.findViewById(R.id.sponsorsListView);
        this.progressBar = (ProgressBar) fragmentView.findViewById(R.id.progress);

        if(hasPermissions()) {
            executeCachingTask();
        }else{
            requestPerm();
        }

        return fragmentView;
    }

    public void executeCachingTask(){
        try {
            lm = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
        }catch (SecurityException e){
            Log.e("requestLocation fail",e.getLocalizedMessage());
        }
        if(OnlineConnectClass.isOnline(getContext())){
            try {
                new GetSponsorsTask().execute(MainActivity.feedLink);
            }catch (Exception e){
                Log.e("render task fail",e.getLocalizedMessage());
            }
        }else {
            if(getView() != null){
                Snackbar.make(getView(),"Conexión a Internet no disponible.",Snackbar.LENGTH_LONG).show();
            }else{
                Toast.makeText(getContext(),"Conexión a Internet no disponible.",Toast.LENGTH_LONG).show();
            }
        }
    }

    public boolean hasPermissions(){
        int res=0;

        for(String perms : permissions){
            if(!(getActivity().checkCallingOrSelfPermission(perms)== PackageManager.PERMISSION_GRANTED)){
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
            executeCachingTask();
        }else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                    Log.i("permissions","no hay permisos de caching");
                }
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void renderSponsorsLogos(){
        if(!MainActivity.sponsorsList.isEmpty()){
            //this.gridViewAdapter = new GridViewAdapter(getActivity().getApplicationContext(),MainActivity.sponsorsList);
            //sponsorsListView.setAdapter(gridViewAdapter);
            //sponsorsListView.setOnItemClickListener(FragmentTabSponsors.this);

            this.listViewAdapter = new ListViewAdapter(getActivity().getApplicationContext(),MainActivity.sponsorsList);
            this.expandableListView.setAdapter(listViewAdapter);
            this.expandableListView.setExpanded(true);
            this.expandableListView.setOnItemClickListener(FragmentTabSponsors.this);
        }
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        final SponsorsClass aux = MainActivity.sponsorsList.get(i);

        final Dialog dialog = new Dialog(getContext(),android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.custome_dialog);
        dialog.setCancelable(false);

        RelativeLayout relativeLayout = (RelativeLayout)dialog.findViewById(R.id.modalParentLayout);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        TextView lblName = (TextView)dialog.findViewById(R.id.lblName);
        WebView webView = (WebView)dialog.findViewById(R.id.contentHtml);
        String body = aux.getHtmlContent();
        if(body != null && !body.isEmpty()){
            String html = "<!DOCTYPE html><html><body>"+body+"</body></html>";

            webView.loadData(html,"text/html; charset=utf-8","UTF-8");
        }
        ImageView sponsorLogo = (ImageView)dialog.findViewById(R.id.sponsorLogo);
        lblName.setText(aux.getTitle());

        Picasso.with(getContext()).load(aux.getImageLink()).error(R.drawable.ads).into(sponsorLogo);

        Button visitBtn = (Button)dialog.findViewById(R.id.btnVisitUs);
        Button goBtn = (Button)dialog.findViewById(R.id.btnGoMap);
        visitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String link = aux.getUrl();

                if(link != "null" && link != "false" && link != " "){
                    try{
                        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(link));
                        startActivity(intent);
                    }catch (Exception e){
                        Log.e("visit us","error abrir link en google");
                    }
                }
            }
        });
        goBtn.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                if(aux.getLat()!=null && aux.getLon() !=null && aux.getAddress() != null){
                    try{
                        Uri gmmIntentUri = Uri.parse("geo:"+aux.getLat().toString()+","+aux.getLon().toString()+"?q=" + Uri.encode(aux.getAddress()));
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                            startActivity(mapIntent);
                        }else{
                            String link = "https://www.google.com/maps/search/?api=1&query="+aux.getLat()+","+aux.getLon();
                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,Uri.parse(link));
                            startActivity(intent);
                        }
                    }catch (Exception e){
                        String link = "https://www.google.com/maps/search/?api=1&query="+aux.getLat()+","+aux.getLon();
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,Uri.parse(link));
                        startActivity(intent);
                    }
                }else{
                    Toast.makeText(getContext(), "NADA QUE PRESENTAR", Toast.LENGTH_LONG).show();
                }
            }
        });

        dialog.show();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private class GetSponsorsTask extends AsyncTask<String,Integer,ArrayList<SponsorsClass>> {


        @Override
        protected ArrayList<SponsorsClass> doInBackground(String... strings) {
            return getsSponsors(strings[0]);
        }

        @Override
        protected void onPreExecute() {
            showProgress(true);
        }

        @Override
        protected void onPostExecute(ArrayList<SponsorsClass> strings) {
            showProgress(false);
            MainActivity.sponsorsList = strings;
            renderSponsorsLogos();
        }

        private ArrayList<SponsorsClass> getsSponsors(String link){
            ArrayList<SponsorsClass> sponsorsLinks = new ArrayList<>();
            try {
                double longitude;
                double latitude;
                LocationManager lm = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
                Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if(location != null){
                     longitude = location.getLongitude();
                     latitude = location.getLatitude();
                }else{
                    longitude = FragmentTabSponsors.longitude;
                    latitude = FragmentTabSponsors.latitude;
                }

                URL url = new URL(link);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.connect();
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("lat", String.valueOf(latitude))
                        .appendQueryParameter("lng", String.valueOf(longitude));
                String query = builder.build().getEncodedQuery();
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();

                // Starts the query

                int response = conn.getResponseCode();

                InputStream in = conn.getInputStream();
                String encoding = conn.getContentEncoding();
                encoding = encoding == null ? "UTF-8" : encoding;
                String body = IOUtils.toString(in, encoding);

                JSONObject object = new JSONObject(body);
                System.out.println(object.toString());
                System.out.println(latitude + " " + longitude);
                System.out.println("UserLat: " + object.getString("lat"));
                System.out.println("UserLng: " + object.getString("lng"));
                JSONArray items = object.getJSONArray("items");

                for (int i=0;i<items.length();i++){
                    JSONObject aux = items.getJSONObject(i);
                    JSONObject coors = aux.getJSONObject("position");
                    SponsorsClass sponsor = new SponsorsClass(i+1,coors.getDouble("lat"),coors.getDouble("lng"),aux.getString("url"),coors.getString("address"),aux.getString("image"),aux.getString("title"),aux.getString("distance"), aux.getString("localUrl"));
                    if(aux.has("content_html")){
                        sponsor.setHtmlContent(aux.getString("content_html"));
                    }
                    sponsorsLinks.add(sponsor);
                }


            } catch (JSONException e) {
                Log.e("error en json parse",e.getLocalizedMessage());
            } catch (SecurityException e) {
                Log.e("error obtener links",e.getLocalizedMessage());
            }catch (IOException e){
                Log.e("error obtener links",e.getLocalizedMessage());
            }catch (Exception e){
                Log.e("error obtener links",e.getLocalizedMessage());
            }

            return  sponsorsLinks;
        }

    }


}
