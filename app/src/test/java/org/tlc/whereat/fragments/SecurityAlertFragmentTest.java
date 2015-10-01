package org.tlc.whereat.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.widget.Button;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
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

/**
 * Author: @aguestuser
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)

public class SecurityAlertFragmentTest {

    protected static SecurityAlertFragment mFrag;
    protected static AlertDialog mAlert;

    @Before
    public void setup() throws Exception {
        mFrag = new SecurityAlertFragment();
        startFragment(mFrag);
        mAlert = ShadowAlertDialog.getLatestAlertDialog();
    }

    @Test
    public void securityAlertFragment_should_haveCorrectContents(){

        assertThat(mFrag).isNotNull();
        assertThat(mAlert).isNotNull();
        assertThat(mAlert.isShowing()).isTrue();

        ShadowAlertDialog shAlert = shadowOf(mAlert);
        assertThat(shAlert).isNotNull();
        assertThat(shAlert.getTitle()).isEqualTo(mFrag.getString(R.string.sec_alert_title));
        assertThat(shAlert.getMessage()).isEqualTo(mFrag.getString(R.string.sec_alert_message));

        Button noBtn = mAlert.getButton(AlertDialog.BUTTON_NEGATIVE);
        assertThat(noBtn).isNotNull();
        assertThat(noBtn.getText()).isEqualTo(mFrag.getString(R.string.sec_alert_negative_button_text));

        Button yesBtn = mAlert.getButton(AlertDialog.BUTTON_POSITIVE);
        assertThat(yesBtn).isNotNull();
        assertThat(yesBtn.getText()).isEqualTo(mFrag.getString(R.string.sec_alert_positive_button_text));
    }

    @Test
    public void clickingNegativeButton_should_dismissAlert(){
        Button noBtn = mAlert.getButton(AlertDialog.BUTTON_NEGATIVE);

        assertThat(mAlert.isShowing()).isTrue();
        noBtn.performClick();
        assertThat(mAlert.isShowing()).isFalse();
    }

    @Test
    public void clickingPositiveButton_should_dismissAlert(){
        ShadowApplication app = ShadowApplication.getInstance();
        Button yesBtn = mAlert.getButton(AlertDialog.BUTTON_POSITIVE);
        yesBtn.performClick();

        ShadowIntent si = shadowOf(app.getNextStartedActivity());
        assertThat(si.getAction()).isEqualTo(Intent.ACTION_VIEW);
        assertThat(si.getData()).isEqualTo(Uri.parse(app.getString(R.string.sec_alert_url)));
    }


}