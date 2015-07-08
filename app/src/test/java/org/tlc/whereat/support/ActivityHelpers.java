package org.tlc.whereat.support;


import android.app.Activity;
import android.content.ComponentName;
import android.os.IBinder;

import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.util.ActivityController;

import static org.robolectric.Shadows.shadowOf;

public class ActivityHelpers {

    public static <T extends Activity> T createActivity(Class<T> activity){
        return Robolectric.buildActivity(activity).create().get();
    }

    public static <T extends Activity> T resumeActivity(Class<T> activity){
        return Robolectric.buildActivity(activity).create().resume().visible().get();
    }

    public static String nextActivity(Activity a){
        return shadowOf(shadowOf(a).getNextStartedActivity()).getComponent().getClassName();
    }

    public static String nextService(Activity a){
        return shadowOf(shadowOf(a).getNextStartedService()).getComponent().getClassName();
    }

    public static String lastToast(){
        return ShadowToast.getTextOfLatestToast();
    }

}
