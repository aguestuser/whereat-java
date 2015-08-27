package org.tlc.whereat.api;


import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;

import static org.tlc.whereat.support.LocationHelpers.*;
import static org.tlc.whereat.support.ApiHelpers.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Author: @aguestuser
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */

public class WhereatApiClientTest {

    protected static MockWebServer mServer;
    protected static String mServerRoot;
    protected WhereatApiClient mClient;

    @BeforeClass
    public static void setup() throws Exception {
        mServer = new MockWebServer();
        mServer.start();
        mServerRoot = mServer.getUrl("/").toString();
    }

    @AfterClass
    public static void teardown() throws Exception {
        mServer.shutdown();
    }


    @Test
    public void update_onInitialPing_should_returnAllLocations(){
        mServer.enqueue(new MockResponse().setResponseCode(200).setBody(API_INIT_RESPONSE));
        mClient = WhereatApiClient.getInstance(mServerRoot);

        assertThat(
            mClient.update(updateInitStub()).toBlocking().first())
            .isEqualTo(Arrays.asList(s17UserLocationStub(), n17UserLocationStub()));
    }

    @Test
    public void update_onSubsequentPings_should_returnLocsPostedSinceLastPing(){
        mServer.enqueue(new MockResponse().setResponseCode(200).setBody(API_REFRESH_RESPONSE));
        mClient = WhereatApiClient.getInstance(mServerRoot);

        assertThat(
            mClient.update(updateRefreshStub()).toBlocking().first())
            .isEqualTo(Arrays.asList(n17UserLocationStub()));
    }

    @Test
    public void remove_should_returnDeletionNotification() throws Exception {
        mServer.enqueue(new MockResponse().setResponseCode(200).setBody(REMOVE_MSG_JSON));
        mClient = WhereatApiClient.getInstance(mServerRoot);

        assertThat(
            mClient.remove(s17UserLocationStub()).toBlocking().first())
            .isEqualTo(removeMsgStub());
    }
}