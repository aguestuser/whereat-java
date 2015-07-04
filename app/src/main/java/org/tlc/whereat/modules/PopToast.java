package org.tlc.whereat.modules;

import android.content.Context;
import android.location.Location;
import android.widget.Toast;

public class PopToast {
    public static void briefly(Context ctx, String msg){
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }
    public static void location(Context ctx, Location loc) {
        briefly(ctx, "Location shared: " + loc.toString());
    }
}
