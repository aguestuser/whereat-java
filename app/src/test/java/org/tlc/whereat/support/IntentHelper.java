package org.tlc.whereat.support;

import android.content.Intent;

public class IntentHelper {

    public static boolean sameAction(Intent i1, Intent i2){

        String axn1 = i1.getAction();
        String val1 = i1.getExtras().getString(axn1);

        String axn2 = i2.getAction();
        String val2 = i2.getExtras().getString(axn2);

        return axn1.equals(axn2) && val1.equals(val2);
    }
}
