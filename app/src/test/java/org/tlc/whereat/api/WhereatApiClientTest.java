package org.tlc.whereat.api;


import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.tlc.whereat.model.UserLocation;

import java.util.Arrays;
import java.util.List;

import static org.tlc.whereat.support.LocationHelpers.*;
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
    public void testInit() throws Exception {
        mServer.enqueue(new MockResponse().setResponseCode(200).setBody(API_INIT_RESPONSE));
        mClient = WhereatApiClient.getInstance(mServerRoot);

        assertThat(
            mClient.init(s17UserLocationStub()).toBlocking().first())
            .isEqualTo(Arrays.asList(s17UserLocationStub()));
    }

    @Test
    public void testRefresh() throws Exception {
        mServer.enqueue(new MockResponse().setResponseCode(200).setBody(API_REFRESH_RESPONSE));
        mClient = WhereatApiClient.getInstance(mServerRoot);

        assertThat(
            mClient.refresh(s17LocationTimestampedStub()).toBlocking().first())
            .isEqualTo(Arrays.asList(s17UserLocationStub(), n17UserLocationStub()));

    }
}