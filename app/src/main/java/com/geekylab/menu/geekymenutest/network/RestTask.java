package com.geekylab.menu.geekymenutest.network;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Created by johna on 12/12/14.
 * Kodokux System
 */
public class RestTask extends AsyncTask<HttpUriRequest, Void, String> {
    private static final String TAG = RestTask.class.getSimpleName();
    public static final String HTTP_RESPONSE = "httpResponse";
    private Context mContext;
    private HttpClient mClient;
    private String mAction;

    public RestTask(Context context, String action) {
        mContext = context;
        mAction = action;
        mClient = new DefaultHttpClient();
    }

    @Override
    protected String doInBackground(HttpUriRequest... params) {
        try {
            HttpUriRequest request = params[0];
            HttpResponse httpResponse = mClient.execute(request);
            BasicResponseHandler handler = new BasicResponseHandler();
            return handler.handleResponse(httpResponse);
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
