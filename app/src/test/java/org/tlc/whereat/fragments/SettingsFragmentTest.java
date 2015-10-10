package org.tlc.whereat.fragments;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.tlc.whereat.BuildConfig;
import org.tlc.whereat.R;

import java.util.Arrays;

import static org.robolectric.Shadows.shadowOf;
import static org.robolectric.util.FragmentTestUtil.startFragment;
import static org.assertj.core.api.Assertions.*;
import static org.robolectric.util.FragmentTestUtil.startVisibleFragment;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)


public class SettingsFragmentTest {

    static SettingsFragment frag;
    static Context ctx = RuntimeEnvironment.application;

    static CharSequence[] locShareVals = {
        ctx.getString(R.string.pref_loc_share_interval_value_0),
        ctx.getString(R.string.pref_loc_share_interval_value_1),
        ctx.getString(R.string.pref_loc_share_interval_value_2),
        ctx.getString(R.string.pref_loc_share_interval_value_3),
        ctx.getString(R.string.pref_loc_share_interval_value_4),
        ctx.getString(R.string.pref_loc_share_interval_value_5)
    };

    static CharSequence[] locShareLabels = {
        ctx.getString(R.string.pref_loc_share_interval_label_0),
        ctx.getString(R.string.pref_loc_share_interval_label_1),
        ctx.getString(R.string.pref_loc_share_interval_label_2),
        ctx.getString(R.string.pref_loc_share_interval_label_3),
        ctx.getString(R.string.pref_loc_share_interval_label_4),
        ctx.getString(R.string.pref_loc_share_interval_label_5)
    };


    @Before
    public void setup() {
        frag = new SettingsFragment();
        startFragment(frag);
    }

    @Test
    public void fragment_should_notBeNull(){
        assertThat(frag).isNotNull();
    }

    @Test
    public void locationSharingIntervalSetting_should_haveCorrectContents(){
        ListPreference lp = (ListPreference) frag.findPreference("pref_key_loc_share_interval");

        assertThat(lp).isNotNull();
        assertThat(lp.isPersistent()).isTrue();

        assertThat(lp.getTitle()).isEqualTo(frag.getString(R.string.pref_loc_share_interval_title));
        assertThat(lp.getSummary()).isEqualTo(frag.getString(R.string.pref_loc_share_interval_summary));

        assertThat(lp.getEntries()).isEqualTo(locShareLabels);
        assertThat(lp.getEntryValues()).isEqualTo(locShareVals);
        assertThat(lp.getValue()).isEqualTo(frag.getString(R.string.pref_loc_share_interval_value_2));
    }
}