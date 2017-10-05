package com.passeapp.dark_legion.cradioapp;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.squareup.picasso.Picasso;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
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
    private EditText contactEmail;
    private EditText contactSubject;
    private EditText contactMessage;
    private Button btnSend;
    private Button btnCancel;
    public ProgressDialog progressDialog;

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
        this.contactEmail = (EditText) fragmentView.findViewById(R.id.register_email);
        this.contactSubject = (EditText) fragmentView.findViewById(R.id.register_subject);
        this.contactMessage = (EditText) fragmentView.findViewById(R.id.register_message);
        this.btnSend = (Button) fragmentView.findViewById(R.id.btnSend);
        this.btnCancel = (Button) fragmentView.findViewById(R.id.btnCancel);
        this.progressBar = (ProgressBar) fragmentView.findViewById(R.id.progress);

        if(hasPermissions()) {
            executeCachingTask();
        }else{
            requestPerm();
        }
        init();
        return fragmentView;
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public void attemtpToContact(){
        boolean validForm;
        String email = this.contactEmail.getText().toString();
        String subject = this.contactSubject.getText().toString();
        String message = this.contactMessage.getText().toString();

        if(!email.isEmpty() && !email.equals(" ") && isValidEmail(email)){
            validForm = true;
        }else{
            validForm = false;
            this.contactEmail.setError("Este campo es obligatorio y debe ser un email válido");
        }

        if(!subject.isEmpty() && !subject.equals(" ")){
            validForm = true;
        }else{
            validForm = false;
            this.contactSubject.setError("Este campo es obligatorio");
        }

        if(!message.isEmpty() && !message.equals(" ")){
            validForm = true;
        }else{
            validForm = false;
            this.contactMessage.setError("Este campo es obligatorio");
        }

        if(validForm){
            new ContactTask().execute(email,subject,message);
        }else{
            Toast.makeText(getContext(),"TODOS LOS CAMPOS SON REQUERIDOS",Toast.LENGTH_LONG).show();
        }
    }

    public void init(){
        this.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.fragment1.getView().findViewById(R.id.contactFormLayout).setVisibility(View.GONE);
                clearInputs();
            }
        });
        this.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemtpToContact();
            }
        });
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

        final SponsorsClass aux = MainActivity.sponsorsList.get(i);

        final Dialog dialog = new Dialog(getContext(),android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.custome_dialog);
        dialog.setCancelable(true);

        RelativeLayout relativeLayout = (RelativeLayout)dialog.findViewById(R.id.modalParentLayout);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        TextView lblName = (TextView)dialog.findViewById(R.id.lblName);
        TextView lblAddress = (TextView)dialog.findViewById(R.id.lblAddress);
        ImageView sponsorLogo = (ImageView)dialog.findViewById(R.id.sponsorLogo);
        lblName.setText(aux.getTitle());
        lblAddress.setText(aux.getAddress());

        Picasso.with(getContext()).load(aux.getImageLink()).error(R.drawable.ads).into(sponsorLogo);

        Button visitBtn = (Button)dialog.findViewById(R.id.btnVisitUs);
        Button goBtn = (Button)dialog.findViewById(R.id.btnGoMap);
        visitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String link = aux.getLink();

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

    private class CachingSponsorTask extends AsyncTask<ArrayList<SponsorsClass>,Integer,Void>{

        @Override
        protected void onPostExecute(Void aVoid) {
            showProgress(false);
            renderSponsorsLogos();
            MainActivity.is_contact_form_ready = true;
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

    public ProgressDialog createDialog(String message){
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(message);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        return  progressDialog;
    }

    private class ContactTask extends AsyncTask<String,Integer,Boolean>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = createDialog("Enviando...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progressDialog.dismiss();
            if(aBoolean){
                clearInputs();
                MainActivity.fragment1.getView().findViewById(R.id.contactFormLayout).setVisibility(View.GONE);
                Toast.makeText(getContext(),"Mensaje enviado exitosamente",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getContext(),"Error al enviar el mensaje",Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            String email = strings[0];
            String subject = strings[1];
            String message = strings[2];
            return sendingContactForm(email,subject,message);
        }

        protected boolean sendingContactForm(String email, String subject, String message){
            try{
                URL url = new URL(MainActivity.contactLink);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("USER-AGENT","Mozilla/5.0");
                connection.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("email", email)
                        .appendQueryParameter("subject", subject)
                        .appendQueryParameter("message",message);
                String query = builder.build().getEncodedQuery();
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();

                int responseCode = connection.getResponseCode();
                if(responseCode == 200){
                    return true;
                }else{
                    return false;
                }

            }catch (Exception e){
                Log.e("error contact",e.getLocalizedMessage());
            }
            return false;
        }
    }

    public void clearInputs(){
        this.contactEmail.setText("");
        this.contactSubject.setText("");
        this.contactMessage.setText("");
    }
}
