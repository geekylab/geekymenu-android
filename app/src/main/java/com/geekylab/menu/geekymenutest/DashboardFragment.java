package com.geekylab.menu.geekymenutest;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.geekylab.menu.geekymenutest.adapters.GlobalCategoryAdapter;
import com.geekylab.menu.geekymenutest.db.table.GlobalCategoryTable;
import com.geekylab.menu.geekymenutest.network.DownloadJsonAsyncTaskHelper;
import com.geekylab.menu.geekymenutest.network.IFTaskCallback;
import com.geekylab.menu.geekymenutest.utils.NetworkUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DashboardFragment extends Fragment implements IFTaskCallback, AdapterView.OnItemClickListener {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG = "DashboardFragment";
    private final String OPEN_API_GLOBAL_CATEGORY = "http://192.168.111.103:8080/open-api/global-category";
    private ArrayList<JSONObject> m_parts = new ArrayList<JSONObject>();
    private GlobalCategoryAdapter globalCategoryAdapter;
    private GlobalCategoryTable globalCategoryTable;

    public static DashboardFragment newInstance(int sectionNumber) {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "onAttach");
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        globalCategoryTable = new GlobalCategoryTable(getActivity());
//        Cursor globalCategoryCursor = globalCategoryTable.findAll();

//        if (globalCategoryCursor.moveToFirst()) {
//            do {
//                Log.d(TAG,
//                        globalCategoryCursor.getString(
//                                globalCategoryCursor.getColumnIndex(GlobalCategoryTable.COL_CATEGORY_NAME)
//                        )
//                );
////                Note note = new Note(
////                        c.getInt(c.getColumnIndex(DBAdapter.COL_ID)),
////                        c.getString(c.getColumnIndex(DBAdapter.COL_NOTE)),
////                        c.getString(c.getColumnIndex(DBAdapter.COL_LASTUPDATE))
////                );
////                noteList.add(note);
//            } while (globalCategoryCursor.moveToNext());
//        }

        globalCategoryAdapter = new GlobalCategoryAdapter(this.getActivity(), R.layout.category_list_item, m_parts);


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
        if (NetworkUtil.netWorkCheck(getActivity())) {
            new DownloadJsonAsyncTaskHelper(this.getActivity(), this).execute(OPEN_API_GLOBAL_CATEGORY);
        } else {
            Toast.makeText(getActivity(), "network off", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onStart();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        ListView listView = (ListView) view.findViewById(R.id.listView);
        GridView gridView = (GridView) view.findViewById(R.id.gridView);

        if (listView != null) {
            Log.d(TAG, "listView");
            listView.setAdapter(globalCategoryAdapter);
            listView.setOnItemClickListener(this);
        } else if (gridView != null) {
            Log.d(TAG, "gridView");
            gridView.setAdapter(globalCategoryAdapter);
            gridView.setOnItemClickListener(this);
        }


        return view;
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
//                        if (categoryObject.has("name") && categoryObject.has("name")) {
//                            String categoryName = categoryObject.getString("name");
//                            ContentValues values = new ContentValues();
//                            values.put("category_name", categoryName);
//                            globalCategoryTable.insert(values);
//                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            globalCategoryAdapter.notifyDataSetChanged();

//            Log.d(TAG, "onFinish" + jsonObject.toString());

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        JSONObject jsonObject = (JSONObject) parent.getItemAtPosition(position);
        if (jsonObject.has("name")) {
            String categoryName = "";
            try {
                categoryName = jsonObject.getString("name");
            } catch (JSONException e) {
            }
//            Toast.makeText(this.getActivity(), categoryName, Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this.getActivity().getApplicationContext(), StoreListActivity.class);
            startActivity(i);
        }
    }
}
