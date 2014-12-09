package com.geekylab.menu.geekymenutest;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.util.List;


public class FirstActivity extends Activity implements View.OnClickListener {

    private static final int SCANNER_REQUEST_CODE = 1234;
    private static final String TAG = FirstActivity.class.getSimpleName();
    public static final String ARG_STORE_ID = "store_id";
    public static final String ARG_TABLE_ID = "table_id";
    private Button mQrCodeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_first);
        mQrCodeButton = (Button) findViewById(R.id.read_qrcode);
        mQrCodeButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.read_qrcode:

            default:
                Intent zxing_intent = new Intent("com.google.zxing.client.android.SCAN");
                zxing_intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                startActivityForResult(zxing_intent, SCANNER_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == SCANNER_REQUEST_CODE) {
            // Handle scan intent
            if (resultCode == Activity.RESULT_OK) {
                // Handle successful scan
                String contents = intent.getStringExtra("SCAN_RESULT");
                String formatName = intent.getStringExtra("SCAN_RESULT_FORMAT");
                byte[] rawBytes = intent.getByteArrayExtra("SCAN_RESULT_BYTES");
                int intentOrientation = intent.getIntExtra("SCAN_RESULT_ORIENTATION", Integer.MIN_VALUE);
                Integer orientation = (intentOrientation == Integer.MIN_VALUE) ? null : intentOrientation;
                String errorCorrectionLevel = intent.getStringExtra("SCAN_RESULT_ERROR_CORRECTION_LEVEL");

                try {
                    Uri uri = Uri.parse(contents);
                    if (uri.getHost().equals("menu.geekylab.net")) {
                        List<String> pathSegments = uri.getPathSegments();
                        if (pathSegments.size() >= 2) {
                            if (pathSegments.get(0).equals("_store")) {

                                String mStoreId = pathSegments.get(1);

                                String mTableId = null;
                                if (pathSegments.size() >= 3) {
                                    mTableId = pathSegments.get(2);
                                }

                                //check table

                                Intent menuIntent = new Intent(FirstActivity.this, MenuActivity.class);
                                menuIntent.putExtra(ARG_STORE_ID, mStoreId);
                                menuIntent.putExtra(ARG_TABLE_ID, mTableId);
                                startActivity(menuIntent);


//                                Log.d(TAG, pathSegments.get(1));
//                                mSectionsPagerAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());
                }


            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Handle cancel
            }
        } else {
            // Handle other intents
        }

    }

}
