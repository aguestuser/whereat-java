package org.tlc.whereat.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;


import org.tlc.whereat.R;
import org.tlc.whereat.util.PopToast;

/**
 * Author: @aguestuser
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */
public class SecurityAlertFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        return new AlertDialog.Builder(getActivity())
            .setIcon(android.R.drawable.stat_notify_error)
            .setTitle(R.string.security_alert_title)
            .setMessage(R.string.security_alert_message)
            .setNeutralButton(R.string.security_alert_neutral_button_text, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            }).create();
    }

}