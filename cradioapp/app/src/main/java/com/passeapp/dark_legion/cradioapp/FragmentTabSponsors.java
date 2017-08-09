package com.passeapp.dark_legion.cradioapp;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

    private GridView sponsorsListView;
    private GridViewAdapter gridViewAdapter;
    private static Integer totalIcons = 0;

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
        if(hasPermissions()) {
            executeCachingTask();
        }else{
            requestPerm();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_fragment_tab_sponsors, container, false);
        this.sponsorsListView = (GridView) fragmentView.findViewById(R.id.sponsorsListView);
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
            this.gridViewAdapter = new GridViewAdapter(getActivity().getApplicationContext(),MainActivity.sponsorsList);
            sponsorsListView.setAdapter(gridViewAdapter);
            sponsorsListView.setOnItemClickListener(FragmentTabSponsors.this);
        }
    }

    /*
    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(OnlineConnectClass.isOnline(getContext())){
            Toast.makeText(getContext(), "CLICK EN ITEM", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(getContext(), "CONEXION A INTERNET NO DISPONIBLE", Toast.LENGTH_LONG).show();
        }
        return false;
    }*/

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(OnlineConnectClass.isOnline(getContext())){
            Toast.makeText(getContext(), "CLICK EN ITEM", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(getContext(), "CONEXION A INTERNET NO DISPONIBLE", Toast.LENGTH_LONG).show();
        }
    }

    private class CachingSponsorTask extends AsyncTask<ArrayList<SponsorsClass>,Integer,Void>{

        @Override
        protected void onPostExecute(Void aVoid) {
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

    }
}
