package com.geekylab.menu.geekymenutest.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.geekylab.menu.geekymenutest.R;
import com.geekylab.menu.geekymenutest.db.entity.ItemEntity;
import com.geekylab.menu.geekymenutest.db.entity.ItemImageEntity;
import com.geekylab.menu.geekymenutest.network.ImageDownloader;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by johna on 25/11/14.
 * Geekylab
 */
public class ItemAdapter extends ArrayAdapter<ItemEntity> {
    private static final String TAG = "GlobalCategoryAdapter";
    private final int resource;
    private final ArrayList<ItemEntity> objects;
    private final ImageDownloader imageDownloader = new ImageDownloader();
    private final NumberFormat currencyInstance;
    private final OnItemOrderListener mListener;


    public ItemAdapter(Context context, int resource, ArrayList<ItemEntity> objects) {
        super(context, resource, objects);
        this.objects = objects;
        this.resource = resource;
        Locale ptBr = new Locale("pt", "BR");
        currencyInstance = NumberFormat.getCurrencyInstance(ptBr);
        imageDownloader.setMode(ImageDownloader.Mode.NO_DOWNLOADED_DRAWABLE);


        try {
            mListener = (OnItemOrderListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnItemOrderListener");
        }


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

        String itemName = i.getName();
        ArrayList<ItemImageEntity> imageUrls = i.getImageUrls();
        String itemImageUrl = null;
        if (imageUrls.size() > 0) {
            itemImageUrl = imageUrls.get(0).getUrl();
        }

        TextView itemNameTextView = (TextView) v.findViewById(R.id.itemName);
        TextView itemPriceTextView = (TextView) v.findViewById(R.id.itemPrice);
        ImageView itemImageView = (ImageView) v.findViewById(R.id.imageView);
        Button orderButton = (Button) v.findViewById(R.id.orderButton);
        orderButton.setTag(i);

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClickOrderButton((ItemEntity) v.getTag());
            }
        });


        itemNameTextView.setText(itemName);
        itemPriceTextView.setText(currencyInstance.format(i.getPrice()));

        if (itemImageView != null) {
            itemImageView.setTag(itemImageUrl);
            if (itemImageUrl != null)
                imageDownloader.download(itemImageUrl, itemImageView);
            else
                itemImageView.setVisibility(View.INVISIBLE);
        }

        return v;
    }

    public interface OnItemOrderListener {
        public void onClickOrderButton(ItemEntity itemEntity);
    }


}
