package com.puristit.livechat.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import com.puristit.livechat.activities.MainActivity;

/**
 * Created by Anas on 12/24/2016.
 */

public class MyUtils {


    public static void setSharedPrefString(Context context, String key, String value){
        SharedPreferences oSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor oEditor = oSharedPreferences.edit();
        oEditor.putString(key, value).commit();
    }


    public static String getSharedPrefString(Context context, String key, String defValue){
        SharedPreferences oSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return oSharedPreferences.getString(key, defValue);
    }


    public static boolean haveNetworkConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        if (info != null && info.isConnected()) {
            return true;
        }
        return false;
    }


}

