package com.geekylab.menu.geekymenutest;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.geekylab.menu.geekymenutest.adapters.StoreAdapter;
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
public class StoreListFragment extends AbstractBaseListFragment implements IFTaskCallback {
    private static final String TAG = "StoreListFragment";
    private final String OPEN_API_STORE_URL = "http://192.168.111.103:8080/open-api/store";
    private final String OPEN_API_IMAGES = "http://192.168.111.103:8080/open-api/image";
    private final String defaultLanguage;
    private ArrayList<StoreEntity> m_parts = new ArrayList<StoreEntity>();
    private static final String ARG_STORE_ID = "store_id";
    private StoreAdapter mListAdapter = null;

    public static StoreListFragment newInstance(Bundle args) {
        StoreListFragment fragment = new StoreListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public StoreListFragment() {
        defaultLanguage = Locale.getDefault().getLanguage();
    }

    @Override
    public BaseAdapter getListAdapter() {
        if (mListAdapter == null) {
            mListAdapter = new StoreAdapter(this.getActivity(), R.layout.store_list_item, m_parts);
        }
        return mListAdapter;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (NetworkUtil.netWorkCheck(getActivity())) {
            String url = OPEN_API_STORE_URL + "?l=" + defaultLanguage;
            Log.d(TAG, url);
            new DownloadJsonAsyncTaskHelper(getActivity(), this, HttpPost.METHOD_NAME).execute(url);
        } else {
            Toast.makeText(getActivity(), "network off", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            StoreEntity itemAtPosition = (StoreEntity) parent.getItemAtPosition(position);
            Bundle bundle = new Bundle();
            bundle.putString("store_id", itemAtPosition.getStoreName());

//            mListener.onFragmentInteraction(bundle, R.layout.fragment_table);
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
                        JSONObject storeObject = datas.getJSONObject(i);
                        Log.d(TAG, storeObject.toString());
                        StoreEntity entity = new StoreEntity();
                        //{"_id":"54774fd56f11c33f0052be24","seat_count":150,"tel":"12346","desc":"test","store_name":"コドックスシステム","user":"54774fa66f11c33f0052be23","org_id":"54774fd33e0895110043abac","__v":0,"created":"2014-11-27T16:22:43.433Z","opts":[],"images":["54774fd56f11c33f0052be25"],"seat_type":[],"opening_hour":{"start":"15:15","end":"15:15","last_order":"15:15"}
                        entity.setId(storeObject.getString("_id"));
                        entity.setStoreName(storeObject.getString("store_name"));
                        entity.setSeatCount(storeObject.getInt("seat_count"));
                        entity.setTel(storeObject.getString("tel"));
                        entity.setAddress1(storeObject.getString("address"));

                        JSONArray imagesArray = storeObject.getJSONArray("images");
                        ArrayList<String> imagesUrls = new ArrayList<String>();
                        for (int j = 0; j < imagesArray.length(); j++) {
                            imagesUrls.add(OPEN_API_IMAGES + "/" + imagesArray.get(j).toString());
                        }
                        entity.setImages(imagesUrls);

                        if (storeObject.has("opening_hour")) {
                            JSONObject openingHour = storeObject.getJSONObject("opening_hour");
                            if (openingHour.has("start")) {
                                entity.setStartOpeningHour(openingHour.getString("start"));
                            }

                            if (openingHour.has("end")) {
                                entity.setEndOpeningHour(openingHour.getString("end"));
                            }

                            if (openingHour.has("last_order")) {
                                entity.setLastOrderTime(openingHour.getString("last_order"));
                            }
                        }
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
