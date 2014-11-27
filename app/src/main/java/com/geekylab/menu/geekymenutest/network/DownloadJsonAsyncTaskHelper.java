package com.geekylab.menu.geekymenutest.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.geekylab.menu.geekymenutest.download.JSONParser;

import org.apache.http.client.methods.HttpGet;
import org.json.JSONObject;

/**
 * Created by johna on 25/11/14.
 *
 */
public class DownloadJsonAsyncTaskHelper extends AsyncTask<String, Integer, JSONObject>
        implements DialogInterface.OnCancelListener {

    private static final String TAG = "DownloadJsonAsyncTaskHelper";
    private final IFTaskCallback callback;
    private final String method;
    private ProgressDialog dialog;
    private final Context context;
    private final JSONParser jsonParse;

    public DownloadJsonAsyncTaskHelper(Context context, IFTaskCallback callback, String method) {
        this.context = context;
        this.jsonParse = new JSONParser();
        this.callback = callback;
        this.method = method;
    }

    public DownloadJsonAsyncTaskHelper(Context context, IFTaskCallback callback) {
        this(context, callback, HttpGet.METHOD_NAME);
    }

    @Override
    protected void onPreExecute() {
        Log.d(TAG, "onPreExecute");
        dialog = new ProgressDialog(context);
        dialog.setTitle("Please wait");
        dialog.setMessage("Loading data...");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(this);
        dialog.setMax(100);
        dialog.setProgress(0);
        dialog.show();
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        String url = params[0];
        Log.d(TAG, "doInBackground - " + params[0]);

        publishProgress(100);
        return this.jsonParse.getJSONFromUrl(url, method);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
//        Log.d(TAG, "onProgressUpdate - " + values[0]);
        dialog.setProgress(values[0]);
    }

    @Override
    protected void onCancelled() {
        Log.d(TAG, "onCancelled");
        dialog.dismiss();
    }


    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        dialog.dismiss();
        this.callback.onFinish(jsonObject);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        Log.d(TAG, "Dialog onCancell... calling cancel(true)");
        this.cancel(true);
    }
}
