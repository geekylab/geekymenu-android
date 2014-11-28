package com.geekylab.menu.geekymenutest;


import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.geekylab.menu.geekymenutest.db.entity.StoreEntity;
import com.geekylab.menu.geekymenutest.network.DownloadJsonAsyncTaskHelper;
import com.geekylab.menu.geekymenutest.network.IFTaskCallback;
import com.geekylab.menu.geekymenutest.network.ImageDownloader;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class StoreFragment extends Fragment implements IFTaskCallback {

    private static final String OPEN_API_STORE_URL = "http://192.168.111.103:8080/open-api/store";
    private static final String OPEN_API_IMAGES = "http://192.168.111.103:8080/open-api/image";
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_CURRENT_STORE_ID = "current_store_id";
    private static final String TAG = "StoreFragment";
    private final ImageDownloader imageDownloader = new ImageDownloader();
    private final String defaultLanguage;

    private String mStoreID;
    protected View inflate;

    public static StoreFragment newInstance(int sectionNumber, String storeId) {
        StoreFragment fragment = new StoreFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString(ARG_CURRENT_STORE_ID, storeId);
        fragment.setArguments(args);
        return fragment;
    }


    public StoreFragment() {
        // Required empty public constructor
        imageDownloader.setMode(ImageDownloader.Mode.NO_DOWNLOADED_DRAWABLE);
        defaultLanguage = Locale.getDefault().getLanguage();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mStoreID = getArguments().getString(ARG_CURRENT_STORE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        inflate = inflater.inflate(R.layout.fragment_store, container, false);

        String url = OPEN_API_STORE_URL + "/" + mStoreID + "?l=" + defaultLanguage;
        Log.d(TAG, "store request url : " + url);
        new DownloadJsonAsyncTaskHelper(getActivity(), this, HttpPost.METHOD_NAME).execute(url);

        return inflate;
    }


    @Override
    public void onFinish(Object obj) {
        JSONObject jsonObject = (JSONObject) obj;
        if (jsonObject != null) {
            Boolean status;
            try {
                Log.d(TAG, jsonObject.toString());
                status = jsonObject.getBoolean("status");
                if (status) {
                    JSONObject storeData = jsonObject.getJSONObject("data");
                    StoreEntity storeEntity = new StoreEntity();
                    storeEntity.setId(storeData.getString("_id"));
                    storeEntity.setStoreName(storeData.getString("store_name"));
                    storeEntity.setDescription(storeData.getString("desc"));

                    JSONArray imagesArray = storeData.getJSONArray("images");
                    ArrayList<String> imagesUrls = new ArrayList<String>();
                    for (int j = 0; j < imagesArray.length(); j++) {
                        imagesUrls.add(OPEN_API_IMAGES + "/" + imagesArray.get(j).toString());
                    }
                    storeEntity.setImages(imagesUrls);

                    setUpView(storeEntity);
                } else {
                    //TODO: STATUS false error
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void setUpView(StoreEntity storeEntity) {
        if (getView() != null) {
            Log.d(TAG, "setUpView");

            TextView storeNameTextView = (TextView) getView().findViewById(R.id.storeNameTextView);
            storeNameTextView.setText(storeEntity.getStoreName());

            //desc
            TextView storeDescTextView = (TextView) getView().findViewById(R.id.descTextView);
            storeDescTextView.setText(storeEntity.getDescription());

            ImageView headerImageView = (ImageView) getView().findViewById(R.id.headerImageView);
            if (headerImageView != null) {
                ArrayList<String> images = storeEntity.getImages();
                if (images.size() > 0) {
                    String categoryImageUrl = images.get(0);
                    Log.d(TAG, "Image url : " + categoryImageUrl);
//                    headerImageView.setTag(categoryImageUrl);
                    imageDownloader.download(categoryImageUrl, headerImageView);
                }
            }
        } else {
            Log.d(TAG, "inflate is null");
        }
    }
}
