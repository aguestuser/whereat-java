package org.tlc.whereat.fragments;

import android.app.AlertDialog;
import android.provider.Settings;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowIntent;
import org.tlc.whereat.BuildConfig;
import org.tlc.whereat.R;

import static org.robolectric.Shadows.shadowOf;
import static org.robolectric.util.FragmentTestUtil.startFragment;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static android.app.AlertDialog.BUTTON_NEGATIVE;
import static android.app.AlertDialog.BUTTON_POSITIVE;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)

public class LocServicesAlertFragmentTest {

    protected static LocServicesAlertFragment mFrag;
    protected static AlertDialog mAlert;

    @Before
    public void setup() throws Exception {
        mFrag = new LocServicesAlertFragment();
        startFragment(mFrag);
        mAlert = ShadowAlertDialog.getLatestAlertDialog();
    }

    @Test
    public void fragment_should_haveCorrectContents(){
        ShadowAlertDialog shAlert = shadowOf(mAlert);

        assertThat(mFrag).isNotNull();
        assertThat(mAlert).isNotNull();
        assertThat(mAlert.isShowing()).isTrue();

        assertThat(shAlert.getMessage())
            .isEqualTo(mFrag.getString(R.string.goog_loc_services_alert_title));
        assertThat(mAlert.getButton(BUTTON_NEGATIVE).getText())
            .isEqualTo(mFrag.getString(R.string.goog_loc_services_alert_no_btn));
        assertThat(mAlert.getButton(BUTTON_POSITIVE).getText())
            .isEqualTo(mFrag.getString(R.string.goog_loc_services_alert_yes_btn));
    }

    @Test
    public void clickingNegativeButton_should_dismissAlert(){
        assertThat(mAlert.isShowing()).isTrue();
        mAlert.getButton(BUTTON_NEGATIVE).performClick();
        assertThat(mAlert.isShowing()).isFalse();
    }

    @Test
    public void clickingPositiveButton_should_startProcessOfEnablingLocServices(){
        ShadowApplication app = ShadowApplication.getInstance();
        mAlert.getButton(BUTTON_POSITIVE).performClick();

        ShadowIntent si = shadowOf(app.getNextStartedActivity());
        assertThat(si.getAction()).isEqualTo(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
    }
}