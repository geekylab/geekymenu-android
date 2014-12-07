package com.geekylab.menu.geekymenutest;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.geekylab.menu.geekymenutest.adapters.ItemAdapter;
import com.geekylab.menu.geekymenutest.db.entity.ItemEntity;
import com.geekylab.menu.geekymenutest.db.entity.ItemImageEntity;
import com.geekylab.menu.geekymenutest.network.DownloadJsonAsyncTaskHelper;
import com.geekylab.menu.geekymenutest.network.IFTaskCallback;
import com.geekylab.menu.geekymenutest.openapi.Params;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;


public class ItemActivity extends Activity implements
        ItemListFragment.OnFragmentInteractionListener,
        ItemAdapter.OnItemOrderListener {
    private static final String ARG_CURRENT_STORE_ID = "current_store_id";
    private static final String ARG_CURRENT_CATEGORY_ID = "current_category_id";
    private static final String TAG = "ItemActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        Intent intent = getIntent();
        String mStoreId = intent.getStringExtra(ARG_CURRENT_STORE_ID);
        String mCategoryId = intent.getStringExtra(ARG_CURRENT_CATEGORY_ID);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, ItemListFragment.newInstance(mStoreId, mCategoryId))
                    .commit();
        }
    }

    @Override
    public void onFragmentInteraction(String id) {

    }

    @Override
    public void onClickOrderButton(ItemEntity itemEntity) {
        Log.d(TAG, itemEntity.getName());
    }
}
