package org.tlc.whereat.receivers;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.common.ConnectionResult;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import org.tlc.whereat.BuildConfig;
import org.tlc.whereat.R;
import org.tlc.whereat.activities.MainActivity;
import org.tlc.whereat.broadcasters.LocPubBroadcasters;
import org.tlc.whereat.fragments.LocServicesAlertFragment;
import org.tlc.whereat.fragments.PlayServicesAlertFragment;
import org.tlc.whereat.pubsub.LocationPublisher;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.tlc.whereat.support.ActivityHelpers.*;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)

public class GoogleApiReceiversTest extends ReceiversTest {

    GoogleApiReceivers rcv;

    @Before
    public void setup(){
        ctx = createActivity(MainActivity.class);
        lbm = spy(LocalBroadcastManager.getInstance(RuntimeEnvironment.application));
        rcv = new GoogleApiReceivers(ctx, lbm);
        ifArg = ArgumentCaptor.forClass(IntentFilter.class);
    }

    @Test
    public void constructor_should_instantiateFragments(){
        assertThat(rcv.mLocServicesAlert).isNotNull();
    }

    @Test
    public void register_should_registerBroadcastReceivers(){
        rcv.register();

        verify(lbm).registerReceiver(eq(rcv.mApiClientDisconnected), ifArg.capture());
        assertThat(ifArg.getValue().hasAction(LocPubBroadcasters.ACTION_GOOGLE_API_CLIENT_DISCONNECTED)).isTrue();

        verify(lbm).registerReceiver(eq(rcv.mLocationServicesDisabledReceiver), ifArg.capture());
        assertThat(ifArg.getValue().hasAction(LocPubBroadcasters.ACTION_LOCATION_SERVICES_DISABLED)).isTrue();

        verify(lbm).registerReceiver(eq(rcv.mPlayServicesDisabledReceiver), ifArg.capture());
        assertThat(ifArg.getValue().hasAction(LocPubBroadcasters.ACTION_PLAY_SERVICES_DISABLED)).isTrue();
    }

    @Test
    public void unregister_should_unRegisterAllReceivers() {
        rcv.unregister();

        verify(lbm).unregisterReceiver(rcv.mApiClientDisconnected);
        verify(lbm).unregisterReceiver(rcv.mLocationServicesDisabledReceiver);
        verify(lbm).unregisterReceiver(rcv.mPlayServicesDisabledReceiver);
    }

    @Test
    public void apiClientDisconnectedReceiver_should_tryToFixApiConnection() throws IntentSender.SendIntentException {
        ConnectionResult cr = mock(ConnectionResult.class);
        when(cr.hasResolution()).thenReturn(true);

        rcv.register();
        broadcastApiFailure(lbm, cr);

        verify(cr).startResolutionForResult(ctx, GoogleApiReceivers.CONNECTION_FAILURE_RESOLUTION_REQUEST);
    }

    @Test
    public void apiClientDisconnectedReceiver_should_notifyUserIfApiConnectionUnfixable() throws IntentSender.SendIntentException {
        ConnectionResult cr = mock(ConnectionResult.class);
        when(cr.hasResolution()).thenReturn(false);

        rcv.register();
        broadcastApiFailure(lbm, cr);

        assertThat(lastToast()).isEqualTo(ctx.getString(R.string.goog_loc_api_disconnected_toast));
    }

    protected void broadcastApiFailure(LocalBroadcastManager lbm, ConnectionResult cr){
        lbm.sendBroadcast(new Intent()
            .setAction(LocPubBroadcasters.ACTION_GOOGLE_API_CLIENT_DISCONNECTED)
            .putExtra(LocPubBroadcasters.ACTION_GOOGLE_API_CLIENT_DISCONNECTED, cr));
    }

    @Test
    public void locationServicesDisabledReceiver_should_tryToFixLocationServices(){
        rcv.mLocServicesAlert = mock(LocServicesAlertFragment.class);
        rcv.register();
        lbm.sendBroadcast(new Intent().setAction(LocPubBroadcasters.ACTION_LOCATION_SERVICES_DISABLED));

        verify(rcv.mLocServicesAlert)
            .show(ctx.getFragmentManager(),ctx.getString(R.string.goog_loc_services_alert_tag));
    }

    @Test
    public void playServicesDisabledReceiver_should_tryToFixPlayServices(){
        rcv = new ReceiverWithPlay(ctx, lbm);
        rcv.mPlayServicesAlert = mock(PlayServicesAlertFragment.class);
        rcv.register();
        broadcastPlayServicesDisabled(lbm);

        verify(rcv.mPlayServicesAlert)
            .show(ctx.getFragmentManager(), ctx.getString(R.string.goog_play_services_alert_tag));
    }

    @Test
    public void playServicesDisabledReceiver_should_notifyUserIfPlayServicesUnfixable(){
        rcv = new ReceiverWithNoPlay(ctx, lbm);
        rcv.register();
        broadcastPlayServicesDisabled(lbm);

        assertThat(lastToast()).isEqualTo(ctx.getString(R.string.goog_play_services_unavailable_toast));
    }

    private void broadcastPlayServicesDisabled(LocalBroadcastManager lbm){
        lbm.sendBroadcast(new Intent().setAction(LocPubBroadcasters.ACTION_PLAY_SERVICES_DISABLED));
    }

    private class ReceiverWithPlay extends GoogleApiReceivers {

        public ReceiverWithPlay(Context ctx, LocalBroadcastManager lbm){
            super(ctx, lbm);
        }

        @Override
        protected int playAvailable(Context ctx){
            return 1; // recoverable
        }
    }

    private class ReceiverWithNoPlay extends GoogleApiReceivers {

        public ReceiverWithNoPlay(Context ctx, LocalBroadcastManager lbm){
            super(ctx, lbm);
        }

        @Override
        protected int playAvailable(Context ctx){
            return 5; // not recoverable

        }

        @Override
        protected boolean recoverable(int code){
            return false;
        }

    }
}
