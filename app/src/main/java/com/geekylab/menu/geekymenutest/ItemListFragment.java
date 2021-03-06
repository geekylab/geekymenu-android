package com.geekylab.menu.geekymenutest;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.geekylab.menu.geekymenutest.adapters.ItemAdapter;
import com.geekylab.menu.geekymenutest.db.entity.ItemEntity;
import com.geekylab.menu.geekymenutest.db.entity.ItemImageEntity;
import com.geekylab.menu.geekymenutest.dummy.DummyContent;
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

public class ItemListFragment extends AbstractBaseListFragment implements
        AbsListView.OnItemClickListener,
        IFTaskCallback {

    private static final String ARG_CATEGORY_ID = "category_id";
    private static final String ARG_STORE_ID = "store_id";
    private static final String TAG = ItemListFragment.class.getSimpleName();
    private final String defaultLanguage;
    private String mCategoryId;
    private String mStoreId;
    private OnFragmentInteractionListener mListener;
    private ArrayList<ItemEntity> m_parts = new ArrayList<ItemEntity>();

    public static ItemListFragment newInstance(String store_id, String categoryId) {
        ItemListFragment fragment = new ItemListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY_ID, categoryId);
        args.putString(ARG_STORE_ID, store_id);
        fragment.setArguments(args);
        return fragment;
    }

    public ItemListFragment() {
        defaultLanguage = Locale.getDefault().getLanguage();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mCategoryId = getArguments().getString(ARG_CATEGORY_ID);
            mStoreId = getArguments().getString(ARG_STORE_ID);
        }

        String url = Params.OPEN_API_ITEM_URL + "/" + mStoreId + "?l=" + defaultLanguage;
        ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("category_id", mCategoryId));
        new DownloadJsonAsyncTaskHelper(getActivity(), this, HttpPost.METHOD_NAME, postParameters)
                .execute(url);
    }

    public BaseAdapter getListAdapter() {
        return new ItemAdapter(getActivity(), R.layout.item_list_row, m_parts);
    }

//    @Override
//    protected int getLayout() {
//        return R.layout.fragment_itemlist;
//    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            mListener.onFragmentInteraction(m_parts.get(position));
        }
    }

    @Override
    public void onFinish(Object obj) {
        JSONObject jsonObject = (JSONObject) obj;
        if (jsonObject != null) {
            Boolean status;
            try {
                status = jsonObject.getBoolean("status");
                if (status) {
                    JSONArray data = jsonObject.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject item = data.getJSONObject(i);
                        String id = item.getString("_id");
                        String name = item.getString("name");
                        String desc = item.getString("desc");
                        JSONArray imagesArray = item.getJSONArray("images");

                        ArrayList<ItemImageEntity> imagesUrls = new ArrayList<ItemImageEntity>();
                        for (int j = 0; j < imagesArray.length(); j++) {
                            JSONObject imageObj = imagesArray.getJSONObject(j);
                            ItemImageEntity imageEntity = new ItemImageEntity();
                            if (imageObj.has("image")) {
                                imageEntity.setUrl(Params.OPEN_API_IMAGE_URL + "/" + imageObj.getString("image"));
                                imageEntity.setId(imageObj.getString("image"));
                                if (imageObj.has("filename"))
                                    imageEntity.setName(imageObj.getString("filename"));
                                if (imageObj.has("image_type"))
                                    imageEntity.setImageType(imageObj.getInt("image_type"));

                                imagesUrls.add(imageEntity);
                            }

                        }

                        String time = item.getString("time");
                        Double price = item.getDouble("price");

                        ItemEntity itemEntity = new ItemEntity();
                        itemEntity
                                .setId(id)
                                .setName(name)
                                .setDesc(desc)
                                .setPrice(price)
                                .setTime(time)
                                .setImageUrls(imagesUrls);

                        m_parts.add(itemEntity);
                    }

                    getListAdapter().notifyDataSetChanged();
                } else {
                    String message = jsonObject.getString("message");
                    if (message == null)
                        message = "Has error";
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(ItemEntity itemEntity);
    }

}
