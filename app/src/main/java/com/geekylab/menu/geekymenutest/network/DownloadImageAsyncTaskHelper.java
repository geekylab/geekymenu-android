package com.geekylab.menu.geekymenutest.network;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.geekylab.menu.geekymenutest.R;
import com.geekylab.menu.geekymenutest.download.DownloadBitmap;

import java.lang.ref.WeakReference;

/**
 * Created by johna on 25/11/14.
 */
public class DownloadImageAsyncTaskHelper extends AsyncTask<String, Void, DownloadImageAsyncTaskHelper.ImageObject> {

    private static final String TAG = "DownloadImageAsyncTaskHelper";
    private final WeakReference imageViewReference;
    private final WeakReference imageViewProgressBarReference;
    final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
    final int cacheSize = maxMemory;
    final private LruCache<String, Bitmap> mMemoryCache;
    final private String path;


    public DownloadImageAsyncTaskHelper(ImageView imageView, ProgressBar categoryImageViewProgressBar) {
        imageViewReference = new WeakReference(imageView);
        imageViewProgressBarReference = new WeakReference(categoryImageViewProgressBar);
        path = imageView.getTag().toString();

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };


    }

    @Override
    protected void onPreExecute() {
        if (imageViewProgressBarReference != null) {
            ((ProgressBar) imageViewProgressBarReference.get()).setVisibility(View.VISIBLE);
            ((ImageView) imageViewReference.get()).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected DownloadImageAsyncTaskHelper.ImageObject doInBackground(String... params) {
        String url = params[0];

        Bitmap image = getBitmapFromMemCache(url);

        if (image == null) {
//            Log.d(TAG, "no cache");
//            Log.d(TAG, "doInBackground - " + params[0]);
            image = DownloadBitmap.download(url);
        } else {
            Log.d(TAG, "HAS cache");
        }

        ImageObject imageObject = new DownloadImageAsyncTaskHelper.ImageObject();
        imageObject.image_url = url;
        imageObject.bitmap = image;

        return imageObject;
    }


    @Override
    protected void onCancelled() {
        Log.d(TAG, "onCancelled");
    }


    @Override
    protected void onPostExecute(DownloadImageAsyncTaskHelper.ImageObject imageObject) {


        if (isCancelled()) {
            imageObject.bitmap = null;
        }

        if (imageViewReference != null) {
            ImageView imageView = (ImageView) imageViewReference.get();

            if (!imageView.getTag().toString().equals(path)) {
                return;
            }


            if (imageViewProgressBarReference != null) {
                ((ProgressBar) imageViewProgressBarReference.get()).setVisibility(View.INVISIBLE);
            }

            if (imageView.getTag().equals(path)) {
                if (imageView != null) {
                    imageView.setVisibility(View.VISIBLE);
                    if (imageObject.bitmap != null) {
                        addBitmapToMemoryCache(imageObject.image_url, imageObject.bitmap);
                        imageView.setImageBitmap(imageObject.bitmap);
                    } else {
                        imageView.setImageDrawable(imageView.getContext().getResources()
                                .getDrawable(R.drawable.image1));
                    }
                }
            }
        }
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    public class ImageObject {
        public String image_url;
        public Bitmap bitmap;
    }


}
