package com.geekylab.menu.geekymenutest;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.geekylab.menu.geekymenutest.network.GoogleTokenRestTask;
import com.geekylab.menu.geekymenutest.network.RestTask;
import com.geekylab.menu.geekymenutest.openapi.Params;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class FirstActivity extends Activity {

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String PROPERTY_USER_TOKEN = "user_token";
    private static final String PROPERTY_SERVICE_TOKEN = "service_token";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    private static final int REQ_SIGN_IN_REQUIRED = 55664;
    private static final String GOOGLE_TOKEN_ACTION = "google_token_action";
    private static final String SEND_REGISTRATION_ID_ACTION = "SEND_REGISTRATION_ID_ACTION";

    private ProgressDialog progressDialog;

    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    String SENDER_ID = "301670391865";

    private static final int SCANNER_REQUEST_CODE = 1234;
    private static final String TAG = FirstActivity.class.getSimpleName();
    public static final String ARG_STORE_ID = "store_id";
    public static final String ARG_TABLE_ID = "table_id";
    private GoogleCloudMessaging gcm;
    private String regid;
    private String mServiceToken;
    private Context context;
    private Button mQrCodeButton;
    private AutoCompleteTextView mEmailView;
    private SignInButton mPlusSignInButton;

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    private BroadcastReceiver taskReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (progressDialog != null)
                progressDialog.dismiss();

            String response = "";
            //google token result
            if (intent.getAction().equals(GOOGLE_TOKEN_ACTION)) {
                response = intent.getStringExtra(GoogleTokenRestTask.HTTP_RESPONSE);
                if (response != null) {
                    JSONObject jsonObject = null;
                    Boolean status = false;
                    try {
                        jsonObject = new JSONObject(response);
                        status = jsonObject.getBoolean("status");
                        if (jsonObject.has("profile")) {
                            JSONObject profileJsonObject = jsonObject.getJSONObject("profile");
                            mServiceToken = profileJsonObject.getString("service_token");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (status && !mServiceToken.isEmpty()) {
                        storeServiceToken(context, mServiceToken);
                        //register regId
                        gcm = GoogleCloudMessaging.getInstance(FirstActivity.this);
                        regid = getRegistrationId(context);
                        if (regid.isEmpty()) {
                            progressDialog = ProgressDialog.show(FirstActivity.this, "Registration service", "Waitng for results....");
                            registerInBackground();
                        }

                        Intent DashBoardIntent = new Intent(FirstActivity.this, DashBoardActivity.class);
                        startActivity(DashBoardIntent);
                    }
                }
            } else {
                response = intent.getStringExtra(RestTask.HTTP_RESPONSE);
            }

            Log.d(TAG, "BroadcastReceiver : " + response);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_first);

        context = getApplicationContext();

        mPlusSignInButton = (SignInButton) findViewById(R.id.plus_sign_in_button);
        if (checkPlayServices()) {
            // Set a listener to connect the user when the G+ button is clicked.
            mPlusSignInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mServiceToken = getServiceToken(context);
                    if (mServiceToken.isEmpty()) {
                        pickUserAccount();
                    } else {
                        Intent intent = new Intent(FirstActivity.this, DashBoardActivity.class);
                        startActivity(intent);
                    }
                }
            });
        } else {
            // Don't offer G+ sign in if the app's version is too low to support Google Play
            // Services.
            mPlusSignInButton.setVisibility(View.GONE);
            return;
        }


//        mPlusSignInButton = (SignInButton) findViewById(R.id.plus_sign_in_button);
//        if (checkPlayServices()) {
//            // Set a listener to connect the user when the G+ button is clicked.
//            mPlusSignInButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    signIn();
//                }
//            });
//
//        } else {
//            // Don't offer G+ sign in if the app's version is too low to support Google Play
//            // Services.
//            mPlusSignInButton.setVisibility(View.GONE);
//            return;
//        }

//        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
//        populateAutoComplete();


