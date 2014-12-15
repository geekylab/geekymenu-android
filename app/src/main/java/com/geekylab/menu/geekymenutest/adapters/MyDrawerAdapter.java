package com.geekylab.menu.geekymenutest.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geekylab.menu.geekymenutest.R;

import java.util.List;

/**
 * Created by johna on 14/12/14.
 * Kodokux System
 */
public class MyDrawerAdapter extends ArrayAdapter<MyDrawerAdapter.DrawerItem> {


    private final Context context;
    private final List<DrawerItem> drawerItemList;
    private final int layoutResID;

    public MyDrawerAdapter(Context context, int layoutResourceID, List<DrawerItem> listItems) {
        super(context, layoutResourceID, listItems);
        this.context = context;
        this.drawerItemList = listItems;
        this.layoutResID = layoutResourceID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DrawerItemHolder drawerHolder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            drawerHolder = new DrawerItemHolder();

            view = inflater.inflate(layoutResID, parent, false);
            drawerHolder.ItemName = (TextView) view.findViewById(R.id.drawer_itemName);
            drawerHolder.icon = (ImageView) view.findViewById(R.id.drawer_icon);
            drawerHolder.simpleItemLayout = (LinearLayout) view.findViewById(R.id.simple_item_layout);
            drawerHolder.userItemLayout = (FrameLayout) view.findViewById(R.id.user_item_layout);
            drawerHolder.userName = (TextView) view.findViewById(R.id.user_name);
            view.setTag(drawerHolder);
        } else {
            drawerHolder = (DrawerItemHolder) view.getTag();
        }

        DrawerItem dItem = (DrawerItem) this.drawerItemList.get(position);

        if (dItem.isSpinner()) {
            drawerHolder.userItemLayout.setVisibility(View.VISIBLE);
            drawerHolder.simpleItemLayout.setVisibility(View.GONE);
            drawerHolder.userName.setText(dItem.getItemName());

        } else {
            drawerHolder.userItemLayout.setVisibility(View.GONE);
            drawerHolder.simpleItemLayout.setVisibility(View.VISIBLE);
            if (dItem.getImageId() > 0) {
                drawerHolder.icon.setImageDrawable(view.getResources().getDrawable(dItem.getImageId()));
                drawerHolder.icon.setVisibility(View.VISIBLE);
            } else {
                drawerHolder.icon.setVisibility(View.GONE);
            }
            drawerHolder.ItemName.setText(dItem.getItemName());
        }


        return view;
    }

    private static class DrawerItemHolder {
        public TextView ItemName;
        public ImageView icon;
        public LinearLayout simpleItemLayout;
        public FrameLayout userItemLayout;
        public TextView userName;
    }

    public static class DrawerItem {
        private boolean mIsSpinner;
        private int mImgResID;
        private String mTitle;
        private String mItemName;


        public DrawerItem(Boolean isSpinner, String title, String itemName) {
            mIsSpinner = isSpinner;
            mTitle = title;
            mItemName = itemName;
        }

        public DrawerItem(String title, int imageId) {
            mTitle = title;
            mItemName = title;
            mImgResID = imageId;
        }

        public DrawerItem(String title) {
            this(title, 0);
        }

        public String getTitle() {
            return mTitle;
        }

        public int getImageId() {
            return mImgResID;
        }

        public Boolean isSpinner() {
            return mIsSpinner;
        }

        public String getItemName() {
            return mItemName;
        }
    }
}
