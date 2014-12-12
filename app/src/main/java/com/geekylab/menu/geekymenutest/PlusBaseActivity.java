package com.geekylab.menu.geekymenutest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.geekylab.menu.geekymenutest.openapi.Params;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.Person;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


/**
 * A base class to wrap communication with the Google Play Services PlusClient.
 */
public abstract class PlusBaseActivity extends Activity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = PlusBaseActivity.class.getSimpleName();

    // A magic number we will use to know that our sign-in error resolution activity has completed
    private static final int OUR_REQUEST_CODE = 49404;
    private static final int REQ_SIGN_IN_REQUIRED = 55664;

    // A flag to stop multiple dialogues appearing for the user
    private boolean mAutoResolveOnFail;

    // A flag to track when a connection is already in progress
    public boolean mPlusClientIsConnecting = false;

    // This is the helper object that connects to Google Play Services.
//    private PlusClient mPlusClient;
    private GoogleApiClient mGoogleApiClient;

    // The saved result from {@link #onConnectionFailed(ConnectionResult)}.  If a connection
    // attempt has been made, this is non-null.
    // If this IS null, then the connect method is still running.
    private ConnectionResult mConnectionResult;
    protected String mAccountName;


    /**
     * Called when the {@link PlusClient} revokes access to this app.
     */
    protected abstract void onPlusClientRevokeAccess();

    /**
     * Called when the PlusClient is successfully connected.
     */
    protected abstract void onPlusClientSignIn();

    /**
     * Called when the {@link PlusClient} is disconnected.
     */
    protected abstract void onPlusClientSignOut();

    /**
     * Called when the {@link PlusClient} is blocking the UI.  If you have a progress bar widget,
     * this tells you when to show or hide it.
     */
    protected abstract void onPlusClientBlockingUI(boolean show);

    /**
     * Called when there is a change in connection state.  If you have "Sign in"/ "Connect",
     * "Sign out"/ "Disconnect", or "Revoke access" buttons, this lets you know when their states
     * need to be updated.
     */
    protected abstract void updateConnectButtonState();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    /**
     * Try to sign in the user.
     */
    public void signIn() {
        if (!mGoogleApiClient.isConnected()) {
            // Show the dialog as we are now signing in.
            setProgressBarVisible(true);
            // Make sure that we will start the resolution (e.g. fire the intent and pop up a
            // dialog for the user) for any errors that come in.
            mAutoResolveOnFail = true;
            // We should always have a connection result ready to resolve,
            // so we can start that process.
            if (mConnectionResult != null) {
                startResolution();
            } else {
                // If we don't have one though, we can start connect in
                // order to retrieve one.
                initiatePlusClientConnect();
            }
        }

        updateConnectButtonState();
    }

    /**
     * Connect the {@link PlusClient} only if a connection isn't already in progress.  This will
     * call back to {@link #onConnected(android.os.Bundle)} or
     * {@link #onConnectionFailed(com.google.android.gms.common.ConnectionResult)}.
     */
    private void initiatePlusClientConnect() {
        if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        }
    }


    private void initiatePlusClientDisconnect() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Sign out the user (so they can switch to another account).
     */
    public void signOut() {

        // We only want to sign out if we're connected.
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            initiatePlusClientDisconnect();
            Log.v(TAG, "Sign out successful!");
        }

        updateConnectButtonState();
    }

    /**
     * Revoke Google+ authorization completely.
     */
    public void revokeAccess() {

        if (mGoogleApiClient.isConnected()) {
            // Clear the default account as in the Sign Out.
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            updateConnectButtonState();
            onPlusClientRevokeAccess();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        initiatePlusClientConnect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        initiatePlusClientDisconnect();
    }

    public boolean isPlusClientConnecting() {
        return mPlusClientIsConnecting;
    }

    private void setProgressBarVisible(boolean flag) {
        mPlusClientIsConnecting = flag;
        onPlusClientBlockingUI(flag);
    }

    /**
     * A helper method to flip the mResolveOnFail flag and start the resolution
     * of the ConnectionResult from the failed connect() call.
     */
    private void startResolution() {
        try {
            // Don't start another resolution now until we have a result from the activity we're
            // about to start.
            mAutoResolveOnFail = false;
            // If we can resolve the error, then call start resolution and pass it an integer tag
            // we can use to track.
            // This means that when we get the onActivityResult callback we'll know it's from
            // being started here.
            mConnectionResult.startResolutionForResult(this, OUR_REQUEST_CODE);
        } catch (IntentSender.SendIntentException e) {
            // Any problems, just try to connect() again so we get a new ConnectionResult.
            mConnectionResult = null;
            initiatePlusClientConnect();
        }
    }

    /**
     * An earlier connection failed, and we're now receiving the result of the resolution attempt
     * by PlusClient.
     *
     * @see #onConnectionFailed(ConnectionResult)
     */
    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        updateConnectButtonState();
        if (requestCode == OUR_REQUEST_CODE && responseCode == RESULT_OK) {
            // If we have a successful result, we will want to be able to resolve any further
            // errors, so turn on resolution with our flag.
            mAutoResolveOnFail = true;
            // If we have a successful result, let's call connect() again. If there are any more
            // errors to resolve we'll get our onConnectionFailed, but if not,
            // we'll get onConnected.
            initiatePlusClientConnect();
        } else if (requestCode == OUR_REQUEST_CODE && responseCode != RESULT_OK) {
            // If we've got an error we can't resolve, we're no longer in the midst of signing
            // in, so we can stop the progress spinner.
            setProgressBarVisible(false);
        } else if (requestCode == REQ_SIGN_IN_REQUIRED && responseCode == RESULT_OK) {
//            new RetrieveTokenTask().execute(mAccountName);
        }
    }

    /**
     * Successfully connected (called by PlusClient)
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        updateConnectButtonState();
        setProgressBarVisible(false);


        onPlusClientSignIn();
    }


    @Override
    public void onConnectionSuspended(int i) {
        updateConnectButtonState();
        onPlusClientSignOut();
    }

    /**
     * Connection failed for some reason (called by PlusClient)
     * Try and resolve the result.  Failure here is usually not an indication of a serious error,
     * just that the user's input is needed.
     *
     * @see #onActivityResult(int, int, Intent)
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        updateConnectButtonState();

        // Most of the time, the connection will fail with a user resolvable result. We can store
        // that in our mConnectionResult property ready to be used when the user clicks the
        // sign-in button.
        if (result.hasResolution()) {
            mConnectionResult = result;
            if (mAutoResolveOnFail) {
                // This is a local helper function that starts the resolution of the problem,
                // which may be showing the user an account chooser or similar.
                startResolution();
            }
        }
    }

    public GoogleApiClient getPlusClient() {
        return mGoogleApiClient;
    }

    protected class RetrieveTokenTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String accountName = params[0];
            String scopes = "oauth2:profile email";
            String token = null;
            String profile_raw_json = null;
            try {
                token = GoogleAuthUtil.getToken(getApplicationContext(), accountName, scopes);
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Params.HOST_URL + "/auth/google-token");
                BasicNameValuePair access_token = new BasicNameValuePair("access_token", token);
                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(access_token);
                try {
                    // UrlEncodedFormEntity is an entity composed of a list of url-encoded pairs.
                    //This is typically useful while sending an HTTP POST request.
                    UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);

                    // setEntity() hands the entity (here it is urlEncodedFormEntity) to the request.
                    httpPost.setEntity(urlEncodedFormEntity);

                    try {
                        // HttpResponse is an interface just like HttpPost.
                        //Therefore we can't initialize them
                        HttpResponse httpResponse = httpClient.execute(httpPost);

                        // According to the JAVA API, InputStream constructor do nothing.
                        //So we can't initialize InputStream although it is not an interface
                        InputStream inputStream = httpResponse.getEntity().getContent();

                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                        StringBuilder stringBuilder = new StringBuilder();

                        String bufferedStrChunk = null;

                        while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
                            stringBuilder.append(bufferedStrChunk);
                        }

                        profile_raw_json = stringBuilder.toString();
                    } catch (ClientProtocolException cpe) {
                        Log.d(TAG, "First Exception caz of HttpResponese :" + cpe);
                        cpe.printStackTrace();
                    } catch (IOException ioe) {
                        Log.d(TAG, "Second Exception caz of HttpResponse :" + ioe);
                        ioe.printStackTrace();
                    }

                } catch (UnsupportedEncodingException uee) {
                    Log.d(TAG, "An Exception given because of UrlEncodedFormEntity argument :" + uee);
                    uee.printStackTrace();
                }

            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } catch (UserRecoverableAuthException e) {
                startActivityForResult(e.getIntent(), REQ_SIGN_IN_REQUIRED);
            } catch (GoogleAuthException e) {
                Log.e(TAG, e.getMessage());
            }
            return profile_raw_json;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //access_token
            if (s != null) {
                try {
                    JSONObject profileJsonObject = new JSONObject(s);
                    if (profileJsonObject.has("status") && profileJsonObject.getBoolean("status")) {
                        //ok
                        if (profileJsonObject.has("profile")) {
                            JSONObject profile = profileJsonObject.getJSONObject("profile");
                            Log.d(TAG, profile.toString());

                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(PlusBaseActivity.this);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("service_token", profile.getString("service_token"));
                            editor.putString("display_name", profile.getString("display_name"));
                            editor.putString("image_url", profile.getString("image_url"));
                            editor.apply();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
