package org.tlc.whereat.fragments;

import android.app.AlertDialog;
import android.widget.Button;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;
import org.tlc.whereat.BuildConfig;
import org.tlc.whereat.R;

import static org.junit.Assert.*;
import static org.robolectric.Shadows.shadowOf;
import static org.robolectric.util.FragmentTestUtil.startFragment;
import static org.assertj.core.api.Assertions.*;

/**
 * Author: @aguestuser
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)

public class SecurityAlertFragmentTest {

    @Test
    public void securityAlertFragment_should_haveCorrectContents(){
        SecurityAlertFragment frag = new SecurityAlertFragment();
        startFragment(frag);
        assertThat(frag).isNotNull();

        AlertDialog alert = ShadowAlertDialog.getLatestAlertDialog();
        assertThat(alert).isNotNull();

        ShadowAlertDialog shAlert = shadowOf(alert);
        assertThat(shAlert).isNotNull();

        Button button = alert.getButton(AlertDialog.BUTTON_NEUTRAL);
        assertThat(button).isNotNull();

        assertThat(shAlert.getTitle()).isEqualTo(frag.getString(R.string.security_alert_title));
        assertThat(shAlert.getMessage()).isEqualTo(frag.getString(R.string.security_alert_message));
        assertThat(button.getText()).isEqualTo(frag.getString(R.string.security_alert_neutral_button_text));
    }


}