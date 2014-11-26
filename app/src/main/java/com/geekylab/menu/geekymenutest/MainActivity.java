package com.geekylab.menu.geekymenutest;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        AbstractBaseListFragment.OnFragmentInteractionListener {

    private static final String TAG = "MainActivity";
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private int mItemSelectedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentManager fragmentManager = getFragmentManager();

        Fragment target = null;

        switch (position) {
            case 0:
                target = GlobalCategoryListFragment.newInstance(position + 1);
                break;
            case 1:
                target = GlobalCategoryListFragment.newInstance(position + 1);
                break;
            default:
                target = PlaceholderFragment.newInstance(position + 1);
        }


        FragmentTransaction transaction = fragmentManager
                .beginTransaction()
                .replace(R.id.container, target);

        transaction.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
        if (position != mItemSelectedPosition) {
            transaction.addToBackStack(String.valueOf(mItemSelectedPosition));
        }


        transaction.commit();
        mItemSelectedPosition = position;
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.store_category);
                break;
            case 2:
                mTitle = getString(R.string.mesa);
                break;
            case 3:
                mTitle = getString(R.string.user_setting);
                break;
            case 4:
                mTitle = getString(R.string.test_list);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        if (count > 0) {
            int index = Integer.parseInt(
                    getFragmentManager().getBackStackEntryAt(count - 1).getName());
            getFragmentManager().popBackStack();
            mItemSelectedPosition = index;
            onSectionAttached(index + 1);
            restoreActionBar();
            Log.d(TAG, "onBackPressed : " + index);
            return;
        }
        super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
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
    public void onFragmentInteraction(String id) {
        Log.d(TAG, id);
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
        private static final String TAG = "PlaceholderFragment";

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
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            int anInt = getArguments().getInt(ARG_SECTION_NUMBER);

            View rootView = null;
            switch (anInt) {
                case 2:
                    rootView = inflater.inflate(R.layout.fragment_table, container, false);
                    ((TextView) rootView.findViewById(R.id.section_label)).setText("" + anInt);
                    break;
                default:
                    rootView = inflater.inflate(R.layout.fragment_settings, container, false);
                    ((TextView) rootView.findViewById(R.id.section_label)).setText("" + anInt);
                    break;
            }


            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }
}
