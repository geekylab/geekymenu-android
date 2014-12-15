package com.geekylab.menu.geekymenutest;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;


public class DashBoardActivity extends DebugActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, CheckInFragment.OnFragmentInteractionListener {

    public static final String TAG = DashBoardActivity.class.getSimpleName();
    private static final String ARG_STORE_ID = "store_id";
    private static final String ARG_TABLE_ID = "table_id";
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private String mStoreId;
    private String mTableId;
    private boolean mIsSavedInstanceState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mStoreId = savedInstanceState.getString(ARG_STORE_ID);
            mTableId = savedInstanceState.getString(ARG_TABLE_ID);
            Log.d(TAG, "onCreate mStoreId : " + mStoreId);
            Log.d(TAG, "onCreate mTableId : " + mTableId);
            mIsSavedInstanceState = true;
        }

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .build();
        ImageLoader.getInstance().init(config);

        setContentView(R.layout.activity_dash_board);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.d(TAG, "onSaveInstanceState mStoreId : " + mStoreId);
        Log.d(TAG, "onSaveInstanceState mTableId : " + mTableId);
        savedInstanceState.putString(ARG_STORE_ID, mStoreId);
        savedInstanceState.putString(ARG_TABLE_ID, mTableId);
    }

//    @Override
//    public void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        // Restore UI state from the savedInstanceState.
//        // This bundle has also been passed to onCreate.
//        mStoreId = savedInstanceState.getString(ARG_STORE_ID);
//        mTableId = savedInstanceState.getString(ARG_TABLE_ID);
//
//        Log.d(TAG, "onRestoreInstanceState mStoreId : " + mStoreId);
//        Log.d(TAG, "onRestoreInstanceState mTableId : " + mTableId);
//    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments

        Log.d(TAG, "onNavigationDrawerItemSelected : " + position);

        Fragment fragment = null;
        String tag = null;
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position) {
            case 0: //user settings
                fragment = PlaceholderFragment.newInstance(position + 1);
//                tag = "pos " + position + 1;
                break;
            case 1: //checkin
                if (mStoreId != null) {
                    Log.d(TAG, "TabMenuFragment.newInstance");
                    fragment = StoreFragment.newInstance(position + 1, mStoreId, mTableId);
//                    tag = TabMenuFragment.TAG;
                } else {
                    fragment = CheckInFragment.newInstance(mStoreId, mTableId);
                }
                break;
            case 2: //all history
                fragment = PlaceholderFragment.newInstance(position + 1);
//                tag = "pos " + position + 1;
                break;
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction()
//                .setCustomAnimations(android.R.animator.fade_in,
//                        android.R.animator.fade_out)
                ;
        if (tag != null) {
            transaction.replace(R.id.container, fragment, tag);
        } else {
            transaction.replace(R.id.container, fragment);
        }

        transaction.commit();
    }

    public void onSectionAttached(int number) {
        Log.d(TAG, "onSectionAttached : " + number);
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }


    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff33b5e5")));
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.dash_board, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(String storeId, String tableId) {
        mStoreId = storeId;
        mTableId = tableId;
        Fragment tabMenuFragment = StoreFragment.newInstance(2, storeId, tableId);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
//                .
//                setCustomAnimations(android.R.animator.fade_in,
//                        android.R.animator.fade_out)
                .replace(R.id.container, tabMenuFragment)
                .commit();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
//            setRetainInstance(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_dash_board, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((DashBoardActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
