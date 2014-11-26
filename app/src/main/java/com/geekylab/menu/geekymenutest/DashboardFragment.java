package com.geekylab.menu.geekymenutest;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.geekylab.menu.geekymenutest.adapters.GlobalCategoryAdapter;
import com.geekylab.menu.geekymenutest.network.DownloadJsonAsyncTaskHelper;
import com.geekylab.menu.geekymenutest.network.IFTaskCallback;

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

    public static DashboardFragment newInstance() {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, 1);
        fragment.setArguments(args);
        return fragment;
    }

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        globalCategoryAdapter = new GlobalCategoryAdapter(this.getActivity(), R.layout.category_list_item, m_parts);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        new DownloadJsonAsyncTaskHelper(this.getActivity(), this).execute(OPEN_API_GLOBAL_CATEGORY);

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
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            globalCategoryAdapter.notifyDataSetChanged();
            Log.d(TAG, "onFinish" + jsonObject.toString());
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
