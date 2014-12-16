package com.geekylab.menu.geekymenutest.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.geekylab.menu.geekymenutest.R;
import com.geekylab.menu.geekymenutest.db.entity.StoreEntity;
import com.geekylab.menu.geekymenutest.db.entity.StoreImageEntity;
import com.geekylab.menu.geekymenutest.network.ImageDownloader;

import java.util.ArrayList;

/**
 * Created by johna on 25/11/14.
 * kodokux System
 */
public class StoreAdapter extends ArrayAdapter<StoreEntity> {
    private static final String TAG = "GlobalCategoryAdapter";
    private final int resource;
    private final ArrayList<StoreEntity> objects;
    private final ImageDownloader imageDownloader = new ImageDownloader();


    public StoreAdapter(Context context, int resource, ArrayList<StoreEntity> objects) {
        super(context, resource, objects);
        this.objects = objects;
        this.resource = resource;
        imageDownloader.setMode(ImageDownloader.Mode.NO_DOWNLOADED_DRAWABLE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        return super.getView(position, convertView, parent);

        // assign the view we are converting to a local variable
        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(resource, null);
        }

        StoreEntity i = objects.get(position);

        String storeName = i.getStoreName();
        String tel = i.getTel();

        ArrayList<StoreImageEntity> images = i.getImages();
        String storeMainImageUrl = null;
        if (images.size() > 0) {
            storeMainImageUrl = images.get(0).getImageUrl();
        }

//        ProgressBar categoryImageViewProgressBar = (ProgressBar) v.findViewById(R.id.imageViewProgressBar);
        TextView storeNameTextView = (TextView) v.findViewById(R.id.storeName);
        ImageView storeImageView = (ImageView) v.findViewById(R.id.imageView);
        TextView telTextView = (TextView) v.findViewById(R.id.tel);
        TextView addressTextView = (TextView) v.findViewById(R.id.address);
        TextView startOpeningHourTextView = (TextView) v.findViewById(R.id.openingHourStart);
        TextView endOpeningHourTextView = (TextView) v.findViewById(R.id.openingHourEnd);
        TextView lastOrderOpeningHourTextView = (TextView) v.findViewById(R.id.openingHourLastOrder);

        storeNameTextView.setText(storeName);
        telTextView.setText(tel);
        addressTextView.setText(i.getAddress1());

        startOpeningHourTextView.setText(i.getStartOpeningHour());
        endOpeningHourTextView.setText(i.getEndOpeningHour());
        lastOrderOpeningHourTextView.setText(i.getLastOrderTime());


        if (storeImageView != null) {
            storeImageView.setTag(storeMainImageUrl);
            Log.d(TAG, storeMainImageUrl);
//            new DownloadImageAsyncTaskHelper(storeImageView, categoryImageViewProgressBar).execute(storeMainImageUrl);
            imageDownloader.download(storeMainImageUrl, storeImageView);
        }

        return v;
    }
}
