package org.tlc.whereat.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import org.tlc.whereat.R;

public class LocServicesAlertFragment extends DialogFragment{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        return new AlertDialog.Builder(getActivity())
            .setMessage(R.string.goog_loc_services_alert_title)
            .setPositiveButton(R.string.goog_loc_services_alert_yes_btn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getActivity().startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            })
            .setNegativeButton(R.string.goog_loc_services_alert_no_btn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            })
            .create();
    }
}
