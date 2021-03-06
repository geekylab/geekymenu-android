package com.geekylab.menu.geekymenutest.dialog;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.geekylab.menu.geekymenutest.R;
import com.geekylab.menu.geekymenutest.db.table.OrderTable;
import com.geekylab.menu.geekymenutest.network.DownloadJsonAsyncTaskHelper;
import com.geekylab.menu.geekymenutest.network.IFTaskCallback;
import com.geekylab.menu.geekymenutest.openapi.Params;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link DialogFragment} subclass.
 * Use the {@link OnTheTableFragmentDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OnTheTableFragmentDialog extends DialogFragment implements IFTaskCallback {
    private static final String TAG = OnTheTableFragmentDialog.class.getSimpleName();
    private static final String ARG_STORE_ID = "store_id";
    private static final String ARG_TABLE_ID = "table_id";
    private String mStoreID;
    private String mTableID;
    private Activity activity;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment OnTheTableFragmentDialog.
     */
    public static OnTheTableFragmentDialog newInstance(String store_id, String table_id) {
        OnTheTableFragmentDialog fragment = new OnTheTableFragmentDialog();
        Bundle args = new Bundle();
        args.putString(ARG_STORE_ID, store_id);
        args.putString(ARG_TABLE_ID, table_id);
        fragment.setArguments(args);
        return fragment;
    }

    public OnTheTableFragmentDialog() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        CharSequence[] items = {"使い方", "よくある質問", "メール", "閉じる"};

        if (getArguments() != null) {
            mStoreID = getArguments().getString(ARG_STORE_ID);
            mTableID = getArguments().getString(ARG_TABLE_ID);
        }

        activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        final LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View content = inflater.inflate(R.layout.fragment_on_the_table_fragment_dialog, null);
        final EditText tableTokenEditText = (EditText) content.findViewById(R.id.table_token);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String service_token = prefs.getString("service_token", null);

        builder.setView(content);

        builder.setMessage(getString(R.string.im_on_the_table))
                .setNegativeButton(getString(R.string.close), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                })
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String url = Params.OPEN_API_ORDER_URL + "/" + mStoreID;
                        ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
                        postParameters.add(new BasicNameValuePair("table_token", tableTokenEditText.getText().toString()));
                        postParameters.add(new BasicNameValuePair("table_id", mTableID));
                        postParameters.add(new BasicNameValuePair("service_token", service_token));
                        new DownloadJsonAsyncTaskHelper(activity, OnTheTableFragmentDialog.this, HttpPost.METHOD_NAME, postParameters)
                                .execute(url);
                    }
                });
        return builder.create();
    }

    @Override
    public void onFinish(Object obj) {
        JSONObject jsonObject = (JSONObject) obj;
        if (jsonObject != null) {
            try {
                if (jsonObject.has("status") && jsonObject.getBoolean("status")) {
                    Toast.makeText(activity, "get response from cloud true", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, jsonObject.toString());
                    if (jsonObject.has("data")) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        if (data.getBoolean("is_new")) {
                            JSONObject orderJsonObject = data.getJSONObject("order");
                            //getActivity().deleteDatabase(MySQLiteOpenHelper.DB);
                            OrderTable orderTable = new OrderTable(activity);
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(OrderTable.COL_ORDER_TOKEN, orderJsonObject.getString("order_token"));
                            contentValues.put(OrderTable.COL_ORDER_NUMBER, orderJsonObject.getString("order_token"));
                            contentValues.put(OrderTable.COL_STORE_ID, mStoreID);
                            contentValues.put(OrderTable.COL_TABLE_ID, mTableID);
                            contentValues.put(OrderTable.COL_STATUS, orderJsonObject.getString("status"));
                            orderTable.insert(contentValues);
                        } else {

                        }
                    } else {
                        //TODO: data is ivalid.
                        Log.d(TAG, jsonObject.toString());
                    }
                } else {
                    //TODO: check in error
                    Log.d(TAG, jsonObject.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
