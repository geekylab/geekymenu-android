package com.geekylab.menu.geekymenutest.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.geekylab.menu.geekymenutest.R;
import com.geekylab.menu.geekymenutest.network.DownloadImageAsyncTaskHelper;
import com.geekylab.menu.geekymenutest.network.ImageDownloader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by johna on 25/11/14.
 *
 */
public class GlobalCategoryAdapter extends ArrayAdapter<JSONObject> {
    private static final String TAG = "GlobalCategoryAdapter";
    private final int resource;
    private final ArrayList<JSONObject> objects;
    private final ImageDownloader imageDownloader = new ImageDownloader();


    public GlobalCategoryAdapter(Context context, int resource, ArrayList<JSONObject> objects) {
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
            v = inflater.inflate(R.layout.category_list_item, null);
        }

        JSONObject i = objects.get(position);
        String categoryName = null;
        String categoryImageUrl = "";
        try {
            categoryName = i.getString("name");
            if (i.has("image_url"))
                categoryImageUrl = i.getString("image_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        TextView categoryNameTextView = (TextView) v.findViewById(R.id.categoryName);
        ImageView categoryImageView = (ImageView) v.findViewById(R.id.imageView);
        ProgressBar categoryImageViewProgressBar = (ProgressBar) v.findViewById(R.id.imageViewProgressBar);
        categoryNameTextView.setText(categoryName);

        if (categoryImageView != null) {
            categoryImageView.setTag(categoryImageUrl);
            new DownloadImageAsyncTaskHelper(categoryImageView, categoryImageViewProgressBar).execute(categoryImageUrl);
            imageDownloader.download(categoryImageUrl, categoryImageView);
        }

        return v;
    }
}
