package com.passeapp.dark_legion.cradioapp;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;



public class OnlineConnectClass {

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }
}