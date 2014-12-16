package com.geekylab.menu.geekymenutest;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.geekylab.menu.geekymenutest.db.entity.StoreEntity;
import com.geekylab.menu.geekymenutest.db.entity.StoreImageEntity;
import com.geekylab.menu.geekymenutest.db.table.StoreCacheTable;
import com.geekylab.menu.geekymenutest.db.table.StoreImagesCacheTable;
import com.geekylab.menu.geekymenutest.dialog.OnTheTableFragmentDialog;
import com.geekylab.menu.geekymenutest.network.DownloadJsonAsyncTaskHelper;
import com.geekylab.menu.geekymenutest.network.IFTaskCallback;
import com.geekylab.menu.geekymenutest.network.ImageDownloader;
import com.geekylab.menu.geekymenutest.openapi.Params;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class StoreFragment extends DebugFragment implements IFTaskCallback {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_CURRENT_STORE_ID = "current_store_id";
    private static final String ARG_CURRENT_TABLE_ID = "current_table_id";
    private static final String TAG = "StoreFragment";

    private final ImageDownloader imageDownloader = new ImageDownloader();
    private final String defaultLanguage;

    private String mStoreID;
    private String mTableID;
    protected View inflate;
    private ImageLoader loader;
    private StoreCacheTable mStoreCacheTable;
    private StoreImagesCacheTable mStoreImagesCacheTable;

    public static StoreFragment newInstance(int sectionNumber, String storeId, String TableId) {
        StoreFragment fragment = new StoreFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString(ARG_CURRENT_STORE_ID, storeId);
        args.putString(ARG_CURRENT_TABLE_ID, TableId);
        fragment.setArguments(args);
        return fragment;
    }


    public StoreFragment() {
        // Required empty public constructor
        imageDownloader.setMode(ImageDownloader.Mode.NO_DOWNLOADED_DRAWABLE);
        defaultLanguage = Locale.getDefault().getLanguage();
    }

    @Override
    String getClassTag() {
        return TAG;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        loader = ImageLoader.getInstance();
        if (getArguments() != null) {
            mStoreID = getArguments().getString(ARG_CURRENT_STORE_ID);
            mTableID = getArguments().getString(ARG_CURRENT_TABLE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        inflate = inflater.inflate(R.layout.fragment_store, container, false);

        mStoreCacheTable = StoreCacheTable.getInstance(getActivity());
        mStoreImagesCacheTable = StoreImagesCacheTable.getInstance(getActivity());

        View onTheTableButton = inflate.findViewById(R.id.imOnTheTableButton);

        if (onTheTableButton != null) {
            onTheTableButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadByNetwork();
//                    OnTheTableFragmentDialog onTheTableFragmentDialog = OnTheTableFragmentDialog.newInstance(mStoreID, mTableID);
//                    onTheTableFragmentDialog.show(getFragmentManager(), getString(R.string.im_on_the_table));
                }
            });
        } else {
            Log.d(TAG, "Button is null");
        }


        return inflate;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Cursor StoreCursor = mStoreCacheTable.findById(mStoreID);
        if (StoreCursor.getCount() == 0) {
            Log.d(TAG, "Load from network");
            loadByNetwork();
        } else {
            Log.d(TAG, "load from local");
            StoreCursor.moveToFirst();
            StoreEntity storeEntity = new StoreEntity();
            storeEntity.setId(StoreCursor.getString(StoreCursor.getColumnIndex(StoreCacheTable.COL_ID)));
            storeEntity.setStoreName(StoreCursor.getString(StoreCursor.getColumnIndex(StoreCacheTable.COL_STORE_NAME)));
            storeEntity.setAddress1(StoreCursor.getString(StoreCursor.getColumnIndex(StoreCacheTable.COL_ADDRESS1)));
            storeEntity.setDescription(StoreCursor.getString(StoreCursor.getColumnIndex(StoreCacheTable.COL_DESCRIPTION)));
            storeEntity.setEndOpeningHour(StoreCursor.getString(StoreCursor.getColumnIndex(StoreCacheTable.COL_END_OPENING_HOUR)));
            storeEntity.setStartOpeningHour(StoreCursor.getString(StoreCursor.getColumnIndex(StoreCacheTable.COL_START_OPENING_HOUR)));
            storeEntity.setLastOrderTime(StoreCursor.getString(StoreCursor.getColumnIndex(StoreCacheTable.COL_LAST_ORDER_TIME)));
            storeEntity.setTel(StoreCursor.getString(StoreCursor.getColumnIndex(StoreCacheTable.COL_TEL)));

            //load images
            Cursor imagesCursor = mStoreImagesCacheTable.findByStoreId(mStoreID);
            ArrayList<StoreImageEntity> storeImageEntityList = new ArrayList<StoreImageEntity>();
            if (imagesCursor.getCount() > 0) {
                while (imagesCursor.moveToNext()) {
                    StoreImageEntity imageEntity = new StoreImageEntity();
                    imageEntity.setId(imagesCursor.getString(imagesCursor.getColumnIndex(StoreImagesCacheTable.COL_ID)));
                    imageEntity.setImageUrl(imagesCursor.getString(imagesCursor.getColumnIndex(StoreImagesCacheTable.COL_IMAGE_URL)));
                    imageEntity.setStoreId(imagesCursor.getString(imagesCursor.getColumnIndex(StoreImagesCacheTable.COL_STORE_ID)));
                    storeImageEntityList.add(imageEntity);
                }
                storeEntity.setImages(storeImageEntityList);
            }

            Log.d(TAG, storeEntity.toString());
            setUpView(storeEntity);
        }
    }

    private void loadByNetwork() {
        String url = Params.OPEN_API_STORE_URL + "/" + mStoreID + "?l=" + defaultLanguage;
        new DownloadJsonAsyncTaskHelper(getActivity(), this, HttpPost.METHOD_NAME).execute(url);
    }

    @Override
    public void onFinish(Object obj) {
        JSONObject jsonObject = (JSONObject) obj;
        if (jsonObject != null) {
            Boolean status;
            try {
                Log.d(TAG, jsonObject.toString());
                status = jsonObject.getBoolean("status");
                if (status && jsonObject.has("data")) {
                    JSONObject storeData = jsonObject.getJSONObject("data");
                    StoreEntity storeEntity = new StoreEntity();

                    if (storeData.has("_id"))
                        storeEntity.setId(storeData.getString("_id"));

                    if (storeData.has("store_name"))
                        storeEntity.setStoreName(storeData.getString("store_name"));

                    if (storeData.has("desc"))
                        storeEntity.setDescription(storeData.getString("desc"));

                    if (storeData.has("images")) {
                        JSONArray imagesArray = storeData.getJSONArray("images");
                        ArrayList<StoreImageEntity> imagesUrls = new ArrayList<StoreImageEntity>();
                        for (int j = 0; j < imagesArray.length(); j++) {
                            StoreImageEntity imageEntity = new StoreImageEntity();
                            imageEntity.setId(imagesArray.get(j).toString());
                            imageEntity.setImageUrl(Params.OPEN_API_IMAGE_URL + "/" + imagesArray.get(j).toString());
                            imageEntity.setStoreId(mStoreID);
                            imagesUrls.add(imageEntity);
//                            imagesUrls.add(Params.OPEN_API_IMAGE_URL + "/" + imagesArray.get(j).toString());
                        }
                        storeEntity.setImages(imagesUrls);
                        StoreCacheTable storeCacheTable = StoreCacheTable.getInstance(StoreFragment.this.getActivity());
                        storeCacheTable.save(storeEntity);
                    }

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
                ArrayList<StoreImageEntity> images = storeEntity.getImages();
                if (images != null && images.size() > 0) {
                    String categoryImageUrl = images.get(0).getImageUrl();
                    Log.d(TAG, "Image url : " + categoryImageUrl);
//                    headerImageView.setTag(categoryImageUrl);
                    loader.displayImage(categoryImageUrl, headerImageView);
                }
            }
        } else {
            Log.d(TAG, "inflate is null");
        }
    }
}
