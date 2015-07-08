package org.tlc.whereat.support;


import android.app.Activity;
import android.content.ComponentName;
import android.os.IBinder;

import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.util.ActivityController;

import static org.robolectric.Shadows.shadowOf;

public class TestHelpers {

    public static String nextActivity(Activity a){
        return shadowOf(shadowOf(a).getNextStartedActivity()).getComponent().getClassName();
    }

    public static String nextService(Activity a){
        return shadowOf(shadowOf(a).getNextStartedService()).getComponent().getClassName();
    }

    public static String lastToast(){
        return ShadowToast.getTextOfLatestToast();
    }

    public static void shadowBind(ComponentName cn, IBinder ib){
        shadowOf(RuntimeEnvironment.application)
            .setComponentNameAndServiceForBindService(cn, ib);
    }

}
