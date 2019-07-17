package com.example.xyzreader2.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;

import timber.log.Timber;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class NetConnectionUtil {
    public static boolean isNetConnection(@NonNull Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null || !ni.isConnected()) {
            Timber.e("Not online, not refreshing.");
            return false;
        }
        return true;
    }
}
