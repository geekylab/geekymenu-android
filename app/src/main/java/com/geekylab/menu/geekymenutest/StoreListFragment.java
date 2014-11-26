package com.geekylab.menu.geekymenutest;

import android.os.Bundle;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.geekylab.menu.geekymenutest.adapters.GlobalCategoryAdapter;
import com.geekylab.menu.geekymenutest.network.DownloadJsonAsyncTaskHelper;
import com.geekylab.menu.geekymenutest.network.IFTaskCallback;
import com.geekylab.menu.geekymenutest.utils.NetworkUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by johna on 26/11/14.
 * Kodokux System
 */
public class StoreListFragment extends AbstractBaseListFragment implements IFTaskCallback {
    private static final String TAG = "StoreListFragment";
    private final String OPEN_API_GLOBAL_CATEGORY = "http://192.168.111.103:8080/open-api/global-category";
    private ArrayList<JSONObject> m_parts = new ArrayList<JSONObject>();
    private static final String ARG_SECTION_NUMBER = "section_number";
    private GlobalCategoryAdapter mListAdapter = null;

    public static StoreListFragment newInstance(int sectionNumber) {
        StoreListFragment fragment = new StoreListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public BaseAdapter getListAdapter() {
        if (mListAdapter == null) {
            mListAdapter = new GlobalCategoryAdapter(this.getActivity(), R.layout.category_list_item, m_parts);
        }
        return mListAdapter;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (NetworkUtil.netWorkCheck(getActivity())) {
            new DownloadJsonAsyncTaskHelper(getActivity(), this).execute(OPEN_API_GLOBAL_CATEGORY);
        } else {
            Toast.makeText(getActivity(), "network off", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFinish(Object obj) {
        if (obj != null) {
            JSONObject jsonObject = (JSONObject) obj;
            try {
                JSONArray datas = jsonObject.getJSONArray("data");
                if (datas.length() > 0) {
                    for (int i = 0; i < datas.length(); i++) {
                        JSONObject categoryObject = datas.getJSONObject(i);
                        m_parts.add(categoryObject);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            getListAdapter().notifyDataSetChanged();
        }
    }
}