//        mQrCodeButton = (Button) findViewById(R.id.read_qrcode);
//        mQrCodeButton.setOnClickListener(this);
    }

    private void pickUserAccount() {
        String[] accountTypes = new String[]{"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, null, null, null, null);
        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }

    // You need to do the Play Services APK check here too.
    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GOOGLE_TOKEN_ACTION);
        intentFilter.addAction(SEND_REGISTRATION_ID_ACTION);
        registerReceiver(taskReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(taskReceiver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
            // Receiving a result from the AccountPicker
            if (resultCode == RESULT_OK) {
                String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                // With the account name acquired, go get the auth token

                if (!accountName.isEmpty()) {
                    GoogleTokenRestTask googleTokenRestTask =
                            new GoogleTokenRestTask(context, GOOGLE_TOKEN_ACTION, accountName);
                    googleTokenRestTask.execute();
                    progressDialog = ProgressDialog.show(this, "Login", "Waitng for results....");
                } else {
                    Log.d(TAG, "Account Name is null");
                }

//                getUsername();
            } else if (resultCode == RESULT_CANCELED) {
                // The account picker dialog closed without selecting an account.
                // Notify users that they must pick an account to proceed.
//                Toast.makeText(this, R.string.pick_account, Toast.LENGTH_SHORT).show();
            }
        }


//        if (requestCode == SCANNER_REQUEST_CODE) {
//            // Handle scan intent
//            if (resultCode == Activity.RESULT_OK) {
//                // Handle successful scan
//                String contents = intent.getStringExtra("SCAN_RESULT");
//                String formatName = intent.getStringExtra("SCAN_RESULT_FORMAT");
//                byte[] rawBytes = intent.getByteArrayExtra("SCAN_RESULT_BYTES");
//                int intentOrientation = intent.getIntExtra("SCAN_RESULT_ORIENTATION", Integer.MIN_VALUE);
//                Integer orientation = (intentOrientation == Integer.MIN_VALUE) ? null : intentOrientation;
//                String errorCorrectionLevel = intent.getStringExtra("SCAN_RESULT_ERROR_CORRECTION_LEVEL");
//
//                try {
//                    Uri uri = Uri.parse(contents);
//                    if (uri.getHost().equals("menu.geekylab.net")) {
//                        List<String> pathSegments = uri.getPathSegments();
//                        if (pathSegments.size() >= 2) {
//                            if (pathSegments.get(0).equals("_store")) {
//
//                                String mStoreId = pathSegments.get(1);
//
//                                String mTableId = null;
//                                if (pathSegments.size() >= 3) {
//                                    mTableId = pathSegments.get(2);
//                                }
//
//                                //check table
//
//                                Intent menuIntent = new Intent(FirstActivity.this, MenuActivity.class);
//                                menuIntent.putExtra(ARG_STORE_ID, mStoreId);
//                                menuIntent.putExtra(ARG_TABLE_ID, mTableId);
//                                startActivity(menuIntent);
//
//
////                                Log.d(TAG, pathSegments.get(1));
////                                mSectionsPagerAdapter.notifyDataSetChanged();
//                            }
//                        }
//                    }
//                } catch (Exception e) {
//                    Log.d(TAG, e.getMessage());
//                }
//
//
//            } else if (resultCode == Activity.RESULT_CANCELED) {
//                // Handle cancel
//            }
//        } else {
//            // Handle other intents
//        }
    }


    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @param context Application context
     * @return registrationId Geeky Menu Service Token
     */
    private String getServiceToken(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String serviceToken = prefs.getString(PROPERTY_SERVICE_TOKEN, "");
        if (serviceToken.isEmpty()) {
            Log.i(TAG, "service token not found.");
            return "";
        }

        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return serviceToken;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(FirstActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.d(TAG, msg);
            }

        }.execute(null, null, null);
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * or CCS to send messages to your app. Not needed for this demo since the
     * device sends upstream messages to a server that echoes back the message
     * using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
        // Eu jah estou no background!!!
        if (mServiceToken != null && !mServiceToken.isEmpty()) {

            HttpPut httpPut;
            try {
                httpPut = new HttpPut(new URI(Params.OPEN_API_HOST_URL + "/regid"));
                httpPut.addHeader("X-Auth-Hash", mServiceToken);
                List<NameValuePair> parameters = new ArrayList<NameValuePair>();
                parameters.add(new BasicNameValuePair("regid", regid));
                httpPut.setEntity(new UrlEncodedFormEntity(parameters));

                new RestTask(context, SEND_REGISTRATION_ID_ACTION)
                        .execute(httpPut);

            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


            Log.d(TAG, "sendRegistrationIdToBackend :: " + mServiceToken);
        }
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context     application's context.
     * @param googleToken registration ID
     */
    private void storeGoogleToken(Context context, String googleToken) {
        final SharedPreferences prefs = getGCMPreferences(context);
        Log.i(TAG, "Saving google token " + googleToken);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_USER_TOKEN, googleToken);
        editor.commit();
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context      application's context.
     * @param serviceToken registration ID
     */
    private void storeServiceToken(Context context, String serviceToken) {
        final SharedPreferences prefs = getGCMPreferences(context);
        Log.i(TAG, "Saving service Token " + serviceToken);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_SERVICE_TOKEN, serviceToken);
        editor.commit();
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

}
