package com.passeapp.dark_legion.cradioapp;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.squareup.picasso.Picasso;

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
    private String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    //private GridView sponsorsListView;
    //private GridViewAdapter gridViewAdapter;
    private static Integer totalIcons = 0;
    private ExpandableHeightListView expandableListView;
    private ListViewAdapter listViewAdapter;
    private ProgressBar progressBar;

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
        //this.sponsorsListView = (GridView) fragmentView.findViewById(R.id.sponsorsListView);
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
            new CachingSponsorTask().execute();
        }catch (Exception e){
            Log.e("render task fail",e.getLocalizedMessage());
        }
    }

    public boolean hasPermissions(){
        int res=0;

        for(String perms : permissions){
            res = getActivity().checkCallingOrSelfPermission(perms);
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
            executeCachingTask();
        }else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
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

    private class CachingSponsorTask extends AsyncTask<ArrayList<SponsorsClass>,Integer,Void>{

        @Override
        protected void onPostExecute(Void aVoid) {
            showProgress(false);
            renderSponsorsLogos();
        }

        @Override
        protected Void doInBackground(ArrayList<SponsorsClass>... arrayLists) {
            renderSponsors();
            return null;
        }

        private void renderSponsors(){
            if(!OnlineConnectClass.isOnline(getContext())){
                MainActivity.sponsorsList = MainActivity.database.getSponsorsRows();
            }
        }

        @Override
        protected void onPreExecute() {
            showProgress(true);
        }
    }
}
