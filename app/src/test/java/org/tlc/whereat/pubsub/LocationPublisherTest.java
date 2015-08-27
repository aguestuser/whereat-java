package org.tlc.whereat.pubsub;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.tlc.whereat.BuildConfig;


import org.tlc.whereat.pubsub.LocationPublisher;

import static org.assertj.core.api.Assertions.*;


/**
 * Author: @aguestuser
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */


@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)

public class LocationPublisherTest {

    // LIFE CYCLE METHODS

    //#onStartCommand()

    @Test
    private void onStartCommand_whenPlayServicesEnabled_initalizesLocPub(){

    }

    @Test
    private void onStartCommand_whenPlayServicesDisabled_broadcastsPlayServicesDisabled() {

    }

    //#initialize()

    @Test
    private void initialize_should_initializePrivateFields(){

    }

    //#connect()
    @Test
    private void connect_whenAlreadyConnected_doesNothing(){

    }

    @Test
    private void connect_whenNotConnected_connectsGoogleApiClient(){

    }

    //#onBind()

    @Test
    private void onBind_returnsLocationServiceBinderWith_getServiceThatReturnsThis(){

    }

    //#onDestroy()

    @Test
    private void onDestory_cleansUpResources(){

    }

    // PUBLIC METHODS

    //#ping()

    @Test
    public void ping_whenLocationNull_broadcastsFailedLocationRequest(){

    }

    @Test
    public void ping_whenLocationExists_relaysLocation(){

    }

    //#poll

    @Test
    public void poll_turnsOnPolling(){

    }

    //#stopPolling
    public void stopPolling_stopsPolling(){

    }

    //#clear
    public void clear_clearsUserFromServerAllLocsFromPhone(){

    }



}