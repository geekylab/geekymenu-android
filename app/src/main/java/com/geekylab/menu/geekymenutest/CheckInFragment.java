package com.geekylab.menu.geekymenutest;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CheckInFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CheckInFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CheckInFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = CheckInFragment.class.getSimpleName();
    private static final int SCANNER_REQUEST_CODE = 1234;
    public static final String ARG_STORE_ID = "store_id";
    public static final String ARG_TABLE_ID = "table_id";

    private String mStoreId;
    private String mTableId;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CheckInFragment.
     */
    public static CheckInFragment newInstance(String storeId, String tableId) {
        CheckInFragment fragment = new CheckInFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STORE_ID, storeId);
        args.putString(ARG_TABLE_ID, tableId);
        fragment.setArguments(args);
        return fragment;
    }

    public CheckInFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStoreId = getArguments().getString(ARG_STORE_ID);
            mTableId = getArguments().getString(ARG_TABLE_ID);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            mStoreId = savedInstanceState.getString(ARG_STORE_ID);
            mTableId = savedInstanceState.getString(ARG_TABLE_ID);
        }

        Log.d(TAG, "CheckInFragment onRestoreInstanceState mStoreId : " + mStoreId);
        Log.d(TAG, "CheckInFragment onRestoreInstanceState mTableId : " + mTableId);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.d(TAG, "CheckInFragment onSaveInstanceState mStoreId : " + mStoreId);
        Log.d(TAG, "CheckInFragment onSaveInstanceState mTableId : " + mTableId);
        savedInstanceState.putString(ARG_STORE_ID, mStoreId);
        savedInstanceState.putString(ARG_TABLE_ID, mTableId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_check_in, container, false);
        View qrCodeButton = view.findViewById(R.id.read_qrcode);
        qrCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent zxing_intent = new Intent("com.google.zxing.client.android.SCAN");
                zxing_intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                startActivityForResult(zxing_intent, SCANNER_REQUEST_CODE);

            }
        });
        return view;
    }

    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onFragmentInteraction(mStoreId, mTableId);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String storeId, String tableId);
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
                    Log.d(TAG, "Response URI : " + contents);
                    if (uri.getHost().equals("menu.geekylab.net")) {
                        List<String> pathSegments = uri.getPathSegments();
                        if (pathSegments.size() >= 2) {
                            if (pathSegments.get(0).equals("_store")) {

                                String mStoreId = pathSegments.get(1);
                                Log.d(TAG, "mStoreId : " + mStoreId);
                                Log.d(TAG, "mTableId : " + mTableId);

                                String mTableId = null;
                                if (pathSegments.size() >= 3) {
                                    mTableId = pathSegments.get(2);
                                }

                                //check table
                                if (mListener != null) {
                                    mListener.onFragmentInteraction(mStoreId, mTableId);
                                }
//                                Intent menuIntent = new Intent(CheckInFragment.this.getActivity(), MenuActivity.class);
//                                menuIntent.putExtra(ARG_STORE_ID, mStoreId);
//                                menuIntent.putExtra(ARG_TABLE_ID, mTableId);
//                                startActivity(menuIntent);
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
