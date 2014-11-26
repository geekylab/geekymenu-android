package com.geekylab.menu.geekymenutest.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by johna on 26/11/14.
 * Kodokux System
 */
public class NetworkUtil {
    public static boolean netWorkCheck(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null) {
            return info.isConnected();
        } else {
            return false;
        }
    }
}
