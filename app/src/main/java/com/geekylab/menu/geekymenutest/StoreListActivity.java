package com.geekylab.menu.geekymenutest;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;

/**
 * Created by johna on 25/11/14.
 * Kodokux System
 */
public class StoreListActivity extends Activity {
    private String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_dashboard);
        mTitle = getString(R.string.store_list);

        restoreActionBar();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);

        actionBar.setTitle(mTitle);
    }
}
