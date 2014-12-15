package com.geekylab.menu.geekymenutest;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TabMenuFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TabMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabMenuFragment extends DebugFragment {
    private static final String ARG_TABLE_ID = "table_id";
    private static final String ARG_STORE_ID = "store_id";
    public static final String TAG = TabMenuFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private String mStoreId;
    private String mTableId;
    private ViewPager mViewPager;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param storeId Parameter 1.
     * @param tableId Parameter 2.
     * @return A new instance of fragment TabMenuFragment.
     */

    public static TabMenuFragment newInstance(String storeId, String tableId) {
        Log.d(TAG, "newInstance");
        Log.d(TAG, "newInstance : storeId" + storeId);
        Log.d(TAG, "newInstance : tableId" + tableId);
        TabMenuFragment fragment = new TabMenuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TABLE_ID, tableId);
        args.putString(ARG_STORE_ID, storeId);
        fragment.setArguments(args);
        return fragment;
    }

    public TabMenuFragment() {
        // Required empty public constructor
    }

    @Override
    String getClassTag() {
        return TAG;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRetainInstance(true);
        if (getArguments() != null) {
            mStoreId = getArguments().getString(ARG_STORE_ID);
            mTableId = getArguments().getString(ARG_TABLE_ID);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_tab_menu, container, false);


        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) inflate.findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Locale l = Locale.getDefault();
                ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
                String mTitle = "";
                switch (position) {
                    case 0:
                        mTitle = getString(R.string.title_about).toUpperCase(l);
                        break;
                    case 1:
                        mTitle = getString(R.string.title_menu).toUpperCase(l);
                        break;
                    case 2:
                        mTitle = getString(R.string.title_history).toUpperCase(l);
                        break;
                }

                Log.d(TAG, mTitle);
                if (actionBar != null) {
                    actionBar.setTitle(mTitle);
                    Log.d(TAG, "set action bar title");
                }

                mViewPager.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return inflate;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((DashBoardActivity) activity).onSectionAttached(1);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * A {@link android.support.v13.app.FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return StoreFragment.newInstance(position + 1, mStoreId, mTableId);
//                case 1:
//                    return StoreCategoryListFragment.newInstance(position + 1, mStoreId);
//                default:
//                    return HistoryFragment.newInstance(position + 1, mStoreId);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_about).toUpperCase(l);
                case 1:
                    return getString(R.string.title_menu).toUpperCase(l);
                case 2:
                    return getString(R.string.title_history).toUpperCase(l);
            }
            return null;
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
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
