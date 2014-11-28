package com.geekylab.menu.geekymenutest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.geekylab.menu.geekymenutest.adapters.GlobalCategoryAdapter;
import com.geekylab.menu.geekymenutest.adapters.StoreCategoryAdapter;
import com.geekylab.menu.geekymenutest.db.entity.GlobalCategoryEntity;
import com.geekylab.menu.geekymenutest.db.entity.StoreCategoryEntity;
import com.geekylab.menu.geekymenutest.db.entity.StoreEntity;
import com.geekylab.menu.geekymenutest.network.DownloadJsonAsyncTaskHelper;
import com.geekylab.menu.geekymenutest.network.IFTaskCallback;
import com.geekylab.menu.geekymenutest.utils.NetworkUtil;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by johna on 26/11/14.
 * Kodokux System
 */
public class StoreCategoryListFragment extends AbstractBaseListFragment implements IFTaskCallback {
    private static final String TAG = "StoreCategoryListFragment";
    private static final String ARG_CURRENT_STORE_ID = "current_store_id";
    private static final String ARG_CURRENT_CATEGORY_ID = "current_category_id";
    private final String OPEN_API_CATEGORY = "http://192.168.111.103:8080/open-api/category";
    private final String defaultLanguage;
    private ArrayList<StoreCategoryEntity> m_parts = new ArrayList<StoreCategoryEntity>();
    private static final String ARG_SECTION_NUMBER = "section_number";
    private StoreCategoryAdapter mListAdapter = null;
    private String mStoreID;

    public static StoreCategoryListFragment newInstance(int sectionNumber, String storeId) {
        StoreCategoryListFragment fragment = new StoreCategoryListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString(ARG_CURRENT_STORE_ID, storeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mStoreID = getArguments().getString(ARG_CURRENT_STORE_ID);
        }
    }

    public StoreCategoryListFragment() {
        defaultLanguage = Locale.getDefault().getLanguage();
    }

    @Override
    public BaseAdapter getListAdapter() {
        if (mListAdapter == null) {
            mListAdapter = new StoreCategoryAdapter(this.getActivity(), R.layout.category_list_item, m_parts);
        }
        return mListAdapter;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (NetworkUtil.netWorkCheck(getActivity())) {
            String url = OPEN_API_CATEGORY + "/" + mStoreID + "?l=" + defaultLanguage;
            Log.d(TAG, url);
            new DownloadJsonAsyncTaskHelper(getActivity(), this, HttpPost.METHOD_NAME).execute(url);
        } else {
            Toast.makeText(getActivity(), "network off", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        StoreCategoryEntity categoryEntity = (StoreCategoryEntity) parent.getItemAtPosition(position);
        if (categoryEntity != null) {
            Intent intent = new Intent(this.getActivity(), ItemActivity.class);
            intent.putExtra(ARG_CURRENT_STORE_ID, mStoreID);
            intent.putExtra(ARG_CURRENT_CATEGORY_ID, categoryEntity.getId());
            startActivity(intent);
        }

//        if (null != mListener) {
//
//            mListener.onFragmentInteraction(1);
//        }
    }

    @Override
    public void onFinish(Object obj) {
        if (obj != null) {
            JSONObject jsonObject = (JSONObject) obj;
            try {
                Log.d(TAG, jsonObject.toString());
                JSONArray data = jsonObject.getJSONArray("data");

                if (data.length() > 0) {
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject categoryObject = data.getJSONObject(i);
                        Log.d(TAG, categoryObject.getString("name"));
                        StoreCategoryEntity entity = new StoreCategoryEntity();
                        entity.setId(categoryObject.getString("_id"));
                        entity.setName(categoryObject.getString("name"));
                        if (categoryObject.has("image_url"))
                            entity.setImageUrl(categoryObject.getString("image_url"));
                        m_parts.add(entity);
                    }
                }
                getListAdapter().notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
