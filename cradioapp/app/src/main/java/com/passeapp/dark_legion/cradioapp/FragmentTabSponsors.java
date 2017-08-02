package com.passeapp.dark_legion.cradioapp;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
public class FragmentTabSponsors extends Fragment implements AdapterView.OnItemLongClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private GridView sponsorsListLayout;

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
        try {
            new CachingSponsorTask().execute();
        }catch (Exception e){
            Log.e("render task fail",e.getLocalizedMessage());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_fragment_tab_sponsors, container, false);
        this.sponsorsListLayout = (GridView) fragmentView.findViewById(R.id.sponsorsListLayout);
        return fragmentView;
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


    private class CachingSponsorTask extends AsyncTask<ArrayList<SponsorsClass>,Integer,Void> {


        @Override
        protected void onPostExecute(Void aVoid) {
            new RenderSponsorTask().execute();
        }

        @Override
        protected Void doInBackground(ArrayList<SponsorsClass>... arrayLists) {
            renderSponsors();
            return null;
        }

        private void renderSponsors(){
            if(OnlineConnectClass.isOnline(getContext())){
                MainActivity.sponsorsList = MainActivity.database.getSponsorsRows();
            }
            if(!MainActivity.sponsorsList.isEmpty()){
                handleStorage(MainActivity.sponsorsList);
            }
        }

        private void handleStorage(ArrayList<SponsorsClass> sponsors){
            if(!sponsors.isEmpty()){
                for (SponsorsClass sp:sponsors) {
                    Picasso.with(getContext())
                            .load(sp.getImageLink())
                            .into(getTarget(sp.getName()));
                }
            }
        }

        private Target getTarget(final String name){
            Target target = new Target(){
                ContextWrapper cw = new ContextWrapper(getContext());
                // path to /data/data/yourapp/app_data/imageDir
                final File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

                @Override
                public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                    new Thread(new Runnable() {

                        @Override
                        public void run() {

                            File file = new File(directory,name+".png");
                            //File file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + url);
                            try {
                                file.createNewFile();
                                FileOutputStream ostream = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
                                ostream.flush();
                                ostream.close();
                            } catch (IOException e) {
                                Log.e("IOException", e.getLocalizedMessage());
                            }
                        }
                    }).start();

                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };
            return target;
        }

        /*
        private String saveToInternalStorage(){
            ContextWrapper cw = new ContextWrapper(getContext());
            // path to /data/data/yourapp/app_data/imageDir
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            // Create imageDir
            File mypath=new File(directory,"profile.jpg");

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(mypath);
                // Use the compress method on the BitMap object to write image to the OutputStream
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return directory.getAbsolutePath();
        }*/

    }

    class GridAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return MainActivity.sponsorsList.size();
        }

        @Override
        public Object getItem(int i) {
            return MainActivity.sponsorsList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return MainActivity.sponsorsList.get(i).get_id();
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            View view = convertView;

            if (view == null) {
                view = getActivity().getLayoutInflater().inflate(R.layout.custom_grid_element,null);
            }

            ImageView icon = (ImageView)view.findViewById(R.id.icono);
            SponsorsClass aux = (SponsorsClass) getItem(i);

            ContextWrapper cw = new ContextWrapper(getContext());
            // path to /data/data/yourapp/app_data/imageDir
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            // Create imageDir
            File file = new File(directory,aux.getImageLink()+".png");
            Picasso.with(getContext()).load(file).into(icon);


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
            icon.getLayoutParams().height = iconSizeHeight;
            icon.getLayoutParams().width = iconSizeWidth;
            return view;
        }

    }


    private class RenderSponsorTask extends AsyncTask<Void,Integer,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            renderSponsorsLogos();
            return null;
        }

        public void renderSponsorsLogos(){
            sponsorsListLayout.setAdapter(new GridAdapter());
            sponsorsListLayout.setOnItemLongClickListener(FragmentTabSponsors.this);
        }
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(OnlineConnectClass.isOnline(getContext())){

        }else {
            Toast.makeText(getContext(), "CONEXION A INTERNET NO DISPONIBLE", Toast.LENGTH_LONG).show();
        }
        return false;
    }
}
