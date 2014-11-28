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
import com.geekylab.menu.geekymenutest.db.entity.ItemEntity;
import com.geekylab.menu.geekymenutest.db.entity.ItemImageEntity;
import com.geekylab.menu.geekymenutest.db.entity.StoreCategoryEntity;
import com.geekylab.menu.geekymenutest.network.ImageDownloader;

import java.util.ArrayList;

/**
 * Created by johna on 25/11/14.
 */
public class ItemAdapter extends ArrayAdapter<ItemEntity> {
    private static final String TAG = "GlobalCategoryAdapter";
    private final int resource;
    private final ArrayList<ItemEntity> objects;
    private final ImageDownloader imageDownloader = new ImageDownloader();


    public ItemAdapter(Context context, int resource, ArrayList<ItemEntity> objects) {
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

        ItemEntity i = objects.get(position);

        String categoryName = i.getName();
        ArrayList<ItemImageEntity> imageUrls = i.getImageUrls();
        String categoryImageUrl = null;
        if (imageUrls.size() > 0) {
            categoryImageUrl = imageUrls.get(0).getUrl();
        }

        Log.d(TAG, "getView" + categoryName);

        TextView categoryNameTextView = (TextView) v.findViewById(R.id.categoryName);
        ImageView categoryImageView = (ImageView) v.findViewById(R.id.imageView);
//        ProgressBar categoryImageViewProgressBar = (ProgressBar) v.findViewById(R.id.imageViewProgressBar);
        categoryNameTextView.setText(categoryName);

        if (categoryImageView != null) {
            categoryImageView.setTag(categoryImageUrl);
            if (categoryImageUrl != null)
                imageDownloader.download(categoryImageUrl, categoryImageView);
            else
                categoryImageView.setVisibility(View.INVISIBLE);
        }

        return v;
    }
}
