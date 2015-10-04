package org.tlc.whereat.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import com.google.android.gms.common.GooglePlayServicesUtil;

public class PlayServicesAlertFragment extends DialogFragment {

    protected final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000; //TODO: move to resources
    int mCode;

    public PlayServicesAlertFragment setCode(int code){
        mCode = code;
        return this;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return GooglePlayServicesUtil.getErrorDialog(mCode, getActivity(), PLAY_SERVICES_RESOLUTION_REQUEST);
    }
}
