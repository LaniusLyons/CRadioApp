package com.passeapp.dark_legion.cradioapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentTabContact.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentTabContact#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentTabContact extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EditText contactEmail;
    private EditText contactFirstName;
    private EditText contactLastName;
    private EditText contactMessage;
    private Button btnSend;
    private Button btnCancel;
    public ProgressDialog progressDialog;

    private OnFragmentInteractionListener mListener;

    public FragmentTabContact() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentTabContact.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentTabContact newInstance(String param1, String param2) {
        FragmentTabContact fragment = new FragmentTabContact();
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
        View fragmentView = inflater.inflate(R.layout.fragment_fragment_tab_contact, container, false);
        this.contactEmail = (EditText) fragmentView.findViewById(R.id.register_email);
        this.contactFirstName = (EditText) fragmentView.findViewById(R.id.register_first_name);
        this.contactLastName = (EditText) fragmentView.findViewById(R.id.register_last_name);
        this.contactMessage = (EditText) fragmentView.findViewById(R.id.register_message);
        this.btnSend = (Button) fragmentView.findViewById(R.id.btnSend);
        this.btnCancel = (Button) fragmentView.findViewById(R.id.btnCancel);

        init();

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
        String firstName = this.contactFirstName.getText().toString();
        String lastName = this.contactLastName.getText().toString();
        String message = this.contactMessage.getText().toString();

        if(!email.isEmpty() && !email.equals(" ") && isValidEmail(email)){
            validForm = true;
        }else{
            validForm = false;
            this.contactEmail.setError("Este campo es obligatorio y debe ser un email válido");
        }

        if(!firstName.isEmpty() && !firstName.equals(" ")){
            validForm = true;
        }else{
            validForm = false;
            this.contactFirstName.setError("Este campo es obligatorio");
        }

        if(!lastName.isEmpty() && !lastName.equals(" ")){
            validForm = true;
        }else{
            validForm = false;
            this.contactLastName.setError("Este campo es obligatorio");
        }

        if(!message.isEmpty() && !message.equals(" ")){
            validForm = true;
        }else{
            validForm = false;
            this.contactMessage.setError("Este campo es obligatorio");
        }

        if(validForm){
            new ContactTask().execute(email,firstName,lastName,message);
        }else{
            Toast.makeText(getContext(),"TODOS LOS CAMPOS SON REQUERIDOS",Toast.LENGTH_LONG).show();
        }
    }


    public void init(){
        this.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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


    public ProgressDialog createDialog(String message){
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(message);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        return  progressDialog;
    }


    private class ContactTask extends AsyncTask<String,Integer,Boolean> {

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
                Toast.makeText(getContext(),"Mensaje enviado exitosamente",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getContext(),"Error al enviar el mensaje",Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            String email = strings[0];
            String firstName = strings[1];
            String lastName = strings[2];
            String message = strings[3];
            return sendingContactForm(email,firstName,lastName,message);
        }

        protected boolean sendingContactForm(String email, String firstName, String lastName, String message){
            try{
                URL url = new URL(MainActivity.contactLink);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("USER-AGENT","Mozilla/5.0");
                connection.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("email", email)
                        .appendQueryParameter("first_name", firstName)
                        .appendQueryParameter("last_name", lastName)
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
        this.contactFirstName.setText("");
        this.contactLastName.setText("");
        this.contactMessage.setText("");
    }
}
