package com.geekylab.menu.geekymenutest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.geekylab.menu.geekymenutest.adapters.ItemAdapter;
import com.geekylab.menu.geekymenutest.adapters.StoreCategoryAdapter;
import com.geekylab.menu.geekymenutest.db.entity.GlobalCategoryEntity;
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


public class ItemActivity extends Activity implements IFTaskCallback, AbsListView.OnItemClickListener {
    private static final String ARG_CURRENT_STORE_ID = "current_store_id";
    private static final String ARG_CURRENT_CATEGORY_ID = "current_category_id";
    private static final String TAG = "ItemActivity";
    private ArrayList<ItemEntity> m_parts = new ArrayList<ItemEntity>();
    private ListView listView;
    private ItemAdapter mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        Intent intent = getIntent();
        String mStoreId = intent.getStringExtra(ARG_CURRENT_STORE_ID);
        String mCategoryId = intent.getStringExtra(ARG_CURRENT_CATEGORY_ID);
        String defaultLanguage = Locale.getDefault().getLanguage();

        Log.d(TAG, mStoreId);
        Log.d(TAG, mCategoryId);

        String url = Params.OPEN_API_ITEM_URL + "/" + mStoreId + "?l=" + defaultLanguage;
        ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("category_id", mCategoryId));
        new DownloadJsonAsyncTaskHelper(this, this, HttpPost.METHOD_NAME, postParameters)
                .execute(url);


        listView = (ListView) findViewById(R.id.listView);
        mListAdapter = new ItemAdapter(this, R.layout.category_list_item, m_parts);
        listView.setAdapter(mListAdapter);
        listView.setOnItemClickListener(this);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_item, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

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
                        String price = item.getString("price");


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

                    mListAdapter.notifyDataSetChanged();
                } else {
                    String message = jsonObject.getString("message");
                    if (message == null)
                        message = "Has error";
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, ((ItemEntity) parent.getItemAtPosition(position)).getName());
    }
}
