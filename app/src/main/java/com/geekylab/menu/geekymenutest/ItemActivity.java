package com.geekylab.menu.geekymenutest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.geekylab.menu.geekymenutest.adapters.ItemAdapter;
import com.geekylab.menu.geekymenutest.db.entity.ItemEntity;


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
