package com.geekylab.menu.geekymenutest;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.geekylab.menu.geekymenutest.adapters.GlobalCategoryAdapter;
import com.geekylab.menu.geekymenutest.db.entity.GlobalCategoryEntity;
import com.geekylab.menu.geekymenutest.dummy.DummyContent;
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
public class GlobalCategoryListFragment extends AbstractBaseListFragment implements IFTaskCallback {
    private static final String TAG = "GlobalCategoryListFragment";
    private final String OPEN_API_GLOBAL_CATEGORY = "http://192.168.111.103:8080/open-api/global-category";
    private ArrayList<GlobalCategoryEntity> m_parts = new ArrayList<GlobalCategoryEntity>();
    private static final String ARG_SECTION_NUMBER = "section_number";
    private GlobalCategoryAdapter mListAdapter = null;

    public static GlobalCategoryListFragment newInstance(int sectionNumber) {
        GlobalCategoryListFragment fragment = new GlobalCategoryListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public BaseAdapter getListAdapter() {
        if (mListAdapter == null) {
            Log.d(TAG, "new GlobalCategoryAdapter");
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            GlobalCategoryEntity itemAtPosition = (GlobalCategoryEntity) parent.getItemAtPosition(position);
            Bundle bundle = new Bundle();
            bundle.putString("store_id", itemAtPosition.getName());

            mListener.onFragmentInteraction(StoreListFragment.newInstance(bundle));
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
                        Log.d(TAG, categoryObject.getString("name"));
                        GlobalCategoryEntity entity = new GlobalCategoryEntity();
                        entity.setName(categoryObject.getString("name"));
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
