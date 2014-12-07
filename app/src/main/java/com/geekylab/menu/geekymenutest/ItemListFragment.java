package com.geekylab.menu.geekymenutest;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
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

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class ItemListFragment extends Fragment implements
        AbsListView.OnItemClickListener,
        IFTaskCallback {

    private static final String ARG_CATEGORY_ID = "category_id";
    private static final String ARG_STORE_ID = "store_id";
    private static final String TAG = ItemListFragment.class.getSimpleName();
    private final String defaultLanguage;

    private String mCategoryId;
    private String mStoreId;

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ItemAdapter mAdapter;

    private ArrayList<ItemEntity> m_parts = new ArrayList<ItemEntity>();

    public static ItemListFragment newInstance(String store_id, String categoryId) {
        ItemListFragment fragment = new ItemListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY_ID, categoryId);
        args.putString(ARG_STORE_ID, store_id);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
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

        mAdapter = new ItemAdapter(getActivity(), R.layout.item_list_row, m_parts);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_itemlist, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

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
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
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

                    mAdapter.notifyDataSetChanged();
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(String id);
    }

}
