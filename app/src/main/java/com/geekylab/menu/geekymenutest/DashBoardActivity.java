package com.geekylab.menu.geekymenutest;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
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

import com.geekylab.menu.geekymenutest.db.entity.UserOrderEntity;
import com.geekylab.menu.geekymenutest.db.table.OrderTable;
import com.geekylab.menu.geekymenutest.network.GoogleTokenRestTask;
import com.geekylab.menu.geekymenutest.network.RestTask;
import com.geekylab.menu.geekymenutest.openapi.Params;
import com.geekylab.menu.geekymenutest.services.OrderService;
import com.geekylab.menu.geekymenutest.utils.AppParams;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class DashBoardActivity extends DebugActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, CheckInFragment.OnFragmentInteractionListener {

    public static final String TAG = DashBoardActivity.class.getSimpleName();
    public static final String ARG_STORE_ID = "store_id";
    public static final String ARG_TABLE_ID = "table_id";
    public static final String ARG_USER_TOKEN = "user_token";
    public static final String ARG_TABLE_TOKEN = "table_token";
    private static final String TABLE_CHECK_IN_ACTION = "TABLE_CHECK_IN_ACTION";
    public static final String ACCEPT_CHECK_IN_ACTION = "ACCEPT_CHECK_IN_ACTION";
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
    private OrderService myService;
    private Intent serviceIntent;
    private String mServiceToken;

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myService = ((OrderService.MyBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
            myService = null;
        }
    };

    private BroadcastReceiver taskReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(TABLE_CHECK_IN_ACTION)) {
                String response = intent.getStringExtra(RestTask.HTTP_RESPONSE);
                Log.d(TAG, "BroadcastReceiver : " + response);
                if (response != null) {
                    try {
                        JSONObject checkInJsonObject = new JSONObject(response);
                        if (checkInJsonObject.has("status") && checkInJsonObject.getBoolean("status")) {
                            //OK
                            if (myService != null) {
                                if (!myService.isConnected()) {
                                    myService.setUrl(Params.HOST_URL);
                                    myService.setStoreId(mStoreId);
                                    myService.setTableId(mTableId);
                                    myService.setUserToken(mServiceToken);
                                    startService(serviceIntent);
                                    Log.d(TAG, "start orderService");
                                }
                            } else {
                                Log.d(TAG, "myService is null!!!! fuck!!");
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (intent.getAction().equals(ACCEPT_CHECK_IN_ACTION)) {
                Log.d(TAG, "ACCEPT_CHECK_IN_ACTION");
                String response = intent.getStringExtra(OrderService.SOCKET_RESPONSE);
                if (response != null) {
                }


                Log.d(TAG, response);
            }
        }
    };


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

        if (mTableId != null && mStoreId != null) {
            startOrderService();
        }

        mServiceToken = getServiceToken(getApplicationContext());
        serviceIntent = new Intent(DashBoardActivity.this, OrderService.class);

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
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TABLE_CHECK_IN_ACTION);
        intentFilter.addAction(ACCEPT_CHECK_IN_ACTION);
        registerReceiver(taskReceiver, intentFilter);

        if (serviceIntent != null)
            bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(taskReceiver);

        if (serviceIntent != null)
            unbindService(serviceConnection);

        Log.d(TAG, "onPause unbindService");
    }

    /**
     * Check services
     *
     * @param serviceClass serviceClass
     * @return return true if running false if not.
     */
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            //Log.d(TAG, serviceClass.getName() + "==" + service.service.getClassName());
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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

        if (mStoreId != null && mTableId != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Check-in on table?");
            builder.setPositiveButton("Check-in", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    HttpPost httpPost;
                    try {
                        httpPost = new HttpPost(new URI(Params.OPEN_API_TABLE_TOKEN + "/" + mStoreId));
                        httpPost.addHeader("X-Auth-Hash", mServiceToken);
                        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
                        parameters.add(new BasicNameValuePair("service_token", mServiceToken));
                        parameters.add(new BasicNameValuePair("table_id", mTableId));
                        httpPost.setEntity(new UrlEncodedFormEntity(parameters));

                        new RestTask(getApplicationContext(), TABLE_CHECK_IN_ACTION)
                                .execute(httpPost);

                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();
        }

        Fragment tabMenuFragment = StoreFragment.newInstance(2, storeId, tableId);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
//                .
//                setCustomAnimations(android.R.animator.fade_in,
//                        android.R.animator.fade_out)
                .replace(R.id.container, tabMenuFragment)
                .commit();
    }

    private void startOrderService() {
        //service
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

    /**
     * @param context Application context
     * @return registrationId Geeky Menu Service Token
     */
    private String getServiceToken(Context context) {

        final SharedPreferences prefs = getSharedPreferences(AppParams.SHARED_PREF_NAME, Activity.MODE_PRIVATE);
        String serviceToken = prefs.getString("service_token", "");
        if (serviceToken.isEmpty()) {
            Log.i(TAG, "service token not found.");
            return "";
        }
        return serviceToken;
    }

}
