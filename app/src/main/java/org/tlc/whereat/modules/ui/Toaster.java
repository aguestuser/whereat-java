package org.tlc.whereat.modules.ui;

import android.content.Context;
import android.widget.Toast;

import org.tlc.whereat.model.UserLocation;

public class Toaster {
    public static void shortToast(Context ctx, String msg){
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }
    public static void locToast(Context ctx, UserLocation loc) {
        shortToast(ctx, "Location shared: " + loc.toString());
    }
}
