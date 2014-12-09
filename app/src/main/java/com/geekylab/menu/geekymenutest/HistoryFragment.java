package com.geekylab.menu.geekymenutest;


import android.app.Fragment;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;

import com.geekylab.menu.geekymenutest.db.table.OrderTable;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends ListFragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_STORE_ID = "store_id";
    private static final String TAG = "HistoryFragment";

    private int mSectionNumber;
    private String mStoreId;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(int sectionNumber, String store_id) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString(ARG_STORE_ID, store_id);
        fragment.setArguments(args);
        return fragment;
    }

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            mStoreId = getArguments().getString(ARG_STORE_ID);
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        OrderTable orderTable = new OrderTable(getActivity());
        Cursor orderCursor = orderTable.findByStoreId(mStoreId);
        String[] from = {
                OrderTable.COL_STORE_ID
        };

        int[] to = {
                R.id.testText,
        };
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), R.layout.history_item, orderCursor, from, to, 0);
        setListAdapter(adapter);

//        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
//
//            @Override
//            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
//                return false;
//            }
//        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

}
