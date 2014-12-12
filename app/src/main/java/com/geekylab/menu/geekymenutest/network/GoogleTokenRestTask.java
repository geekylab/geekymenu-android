package com.geekylab.menu.geekymenutest.network;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.geekylab.menu.geekymenutest.openapi.Params;
import com.google.android.gms.auth.GoogleAuthUtil;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by johna on 12/12/14.
 * Kodokux System
 */
public class GoogleTokenRestTask extends AsyncTask<Void, Void, String> {
    private static final String TAG = RestTask.class.getSimpleName();
    public static final String HTTP_RESPONSE = "GoogleTokenRestTask";
    private static final String SCOPES = "oauth2:profile email";
    private final Context mContext;
    private final HttpClient mClient;
    private final String mAction;
    private final String mAccountName;


    public GoogleTokenRestTask(Context context, String action, String accountName) {
        mContext = context;
        mAction = action;
        mClient = new DefaultHttpClient();
        mAccountName = accountName;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {

            String token = GoogleAuthUtil.getToken(mContext, mAccountName, SCOPES);
            if (!token.isEmpty()) {
                //GeekyMenu url
                HttpPost httpPost = new HttpPost(new URI(Params.HOST_URL + "/auth/google-token"));
                List<NameValuePair> paramaters = new ArrayList<NameValuePair>();
                paramaters.add(new BasicNameValuePair("access_token", token));
                httpPost.setEntity(new UrlEncodedFormEntity(paramaters));

                HttpResponse httpResponse = mClient.execute(httpPost);
                BasicResponseHandler handler = new BasicResponseHandler();
                return handler.handleResponse(httpResponse);

            } else {
                return null;
            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "RestTask", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        Intent intent = new Intent(mAction);
        intent.putExtra(HTTP_RESPONSE, result);
        mContext.sendBroadcast(intent);
    }
}
